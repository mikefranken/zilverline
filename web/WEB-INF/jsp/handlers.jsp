<%@ include file="header.jsp"%>
<spring:bind path="command">
	<span class="error"><c:out value="${status.errorMessage}" /></span>
</spring:bind>

<FORM method="POST">

<div class="zilverpanel">
<ul>
	<li><a href="<c:url value="searchDefaults.htm"/>"
		title="<fmt:message key="SearchDefaults.hint" />"><fmt:message key="SearchDefaults" /></a>
	</li>
	<li><a href="<c:url value="indexDefaults.htm"/>"
		title="<fmt:message key="IndexDefaults.hint" />"><fmt:message key="IndexDefaults" /></a>
	</li>
	<li><a href="<c:url value="extractorMappings.htm"/>"
		title="<fmt:message key="ExtractorMappings.hint" />"><fmt:message key="ExtractorMappings" /></a>
	</li>
	<li id="current"><a href="<c:url value="handlers.htm"/>"
		title="<fmt:message key="Handlers.hint" />"><fmt:message key="Handlers" /></a>
	</li>
</ul>

<input type="hidden" name="prefix" value="select_"></td>
<table>
    <c:set var="i" value="1" />
	<c:forEach items="${command.mappings}" var="mapping">
	<c:set var="i" value="${i+1}" />
		<tr>
			<td>
				<input type="text" size="6" name="<c:out value="${i}"/>" value="<c:out value="${mapping.key}"/>">
			</td>
			<td>
				<input type="text" size="45" name="<c:out value="select_${i}"/>" value="<c:out value="${mapping.value}"/>">
				<c:if test="${empty mapping.value}"><fmt:message key="Builtin.zip" /></c:if>
			</td>
		</tr>
	</c:forEach>
		<tr>
			<td><input type="text" size="6" name="<c:out value="${i+1}"/>" value="" title="<fmt:message key="ext.hint" />">
			</td>
			<td>
				<input type="text" size="45" name="<c:out value="select_${i+1}"/>" value="<c:out value="${mapping.value}"/>"  title="<fmt:message key="archive.hint" />">
			</td>
		</tr>
</table>
<input type="checkbox" name="casesensitive" 
						<c:if test="${command.caseSensitive}">
			                checked
                		</c:if>><fmt:message key="CaseSensitive" />
</div>
<p><INPUT type="submit" value="<fmt:message key="Save" />" />
</FORM>
<%@include file="footer.jsp"%>
