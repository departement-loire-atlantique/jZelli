package fr.digiwin.module.zelli.alertbuilder;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.jalios.jcms.Member;
import com.jalios.jcms.alert.Alert.Level;
import com.jalios.jcms.alert.AlertBuilder;

import generated.QuestionZelli;

public abstract class AAlertQuestionZelli extends AlertBuilder {
    
    private static final Logger LOGGER = Logger.getLogger(AAlertQuestionZelli.class);

    public AAlertQuestionZelli(Level lvl, String domain, String alertName, QuestionZelli questZelli, Member author) {
        super(lvl, domain, alertName, questZelli, author);
    }
    
    public static String dateFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dateStr = "-1";
        try {
            dateStr = sdf.format(date);
        } catch (Exception e) {
            LOGGER.error("parse date", e);
        }
        return dateStr;
    }

}
