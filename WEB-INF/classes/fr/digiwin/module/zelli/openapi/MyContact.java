package fr.digiwin.module.zelli.openapi;

import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

import com.jalios.jcms.Publication;
import com.jalios.jcms.QueryResultSet;
import com.jalios.jcms.handler.QueryHandler;
import com.jalios.jcms.rest.DataCollectionRestResource;
import com.jalios.util.Util;

import generated.Contact;
import generated.FicheLieu;

public class MyContact extends DataCollectionRestResource  {
    
    private static final Logger LOGGER = Logger.getLogger(MyContact.class);

    public MyContact(Context ctx, Request req, Response rep) {
        super(ctx, req, rep);
        if (Util.isEmpty(this.getLoggedMember())) {
            rep.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
            return;
        }
        
        // list Contact et FicheLieu
        TreeSet<Publication> contactAndLieuList = new TreeSet<Publication>(Publication.getTitleComparator());
        
        QueryHandler qh = new QueryHandler();
        qh.setLoggedMember(this.getLoggedMember());
        qh.setMids(this.getLoggedMember().getId());
        qh.setTypes(Contact.class.getName(), FicheLieu.class.getName());
        qh.setExactType(true);
        qh.setPstatus("0", "2");
        
        QueryResultSet result = qh.getResultSet();
        
        if(!result.isEmpty()) {
            contactAndLieuList.addAll(result);
        }
        
        pagerData.setCollection(contactAndLieuList);
        pagerData.setItemTagName("");
    }
    
}