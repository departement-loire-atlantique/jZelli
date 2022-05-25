package fr.digiwin.module.zelli.openapi;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import com.jalios.jcms.Channel;
import com.jalios.jcms.ControllerStatus;
import com.jalios.jcms.JcmsUtil;
import com.jalios.jcms.Member;
import com.jalios.jcms.accesscontrol.AccessControlManager;
import com.jalios.jcms.rest.JcmsRestResource;
import com.jalios.util.Util;

import fr.digiwin.module.zelli.utils.ZelliUtils;

/**
 * Classe représentant les requêtes GET et PUT sur /plugins/zelli/member/pwd/{memberLogin}
 * @author lchoquet
 *
 */
public class PasswordApi extends JcmsRestResource {

  private static final Logger LOGGER = Logger.getLogger(PasswordApi.class);
  
  protected String memberLogin;

  public PasswordApi(Context ctxt, Request request, Response response) {
    super(ctxt, request, response);

    // vérifier l'accès à l'édition / création de membres
    if (Util.isEmpty(getLoggedMember())
        || (!JcmsUtil.isSameId(getLoggedMember(), Channel.getChannel().getMemberFromLogin((String) getRequest().getAttributes().get("memberLogin"), true)))
            && !JcmsUtil.isSameId(getLoggedMember(), Channel.getChannel().getMemberFromLogin("API"))) {
      response.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
      return;
    }
    
    memberLogin = (String) request.getAttributes().get("memberLogin");
    
    // encodage utf8
    setXmlUTF8Encoding();
    // indiquer que la réponse est en JSON
    Variant jsonVariant = new Variant();
    jsonVariant.setMediaType(MediaType.APPLICATION_JSON);
    getVariants().add(jsonVariant);
  }
  
  // autoriser le PUT pour l'édition de MDP
  public boolean allowPut() {
    return true;
  }

  @Override
  protected String getJSONRepresentation() {
    // On veut vérifier si le membre existe. Si le membre existe, on renvoie un 200 sans erreur, sinon 200 avec erreur en JSON
    
    LOGGER.debug("PasswordAPI : checking if member " + memberLogin + " exists.");
    
    // membre à qui sera associé la clé
    Member member = Channel.getChannel().getMemberFromLogin(memberLogin);
    JSONObject jsonResponse = new JSONObject();
    try {
      if (Util.isEmpty(member)) {
        // pas de membre trouvé
        LOGGER.debug("PasswordAPI : Member " + memberLogin + " does not exist.");
        jsonResponse.put("error", "Le membre indiqué n'existe pas.");
        return jsonResponse.toString();
      } else {
        // membre trouvé
        LOGGER.debug("PasswordAPI : Member " + memberLogin + " exists.");
        jsonResponse.put("success", "OK");
        return jsonResponse.toString();
      }
      
    } catch (JSONException e) {
      getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
      return null;
    }
  }
  
  @Override
  protected void doPut(Representation entity) {
    JSONObject jsonResponse = new JSONObject();
    try {
      LOGGER.debug("PasswordAPI : Updating member " + memberLogin + " password...");
      // récupérer la réponse JSON après exécution
      jsonResponse = new JSONObject(updateMemberPassword(entity));
      // mettre la réponse JSON dans la réponse
      getResponse().setEntity(new StringRepresentation(jsonResponse.toString(), MediaType.APPLICATION_JSON));
      getResponse().setStatus(Status.SUCCESS_OK);
    } catch (JSONException e) {
      LOGGER.error("Erreur doPut PasswordApi : " + e.getMessage());
      getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
    }
  }

  /**
   * Mets à jour le MDP d'un membre
   * @param entity
   * @return
   */
  private String updateMemberPassword(Representation entity) {
    
    JSONObject jsonResponse = new JSONObject();
    
    // On veut créer un membre, puis retourner le token associé
    // récupération des paramètres envoyés
    try {
      Map<String, String> params = ZelliUtils.getUriParams(entity.getText());
      
      // On s'attend à recevoir "pwd"
      if (Util.isEmpty(params.get("pwd"))) {
        // Bad request
        getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        return null;
      }
      
      // Décoder le login et le pwd depuis une base64
      byte[] decodedPwdBytes = Base64.getDecoder().decode(params.get("pwd"));
      String decodedPwd = new String(decodedPwdBytes);
      
      // on récupère le membre et on met à jour son mdp
      Member itMember = Channel.getChannel().getMemberFromLogin(memberLogin);
      
      if ( !JcmsUtil.isSameId(itMember, getLoggedMember()) ) {
        LOGGER.debug("PasswordAPI : Member " + memberLogin + " can't modify password form others.");
        jsonResponse.put("error", "Le membre ne peut modifier le mot de passe des autres membres.");
        return jsonResponse.toString();
      }
      
      Member itMemberClone = (Member) itMember.getUpdateInstance();
      itMemberClone.setPassword(Channel.getChannel().crypt(decodedPwd));
      
      ControllerStatus status = itMemberClone.checkAndPerformUpdate(Channel.getChannel().getDefaultAdmin());
      
      if (!status.isOK()) {
        LOGGER.debug("Password - Member could not be updated.");
        jsonResponse.put("error", "Le mot de passe n'a pas pu être mis à jour.");
        return jsonResponse.toString();
      }
      
      LOGGER.debug("PasswordAPI : Member " + memberLogin + "'s password was updated.");

    } catch (JSONException | IOException e) {
      LOGGER.error("Erreur updateMemberPassword PasswordApi : " + e.getMessage());
      getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
      return null;
    }
    
    return jsonResponse.toString();
  }
  
}