<%@ include file="header.jsp" %>
<H2><fmt:message key="error" /></H2>
<center>
  <table class="border" width="90%">
	<tr>
		<td>
			 Can not extract contents from cache <c:out value="${document.archive}"/>.
			 The cache is not available on server. This is possibly a misconfiguration.
			 In order to retrieve  <c:out value="the file '${document.file}' from  the '${document.collection}' collection's archive ${document.archive}"/>
			 The '${document.collection}' collection's content has to be available on the Zilverline server.
		</td>
	</tr>
</table>
</center>
  <br>
<%@include file="footer.jsp"%>
