package fr.digiwin.module.zelli.utils;

import org.apache.log4j.Logger;

import com.jalios.jcms.comparator.BasicComparator;

import generated.QuestionZelli;

/**
 * Compare question
 * @author fdebiais
 *
 */
public class QuestionZelliQuestionComparator extends BasicComparator<QuestionZelli>{
  
  private static final Logger LOGGER = Logger.getLogger(QuestionZelliQuestionComparator.class);
  
  @Override
  public int compare(QuestionZelli questionZelli1, QuestionZelli questionZelli2) {
    
    if (questionZelli1 == null) {
      return (questionZelli2 == null) ? 0 : -1;
    }
    if (questionZelli2 == null) {
      return 1;
    }
    int i = super.compareString(questionZelli1.getQuestion(language), questionZelli2.getQuestion(language));
    if (i != 0) {
      return i;
    }
    return super.compare(questionZelli1, questionZelli2);
  }
}
