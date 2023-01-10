package fr.digiwin.module.zelli.alertbuilder;

import java.util.Map;

import com.jalios.jcms.Channel;
import com.jalios.jcms.Member;
import com.jalios.jcms.alert.Alert;

import fr.digiwin.module.zelli.utils.ZelliUtils;
import generated.QuestionZelli;

/**
 * représente une alert de nouvelle question
 * 
 * @author Digiwin
 */
public class AlertNewQuestion extends AAlertQuestionZelli {

    public AlertNewQuestion(QuestionZelli questZelli, Member author) {
        super(Alert.Level.ACTION, "zelli", "newQuestion", questZelli, questZelli.getAuthor());
    }

    @Override
    protected void addParams(Member recipient, Map<String, String> paramMap, String markup) {
        QuestionZelli questionZelli = (QuestionZelli) this.data;

        paramMap.put("age", ZelliUtils.getAgeStrFromDateNaissance(
                questionZelli.getAuthor().getExtraData("extra.Member.jcmsplugin.zelli.datenaissance")));
        paramMap.put("demRef", questionZelli.getReferent() ? "oui" : "non");
        paramMap.put("question", questionZelli.getQuestion());

        paramMap.put("date", dateFormat(questionZelli.getCdate()));

        if (HTML_MARKUP.equals(markup)) {
            paramMap.put("linkQuest", "<a href=\"" + Channel.getChannel().getUrl()
                    + "plugins/ZelliPlugin/jsp/questions.jsp\">Répondre à sa question</a>");
        } else {
            paramMap.put("linkQuest", "Répondre à sa question : " + Channel.getChannel().getUrl()
                    + "plugins/ZelliPlugin/jsp/questions.jsp");
        }
    }
}
