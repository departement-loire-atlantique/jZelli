package fr.digiwin.module.zelli.utils;

import org.apache.log4j.Logger;

import com.jalios.jcms.comparator.BasicComparator;
import com.jalios.util.Util;

import generated.QuestionZelli;

public class QuestionZelliQuestionComparator extends BasicComparator<QuestionZelli>{
  
  private static final Logger LOGGER = Logger.getLogger(QuestionZelliQuestionComparator.class);
  
  @Override
  public int compare(QuestionZelli p1, QuestionZelli p2) {
    // object nullity check
    if (p1 == null) {
      return (p2 == null) ? 0 : -1;
    }
    if (p2 == null) {
     return 1;
    }

    // Retrieve QuestionZelli question
    String t1 = p1.getQuestion(language);
    String t2 = p2.getQuestion(language);

    if (t1 == null) {
     return (t2 == null) ? 0 : -1;
    }
    if (t2 == null) {
      return 1;
    }

    t1 = Util.unaccentuate(t1);
    t2 = Util.unaccentuate(t2);
    LOGGER.debug(t1);
    int res = t1.compareToIgnoreCase(t2);
    LOGGER.debug("res " + res + " q1 " + t1 + " q2 " + t2);
    if (res != 0) {
      return res;
    }
    return super.compare(p1, p2);
  }
}
