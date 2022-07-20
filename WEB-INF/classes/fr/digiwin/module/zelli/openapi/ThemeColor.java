package fr.digiwin.module.zelli.openapi;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Variant;

import com.jalios.jcms.Category;
import com.jalios.jcms.Data;
import com.jalios.jcms.Publication;
import com.jalios.jcms.rest.JcmsRestResource;
import com.jalios.util.Util;

public class ThemeColor extends JcmsRestResource {

    private static final Logger LOGGER = Logger.getLogger(ThemeColor.class);
    protected static final String ID_CAT_THEME = "$jcmsplugin.zelli.category.navigationParThemes.root";

    protected Data data;
    protected Category catTheme;

    public ThemeColor(Context ctx, Request req, Response rep) {
        super(ctx, req, rep);
        setXmlUTF8Encoding();
        this.getVariants().add(new Variant(MediaType.TEXT_XML));
        this.getVariants().add(new Variant(MediaType.APPLICATION_JSON));

        // from DataRestResource
        boolean requireLogged = this.channel.getBooleanProperty("rest.data.require-logged-member", true);
        if (requireLogged && Util.isEmpty(this.getLoggedMember())) {
            rep.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
            return;
        }

        // get data
        String id = (String) req.getAttributes().get("id");
        this.data = channel.getData(id);
        if (Util.isEmpty(this.data) || !(this.data instanceof Category || this.data instanceof Publication)) {
            rep.setStatus(Status.CLIENT_ERROR_NOT_FOUND, "Unknown param");
            return;
        }
        if (!data.canBeReadBy(getLoggedMember())) {
            rep.setStatus(Status.CLIENT_ERROR_FORBIDDEN);
            return;
        }

        this.catTheme = channel.getCategory(ID_CAT_THEME);
    }

    @Override
    protected String getItemXmlRepresentation(Object obj) {
        //TODO XML rep
        return this.getColor();
    }

    @Override
    protected String getJSONRepresentation() {
        JSONObject json = new JSONObject();
        try {
            json.put("color", this.getColor());
        } catch (JSONException e) {
            this.getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
            LOGGER.error(e);
        }
        return json.toString();
    }

    protected String getColor() {
        String color = null;

        if (this.data instanceof Category) {
            return this.getColor((Category) this.data);
        }

        if (this.data instanceof Publication) {
            Publication pub = (Publication) data;
            for (Category itCat : pub.getCategories(getLoggedMember())) {
                color = this.getColor(itCat);
                if (Util.notEmpty(color)) {
                    break;
                }
            }
        }

        return color;
    }

    protected String getColor(Category cat) {
        if (cat.getParent().equals(this.catTheme)) {
            return cat.getColor();
        }

        return Util.isEmpty(cat.getParent()) ? "" : this.getColor(cat.getParent());
    }
}
