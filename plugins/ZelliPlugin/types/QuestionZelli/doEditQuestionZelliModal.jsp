<%-- This file has been automatically generated. --%>
<%--
  @Summary: QuestionZelli modal content editor
  @Category: Generated
  @Author: JCMS Type Processor
  @Customizable: True
  @Requestable: False
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file='/jcore/doInitPage.jspf' %>
<%
  EditQuestionZelliHandler formHandler = (EditQuestionZelliHandler)request.getAttribute("formHandler");
  ServletUtil.backupAttribute(pageContext, "classBeingProcessed");
  request.setAttribute("classBeingProcessed", generated.QuestionZelli.class);
%>
<%-- Pseudo ------------------------------------------------------------ --%>
<jalios:field label="Qui">
  <input  type="text"
            name="author"
            value="<%= formHandler.getPublication().getAuthor() %>"
            class="form-control control-textfield form-control-value"
            disabled />
</jalios:field>
<%-- Question ------------------------------------------------------------ --%>
<jalios:field name="question" formHandler="<%= formHandler %>" disabled='<%= request.getAttribute("fieldEnable").toString().contains("question") ? false : true %>'>
  <jalios:control />
</jalios:field>
<%-- Date de la question -------------------------------------------------- --%>
<jalios:field label="Date de la question"> 
    <input  type="text"
            name="cdate"
            value="<jalios:date date="<%= formHandler.getPublication().getCdate() %>"
            format="dd/MM/YYYY' 'HH:mm"/>"
            class="form-control control-textfield form-control-value"
            disabled />
</jalios:field>
<%-- Referent ------------------------------------------------------------ --%>
<jalios:field name="referent" formHandler="<%= formHandler %>" disabled='<%= request.getAttribute("fieldEnable").toString().contains("referent") ? false : true %>'>
  <jalios:control />
</jalios:field>
<%-- Reponse ------------------------------------------------------------ --%>
<jalios:field name="reponse" formHandler="<%= formHandler %>" disabled='<%= request.getAttribute("fieldEnable").toString().contains("reponse") ? false : true %>'>
  <jalios:control type="<%= ControlType.TEXTAREA  %>" />
</jalios:field>
<%-- DateDeLaReponse ------------------------------------------------------------ --%>
<jalios:field name="dateDeLaReponse" formHandler="<%= formHandler %>" disabled='<%= request.getAttribute("fieldEnable").toString().contains("dateDeLaReponse") ? false : true %>'>
  <jalios:control />
</jalios:field>
<%-- Remarque ------------------------------------------------------------ --%>
<jalios:field name="remarque" formHandler="<%= formHandler %>" disabled='<%= request.getAttribute("fieldEnable").toString().contains("remarque") ? false : true %>'>
  <jalios:control type="<%= ControlType.TEXTAREA  %>" />
</jalios:field>
<% if (formHandler.getPublication() != null) { %>
<input type="hidden" name="id" value="<%= formHandler.getPublication().getId() %>" />
<% } %>
<jalios:include target="EDIT_PUB_MAINTAB" targetContext="div" />
<jalios:include jsp="/jcore/doEditExtraData.jsp" />
<% ServletUtil.restoreAttribute(pageContext , "classBeingProcessed"); %><%-- **********4A616C696F73204A434D53 *** SIGNATURE BOUNDARY * DO NOT EDIT ANYTHING BELOW THIS LINE *** --%><%
%><%-- 0+l83Khsmv2McnwALJq/AQ== --%>