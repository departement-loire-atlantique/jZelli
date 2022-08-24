package fr.digiwin.module.zelli.openapi;

import java.util.Random;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Variant;

import com.jalios.jcms.ControllerStatus;
import com.jalios.jcms.JcmsUtil;
import com.jalios.jcms.Member;
import com.jalios.jcms.mail.MailMessage;
import com.jalios.jcms.rest.JcmsRestResource;
import com.jalios.util.Util;

public class ResetPasswordApi extends JcmsRestResource {

    private static final Logger LOGGER = Logger.getLogger(ResetPasswordApi.class);

    protected String memberLogin;

    public ResetPasswordApi(Context ctxt, Request request, Response response) {
        super(ctxt, request, response);

        setXmlUTF8Encoding();
        this.getVariants().add(new Variant(MediaType.APPLICATION_JSON));

        memberLogin = (String) request.getAttributes().get("memberLogin");

        if (Util.isEmpty(memberLogin)) {
            response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Unknown param");
            return;
        }
    }

    @Override
    protected String getJSONRepresentation() {

        Member mbr = channel.getMemberFromLogin(this.memberLogin);
        if (Util.isEmpty(mbr)) {
            return "";
        }

        LOGGER.info("Demande de réinitialisation du mot de passe pour : " + mbr.getFullName());

        // new mdp
        String newMdp = "";
        Random random = new Random();
        int Low = 10;
        int High = 100;
        int Result = random.nextInt(High - Low) + Low;
        newMdp = Util.generatePassword(random.nextInt(10 - 7) + 7);
        newMdp += Util.generatePassword(1).toUpperCase();
        newMdp += Integer.toString(Result);

        Member updateMbr = (Member) mbr.getUpdateInstance();
        updateMbr.setPassword(channel.crypt(newMdp));
        ControllerStatus status = updateMbr.checkAndPerformUpdate(channel.getDefaultAdmin());
        if (status.hasFailed()) {
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
            LOGGER.info("Impossible de sauvegarder le nouveau mot de passe pour : " + mbr.getFullName());
            return "";
        }

        // email
        if (Util.notEmpty(mbr.getEmail()) && Util.isValidEmail(mbr.getEmail())) {
            LOGGER.info("Par mail");
            String content = JcmsUtil.glp("fr", "jcmsplugin.zelli.email.pwd.reset.msg", newMdp);

            MailMessage mail = new MailMessage(JcmsUtil.glp("fr", "jcmsplugin.socle.form.contact-mail.origine"));
            // auto set from
//            mail.setFrom(emailFrom);
            mail.setTo(mbr.getEmail());
            mail.setSubject(JcmsUtil.glp("fr", "jcmsplugin.zelli.email.pwd.reset.subject"));
            mail.setContentText(content);

            try {
                mail.send();
                return "";
            } catch (MessagingException e) {
                LOGGER.error(e.getMessage(), e);
                getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
            }
        }

        // phone
        if (Util.notEmpty(mbr.getPhone())) {
            LOGGER.info("Par SMS");

            getResponse().setStatus(Status.SERVER_ERROR_NOT_IMPLEMENTED);
        }

        LOGGER.info("Imposible de réinitialisation du mot de passe pour : " + mbr.getFullName());

        return "";
    }

}
