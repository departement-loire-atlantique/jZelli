<%@ page contentType="text/html; charset=UTF-8" %><%
%><%-- This file has been automatically generated. --%><%
%><%--
  @Summary: SousthemeASE display page
  @Category: Generated
  @Author: JCMS Type Processor
  @Customizable: True
  @Requestable: True
--%><%
%><%@ include file='/jcore/doInitPage.jspf' %><%
%><% SousthemeASE obj = (SousthemeASE)request.getAttribute(PortalManager.PORTAL_PUBLICATION); %><%
%><%@ include file='/front/doFullDisplay.jspf' %>
<div class="fullDisplay SousthemeASE <%= obj.canBeEditedFieldByField(loggedMember) ? "unitFieldEdition" : "" %>" itemscope="itemscope">
<%@ include file='/front/publication/doPublicationHeader.jspf' %>
<table class="fields">
  <tr class="field chapo textareaEditor  <%= Util.isEmpty(obj.getChapo(userLang)) ? "empty" : "" %>">
    <td class='field-label'><%= channel.getTypeFieldLabel(SousthemeASE.class, "chapo", userLang) %><jalios:edit pub='<%= obj %>' fields='chapo'/></td>
    <td class='field-data' <%= gfla(obj, "chapo") %>>
            <% if (Util.notEmpty(obj.getChapo(userLang))) { %>
            <%= obj.getChapo(userLang) %>
            <% } %>
    </td>
  </tr>
  <tr class="field contenu linkEditor  <%= Util.isEmpty(obj.getContenu()) ? "empty" : "" %>">
    <td class='field-label'><%= channel.getTypeFieldLabel(SousthemeASE.class, "contenu", userLang) %><jalios:edit pub='<%= obj %>' fields='contenu'/></td>
    <td class='field-data' >
            <% if (Util.notEmpty(obj.getContenu())) { %>
            <ol>
              <jalios:foreach name="itData" type="com.jalios.jcms.Content" array="<%= obj.getContenu() %>">
              <% if (itData != null && itData.canBeReadBy(loggedMember)) { %>
              <li>
              <jalios:link data='<%= itData %>'/>
              </li>
              <% } %>
              </jalios:foreach>
            </ol>
            <% } %>
    </td>
  </tr>
  <tr class="field affichagePageAAidee booleanEditor  ">
    <td class='field-label'><%= channel.getTypeFieldLabel(SousthemeASE.class, "affichagePageAAidee", userLang) %><jalios:edit pub='<%= obj %>' fields='affichagePageAAidee'/></td>
    <td class='field-data' >
            <%= obj.getAffichagePageAAideeLabel(userLang) %>
    </td>
  </tr>
  <tr class="field navigation categoryEditor  <%= Util.isEmpty(obj.getNavigation(loggedMember)) ? "empty" : "" %>">
    <td class='field-label'><%= channel.getTypeFieldLabel(SousthemeASE.class, "navigation", userLang) %><jalios:edit pub='<%= obj %>' fields='navigation'/></td>
    <td class='field-data' >
            <% if (Util.notEmpty(obj.getNavigation(loggedMember))) { %>
            <ol>
            <jalios:foreach collection="<%= obj.getNavigation(loggedMember) %>" type="Category" name="itCategory" >
              <li><% if (itCategory != null) { %><a href="<%= ResourceHelper.getQuery() %>?cids=<%= itCategory.getId() %>"><%= itCategory.getAncestorString(channel.getCategory("$jcmsplugin.zelli.category.navigationParThemes.root"), " > ", true, userLang) %></a><% } %></li>
            </jalios:foreach>
            </ol>
            <% } %>
    </td>
  </tr>
 
</table>
<jsp:include page="/front/doFullDisplayCommonFields.jsp" />
</div><%-- **********4A616C696F73204A434D53 *** SIGNATURE BOUNDARY * DO NOT EDIT ANYTHING BELOW THIS LINE *** --%><%
%><%-- 51sSzGmF1KtlVXaUJJNreA== --%>