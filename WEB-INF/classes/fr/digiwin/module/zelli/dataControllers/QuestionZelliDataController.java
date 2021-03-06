package fr.digiwin.module.zelli.dataControllers;

import java.util.Date;
import java.util.Map;

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
import com.jalios.jcms.Member;
import com.jalios.jcms.alert.Alert;
import com.jalios.jcms.alert.Alert.Level;
import com.jalios.jcms.alert.AlertBuilder;
import com.jalios.jcms.alert.AlertQueryBuilder;
import com.jalios.jcms.plugin.PluginComponent;
import com.jalios.util.Util;

import fr.digiwin.module.zelli.firebase.FirebaseMng;
import generated.QuestionZelli;

public class QuestionZelliDataController extends BasicDataController implements PluginComponent {

    private static final Logger LOGGER = Logger.getLogger(QuestionZelliDataController.class);
    private static final Channel CHANNEL = Channel.getChannel();

  /**
   * Add date to DateDeLaReponse when answer is modified to QuestionZelli
   * @author fdebiais
   */
  public void afterWrite(Data data, int op, Member mbr, Map context) {

    if (op != OP_UPDATE) {
      return;
    }

    if (!(data instanceof QuestionZelli)) {
      return;
    }
    
    QuestionZelli questionZelli = (QuestionZelli)data;
    
    if (questionZelli.getReponse().length() > 0) {
      Object dataPrevious = context.get(CTXT_PREVIOUS_DATA);
      
      if (Util.isEmpty(dataPrevious)) {
        return;
      }
      
      QuestionZelli previousquestionZelli = (QuestionZelli) dataPrevious;
      
      if (!previousquestionZelli.getReponse().equals(questionZelli.getReponse())) {
        questionZelli.setDateDeLaReponse(new Date());
        questionZelli.setGestionnaire(CHANNEL.getCurrentLoggedMember());
        ControllerStatus status = questionZelli.checkAndPerformCreate(CHANNEL.getDefaultAdmin());
        if (!status.isOK()) {
          LOGGER.info("Date and gestionnaire of the answer isn't save for " + questionZelli.getId());
        } else {
//          AlertBuilder alertBuilder = new AlertBuilder(Level.INFO, "questionZelli", "reponse", questionZelli);
//          alertBuilder.sendAlert(questionZelli.getAuthor());
            String token = FirebaseMng.getInstance().getToken(questionZelli.getAuthor());
            if (Util.notEmpty(token)) {

                Notification notif = Notification.builder()
                        .setTitle("R??ponse ?? ta question")
                        .setBody(questionZelli.getReponse() + "\nEst-ce que cette r??ponse t'a aid?? ?")
                        .build();

                WebpushNotification webNotif = WebpushNotification.builder()
                        .addAction(new Action("question-oui", "Oui"))
                        .addAction(new Action("question-non", "Non"))
                        .build();
                WebpushConfig webConf = WebpushConfig.builder()
                        .setNotification(webNotif)
                        .build();

                Message message = Message.builder()
                        .setNotification(notif)
                        .setWebpushConfig(webConf)
                        .setToken(token)
                        .build();

                FirebaseMng.getInstance().sendMessage(message);
            }
        }
      }
    }
  }
}
