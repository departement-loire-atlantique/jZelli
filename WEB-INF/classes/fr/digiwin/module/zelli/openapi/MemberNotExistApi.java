package fr.digiwin.module.zelli.openapi;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Variant;

import com.jalios.jcms.Channel;
import com.jalios.jcms.JcmsUtil;
import com.jalios.jcms.Member;
import com.jalios.jcms.rest.JcmsRestResource;
import com.jalios.util.Util;

/**
 * Classe représentant les requêtes GET et /plugins/zelli/member/notexist/{memberLogin}
 * @author fdebiais
 *
 */
public class MemberNotExistApi extends JcmsRestResource {

  private static final Logger LOGGER = Logger.getLogger(MemberNotExistApi.class);
  
  protected String memberLogin;
  protected String memberEmail;

  public MemberNotExistApi(Context ctxt, Request request, Response response) {
    super(ctxt, request, response);

    // vérifier l'accès à l'édition / création de membres
    // L'API doit posséder au moins une ACL de création de membres 
    if (Util.isEmpty(getLoggedMember())) {
      response.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
      return;
    }
    
    this.memberLogin = (String) request.getAttributes().get("memberLogin");
    this.memberEmail = this.formQueryString.getValues("email");
    
    // encodage utf8
    setXmlUTF8Encoding();
    // indiquer que la réponse est en JSON
    Variant jsonVariant = new Variant();
    jsonVariant.setMediaType(MediaType.APPLICATION_JSON);
    getVariants().add(jsonVariant);
  }

  @Override
  protected String getJSONRepresentation() {
    // On veut vérifier si le membre n'existe pas. Si le membre n'existe pas, on renvoie un 200 sans erreur, sinon 200 avec erreur en JSON
    
    LOGGER.debug("PasswordAPI : checking if member " + memberLogin + " exists.");
    
    // membre à qui sera associé la clé
    Member member = Channel.getChannel().getMemberFromLogin(memberLogin);
    JSONObject jsonResponse = new JSONObject();
    try {
      // pseudo
      JSONObject pseudo = new JSONObject();
      if (Util.isEmpty(member)) {
        // pas de membre trouvé
        LOGGER.debug("MemberNotExistApi : Member " + memberLogin + " does not exist.");
        pseudo.put("status", "ok");
        pseudo.put("msg", "Le pseudo indiqué n'existe pas.");
      } else {
        // membre trouvé
        LOGGER.debug("MemberNotExistApi : Member " + memberLogin + " exists.");
        pseudo.put("status", "ko");
        pseudo.put("msg", "Cet identifiant existe déjà dans zelli, merci d’en imaginer un autre.");
      }
      jsonResponse.put("pseudo", pseudo);
      
      // email
      if(Util.notEmpty(memberEmail)) {
          JSONObject email = new JSONObject();
          if (Util.isEmpty(channel.getMemberFromEmail(memberEmail))) {
            // pas de membre trouvé
            LOGGER.debug("MemberNotExistApi : Member " + memberEmail + " does not exist.");
            email.put("status", "ok");
            email.put("msg", "L'email indiqué n'existe pas.");
          } else {
            // membre trouvé
            LOGGER.debug("MemberNotExistApi : Member " + memberEmail + " exists.");
            email.put("status", "ko");
            email.put("msg", "Cet email existe déjà dans zelli.");
          }
          jsonResponse.put("email", email);
      }

      return jsonResponse.toString();
    } catch (JSONException e) {
      getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
      return null;
    }
  }
    
}