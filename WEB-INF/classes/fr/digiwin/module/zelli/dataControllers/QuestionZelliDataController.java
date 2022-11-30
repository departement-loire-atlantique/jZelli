package fr.digiwin.module.zelli.dataControllers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;
import com.google.firebase.messaging.WebpushNotification.Action;
import com.jalios.jcms.BasicDataController;
import com.jalios.jcms.Channel;
import com.jalios.jcms.ControllerStatus;
import com.jalios.jcms.Data;
import com.jalios.jcms.Group;
import com.jalios.jcms.Member;
import com.jalios.jcms.alert.Alert;
import com.jalios.jcms.alert.AlertBuilder;
import com.jalios.jcms.plugin.PluginComponent;
import com.jalios.util.Util;

import fr.digiwin.module.zelli.alertbuilder.AlertReponse;
import fr.digiwin.module.zelli.firebase.FirebaseMng;
import fr.digiwin.module.zelli.utils.ZelliUtils;
import generated.EditQuestionZelliHandler;
import generated.QuestionZelli;

public class QuestionZelliDataController extends BasicDataController implements PluginComponent {

	private static final Logger LOGGER = Logger.getLogger(QuestionZelliDataController.class);
	private static final Channel CHANNEL = Channel.getChannel();

	private final Integer WORKFLOW_A_TRAITER = -12;
	private final Integer WORKFLOW_ATTENTE = -2;
	private final Integer WORKFLOW_TRAITE = 2;

	/**
	 * Change status 
	 */
	public void beforeWrite(Data data, int op, Member mbr, Map context) {

		if (op != OP_UPDATE) {
			return;
		}

		if (!(data instanceof QuestionZelli)) {
			return;
		}

		if (!(context.get("formHandler") instanceof EditQuestionZelliHandler)) {
			return;
		}

		QuestionZelli questionZelli = (QuestionZelli) data;
		if ((questionZelli.getPstatus() == WORKFLOW_A_TRAITER || questionZelli.getPstatus() == WORKFLOW_ATTENTE)
				&& Util.notEmpty(questionZelli.getReponse())) {
			questionZelli.setPstatus(WORKFLOW_TRAITE);
		} else if (questionZelli.getPstatus() == WORKFLOW_A_TRAITER && Util.notEmpty(questionZelli.getRemarque())) {
			questionZelli.setPstatus(WORKFLOW_ATTENTE);
		}
	}

	/**
	 * Add date to DateDeLaReponse when answer is modified to QuestionZelli
	 * 
	 * @author fdebiais
	 */
	public void afterWrite(Data data, int op, Member mbr, Map context) {

		if (!(data instanceof QuestionZelli)) {
			return;
		}

		QuestionZelli questionZelli = (QuestionZelli) data;
        
        if(op == OP_CREATE) {
            // Notif custom
            Group gestQuestRepGrp = channel.getGroup("$jcmsplugin.zelli.groupe.gestionnaires.id");
            if(Util.notEmpty(gestQuestRepGrp)) {
                Set<Member> gestQuestRepSet = gestQuestRepGrp.getMemberSet();
                
                AlertBuilder alertBuilder = new AlertBuilder(Alert.Level.ACTION, "zelli", "newQuestion", questionZelli,
                        questionZelli.getAuthor()) {
                    @Override
                    protected void addParams(Member recipient, Map<String, String> paramMap, String markup) {
                        paramMap.put("age", ZelliUtils.getAgeStrFromDateNaissance(
                                questionZelli.getAuthor().getExtraData("extra.Member.jcmsplugin.zelli.datenaissance")));
                        paramMap.put("demRef", questionZelli.getReferent() ? "oui" : "non");
                        paramMap.put("question", questionZelli.getQuestion());
                        
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        String date = "";
                        try {
                            date = sdf.format(questionZelli.getCdate());
                        } catch (Exception e) {
                            LOGGER.error("parse date", e);
                            date = "-1";
                        }
                        paramMap.put("date", date);

                        if (HTML_MARKUP.equals(markup)) {
                            paramMap.put("linkQuest", "<a href=\"" + channel.getUrl()
                                    + "plugins/ZelliPlugin/jsp/questions.jsp\">Répondre à sa question</a>");
                        } else {
                            paramMap.put("linkQuest", "Répondre à sa question : " + channel.getUrl()
                                    + "plugins/ZelliPlugin/jsp/questions.jsp");
                        }
                    }
                };
                alertBuilder.sendAlert(gestQuestRepSet);
            }
        }

        if (op != OP_UPDATE) {
            return;
        }

		if (Util.notEmpty(questionZelli.getReponse()) && questionZelli.getReponse().length() > 0) {
			Object dataPrevious = context.get(CTXT_PREVIOUS_DATA);

			if (Util.isEmpty(dataPrevious)) {
				questionZelli.setDateDeLaReponse(new Date());
				questionZelli.setGestionnaire(CHANNEL.getCurrentLoggedMember());
				return;
			}

			QuestionZelli previousquestionZelli = (QuestionZelli) dataPrevious;

			if ((Util.notEmpty(previousquestionZelli.getReponse()) ^ Util.notEmpty(questionZelli.getReponse()))
					|| (!previousquestionZelli.getReponse().equals(questionZelli.getReponse()))) {
				questionZelli.setDateDeLaReponse(new Date());
				questionZelli.setGestionnaire(CHANNEL.getCurrentLoggedMember());
				ControllerStatus status = questionZelli.checkAndPerformCreate(CHANNEL.getDefaultAdmin());
				if (!status.isOK()) {
					LOGGER.info("Date and gestionnaire of the answer isn't save for " + questionZelli.getId());
				} else {
					String token = FirebaseMng.getInstance().getToken(questionZelli.getAuthor());
					if (Util.notEmpty(token)) {

						Notification notif = Notification.builder().setTitle("Réponse à ta question")
								.setBody(questionZelli.getReponse() + "\nEst-ce que cette réponse t'a aidé ?").build();

						WebpushNotification webNotif = WebpushNotification.builder()
								.addAction(new Action("question-oui", "Oui"))
								.addAction(new Action("question-non", "Non")).build();
						WebpushConfig webConf = WebpushConfig.builder().setNotification(webNotif).build();

						Message message = Message.builder().setNotification(notif).setWebpushConfig(webConf)
								.setToken(token).build();

						FirebaseMng.getInstance().sendMessage(message);
					}

                    // email
                    AlertBuilder alertBuilder = new AlertReponse(questionZelli, mbr);
                    alertBuilder.sendAlert(questionZelli.getAuthor());
                }
            }
		}
	}
}
