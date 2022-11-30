package fr.digiwin.module.zelli.alertbuilder;

import java.util.Map;

import com.jalios.jcms.Member;
import com.jalios.jcms.alert.Alert;

import generated.QuestionZelli;

/**
 * réprésente une alert de reponse
 * @author Digiwin
 */
public class AlertReponse extends AAlertQuestionZelli {

    public AlertReponse(QuestionZelli questZelli, Member author) {
        super(Alert.Level.INFO, "zelli", "newReponse", questZelli, author);
    }

    @Override
    protected void addParams(Member recipient, Map<String, String> paramMap, String markup) {
        QuestionZelli questionZelli = (QuestionZelli)this.data;
        paramMap.put("question", questionZelli.getQuestion());
        paramMap.put("reponse", questionZelli.getReponse());
        
        paramMap.put("date", dateFormat(questionZelli.getCdate()));
        paramMap.put("authorRep", this.author.getFullName());
        

        if (HTML_MARKUP.equals(markup)) {
//            paramMap.put("linkQuest", "<a href=\"" + channel.getUrl()
//                    + "plugins/ZelliPlugin/jsp/questions.jsp\">Répondre à sa question</a>");
        } else {
//            paramMap.put("linkQuest", "Répondre à sa question : " + channel.getUrl()
//                    + "plugins/ZelliPlugin/jsp/questions.jsp");
        }
    }

}
