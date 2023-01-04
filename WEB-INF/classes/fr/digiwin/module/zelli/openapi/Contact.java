package fr.digiwin.module.zelli.openapi;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

import com.jalios.jcms.Data;
import com.jalios.jcms.rest.JcmsRestResource;
import com.jalios.util.Util;

import generated.FicheLieu;

public class Contact extends JcmsRestResource {

    private static final Logger LOGGER = Logger.getLogger(Contact.class);
    private static final String PREF_KEY = "jcmsplugin.zelli.contact";

    protected Data data;

    public Contact(Context ctx, Request req, Response rep) {
        super(ctx, req, rep);
        // indique que l'encodage du corps de la réponse est en UTF-8
        setXmlUTF8Encoding();
        // indique que l'on supporte la représentation text/xml de la réponse
        getVariants().add(new Variant(MediaType.TEXT_XML));
        getVariants().add(new Variant(MediaType.APPLICATION_JSON));

        if (Util.isEmpty(this.getLoggedMember())) {
            rep.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
        }

        // get data
        String id = (String) req.getAttributes().get("id");
        this.data = channel.getData(id);
        if (Util.isEmpty(this.data)) {
            rep.setStatus(Status.CLIENT_ERROR_NOT_FOUND, "Unknown param");
            return;
        }
        if (!this.data.canBeReadBy(this.getLoggedMember())) {
            rep.setStatus(Status.CLIENT_ERROR_FORBIDDEN);
            return;
        }
        if (!(this.data instanceof generated.Contact || this.data instanceof FicheLieu)) {
            rep.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
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
    public boolean allowDelete() {
        // autorise la méthode HTTP DELETE
        return true;
    }

    @Override
    protected void doPost(Representation rep) {
        super.doPost(rep);
        
        Set<String> contacts = new HashSet<>();
        
        String contactsString = this.getLoggedMember().getPreference(PREF_KEY);
        if (Util.notEmpty(contactsString)) {
            contacts.addAll(Arrays.asList(contactsString.split(",")));
        }
        
        contacts.add(data.getId());

        this.getLoggedMember().savePreference(PREF_KEY, String.join(",", contacts));
    }

    @Override
    protected void doDelete() {
        super.doDelete();
        
        Set<String> contacts = new HashSet<>();
        
        String contactsString = this.getLoggedMember().getPreference(PREF_KEY);
        if (Util.notEmpty(contactsString)) {
            contacts.addAll(Arrays.asList(contactsString.split(",")));
        }

        contacts.remove(data.getId());
        
        this.getLoggedMember().savePreference(PREF_KEY, String.join(",", contacts));
    }

}
