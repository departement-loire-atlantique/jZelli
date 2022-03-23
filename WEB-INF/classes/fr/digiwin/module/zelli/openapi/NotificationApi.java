package fr.digiwin.module.zelli.openapi;

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

import com.jalios.jcms.rest.JcmsRestResource;
import com.jalios.util.Util;

import fr.digiwin.module.zelli.utils.ZelliUtils;

/**
 * Classe représentant les requêtes sur /plugins/zelli/notify/questionAlert/{questionId}
 * @author lchoquet
 *
 */
public class NotificationApi extends JcmsRestResource {
  
  private static final Logger LOGGER = Logger.getLogger(NotificationApi.class);

  protected String questionId;

  public NotificationApi(Context ctxt, Request request, Response response) {
    super(ctxt, request, response);
    
    if (Util.isEmpty(getLoggedMember())) {
      response.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
      return;
    } 
    
    questionId = (String) request.getAttributes().get("questionId");
    
    // encodage utf8
    setXmlUTF8Encoding();
    // indiquer que la réponse est en JSON
    Variant jsonVariant = new Variant();
    jsonVariant.setMediaType(MediaType.APPLICATION_JSON);
    getVariants().add(jsonVariant);
  }
  
  public boolean doGet() {
    return false;
  }
  
  public boolean doPost() {
    return true;
  }
  
  @Override
  protected void doPost(Representation entity) {
    // Générer une notif pour les gestionnaires
    ZelliUtils.sendQuestionNotification(entity, questionId);
    getResponse().setStatus(Status.SUCCESS_OK);
  }
  
}