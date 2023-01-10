package fr.digiwin.module.zelli.alertbuilder;

import java.util.Map;

import com.jalios.jcms.Channel;
import com.jalios.jcms.JcmsUtil;
import com.jalios.jcms.Member;
import com.jalios.jcms.alert.Alert;

import fr.digiwin.module.zelli.utils.ZelliUtils;
import generated.QuestionZelli;

/**
 * représente une alert de nouvelle interaction sur une reponse
 * 
 * @author Digiwin
 */
public class AlertRepInteraction extends AAlertQuestionZelli {
    
    Boolean aAide;

    public AlertRepInteraction(QuestionZelli questZelli, Boolean aAide) {
        super(Alert.Level.ACTION, "zelli", "newRepInteraction", questZelli, questZelli.getAuthor());
        this.aAide = aAide;
    }

    @Override
    protected void addParams(Member recipient, Map<String, String> paramMap, String markup) {
        QuestionZelli questionZelli = (QuestionZelli) this.data;

        paramMap.put("age", ZelliUtils.getAgeStrFromDateNaissance(
                questionZelli.getAuthor().getExtraData("extra.Member.jcmsplugin.zelli.datenaissance")));
        paramMap.put("demRef", questionZelli.getReferent() ? "oui" : "non");
        paramMap.put("question", questionZelli.getQuestion());
        paramMap.put("reponse", questionZelli.getReponse());

        paramMap.put("aAide1", JcmsUtil.glp(recipient.getLanguage(),
                "jcmsplugin.zelli.alert.zelli.newRepInteraction.aAide1", this.aAide ? 1 : 0));
        paramMap.put("aAide2", JcmsUtil.glp(recipient.getLanguage(),
                "jcmsplugin.zelli.alert.zelli.newRepInteraction.aAide2", this.aAide ? 1 : 0));

        paramMap.put("date", dateFormat(questionZelli.getCdate()));

        if (HTML_MARKUP.equals(markup)) {
            paramMap.put("linkQuest", "<a href=\"" + Channel.getChannel().getUrl()
                    + "plugins/ZelliPlugin/jsp/questions.jsp\">Tableau de bord des questions</a>");
        } else {
            paramMap.put("linkQuest", "Répondre à sa question : " + Channel.getChannel().getUrl()
                    + "plugins/ZelliPlugin/jsp/questions.jsp");
        }
    }
}
