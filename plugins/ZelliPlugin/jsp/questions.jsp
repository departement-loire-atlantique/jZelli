<%@ page contentType="text/html; charset=UTF-8"%><%
%><%@ include file='/jcore/doInitPage.jspf' %><%
%><%@ page import="fr.digiwin.module.zelli.utils.ZelliUtils"%><%
%><%@ page import="fr.digiwin.module.zelli.utils.ZelliManager"%><%
%><%@ page import="com.jalios.jcms.tools.PackerUtils"%><%
%><%@ page import="generated.QuestionZelli"%><%


String[] cssUrlArray = channel.getStringArrayProperty("jcmsplugin.socle.css-urls", new String[] {});
List<String> cssUrlAList = Arrays.asList(cssUrlArray);

String packerVersion = Util.getString(PackerUtils.getPackVersion(), "");

for(String itURL:cssUrlAList){%>
    <link rel="stylesheet" href="<%=itURL%>?version=<%=packerVersion %>" media="all" />
<%}
    if(isLogged && loggedMember.belongsToGroup(channel.getGroup(SocleConstants.VISIBLE_TOPBAR_GROUP_PROP))){%>
    <style>
       .ds44-header{top:64px!important;}
       .PortalMode .ds44-header{top:113px!important;}
       .member-profile{margin-top:100px!important;}
    </style>
    <%}


/* if (!checkAccess("admin/reporting/")) {
  sendForbidden(request, response);
  return;
} */
if (!ZelliManager.getInstance().canUseTdb(loggedMember)) {
	sendForbidden(request, response);
	return;  
}
request.setAttribute("title", glp("jcmsplugin.zelli.lbl.admin.title"));
request.setAttribute("operationAdminMenu", "true");

if (checkAccess("admin/reporting/")) {
	%><%@ include file='/admin/doAdminHeader.jspf' %><%
} else {
	%><%@ include file='/work/doWorkHeader.jspf' %><%
}
%>
<%
DataSelector selectorQuery = null;
String auteurParam = getAlphaNumParameter("auteur", "");
if (auteurParam != "") {
  Member mbr = channel.getMember(auteurParam);
  selectorQuery = Member.getAuthorSelector(mbr);
} 
%>
<jalios:pager   name='questionZellyHandler'
                declare='true'
                action='init'
                pageSize="50"
                pageSizes="50,100,150,200,250,300,350,400"/>

<jalios:query   name="collection"
                dataset="<%= channel.getDataSet(QuestionZelli.class)%>" 
                selector='<%= selectorQuery %>'
                comparator='<%= ComparatorManager.getComparator(QuestionZelli.class, questionZellyHandler.getSort(), questionZellyHandler.isReverse()) %>' />

<div class="ds44-form__container">
<jalios:pager   name='questionZellyHandler'
                action='compute'
                size='<%= collection.size() %>' />

<ul class="ds44-collapser">
    <li class="ds44-collapser_element">
        <a href="admin/analytics/report/index.jsp"><%= glp("jcmsplugin.zelli.lbl.statistiques") %></a>
    </li>
    <li class="ds44-collapser_element">
        <a href="plugins/ZelliPlugin/jsp/admin/exportCSVQuery.jsp"><%= glp("ui.com.btn.csv") %></a>
    </li>

</ul>

<%
int questionsATraiter = 0;
for (Object itObject : collection) {
  if (itObject instanceof QuestionZelli && ((QuestionZelli)itObject).getPstatus() == -12) {
    questionsATraiter ++;
    }
  }
%>
<p class="ds44-posRel"><%= glp("jcmsplugin.zelli.lbl.result", questionsATraiter) %></p>
<table class="table">
  <thead>
    <tr>
<!--  TODO Filter age and question fr.digiwin.module.zelli.utils -->
      <th><jalios:pager name='questionZellyHandler'
                        action='showSort'
                        sort='pstatus' 
                        sortTitle='jcmsplugin.zelli.lbl.tableau.statut'/>
      </th>
      <th><jalios:pager name='questionZellyHandler'
                        action='showSort'
                        sort='cdate'
                        sortTitle='jcmsplugin.zelli.lbl.tableau.quand'/>
      </th>
      <th><jalios:pager name='questionZellyHandler'
                        action='showSort'
                        sort='author'
                        sortTitle='jcmsplugin.zelli.lbl.tableau.qui'/>
       <form action="<%= ServletUtil.getUrl(request)%>">
	      <jalios:field name="auteur">
	           <jalios:control type="<%= ControlType.MEMBER %>" 
	                           settings='<%= new MemberSettings().group(channel.getProperty("$jcmsplugin.zelli.groupe.utilisateurs.id")) %>'>
	           </jalios:control>
	      </jalios:field>
	      <jalios:field>
 	      <button type="submit" class="ds44-btnStd">
 	          <span class="ds44-btnInnerText">Filtrer</span>
 	          <i class="icon icon-long-arrow-right" aria-hidden="true"></i>
 	      </button>
 	      </jalios:field>
       </form>
      </th>
      <th><jalios:pager name='questionZellyHandler'
                        action='showSort'
                        sort='age'
                        sortTitle='jcmsplugin.zelli.lbl.tableau.age'/>
      </th>
      <th><jalios:pager name='questionZellyHandler'
                        action='showSort'
                        sort='question'
                        sortTitle='jcmsplugin.zelli.lbl.tableau.question'/>
      </th>
      <th scope="col"><%= glp("jcmsplugin.zelli.lbl.tableau.ref") %></th>
      <th scope="col"><%= glp("jcmsplugin.zelli.lbl.tableau.reponse") %></th>
      <th scope="col"><%= glp("jcmsplugin.zelli.lbl.tableau.remarque") %></th>
    </tr>
  </thead>
  <tbody>
       <jalios:foreach collection="<%= collection %>"
                       name="itQuestion"
                       type="QuestionZelli"
                       max='<%= questionZellyHandler.getPageSize() %>'
                       skip='<%= questionZellyHandler.getStart() %>'>
       
       <jalios:buffer name="age">
        <jalios:if predicate='<%= Util.notEmpty(itQuestion.getAuthor().getExtraData("extradb.Member.jcmsplugin.zelli.datenaissance")) %>'>
        <%=glp("jcmsplugin.zelli.lbl.datedenaissanceAge",
          Integer.parseInt(ZelliUtils.getAgeStrFromDateNaissance(itQuestion.getAuthor().getExtraData("extradb.Member.jcmsplugin.zelli.datenaissance"))),
          itQuestion.getAuthor().getExtraData("extradb.Member.jcmsplugin.zelli.datenaissance")) %>
        </jalios:if>
       </jalios:buffer>
       
       <jalios:buffer name="reponse">
	       <jalios:if predicate="<%= Util.notEmpty(itQuestion.getReponse()) %>">
	           <p><%= itQuestion.getReponse() %></p>
	           <p>&#91;<jalios:date date="<%= itQuestion.getDateDeLaReponse() %>" format="dd/MM/yyyy"/>&#93;</p>
	       </jalios:if>
	       <p><a class="modal" href="plugins/ZelliPlugin/types/QuestionZelli/editQuestionZelliModal.jsp?id=<%= itQuestion.getId() %>&field=reponse&redirectOnClosePopup=false&popupEdition=true&ws=<%= workspace.getId() %>" />
	           <%= glp("jcmsplugin.zelli.lbl.admin.lbl.repondre") %>
	       </a></p> 
       </jalios:buffer>
       
       <jalios:buffer name="remarque">
	       <jalios:if predicate="<%= Util.notEmpty(itQuestion.getRemarque()) %>">
	           <%= itQuestion.getRemarque() %>
	           <p><a class="modal" href="plugins/ZelliPlugin/types/QuestionZelli/editQuestionZelliModal.jsp?id=<%= itQuestion.getId() %>&field=remarque&redirectOnClosePopup=false&popupEdition=true&ws=<%= workspace.getId() %>" />
	               <%= glp("ui.com.alt.edit") %>
	           </a></p> 
	       </jalios:if>
	       <jalios:if predicate="<%= Util.isEmpty(itQuestion.getRemarque()) %>">
	           <p><a class="modal" href="plugins/ZelliPlugin/types/QuestionZelli/editQuestionZelliModal.jsp?id=<%= itQuestion.getId() %>&field=remarque&redirectOnClosePopup=false&popupEdition=true&ws=<%= workspace.getId() %>" />
	               <%= glp("jcmsplugin.zelli.lbl.admin.lbl.commenter") %>
	           </a></p>
	       </jalios:if>
	       <jalios:if predicate="<%= Util.notEmpty(itQuestion.getReponse()) %>">
	           <p><a class="modal" href="work/validateStateChange.jsp?id=<%= itQuestion.getId() %>&ws=<%= workspace.getId() %>&redirectOnClosePopup=false&pstatus=2">
	               <%= glp("ui.com.btn.finish") %></a>
	           </p>
	       </jalios:if>
       </jalios:buffer>
       <tr>
       <td><%= itQuestion.getWFStateLabel(userLang) %></td>
       <td><jalios:date date="<%= itQuestion.getCdate() %>"
                        format="dd/MM/yyyy 'à' HH:mm"
                        nowifnull="false" />
       </td>
       <td><%= itQuestion.getAuthor() %></td>
       <td><%= age %></td>
       <td><%= itQuestion.getQuestion() %></td>
       <td><%= itQuestion.getReferentLabel(userLang) %></td>
       <td><%= reponse %></td>
       <td><%= remarque %></td>
       </tr>
       </jalios:foreach>
  </tbody>
</table>

<%-- Pager infini --%>
<div class="txtcenter center ds44--xl-padding-b ds44-js-search-pager">
<jalios:select>
    <jalios:if predicate="<%= questionZellyHandler.getPageSize() < questionZellyHandler.getItemsNbr() %>">
            <p class="idNbResults"><%= glp("jcmsplugin.zelli.lbl.resultat.questionsAffichees", questionZellyHandler.getPageSize(), questionZellyHandler.getItemsNbr() )%></p>
        <p><a   href="<%=  questionZellyHandler.getShowPageSizeURL(questionZellyHandler.getPageSize() + Util.getInt(questionZellyHandler.getPageSizes(), 0, 50)) %>" 
                title="<%= glp("ui.pager.all.long") %>"
                class="ds44-btnStd ds44-btn--invert ds44-js-search-button"
                data-jalios-ajax-refresh="nohistory noscroll nofocus">
           <span class="ds44-btnInnerText"><%=glp("jcmsplugin.zelli.lbl.resultat.plus") %></span><i class="icon icon-plus" aria-hidden="true"></i></a>
        </p>
    </jalios:if>
    <jalios:default>
        <p class="idNbResults"><%= glp("jcmsplugin.zelli.lbl.resultat.questionsAffichees", questionZellyHandler.getItemsNbr(), questionZellyHandler.getItemsNbr()) %></p>
    </jalios:default>
</jalios:select>
</div>
</div>
<%@ include file='/admin/doAdminFooter.jspf' %>