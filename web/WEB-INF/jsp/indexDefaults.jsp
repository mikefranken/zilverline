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
	<li id="current"><a href="<c:url value="indexDefaults.htm"/>"
		title="<fmt:message key="IndexDefaults.hint" />"><fmt:message key="IndexDefaults" /></a>
	</li>
	<li><a href="<c:url value="extractorMappings.htm"/>"
		title="<fmt:message key="ExtractorMappings.hint" />"><fmt:message key="ExtractorMappings" /></a>
	</li>
	<li><a href="<c:url value="handlers.htm"/>"
		title="<fmt:message key="Handlers.hint" />"><fmt:message key="Handlers" /></a>
	</li>
</ul>
<table>
	<tr>
		<td><fmt:message key="IndexBaseDirectory" /></td>
		<spring:bind path="command.indexBaseDir">
		<td><INPUT type="text" name="indexBaseDir" size="45" value="<c:out value="${status.value}"/>"  title="<fmt:message key="IndexBaseDirectory.hint" />"</td>
        <td><span class="error"><c:out value="${status.errorMessage}" /></span></td>
        </spring:bind>
	</tr>
	<tr>
		<td><fmt:message key="CacheBaseDirectory" /></td>
		<spring:bind path="command.cacheBaseDir">
		<td><INPUT type="text" name="cacheBaseDir" size="45" value="<c:out value="${status.value}"/>"  title="<fmt:message key="CacheBaseDirectory.hint" />"</td>
        <td><span class="error"><c:out value="${status.errorMessage}" /></span></td>
        </spring:bind>
	</tr>
	<tr>
		<td><fmt:message key="Priority" /></td>
		<spring:bind path="command.priority">
		<td><INPUT type="text" name="priority" size="45" value="<c:out value="${status.value}"/>"  title="<fmt:message key="Priority.hint" />"</td>
        <td><span class="error"><c:out value="${status.errorMessage}" /></span></td>
        </spring:bind>
	</tr>
	<tr>
		<td><fmt:message key="MergeFactor" /></td>
		<spring:bind path="command.mergeFactor">
		<td><INPUT type="text" name="mergeFactor" size="45" value="<c:out value="${status.value}"/>"  title="<fmt:message key="MergeFactor.hint" />"</td>
        <td><span class="error"><c:out value="${status.errorMessage}" /></span></td>
        </spring:bind>
	</tr>
	<tr>
		<td><fmt:message key="MinMergeDocs" /></td>
		<spring:bind path="command.minMergeDocs">
		<td><INPUT type="text" name="minMergeDocs" size="45" value="<c:out value="${status.value}"/>"  title="<fmt:message key="MinMergeDocs.hint" />"</td>
        <td><span class="error"><c:out value="${status.errorMessage}" /></span></td>
        </spring:bind>
	</tr>
	<tr>
		<td><fmt:message key="MaxMergeDocs" /></td>
		<spring:bind path="command.maxMergeDocs">
		<td><INPUT type="text" name="maxMergeDocs" size="45" value="<c:out value="${status.value}"/>"  title="<fmt:message key="MaxMergeDocs.hint" />"</td>
        <td><span class="error"><c:out value="${status.errorMessage}" /></span></td>
        </spring:bind>
	</tr>
	<tr>
		<td><fmt:message key="Analyzer" /></td>
		<td>
			<SELECT name="analyzer">
				<c:forEach items="${command.allAnalyzers}" var="option">
				<OPTION value="<c:out value="${option}"/>"
					<c:if test="${command.analyzer == option}">
		                selected
            		</c:if>>
		            <c:out value="${option}"/>
			</c:forEach>
			</SELECT>
		</td>
        <td>&nbsp;</td>
	</tr>
</table>
</div>
<br>
<p><INPUT type="submit" value="<fmt:message key="Save" />" />
</FORM>
<%@include file="footer.jsp"%>
