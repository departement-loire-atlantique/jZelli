<%@ include file='/jcore/doInitPage.jspf' %>
<%-- TODO Sup socle for 10sp5 + --%>
  <div id="ajaxWaitIconWrapper" class="hide">
    <jalios:icon src="ajax-wait" htmlAttributes="id=\"ajaxWaitIcon\"" />
  </div>
  <div class="handlebar-templates hide">
    <script id="jalios-loading-wave" type="text/x-handlebars-template">
      <jalios:icon src="wait-inline" />
    </script>

    <%= jcmsContext.getHandlebarTemplates() %>
  </div> 
<%-- END TODO --%>