<%@ include file="header.jsp"%>
<spring:bind path="command">
	<span class="error"><c:out value="${status.errorMessage}" /></span>
</spring:bind>

<FORM method="POST">

<div class="zilverpanel">
<ul>
	<li id="current"><a href="<c:url value="searchDefaults.htm"/>"
		title="<fmt:message key="SearchDefaults.hint" />"><fmt:message key="SearchDefaults" /></a>
	</li>
	<li><a href="<c:url value="indexDefaults.htm"/>"
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
		<td><fmt:message key="DefaultQuery" /></td>
		<spring:bind path="command.query">
		<td><INPUT type="text" name="query" size="30" value="<c:out value="${status.value}"/>" title="<fmt:message key="DefaultQuery.hint" />"/></td>
        <td><span class="error"><c:out value="${status.errorMessage}" /></span></td>
        </spring:bind>
	</tr>
	<tr>
		<td><fmt:message key="resultsperpage" /></td>
		<spring:bind path="command.maxResults">
		<td><INPUT type="text" name="maxResults" size="4" value="<c:out value="${status.value}"/>" title="<fmt:message key="resultsperpage.hint" />"/></td>
        <td><span class="error"><c:out value="${status.errorMessage}" /></span></td>
        </spring:bind>
	</tr>
</table>
</div>
<br>
<div class="zilverpanel"><span title="<fmt:message key="BoostingFactors.hint" />"><fmt:message key="BoostingFactors" /></span>
<table>
    <c:set var="i" value="0" />
	<c:forEach items="${command.factors.factors}" var="factor">
	<c:set var="i" value="${i+1}" />
		<tr>
			<td>
				<SELECT name="<c:out value="field_${i}"/>">
					<OPTION value=""><fmt:message key="deletethisboost" />
					<c:forEach items="${command.allBoostableFields}" var="fields">
					<OPTION value="<c:out value="${fields}"/>"
						<c:if test="${factor.key == fields}">
			                selected
                		</c:if>>
			            <c:out value="${fields}"/>
				</c:forEach>
				</SELECT>
			</td>
			<td>
				<input type="text" size="4" name="<c:out value="${i}"/>" value="<c:out value="${factor.value}"/>">
			</td>
		</tr>
	</c:forEach>
	<c:if test="${i < allBoostableFieldsSize}">
	<tr>
		<td>
			<SELECT name="<c:out value="field_${i+1}"/>">
				<OPTION value=""><fmt:message key="createnewboost" />
				<c:forEach items="${command.allBoostableFields}" var="fields">
				<OPTION value="<c:out value="${fields}"/>">
		            <c:out value="${fields}"/>
			</c:forEach>
			</SELECT>
		</td>
		<td>
			<input type="text" size="4" name="<c:out value="${i+1}"/>" value="" title="<fmt:message key="BoostingFactors.hint" />">
		</td>
	</tr>
	</c:if>
</table>
</div>
<p><INPUT type="submit" value="<fmt:message key="Save" />" />
</FORM>
<%@include file="footer.jsp"%>
