<%@page import="fr.digiwin.module.zelli.utils.ZelliUtils"%>
<%
  response.setHeader("Content-Disposition", "attachment; filename=results.csv"); 
  // inform doInitPage to set the proper content type
  request.setAttribute("ContentType", "text/csv; charset=" + channel.getProperty("csv.charset"));
%><%@ include file='/jcore/doInitPage.jspf' %><%
 
if (!isLogged || !(isAdmin || loggedMember.isWorker(workspace) || request.getAttribute("authorized") == Boolean.TRUE)) {
  sendForbidden(request, response);
  return;
}
%>
<jalios:query name="collection" dataset="<%= channel.getDataSet(QuestionZelli.class)%>" />
<%
ZelliUtils.exportCSV(collection, userLang, out);
%>
