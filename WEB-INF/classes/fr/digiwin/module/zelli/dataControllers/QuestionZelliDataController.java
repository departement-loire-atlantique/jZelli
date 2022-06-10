package fr.digiwin.module.zelli.dataControllers;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jalios.jcms.BasicDataController;
import com.jalios.jcms.Channel;
import com.jalios.jcms.ControllerStatus;
import com.jalios.jcms.Data;
import com.jalios.jcms.Member;
import com.jalios.jcms.plugin.PluginComponent;
import com.jalios.util.Util;

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
        ControllerStatus status = questionZelli.checkAndPerformCreate(CHANNEL.getDefaultAdmin());
        if (!status.isOK()) {
          LOGGER.info("Date of the answer isn't save to " + questionZelli.getId());
        }
      }
    }
  }
}
