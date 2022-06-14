<%-- This file was automatically generated. --%><%
%><%--
  @Summary: QuestionZelli editor
  @Category: Generated
  @Author: JCMS Type Processor
  @Customizable: True
  @Requestable: True
--%><%
%><%@ page contentType="text/html; charset=UTF-8" %><%
%><%@ include file='/jcore/doInitPage.jspf' %><%

String fieldEnable = getAlphaNumParameter("field", "title");
request.setAttribute("fieldEnable", fieldEnable);

%><jsp:useBean id='formHandler' scope='page' class='generated.EditQuestionZelliHandler'><%
  %><jsp:setProperty name='formHandler' property='request' value='<%= request %>'/><%
  %><jsp:setProperty name='formHandler' property='response' value='<%= response %>'/><%
  %><jsp:setProperty name="formHandler" property="noRedirect" value="true" /><%
  %><jsp:setProperty name='formHandler' property='*' /><%
%></jsp:useBean><%
%><% request.setAttribute("formHandler", formHandler); %><%
%><% if (formHandler.validate()) {
  request.setAttribute("modal.redirect", formHandler.getModalRedirect()); %><%
%><%@ include file="/jcore/modal/modalRedirect.jspf" %><% return; } %><%
%><% formHandler.prepare(); %>
<jalios:modal formHandler="<%= formHandler %>" url="plugins/ZelliPlugin/types/QuestionZelli/editQuestionZelliModal.jsp">
<div class="row QuestionZelli">
  <%-- Workspace -------------------------------------------------------- --%>
<%--  <%@ include file="/plugins/ZelliPlugin/types/QuestionZelli/doWorkspaceField.jspf" %>  --%>
  
	<%-- Title ------------------------------------------------------------ --%>
<%--	<% if (formHandler.isFieldEdition("title")) { %>
	
    <% TypeEntry titleTE = channel.getTypeEntry(formHandler.getPublicationClass()); %>
    <jalios:field name="title" css="focus" formHandler="<%= formHandler %>" disabled='<%= fieldEnable.contains("title") ? false : true %>'>
      <jalios:control />
    </jalios:field>
	<% } %>  --%>
	<jalios:include jsp="plugins/ZelliPlugin/types/QuestionZelli/doEditQuestionZelliModal.jsp" />
</div>
</jalios:modal>
<% 
  TreeSet  removedCatSet = new TreeSet(); 
  Category itRemoveCat = null;
  request.setAttribute("removedCatSet", removedCatSet);
%>
  <%-- **********4A616C696F73204A434D53 *** SIGNATURE BOUNDARY * DO NOT EDIT ANYTHING BELOW THIS LINE *** --%><%
%><%-- yv1TFwfQ9vh/c3vdDDfKRw== --%>