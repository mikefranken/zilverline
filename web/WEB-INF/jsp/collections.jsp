<%@ include file="header.jsp"%>
<script type="text/javascript">

<!--
function SetAllCheckBoxes(FieldName)
{
	if(!document.forms[0])
		return;
	var CheckValue = document.forms[0].elements['dummy'].checked;
	var objCheckBoxes = document.forms[0].elements[FieldName];
	if(!objCheckBoxes)
		return;
	var countCheckBoxes = objCheckBoxes.length;
	if(!countCheckBoxes)
		objCheckBoxes.checked = CheckValue;
	else
		// set the check value for all check boxes
		for(var i = 0; i < countCheckBoxes; i++)
			if (!objCheckBoxes[i].disabled)
				objCheckBoxes[i].checked = CheckValue;
}
// -->
</script>

<form method="post" name="collections"
	action="<c:url value="/createindex.htm"/>">
<div class="zilverpanel"><fmt:message key="Collections" />
<table>
	<tr class="header" valign="top">
		<td width="15%"><fmt:message key="name" /></td>
		<td width="45%"><fmt:message key="description" /></td>
		<td width="15%"><fmt:message key="source" /></td>
		<td width="5%"><fmt:message key="numofdocs" /></td>
		<td width="15%"><fmt:message key="lastIndexed" /></td>
		<td width="5%"><input type="checkbox" name="dummy"
			title="<fmt:message key="checkall"/>" value="dummy"
			onclick="SetAllCheckBoxes('names');"></td>
	</tr>
	<c:forEach items="${collections}" var="col">
		<tr valign="top">
			<td><a class="pager" title="<fmt:message key="tocollection"/>"	
						href="<c:url value="collectiondetails.htm">
								<c:param name="collectionId" value="${col.id}"/>
							  </c:url>"	>
						<c:out value="${col.name}" /> 
					</a>
			</td>
			<td><c:out value="${col.description}" />&nbsp;</td>
			<td><c:out value="${col.root}" /></td>
			<td align="right">
				<c:out value="${col.numberOfDocs}" />
				<c:if test="${col.indexingInProgress}">
					<img src='images/indexing.gif' align='absbottom' title="indexing..." border=0>
				</c:if>
			</td>
			<td align="right"><fmt:formatDate type="both"
				value="${col.lastIndexed}" /></td>
			<td><input type="checkbox" name="names"
				title="<fmt:message key="check"/>"
				value="<c:out value="${col.name}"/>"
				<c:if test="${!col.existsOnDisk}"> DISABLED</c:if> /></td>
		</tr>
	</c:forEach>
</table>
</div>
<p>
<a class="pager" href="<c:url value="/addCollection.htm"/>"><fmt:message key="AddCollection"/></a>
<p>
<a class="pager" href="<c:url value="/addIMAPCollection.htm"/>"><fmt:message key="AddIMAPCollection"/></a>
<p>

	<input type="submit" alignment="center"	value="<fmt:message key="createindex"/>"> 
	<input type="submit" name="_cancel" alignment="center"	value="<fmt:message key="Cancel"/>"> 
	
	<input type="checkbox"	name="fullIndex" value="true" title="<fmt:message key="reindexcheckbox"/>" />
	<fmt:message key="reindex" />
</form>
<%@include file="footer.jsp"%>
