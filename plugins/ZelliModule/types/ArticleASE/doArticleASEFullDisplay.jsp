<%@ page contentType="text/html; charset=UTF-8" %><%
%><%-- This file has been automatically generated. --%><%
%><%--
  @Summary: ArticleASE display page
  @Category: Generated
  @Author: JCMS Type Processor
  @Customizable: True
  @Requestable: True
--%><%
%><%@ include file='/jcore/doInitPage.jspf' %><%
%><% ArticleASE obj = (ArticleASE)request.getAttribute(PortalManager.PORTAL_PUBLICATION); %><%
%><%@ include file='/front/doFullDisplay.jspf' %>
<div class="fullDisplay ArticleASE <%= obj.canBeEditedFieldByField(loggedMember) ? "unitFieldEdition" : "" %>" itemscope="itemscope">
<%@ include file='/front/publication/doPublicationHeader.jspf' %>
<table class="fields">
  <tr class="field picto imageEditor  <%= Util.isEmpty(obj.getPicto(userLang)) ? "empty" : "" %>">
    <td class='field-label'><%= channel.getTypeFieldLabel(ArticleASE.class, "picto", userLang) %><jalios:edit pub='<%= obj %>' fields='picto'/></td>
    <td class='field-data' <%= gfla(obj, "picto") %>>
            <% if (Util.notEmpty(obj.getPicto(userLang))) { %>
            <img src='<%= Util.encodeUrl(obj.getPicto(userLang)) %>' alt='' />
            <% } %>
    </td>
  </tr>
  <tr class="field diaporama linkEditor  <%= Util.isEmpty(obj.getDiaporama()) ? "empty" : "" %>">
    <td class='field-label'><%= channel.getTypeFieldLabel(ArticleASE.class, "diaporama", userLang) %><jalios:edit pub='<%= obj %>' fields='diaporama'/></td>
    <td class='field-data' >
            <% if (Util.notEmpty(obj.getDiaporama())) { %>
            <ol>
              <jalios:foreach name="itData" type="generated.Carousel" collection="<%= obj.getDiaporama() %>">
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
  <tr class="field diaporamaTexte textfieldEditor  ">
    <td class='field-label'><%= channel.getTypeFieldLabel(ArticleASE.class, "diaporamaTexte", userLang) %><jalios:edit pub='<%= obj %>' fields='diaporamaTexte'/></td>
    <td class='field-data' >
        <% if (Util.notEmpty(obj.getDiaporamaTexte(userLang))) { %>
            <ol>
            <jalios:foreach name="itString" type="String" collection="<%= obj.getDiaporamaTexte(userLang) %>">
            <% if (Util.notEmpty(itString)) { %>
              <li>
              <%= itString %>
              </li>
            <% } %>
            </jalios:foreach>
            </ol>
        <% } %>
    </td>
  </tr>
  <tr class="field video linkEditor  <%= Util.isEmpty(obj.getVideo()) ? "empty" : "" %>">
    <td class='field-label'><%= channel.getTypeFieldLabel(ArticleASE.class, "video", userLang) %><jalios:edit pub='<%= obj %>' fields='video'/></td>
    <td class='field-data' >
            <% if (Util.notEmpty(obj.getVideo())) { %>
            <ol>
              <jalios:foreach name="itData" type="generated.Video" collection="<%= obj.getVideo() %>">
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
  <tr class="field videoTexte textfieldEditor  ">
    <td class='field-label'><%= channel.getTypeFieldLabel(ArticleASE.class, "videoTexte", userLang) %><jalios:edit pub='<%= obj %>' fields='videoTexte'/></td>
    <td class='field-data' >
        <% if (Util.notEmpty(obj.getVideoTexte(userLang))) { %>
            <ol>
            <jalios:foreach name="itString" type="String" collection="<%= obj.getVideoTexte(userLang) %>">
            <% if (Util.notEmpty(itString)) { %>
              <li>
              <%= itString %>
              </li>
            <% } %>
            </jalios:foreach>
            </ol>
        <% } %>
    </td>
  </tr>
  <tr class="field titreDescription textfieldEditor  ">
    <td class='field-label'><%= channel.getTypeFieldLabel(ArticleASE.class, "titreDescription", userLang) %><jalios:edit pub='<%= obj %>' fields='titreDescription'/></td>
    <td class='field-data' >
        <% if (Util.notEmpty(obj.getTitreDescription(userLang))) { %>
            <ol>
            <jalios:foreach name="itString" type="String" collection="<%= obj.getTitreDescription(userLang) %>">
            <% if (Util.notEmpty(itString)) { %>
              <li>
              <%= itString %>
              </li>
            <% } %>
            </jalios:foreach>
            </ol>
        <% } %>
    </td>
  </tr>
  <tr class="field description wysiwygEditor  ">
    <td class='field-label'><%= channel.getTypeFieldLabel(ArticleASE.class, "description", userLang) %><jalios:edit pub='<%= obj %>' fields='description'/></td>
    <td class='field-data' >
        <% if (Util.notEmpty(obj.getDescription(userLang))) { %>
            <ol>
            <jalios:foreach name="itString" type="String" collection="<%= obj.getDescription(userLang) %>">
            <% if (Util.notEmpty(itString)) { %>
              <li>
              <jalios:wysiwyg data='<%= obj %>' field='description'><%= itString %></jalios:wysiwyg>
              </li>
            <% } %>
            </jalios:foreach>
            </ol>
        <% } %>
    </td>
  </tr>
  <tr class="field motsCompliques linkEditor  <%= Util.isEmpty(obj.getMotsCompliques()) ? "empty" : "" %>">
    <td class='field-label'><%= channel.getTypeFieldLabel(ArticleASE.class, "motsCompliques", userLang) %><jalios:edit pub='<%= obj %>' fields='motsCompliques'/></td>
    <td class='field-data' >
            <% if (Util.notEmpty(obj.getMotsCompliques())) { %>
            <ol>
              <jalios:foreach name="itData" type="generated.FaqEntry" collection="<%= obj.getMotsCompliques() %>">
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
  <tr class="field liensInternes linkEditor  <%= Util.isEmpty(obj.getLiensInternes()) ? "empty" : "" %>">
    <td class='field-label'><%= channel.getTypeFieldLabel(ArticleASE.class, "liensInternes", userLang) %><jalios:edit pub='<%= obj %>' fields='liensInternes'/></td>
    <td class='field-data' >
            <% if (Util.notEmpty(obj.getLiensInternes())) { %>
            <ol>
              <jalios:foreach name="itData" type="com.jalios.jcms.Content" collection="<%= obj.getLiensInternes() %>">
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
  <tr class="field liensExternes urlEditor  ">
    <td class='field-label'><%= channel.getTypeFieldLabel(ArticleASE.class, "liensExternes", userLang) %><jalios:edit pub='<%= obj %>' fields='liensExternes'/></td>
    <td class='field-data' >
        <% if (Util.notEmpty(obj.getLiensExternes(userLang))) { %>
            <ol>
            <jalios:foreach name="itString" type="String" collection="<%= obj.getLiensExternes(userLang) %>">
            <% if (Util.notEmpty(itString)) { %>
              <li>
              <a href='<%= itString %>'><%= itString %></a>
              </li>
            <% } %>
            </jalios:foreach>
            </ol>
        <% } %>
    </td>
  </tr>
  <tr class="field libelleLien textfieldEditor  ">
    <td class='field-label'><%= channel.getTypeFieldLabel(ArticleASE.class, "libelleLien", userLang) %><jalios:edit pub='<%= obj %>' fields='libelleLien'/></td>
    <td class='field-data' >
        <% if (Util.notEmpty(obj.getLibelleLien(userLang))) { %>
            <ol>
            <jalios:foreach name="itString" type="String" collection="<%= obj.getLibelleLien(userLang) %>">
            <% if (Util.notEmpty(itString)) { %>
              <li>
              <%= itString %>
              </li>
            <% } %>
            </jalios:foreach>
            </ol>
        <% } %>
    </td>
  </tr>
  <tr class="field fichesStructures linkEditor  <%= Util.isEmpty(obj.getFichesStructures()) ? "empty" : "" %>">
    <td class='field-label'><%= channel.getTypeFieldLabel(ArticleASE.class, "fichesStructures", userLang) %><jalios:edit pub='<%= obj %>' fields='fichesStructures'/></td>
    <td class='field-data' >
            <% if (Util.notEmpty(obj.getFichesStructures())) { %>
            <ol>
              <jalios:foreach name="itData" type="generated.Contact" collection="<%= obj.getFichesStructures() %>">
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
  <tr class="field saisieLibre wysiwygEditor  <%= Util.isEmpty(obj.getSaisieLibre(userLang)) ? "empty" : "" %>">
    <td class='field-label'><%= channel.getTypeFieldLabel(ArticleASE.class, "saisieLibre", userLang) %><jalios:edit pub='<%= obj %>' fields='saisieLibre'/></td>
    <td class='field-data' <%= gfla(obj, "saisieLibre") %>>
            <% if (Util.notEmpty(obj.getSaisieLibre(userLang))) { %>
            <jalios:wysiwyg data='<%= obj %>' field='saisieLibre'><%= obj.getSaisieLibre(userLang) %></jalios:wysiwyg>            
            <% } %>
    </td>
  </tr>
  <tr class="field pageAAide booleanEditor  ">
    <td class='field-label'><%= channel.getTypeFieldLabel(ArticleASE.class, "pageAAide", userLang) %><jalios:edit pub='<%= obj %>' fields='pageAAide'/></td>
    <td class='field-data' >
            <%= obj.getPageAAideLabel(userLang) %>
    </td>
  </tr>
  <tr class="field contenuPrecedent linkEditor  <%= Util.isEmpty(obj.getContenuPrecedent()) ? "empty" : "" %>">
    <td class='field-label'><%= channel.getTypeFieldLabel(ArticleASE.class, "contenuPrecedent", userLang) %><jalios:edit pub='<%= obj %>' fields='contenuPrecedent'/></td>
    <td class='field-data' >
            <% if (obj.getContenuPrecedent() != null && obj.getContenuPrecedent().canBeReadBy(loggedMember)) { %>
            <jalios:link data='<%= obj.getContenuPrecedent() %>'/>
            <% } %>
    </td>
  </tr>
  <tr class="field contenuSuivant linkEditor  <%= Util.isEmpty(obj.getContenuSuivant()) ? "empty" : "" %>">
    <td class='field-label'><%= channel.getTypeFieldLabel(ArticleASE.class, "contenuSuivant", userLang) %><jalios:edit pub='<%= obj %>' fields='contenuSuivant'/></td>
    <td class='field-data' >
            <% if (obj.getContenuSuivant() != null && obj.getContenuSuivant().canBeReadBy(loggedMember)) { %>
            <jalios:link data='<%= obj.getContenuSuivant() %>'/>
            <% } %>
    </td>
  </tr>
  <tr class="field navigation categoryEditor  <%= Util.isEmpty(obj.getNavigation(loggedMember)) ? "empty" : "" %>">
    <td class='field-label'><%= channel.getTypeFieldLabel(ArticleASE.class, "navigation", userLang) %><jalios:edit pub='<%= obj %>' fields='navigation'/></td>
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
%><%-- 5WDHn+QlSiFTpt42TPrd4w== --%>