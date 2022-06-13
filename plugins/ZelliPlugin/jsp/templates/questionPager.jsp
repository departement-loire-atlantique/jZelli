<%--
  @Summary: Default template used by the pager tag
--%><%
%><%@ include file='/jcore/doInitPage.jspf' %><%
%><%@ include file='/jcore/pager/pagerTemplateInit.jspf' %><%

boolean hasCtxMenu = pageSizes.length > 1 || (handler.getItemsNbr() > pageSizes[0] && handler.canShowPagerAll());
int ctxMenuCounter = 0;
if (hasCtxMenu) {
  ctxMenuCounter = (Util.toInt(request.getSession().getAttribute("pagerCtxMenuCounter"),0)+1) % 20;
  request.getSession().setAttribute("pagerCtxMenuCounter",""+ctxMenuCounter);
  request.setAttribute("pagerCtxMenuCounter",""+ctxMenuCounter);
}

boolean hasContent = handler.getPagesNbr() != 1 || hasCtxMenu;

if (hasContent) { %>
<div class="txtcenter center ds44--xl-padding-b ds44-js-search-pager">
<jalios:select>
	<jalios:if predicate="<%= handler.getPageSize() > handler.getItemsNbr() %>">
	    <p class="idNbResults"><%= handler.getItemsNbr() %> questions affichées sur <%= handler.getItemsNbr() %></p>
	</jalios:if>
	<jalios:default>
        <p class="idNbResults"><%= handler.getPageSize() %> questions affichées sur <%= handler.getItemsNbr() %></p>
        <p><a   href="<%=  handler.getShowPageSizeURL(handler.getPageSize()+5) %>" 
                title="<%= glp("ui.pager.all.long") %>"
                class="ds44-btnStd ds44-btn--invert ds44-js-search-button"
                data-jalios-ajax-refresh="nohistory noscroll nofocus">
           <span class="ds44-btnInnerText">Plus de résultats</span><i class="icon icon-plus" aria-hidden="true"></i></a>
        </p>	
	</jalios:default>
</jalios:select>
</div>
<%
}
handler.clearSessionParams();
%>