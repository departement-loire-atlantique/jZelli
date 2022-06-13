package fr.digiwin.module.zelli.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.jalios.jcms.Channel;
import com.jalios.jcms.comparator.BasicComparator;
import com.jalios.util.Util;

import generated.QuestionZelli;

public class QuestionZelliAgeComparator extends BasicComparator<QuestionZelli>{
  
  private static final Logger LOGGER = Logger.getLogger(QuestionZelliAgeComparator.class);
  
  @Override
  public int compare(QuestionZelli p1, QuestionZelli p2) {
    // object nullity check
    if (p1 == null) {
      return (p2 == null) ? 0 : -1;
    }
    if (p2 == null) {
     return 1;
    }

    // Retrieve age
    String dateNaissanceStr1 = Util.getString(p1.getAuthor().getExtraData("extra.Member.jcmsplugin.zelli.datenaissance"), "01/01/1980");
    String dateNaissanceStr2 = Util.getString(p2.getAuthor().getExtraData("extra.Member.jcmsplugin.zelli.datenaissance"), "01/01/1980");
    SimpleDateFormat sdf = new SimpleDateFormat(Channel.getChannel().getProperty("jcmsplugin.zelli.simpledateformat.datenaissance"));
    try {
      Date dateNaissance1 = sdf.parse(dateNaissanceStr1);
      Date dateNaissance2 = sdf.parse(dateNaissanceStr2);
      LOGGER.debug("p1: " + p1 + " : "+ dateNaissanceStr1 + " - p2: " + p2 + " : " + dateNaissanceStr2 + " /// " +  super.compareDate(dateNaissance1, dateNaissance2));
      return super.compareDate(dateNaissance1, dateNaissance2);
    } catch (Exception e) {
      LOGGER.error("ZelliUtils QuestionZelliAgeComparator exception : " + e.getMessage());
    }
    return 0;
  }
}
