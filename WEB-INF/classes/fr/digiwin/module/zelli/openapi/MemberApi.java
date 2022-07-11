package fr.digiwin.module.zelli.openapi;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
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
import com.jalios.jcms.rest.JcmsRestResource;
import com.jalios.util.Util;

import fr.digiwin.module.zelli.utils.ZelliUtils;

/**
 * Classe représentant les requêtes POST sur /plugins/zelli/member/create pour créer un membre
 * @author lchoquet
 *
 */
public class MemberApi extends JcmsRestResource {

  private static final Logger LOGGER = Logger.getLogger(MemberApi.class);
  private static final Channel CHANNEL = Channel.getChannel();

  public MemberApi(Context ctxt, Request request, Response response) {
    super(ctxt, request, response);
    
    // vérifier l'accès à l'édition / création de membres
    if (Util.isEmpty(getLoggedMember())) {
      response.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
      return;
    }
    
    // encodage utf8
    setXmlUTF8Encoding();
    // indiquer que la réponse est en JSON
    Variant jsonVariant = new Variant();
    jsonVariant.setMediaType(MediaType.APPLICATION_JSON);
    getVariants().add(jsonVariant);
  }
  
  public boolean allowGet() {
    //  interdit la méthode HTTP GET
    return false;
  }
  
  public boolean allowPost() {
    //  autorise la méthode HTTP POST
    return true;
  }
  
  @Override
  protected void doPost(Representation entity) {
    JSONObject jsonResponse = new JSONObject();
    try {
      // récupérer la réponse JSON après exécution
      jsonResponse = new JSONObject(createMemberAndGetToken(entity));
      // mettre la réponse JSON dans la réponse
      getResponse().setEntity(new StringRepresentation(jsonResponse.toString(), MediaType.APPLICATION_JSON));
      getResponse().setStatus(Status.SUCCESS_CREATED);
    } catch (JSONException e) {
      LOGGER.error("Erreur doPost MemberAPI : " + e.getMessage());
      getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
    }
  }

  private String createMemberAndGetToken(Representation entity) {
    
    JSONObject jsonResponse = new JSONObject();

    // On veut créer un membre, puis retourner le token associé
    // récupération des paramètres envoyés
    try {
      Map<String, String> params = ZelliUtils.getUriParams(entity.getText());
      
      // On s'attend à recevoir "login", "pwd" et "dateNaissance"
      if (Util.isEmpty(params.get("login")) || Util.isEmpty(params.get("pwd")) || Util.isEmpty(params.get("dateNaissance"))) {
        // Bad request
        getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        return null;
      }
      
      // Décoder le login et le pwd depuis une base64
      byte[] decodedLoginBytes = Base64.getDecoder().decode(params.get("login"));
      byte[] decodedPwdBytes = Base64.getDecoder().decode(params.get("pwd"));
      String decodedLogin = new String(decodedLoginBytes);
      String decodedPwd = new String(decodedPwdBytes);

      jsonResponse.put("token", "");
      
      // Si le membre existe déjà, on doit l'indiquer et ne pas créer le compte
      if (Util.notEmpty(CHANNEL.getMemberFromLogin(decodedLogin))) {
        jsonResponse.put("error", "L'username existe déjà.");
        return jsonResponse.toString();
      }
      
      // créer le membre
      Member newMbr = new Member();
      newMbr.setLogin(decodedLogin);
      newMbr.setPassword(CHANNEL.crypt(decodedPwd));
      newMbr.setUsage(0); // 0 = utilisateur, 1 = contact
      newMbr.setName(decodedLogin);
      // date de naissance. On reçoit un timestamp en millisecondes
      Date dateNaissance = new Date(Long.parseLong(params.get("dateNaissance")));
      SimpleDateFormat sdf = new SimpleDateFormat(CHANNEL.getProperty("jcmsplugin.zelli.simpledateformat.datenaissance"));
      newMbr.setExtraData("extra.Member.jcmsplugin.zelli.datenaissance", sdf.format(dateNaissance));
      if (Util.notEmpty(CHANNEL.getGroup( "$jcms.zelli.groupe.utilisateurs.id"))) {
        newMbr.addGroup(CHANNEL.getGroup("$jcms.zelli.groupe.utilisateurs.id"));
      }
      
      // TODO => les groupes et autorisations par défaut
      
      ControllerStatus status = newMbr.checkAndPerformCreate(CHANNEL.getDefaultAdmin());
      
      if (!status.isOK()) {
        LOGGER.debug("MemberApi - Member could not be created.");
        jsonResponse.put("error", "Le membre n'a pas pu être créé.");
        return jsonResponse.toString();
      }
      
      LOGGER.debug("MemberApi - Member " + decodedLogin + " successfully created.");
      
      // le membre a été créé, on génère le token
      String authKey = ZelliUtils.getAuthKey(newMbr);
      
      // récupérer le token s'il existe
      if (Util.notEmpty(authKey)) {
        LOGGER.debug("MemberApi - Auth key successfully generated.");
        jsonResponse.put("token", authKey);
        return jsonResponse.toString();
      }
      
      // il n'y a pas de token
      LOGGER.debug("MemberApi - Auth key could not be generated.");
      jsonResponse.put("error", "Le token n'a pas pu être créé.");

    } catch (JSONException | IOException e) {
      LOGGER.error("Erreur createMemberAndGetToken MemberAPI : " + e.getMessage());
      getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
      return null;
    }
    
    return jsonResponse.toString();
  }
  
}