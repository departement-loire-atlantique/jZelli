package fr.digiwin.module.zelli.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.jalios.jcms.Channel;
import com.jalios.jcms.comparator.BasicComparator;
import com.jalios.util.Util;

import generated.QuestionZelli;

/**
 * Compare age of Author via extraData
 * @author fdebiais
 *
 */
public class QuestionZelliAgeComparator extends BasicComparator<QuestionZelli>{
  
  private static final Logger LOGGER = Logger.getLogger(QuestionZelliAgeComparator.class);
  
  @Override
  public int compare(QuestionZelli questionZelli1, QuestionZelli questionZelli2) {
    if (questionZelli1 == null) {
      return (questionZelli2 == null) ? 0 : -1;
    }
    if (questionZelli2 == null) {
      return 1;
    }
    
    // Retrieve age
    String dateNaissanceStr1 = questionZelli1.getAuthor().getExtraData("extradb.Member.jcmsplugin.zelli.datenaissance");
    String dateNaissanceStr2 = questionZelli2.getAuthor().getExtraData("extradb.Member.jcmsplugin.zelli.datenaissance");
    SimpleDateFormat sdf = new SimpleDateFormat(Channel.getChannel().getProperty("jcmsplugin.zelli.simpledateformat.datenaissance"));
    try {
      Date dateNaissance1 = Util.isEmpty(dateNaissanceStr1) ? new Date() : sdf.parse(dateNaissanceStr1);
      Date dateNaissance2 = Util.isEmpty(dateNaissanceStr2) ? new Date() : sdf.parse(dateNaissanceStr2);
      int i = super.compareDate(dateNaissance1, dateNaissance2);
      if (i != 0) {
        return i;
      }
    } catch (Exception e) {
      LOGGER.error("ZelliUtils QuestionZelliAgeComparator exception : " + e.getMessage());
    }
   
    return super.compare(questionZelli1, questionZelli2);
  }
}
