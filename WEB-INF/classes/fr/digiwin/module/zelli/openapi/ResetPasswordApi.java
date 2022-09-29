package fr.digiwin.module.zelli.openapi;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URLEncoder;
import java.util.Random;

import javax.mail.MessagingException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
import com.jalios.util.HttpClientUtils;
import com.jalios.util.Util;

public class ResetPasswordApi extends JcmsRestResource {

    private static final Logger LOGGER = Logger.getLogger(ResetPasswordApi.class);
    private String tokenBearer = "";
    private String idGroup = "";

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
        String content = JcmsUtil.glp("fr", "jcmsplugin.zelli.email.pwd.reset.msg", newMdp);

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
            final String URL_API = "https://contact-everyone.orange-business.com/api/v1.2/";
            
            String log = channel.getProperty("jcmsplugin.zelli.login-orange");
            String pwd = channel.getProperty("jcmsplugin.zelli.pwd-orange");
            if(Util.notEmpty(log) && Util.notEmpty(pwd)) {
            	try {
            		// get token
    	        	CloseableHttpClient httpClient = HttpClientUtils.newHttpClient();

    	            String login = URLEncoder.encode(log, "UTF-8");
    	            String password = URLEncoder.encode(pwd, "UTF-8");
    	            
    	            HttpPost postToken = new HttpPost(URL_API + "oauth/token?username=" + login + "&password=" + password);
    	            HttpResponse responseToken = httpClient.execute(postToken);
    	            HttpEntity entityToken = responseToken.getEntity();

                    int resCodeToken = responseToken.getStatusLine().getStatusCode();
                    if(resCodeToken >= 200 && resCodeToken < 300) {
                    	if (entityToken != null) {
        	                try (InputStream instream = entityToken.getContent()) {
        	                	String strResponse = EntityUtils.toString(entityToken);
        	                    JSONObject json  = new JSONObject(strResponse); 
            	                this.tokenBearer = json.getString("access_token");
        	                } catch (JSONException e) {
        						e.printStackTrace();
        					}
        	            }
                    }
                    
    	            //get group
    	            if(Util.notEmpty(this.tokenBearer)) {
    	            	HttpGet getGroup = new HttpGet(URL_API + "groups");
    	            	getGroup.addHeader("Authorization", "Bearer " + this.tokenBearer);
    		            HttpResponse responseGroup = httpClient.execute(getGroup);
    		            HttpEntity entityGroup = responseGroup.getEntity();
    		            
    		            int resCodeGroup = responseToken.getStatusLine().getStatusCode();
                        if(resCodeGroup >= 200 && resCodeGroup < 300) {
                        	if (entityGroup != null) {
        		                try (InputStream instream = entityGroup.getContent()) {
        		                	String strResponse = EntityUtils.toString(entityGroup);
        		                	JSONArray  json  = new JSONArray (strResponse);  
        		                	JSONObject object = json.getJSONObject(0);  
        		                	if(Util.notEmpty(object)) {
        			                    this.idGroup = object.getString("id");
        		                	}
        		                } catch (JSONException e) {
        							e.printStackTrace();
        						}
        		            }
                        }
    	            }
    	            
    	            //send sms
    	            String smsContent = StringEscapeUtils.escapeJava(content);
    	    	    
    	            if(Util.notEmpty(this.idGroup)) {
    	            	HttpPost postSMS = new HttpPost(URL_API + "groups/" + this.idGroup + "/diffusion-requests");
    	            	postSMS.addHeader("Authorization", "Bearer " + this.tokenBearer);
    	            	String json = "{\"name\":\"Reinitialisation du mot de passe utilisateur\",\"msisdns\":[\"" + mbr.getPhone() + "\"],\"smsParam\":{\"encoding\":\"GSM7\",\"body\":\" " + smsContent +"\" }}";

    	                StringEntity entity = new StringEntity(json);
    	                postSMS.setEntity(entity);
    	                postSMS.setHeader("Accept", "application/json");
    	                postSMS.setHeader("Content-Type", "application/json");
    	            	
    		            httpClient.execute(postSMS);
    	            }
    	            
    				httpClient.close();
    	            
    			} catch (MalformedURLException e) {
    				e.printStackTrace();
    			} catch (ProtocolException e) {
    				e.printStackTrace();
    			} catch (IOException e) {
    				e.printStackTrace();
    			} 
            }
            
			
        }

        LOGGER.info("Imposible de réinitialisation du mot de passe pour : " + mbr.getFullName());

        return "";
    }
}
