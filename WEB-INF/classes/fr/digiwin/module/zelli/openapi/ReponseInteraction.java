package fr.digiwin.module.zelli.openapi;

import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.log4j.Logger;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

import com.jalios.jcms.Group;
import com.jalios.jcms.Member;
import com.jalios.jcms.alert.AlertBuilder;
import com.jalios.jcms.rest.JcmsRestResource;
import com.jalios.util.Util;

import fr.digiwin.module.zelli.alertbuilder.AlertNewQuestion;
import fr.digiwin.module.zelli.alertbuilder.AlertRepInteraction;
import generated.QuestionZelli;

public class ReponseInteraction extends JcmsRestResource {

    private static final Logger LOGGER = Logger.getLogger(ReponseInteraction.class);

    public ReponseInteraction(Context ctx, Request req, Response rep) {
        super(ctx, req, rep);
        if (Util.isEmpty(this.getLoggedMember())) {
            rep.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
            return;
        }

    }

    @Override
    public boolean allowGet() {
        // interdit la méthode HTTP GET
        return false;
    }

    @Override
    public boolean allowPost() {
        // autorise la méthode HTTP POST
        return true;
    }

    @Override
    protected void doPost(String action, Form form) {
        String questionZelliId = form.getFirstValue("id");
        String aAideStr = form.getFirstValue("aAide");

        if (Util.isEmpty(questionZelliId) || Util.isEmpty(aAideStr)) {
            this.getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return;
        }

        QuestionZelli question = (QuestionZelli) channel.getPublication(questionZelliId);
        if (Util.isEmpty(question)) {
            this.getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "bad id");
            return;
        }
        if (!getLoggedMember().equals(question.getAuthor())) {
            this.getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN);
            return;
        }
        Group gestQuestRepGrp = channel.getGroup("$jcmsplugin.zelli.groupe.gestionnaires.id");
        if (Util.notEmpty(gestQuestRepGrp)) {
            Set<Member> gestQuestRepSet = gestQuestRepGrp.getMemberSet();

            AlertBuilder alertBuilder = new AlertRepInteraction(question, Boolean.valueOf(aAideStr));
            alertBuilder.sendAlert(gestQuestRepSet);
        }

    }

}
