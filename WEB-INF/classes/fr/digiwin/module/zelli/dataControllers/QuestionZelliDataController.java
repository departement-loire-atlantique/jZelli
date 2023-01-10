package fr.digiwin.module.zelli.dataControllers;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushFcmOptions;
import com.jalios.jcms.BasicDataController;
import com.jalios.jcms.Channel;
import com.jalios.jcms.Data;
import com.jalios.jcms.Group;
import com.jalios.jcms.Member;
import com.jalios.jcms.alert.AlertBuilder;
import com.jalios.jcms.plugin.PluginComponent;
import com.jalios.util.Util;

import fr.digiwin.module.zelli.alertbuilder.AlertNewQuestion;
import fr.digiwin.module.zelli.alertbuilder.AlertReponse;
import fr.digiwin.module.zelli.firebase.FirebaseMng;
import generated.EditQuestionZelliHandler;
import generated.QuestionZelli;

public class QuestionZelliDataController extends BasicDataController implements PluginComponent {

    private static final Logger LOGGER = Logger.getLogger(QuestionZelliDataController.class);
    private static final Channel CHANNEL = Channel.getChannel();

    private final Integer WORKFLOW_A_TRAITER = -12;
    private final Integer WORKFLOW_ATTENTE = -2;
    private final Integer WORKFLOW_TRAITE = 2;
    private final static String CTX_NEW_REP = "Zelli.quest-new-rep";

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

        if (Util.notEmpty(questionZelli.getReponse())) {
            Object dataPrevious = context.get(CTXT_PREVIOUS_DATA);

            if (Util.isEmpty(dataPrevious) && Util.notEmpty(questionZelli.getReponse())) {
                questionZelli.setDateDeLaReponse(new Date());
                questionZelli.setGestionnaire(CHANNEL.getCurrentLoggedMember());
                context.put(CTX_NEW_REP, true);
            } else {
                QuestionZelli previousquestionZelli = (QuestionZelli) dataPrevious;
                if ((Util.isEmpty(previousquestionZelli.getReponse()) && Util.notEmpty(questionZelli.getReponse()))
                        || (!previousquestionZelli.getReponse().equals(questionZelli.getReponse()))) {
                    questionZelli.setDateDeLaReponse(new Date());
                    questionZelli.setGestionnaire(CHANNEL.getCurrentLoggedMember());
                    context.put(CTX_NEW_REP, true);
                }
            }
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

        if (op == OP_CREATE) {
            // Notif custom
            Group gestQuestRepGrp = channel.getGroup("$jcmsplugin.zelli.groupe.gestionnaires.id");
            if (Util.notEmpty(gestQuestRepGrp)) {
                Set<Member> gestQuestRepSet = gestQuestRepGrp.getMemberSet();

                AlertBuilder alertBuilder = new AlertNewQuestion(questionZelli, questionZelli.getAuthor());
                alertBuilder.sendAlert(gestQuestRepSet);
            }
        }

        if (op != OP_UPDATE) {
            return;
        }

        boolean hasNewRep = (boolean) context.get(CTX_NEW_REP);
        if (hasNewRep) {

            // Firebase
            String token = FirebaseMng.getInstance().getToken(questionZelli.getAuthor());
            if (Util.notEmpty(token)) {

                Notification notif = Notification.builder()
                        .setTitle("Réponse à ta question")
                        .setBody(questionZelli.getReponse() + "\nEst-ce que cette réponse t'a aidé ?")
                        .build();

                WebpushFcmOptions webFcmOption = WebpushFcmOptions.builder()
                        .setLink("/reponse/" + questionZelli.getId())
                        .build();
                WebpushConfig webConf = WebpushConfig.builder().setFcmOptions(webFcmOption).build();

                Message message = Message.builder()
                        .setNotification(notif)
                        .setWebpushConfig(webConf)
                        .setToken(token).build();

                FirebaseMng.getInstance().sendMessage(message);
            }

            // email
            AlertBuilder alertBuilder = new AlertReponse(questionZelli, mbr);
            alertBuilder.sendAlert(questionZelli.getAuthor());

        }
    }
}
