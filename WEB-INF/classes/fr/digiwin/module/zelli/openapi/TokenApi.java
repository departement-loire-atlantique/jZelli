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

import com.jalios.jcms.Channel;
import com.jalios.jcms.JcmsUtil;
import com.jalios.jcms.Member;
import com.jalios.jcms.accesscontrol.AccessControlManager;
import com.jalios.jcms.rest.JcmsRestResource;
import com.jalios.util.Util;

import fr.digiwin.module.zelli.utils.ZelliUtils;

/**
 * Classe représentant les requêtes GET sur /plugins/zelli/token/create/{memberId}
 * @author lchoquet
 *
 */
public class TokenApi extends JcmsRestResource {

  protected String regionId;
  
  private static final Logger LOGGER = Logger.getLogger(TokenApi.class);

  public TokenApi(Context ctxt, Request request, Response response) {
    super(ctxt, request, response);
    // vérifier si l'utilisateur connecté peut générer le token
    if (Util.isEmpty(getLoggedMember())
        || !JcmsUtil.isSameId(getLoggedMember(), Channel.getChannel().getMemberFromLogin((String) getRequest().getAttributes().get("memberLogin"), true))) {
      LOGGER.debug("TokenApi - Unauthorized request - wrong credentials or not allowed.");
      response.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    }
    
    // encodage utf8
    setXmlUTF8Encoding();
    // indiquer que la réponse est en JSON
    Variant jsonVariant = new Variant();
    jsonVariant.setMediaType(MediaType.APPLICATION_JSON);
    getVariants().add(jsonVariant);
  }

  @Override
  protected String getJSONRepresentation() {
    
    // On veut créer un token pour un membre et renvoyer le token généré
    // membre à qui sera associé la clé
    
    Member member = Channel.getChannel().getMemberFromLogin((String) getRequest().getAttributes().get("memberLogin"), true);
    LOGGER.debug("TokenApi - CREATE TOKEN FOR MEMBER " + (String) getRequest().getAttributes().get("memberLogin") + " -> START");
    JSONObject jsonResponse = new JSONObject();
    try {
      jsonResponse.put("token", "");
      if (Util.isEmpty(member)) {
        // pas de membre trouvé
        jsonResponse.put("error", "Le membre indiqué n'existe pas.");
        LOGGER.debug("TokenApi - Member ID does not exist. Aborting.");
        return jsonResponse.toString();
      }
      
      if (member.getLogin().equals("API")) {
        // membre API ne doit pas changer de token via rest
        jsonResponse.put("error", "Le token ne peut pas être créé pour ce membre.");
        LOGGER.debug("TokenApi - Auth key could not be generated for this member.");
        return jsonResponse.toString();
      }
      
      String authKey = "";
      
      LOGGER.debug("TokenApi - Generating auth key...");
      
      // Création du token
      authKey = ZelliUtils.getAuthKey(member);
      
      // récupérer le token s'il existe
      if (Util.notEmpty(authKey)) {
        LOGGER.debug("TokenApi - Auth key successfully generated.");
        jsonResponse.put("token", authKey);
        return jsonResponse.toString();
      }
      
      // il n'y a pas de token
      LOGGER.debug("TokenApi - Auth key could not be generated.");
      jsonResponse.put("error", "Le token n'a pas pu être créé.");
      
    } catch (JSONException e) {
      LOGGER.error("TokenApi error 500 -> " + e.getMessage());
      getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
      return null;
    }
    return jsonResponse.toString();
  }
  
}