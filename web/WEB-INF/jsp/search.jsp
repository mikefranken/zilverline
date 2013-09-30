<%@ include file="header.jsp"%>
<form action="<c:url value="/search.htm"/>" method="get" accept-charset="utf-8">
<div class="zilverpanel"><fmt:message key="search" />
<TABLE>
	<TR>
		<spring:bind path="command.query">
		<td><input name="<c:out value="${status.expression}"/>" size="66" value="<c:out value="${status.value}"/>" /> 
		</spring:bind>
			&nbsp; 
		<input type="submit" name="Search" value="<fmt:message key="search"/>" /></td>
		
		<spring:bind path="command.maxResults">
		<td align="right"><input name="maxResults" size="4"
			value="<c:out value="${status.value}"/>" /> &nbsp; <fmt:message	key="resultsperpage" /> &nbsp; 
		</spring:bind>
			
		<input type="hidden" name="startAt"	value="1" /></td>
	</TR>
	<tr>
		<td align="right" colspan="2">
		<div id="cols">
		<ul>
			<c:forEach items="${command.collections}" var="col" varStatus="loopStatus">
				<spring:bind path="command.collections[${loopStatus.index}].selected">
				<li>
        			<input type="checkbox" name="collections" value="<c:out value="${col.name}"/>" <c:if test="${status.value}">checked</c:if>/>
				
					<authz:authorize ifAllGranted="ROLE_INDEXER">				
					<a class="nolink" title="<fmt:message key="tocollection"/>"	
						href="<c:url value="collectiondetails.htm">
								<c:param name="collectionId" value="${col.id}"/>
							  </c:url>"	>
					</authz:authorize>
						<c:out value="${col.name}" /> 
					<authz:authorize ifAllGranted="ROLE_INDEXER">				
					</a>
					</authz:authorize>
				</li>
				</spring:bind>
			</c:forEach>
		</ul>
		</div>
		</td>
		</TR>
</TABLE>
</div>
<div><spring:bind path="command.*">
	<c:forEach var="error" items="${status.errorMessages}">
		<p>
		<div class="error"><c:out value="${error}" /></div>
	</c:forEach>
</spring:bind></div>
</form>
<c:if test="${empty results}">
	<!-- only if formsubmission	<p><div class="error"><fmt:message key="error.noresults" /></div> -->
</c:if>
<c:if test="${!empty results}">
	<div class="zilverpanel">
	<table>
		<th align="left" colspan="3"><fmt:message key="search" /> <fmt:message
			key="results" /></th>
		<th align="right"><c:out value="${command.startAt}" /> <fmt:message
			key="to" /> <c:out value="${endAt}" /> <fmt:message key="of" /> <c:out
			value="${hits}" /> <fmt:message key="results" />&nbsp;&nbsp; 
			<!-- if there are more results to display ... -->
		<!-- construct and output the First Page, Previous, Next links --> 
		<c:if test="${command.startAt > 1}">
			<c:url var="first" value="/search.htm">
				<c:param name="Search" value="Search" />
				<c:param name="startAt" value="1" />
				<c:param name="query" value="${command.query}" />
				<c:param name="maxResults" value="${command.maxResults}" />
			</c:url>
			<a class="pager" href='<c:out value="${first}"/>'><fmt:message
				key="firstpage" /></a>
			&nbsp;|

			<c:url var="previous" value="/search.htm">
				<c:param name="Search" value="Search" />
				<c:param name="startAt"	value="${command.startAt-command.maxResults}" />
				<c:param name="query" value="${command.query}" />
				<c:param name="maxResults" value="${command.maxResults}" />
			</c:url>
	        <a class="pager" href='<c:out value="${previous}"/>'> <fmt:message
				key="previous" /> <c:out value="${command.maxResults}" /></a>
		</c:if> 
		<c:if test="${endAt < hits}">
			<c:if test="${command.startAt > 1}"> &nbsp;| </c:if>

			<c:url var="next" value="/search.htm">
				<c:param name="Search" value="Search" />
				<c:param name="startAt"	value="${command.maxResults+command.startAt}" />
				<c:param name="query" value="${command.query}" />
				<c:param name="maxResults" value="${command.maxResults}" />
			</c:url>
			<c:set var="leftover" value="${command.maxResults}" />
			<c:if test="${hits - command.startAt - command.maxResults <  command.maxResults}">
				<c:set var="leftover" value="${hits - command.startAt - command.maxResults + 1}" />
			</c:if>
			<a class="pager" href='<c:out value="${next}"/>'><fmt:message
				key="next" /> <c:out value="${leftover}" /></a>
		</c:if>
		</th>

		<tr class="header" valign="top">
			<td width="2%">&nbsp;</td>
			<td width="4%">&nbsp;</td>
			<td width="4%"><fmt:message key="score" /></td>
			<td><fmt:message key="title" /></td>
		</tr>
		<c:set var="i" value="${command.startAt-1}" />
		<c:forEach items="${results}" var="res">
			<tr>
				<td colspan="4" class="line"></td>
			</tr>
			<c:set var="i" value="${i+1}" />
			<tr>
				<td valign="right"><c:out value="${i}." /></td>
				<td><img src='images/<c:out value="${res.type}"/>.gif' width=16
					height=16 align='absbottom' title="<c:out value="${res.type}"/>"
					border=0>
					<c:if test="${!empty res.ISBN}">
						<a class="small" title="<fmt:message key="lookatbook.hint" />"
						href="<c:url value="http://www.amazon.com/exec/obidos/ASIN/${res.ISBN}/zilverline-20/"/>">
						<fmt:message key="lookatbook" />
						</a>
					</c:if>
				</td>
				<td class="small"><c:out value="${res.scoreString}" /></td>
				<td><c:if test="${!empty res.zipPath}">
					<c:if test="${!empty res.cache}">
						<a class="doc"
							href="<c:url value="${res.cache}${res.path}${res.zipPath}${res.zipName}"/>">
					</c:if>
					<c:if test="${empty res.cache}">
						<a class="doc" title="<fmt:message key="extractfromarchive"/>"
							href="<c:url value="getfromcache.htm?collection=${res.collection}&archive=${res.path}${res.name}&file=${res.path}${res.zipPath}${res.zipName}"/>">
					</c:if>
					<c:out value="${res.title}" escapeXml="false" />
					</a>
					&nbsp;in&nbsp;
					<a class="doc"
						href="<c:url value="${res.URL}"/><c:out value="${res.path}"/><c:out value="${res.name}"/>">
					<c:out value="${res.name}" escapeXml="false" /> </a>
				</c:if> <c:if test="${empty res.zipPath}">
					<a class="doc"
						href="<c:url value="${res.URL}"/><c:out value="${res.path}"/><c:out value="${res.name}" escapeXml="false"/>">
					<c:out value="${res.title}" escapeXml="false" /> </a>
				</c:if> <fmt:message key="fromcollection" /> <c:out
					value="${res.collection}" /></td>
			</tr>
			<tr>
				<td width="2%">&nbsp;</td>
				<td class="small" width="2%"><fmt:formatDate type="date" value="${res.modificationDate}" /></td>
				<td class="small"><c:out value="${res.sizeAsString}" /></td>
				<td class="italic"><fmt:message key="summary" />: ...<c:out
					value="${res.summary}" escapeXml="false" /></td>
			</tr>
		</c:forEach>
	</table></div>
	
	<br>
</c:if>
<%@ include file="footer.jsp"%>
