package fr.digiwin.module.zelli.firebase;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.jalios.jcms.Member;
import com.jalios.util.Util;

public class FirebaseMng {

    private static final Logger LOGGER = Logger.getLogger(FirebaseMng.class);

    private static FirebaseMng INSTANCE;

    private FirebaseMng() {
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.getApplicationDefault())
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            LOGGER.fatal(e.getStackTrace());
        }
    }

    public static FirebaseMng getInstance() {
        if (Util.isEmpty(INSTANCE)) {
            INSTANCE = new FirebaseMng();
        }
        return INSTANCE;
    }

    /**
     * 
     * @param userToken Le token a qui envoyer le message
     */
    public void sendMessage(String userToken, String title, String body) {
        Notification notif = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setNotification(notif)
                .setToken(userToken)
                .build();
        try {
            String response = FirebaseMessaging.getInstance().send(message);
            // Response is a message ID string.
            System.out.println("Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) {
            LOGGER.error(e.getStackTrace());
        }
    }

    public void sendMessage(Member mbr, String title, String body) {
        String token = mbr.getExtraData("extradb.Member.jcmsplugin.zelli.firebase.token");
        if (Util.notEmpty(token)) {
            this.sendMessage(token, title, body);
        } else {
            LOGGER.debug("No token set for member : " + mbr);
        }
    }
}
