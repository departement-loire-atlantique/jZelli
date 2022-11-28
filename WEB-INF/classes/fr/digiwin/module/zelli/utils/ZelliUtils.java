package fr.digiwin.module.zelli.utils;

import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.log4j.Logger;
import org.restlet.data.Request;
import org.restlet.resource.Representation;

import com.jalios.jcms.Channel;
import com.jalios.jcms.Group;
import com.jalios.jcms.JcmsUtil;
import com.jalios.jcms.Member;
import com.jalios.jcms.authentication.handlers.AuthKeyAuthenticationHandler;
import com.jalios.jcms.authentication.handlers.AuthKeyHints;
import com.jalios.jcms.mail.MailMessage;
import com.jalios.util.Util;

import generated.QuestionZelli;

public class ZelliUtils {

	private static final Logger LOGGER = Logger.getLogger(ZelliUtils.class);
	private static final Channel CHANNEL = Channel.getChannel();

	/**
	 * Récupérer une Map<param,value> depuis une requête restlet
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, String> getUriParams(Request request) {
		return generateUriMap(URLEncodedUtils.parse(request.getResourceRef().getQuery(), Charset.forName("UTF-8")));
	}

	/**
	 * Récupérer une Map<param,value> depuis un string de query http
	 * 
	 * @param text
	 * @return
	 */
	public static Map<String, String> getUriParams(String text) {
		return generateUriMap(URLEncodedUtils.parse(text, Charset.forName("UTF-8")));
	}

	private static Map<String, String> generateUriMap(List<NameValuePair> params) {
		Map<String, String> mapParams = new HashMap<>();
		for (Iterator<NameValuePair> iter = params.iterator(); iter.hasNext();) {
			NameValuePair itPair = iter.next();
			mapParams.put(itPair.getName(), itPair.getValue());
		}
		return mapParams;
	}

	/**
	 * Créer un token d'authentification d'une durée de 30j pour un membre La
	 * méthode ne vérifie pas le droit d'accès à cette feature ! Il faut faire la
	 * vérification préalablement.
	 * 
	 * @param mbr
	 * @return
	 */
	public static String getAuthKey(Member mbr) {
		long duration = 60 * 60 * 24 * 30; // nb de secondes sur 30 jours
		AuthKeyHints authKeyHints = new AuthKeyHints();
		authKeyHints.setVersion("2");
		authKeyHints.setExpiration(System.currentTimeMillis() + duration);
		authKeyHints.setPrefixMode(true);
		authKeyHints.setMethods("GET,PUT,POST,DELETE");
		return AuthKeyAuthenticationHandler.getAuthKeyValue(CHANNEL.getUrl(), mbr, authKeyHints);
	}

	/**
	 * Envoie un email aux membres du groupe Gestionnaires suite au vote sur
	 * l'utilité d'une question
	 * 
	 * @param entity
	 * @param questionId
	 */
	public static void sendQuestionNotification(Representation entity, String questionId) {
		// TODO : contenu Question à mettre en place une fois indiqués dans le cahier
		// des charges
		QuestionZelli questionZelli = (QuestionZelli) CHANNEL.getData(questionId);
		if (Util.notEmpty(questionZelli)) {

			// temporaire : placeholders
			String tmpPseudo = questionZelli.getAuthor().getLogin();
			String dateNaissance = questionZelli.getAuthor().getExtraData("extra.Member.jcmsplugin.zelli.datenaissance");
			String datePublication = questionZelli.getPdate().toString(); // TODO : récupérer la date de création de la question et la
															// formatter "dd/MM/yyyy' à 'hh'h'mm"
			String maQuestion = questionZelli.getQuestion();
			String maReponse = questionZelli.getReponse();
			String contactReferent = questionZelli.getReferent() ? "Oui" : "Non"; // TODO : Oui ou Non selon boolean
			boolean isUtile = true; // TODO : récupérer l'utilité depuis la question

			LOGGER.debug("sendQuestionNotification for question " + questionId);

			String age = getAgeStrFromDateNaissance(dateNaissance);

			// récupération des membres gestionnaires
			Group gestionnaires = CHANNEL.getGroup("$jcmsplugin.zelli.groupe.gestionnaires.id");

			for (Member itMember : gestionnaires.getMemberSet()) {
				// Création de l'email par membre
				MailMessage email = new MailMessage();
				email.setTo(itMember);
				email.setSubject(JcmsUtil.glp("jcmsplugin.zelli.email.gestionnaire.question.object", tmpPseudo, age,
						CHANNEL.getCurrentUserLang()));
				if (isUtile) {
					email.setContentHtml(JcmsUtil.glp("jcmsplugin.zelli.email.gestionnaire.question.utile.content",
							tmpPseudo, datePublication, contactReferent, maQuestion, maReponse,
							CHANNEL.getCurrentUserLang()));
				} else {
					email.setContentHtml(JcmsUtil.glp("jcmsplugin.zelli.email.gestionnaire.question.inutile.content",
							tmpPseudo, datePublication, contactReferent, maQuestion, maReponse,
							CHANNEL.getCurrentUserLang()));
				}

				try {
					LOGGER.info("Sending mail to " + itMember.getId());
					email.send();
				} catch (MessagingException e) {
					LOGGER.error("ZelliUtils sendQuestionNotification exception - " + e.getMessage());
					e.printStackTrace();
				}
			}

			LOGGER.info("Emails sent.");
		}

	}

	/**
	 * Renvoie un âge basé sur une date de naissance
	 * 
	 * @param dateNaissance
	 * @return
	 */
	public static String getAgeStrFromDateNaissance(String dateNaissanceStr) {

		SimpleDateFormat sdf = new SimpleDateFormat(
				CHANNEL.getProperty("jcmsplugin.zelli.simpledateformat.datenaissance"));

		try {

			// créer des objets LocalDate pour calculer une période
			LocalDate dateNaissance = LocalDate.ofInstant(sdf.parse(dateNaissanceStr).toInstant(),
					ZoneId.systemDefault());
			LocalDate dateNow = LocalDate.now();

			// période entre les deux dates
			Period period = Period.between(dateNow, dateNaissance);
			int annees = Math.abs(period.getYears());
			return Integer.toString(annees);

		} catch (Exception e) {
			LOGGER.error("ZelliUtils getAgeStrFromDateNaissance exception : " + e.getMessage());
			return "-1";
		}
	}

	/**
	 * Export CSV
	 * 
	 * @param questionZelliSet
	 * @param lang
	 * @param out
	 */
	public static void exportCSV(Set<QuestionZelli> questionZelliSet, String lang, Writer outFile) {
		if (!Util.isEmpty(questionZelliSet) && outFile != null) {
			PrintWriter printWriter = new PrintWriter(outFile);
			printWriter.println(headerForCSV(lang));
			Iterator<? extends QuestionZelli> itQuestionZelli = questionZelliSet.iterator();
			while (true) {
				while (itQuestionZelli.hasNext()) {
					QuestionZelli var6 = (QuestionZelli) itQuestionZelli.next();
					printWriter.println(toCSV(var6, lang));
				}
				return;
			}
		}
	}

	/**
	 * Set header for CSV
	 * 
	 * @param lang
	 * @return
	 */
	private static String headerForCSV(String lang) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(JcmsUtil.convertToCSV(lang, JcmsUtil.glpd("jcmsplugin.zelli.lbl.tableau.statut")))
				.append(';').append(JcmsUtil.convertToCSV(lang, JcmsUtil.glpd("jcmsplugin.zelli.lbl.tableau.quand")))
				.append(';').append(JcmsUtil.convertToCSV(lang, JcmsUtil.glpd("jcmsplugin.zelli.lbl.tableau.qui")))
				.append(';').append(JcmsUtil.convertToCSV(lang, JcmsUtil.glpd("jcmsplugin.zelli.lbl.tableau.age")))
				.append(';').append(JcmsUtil.convertToCSV(lang, JcmsUtil.glpd("jcmsplugin.zelli.lbl.tableau.question")))
				.append(';').append(JcmsUtil.convertToCSV(lang, JcmsUtil.glpd("jcmsplugin.zelli.lbl.tableau.ref")))
				.append(';').append(JcmsUtil.convertToCSV(lang, JcmsUtil.glpd("jcmsplugin.zelli.lbl.tableau.reponse")))
				.append(';').append(JcmsUtil.convertToCSV(lang, JcmsUtil.glpd("jcmsplugin.zelli.lbl.tableau.remarque")))
				.append(';');

		return stringBuilder.toString();
	}

	/**
	 * QuestionZelli to CSV
	 * 
	 * @param questionZelli
	 * @param lang
	 * @return
	 */
	private static String toCSV(QuestionZelli questionZelli, String lang) {
		StringBuilder stringBuilderAge = new StringBuilder();
		if (Util.notEmpty(questionZelli.getAuthor().getExtraData("extra.Member.jcmsplugin.zelli.datenaissance"))) {
			stringBuilderAge
					.append(getAgeStrFromDateNaissance(
							questionZelli.getAuthor().getExtraData("extra.Member.jcmsplugin.zelli.datenaissance")))
					.append(" ans [")
					.append(questionZelli.getAuthor().getExtraData("extra.Member.jcmsplugin.zelli.datenaissance"))
					.append("]");
		}

		SimpleDateFormat sdf = new SimpleDateFormat(CHANNEL.getProperty("jcmsplugin.zelli.simpledateformat.quand"));

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(JcmsUtil.convertToCSV(lang, questionZelli.getWFStateLabel(lang))).append(';')
				.append(JcmsUtil.convertToCSV(lang, sdf.format(questionZelli.getCdate()))).append(';')
				.append(JcmsUtil.convertToCSV(lang, questionZelli.getAuthor().getFullName())).append(';')
				.append(JcmsUtil.convertToCSV(lang, stringBuilderAge.toString())).append(';')
				.append(JcmsUtil.convertToCSV(lang, JcmsUtil.unescapeHtml(questionZelli.getQuestion(lang)))).append(';')
				.append(JcmsUtil.convertToCSV(lang, questionZelli.getReferentLabel(lang))).append(';')
				.append(JcmsUtil.convertToCSV(lang, JcmsUtil.unescapeHtml(questionZelli.getReponse(lang)))).append(';')
				.append(JcmsUtil.convertToCSV(lang, JcmsUtil.unescapeHtml(questionZelli.getRemarque(lang))))
				.append(';');
		return stringBuilder.toString();
	}

}