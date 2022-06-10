
<%@page import="fr.digiwin.module.zelli.utils.QuestionZelliAgeComparator"%>
<%@ page contentType="text/html; charset=UTF-8"%><%
%><%@ include file='/jcore/doInitPage.jspf' %><%
%><%@ page import="fr.digiwin.module.zelli.utils.ZelliUtils"%><%
%><%@ page import="generated.QuestionZelli"%><%

  if (!checkAccess("admin/reporting/")) {
    sendForbidden(request, response);
    return;
  }

  request.setAttribute("title", glp("jcmsplugin.zelli.lbl.admin.title"));
  request.setAttribute("operationAdminMenu", "true");
%>
<%@ include file='/admin/doAdminHeader.jspf' %>

<jalios:pager name='questionZellyHandler' declare='true' action='init' pageSize="50"/>
<jalios:query   name="collection"
                dataset="<%= channel.getDataSet(QuestionZelli.class)%>"
                comparator='<%= Publication.getComparator(questionZellyHandler.getSort(), questionZellyHandler.isReverse()) %>' />
                
                
                
                

 <%
 DataSelector selector = QuestionZelli.getCanWorkOnSelector(loggedMember);
 Comparator comparator = ComparatorManager.getComparator(QuestionZelli.class, "age");
 Set<QuestionZelli> questionZelliSet = JcmsUtil.select(collection, selector, comparator);
 System.out.println("vxv " + questionZelliSet);
  %>

 
 
 
 
 
<jalios:pager name='questionZellyHandler' action='compute' size='<%= collection.size() %>' />

<% int questionsATraiter = 0; %>
<jalios:foreach collection="<%= collection %>" name="itQuestion" type="QuestionZelli">
    <% if(itQuestion.getPstatus() == -12) { questionsATraiter ++; } %>
</jalios:foreach>
<%= glp("jcmsplugin.zelli.lbl.result", questionsATraiter) %>
<a href="plugins/ZelliPlugin/questionsStat.jsp"><%= glp("jcmsplugin.zelli.lbl.statistiques") %></a>
<a href="plugins/ZelliPlugin/questionsExport.jsp"><%= glp("ui.com.btn.csv") %></a>

<table class="table">
  <thead>
    <tr>
<!--  TODO Filter age and question fr.digiwin.module.zelli.utils -->
      <th><jalios:pager name='questionZellyHandler' action='showSort' sort='pstatus' sortTitle='jcmsplugin.zelli.lbl.tableau.statut'/></th>
      <th><jalios:pager name='questionZellyHandler' action='showSort' sort='cdate' sortTitle='jcmsplugin.zelli.lbl.tableau.quand'/></th>
      <th><jalios:pager name='questionZellyHandler' action='showSort' sort='author' sortTitle='jcmsplugin.zelli.lbl.tableau.qui'/></th>
<!--  TODO filtre de recherche sur le nom -->
      <th><jalios:pager name='questionZellyHandler' action='showSort' sort='age' sortTitle='jcmsplugin.zelli.lbl.tableau.age'/></th>
      <th><jalios:pager name='questionZellyHandler' action='showSort' sort='question' sortTitle='jcmsplugin.zelli.lbl.tableau.question'/></th>
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
        <jalios:if predicate='<%= Util.notEmpty(itQuestion.getAuthor().getExtraData("extra.Member.jcmsplugin.zelli.datenaissance")) %>'>
            <%= ZelliUtils.getAgeStrFromDateNaissance(itQuestion.getAuthor().getExtraData("extra.Member.jcmsplugin.zelli.datenaissance")) %>&nbsp;ans [
            <%= itQuestion.getAuthor().getExtraData("extra.Member.jcmsplugin.zelli.datenaissance") %>]
        </jalios:if>
       </jalios:buffer>
       
       <jalios:buffer name="reponse">
	       <jalios:if predicate="<%= Util.notEmpty(itQuestion.getReponse()) %>">
	           <%= itQuestion.getReponse() %>
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
	       <jalios:if predicate="<%= Util.isEmpty(itQuestion.getReponse()) %>">
	           <p><a class="modal" href="work/validateStateChange.jsp?id=<%= itQuestion.getId() %>&ws=<%= workspace.getId() %>&redirect=<%= ServletUtil.getUrl(request) %>&pstatus=<%= itQuestion.getPstatus() %>">
	               <%= glp("ui.com.btn.finish") %>
	           </a></p>
	       </jalios:if>
       </jalios:buffer>
       <tr>
       <td><%= itQuestion.getWFStateLabel(userLang) %></td>
       <td><jalios:date date="<%= itQuestion.getCdate() %>" format="dd/MM/yyyy 'à' HH:mm" nowifnull="false" /></td>
       <td><%= itQuestion.getAuthor() %> </td>
       <td><%= age %></td>
       <td><%= itQuestion.getQuestion() %></td>
       <td><%= itQuestion.getReferentLabel(userLang) %></td>
       <td><%= reponse %></td>
       <td><%= remarque %></td>
       </tr>
       </jalios:foreach>
  </tbody>
</table>
<jalios:pager name='questionZellyHandler' /> 
<%@ include file='/admin/doAdminFooter.jspf' %>