<%@ include file="header.jsp"%>
<script type="text/javascript">

<!--
function getStyleObject(objectId) {
  // checkW3C DOM, then MSIE 4, then NN 4.
  if(document.getElementById && document.getElementById(objectId)) {
	return document.getElementById(objectId).style;
  }
  else if (document.all && document.all(objectId)) {  
	return document.all(objectId).style;
  } 
  else if (document.layers && document.layers[objectId]) { 
	return document.layers[objectId];
  } else {
	return false;
  }
}

function hideAll()
{
   changeObjectVisibility("ez","hidden");
}

function changeObjectVisibility(objectId, newVisibility) {
    // first get the object's stylesheet
    var styleObject = getStyleObject(objectId);
    // then if we find a stylesheet, set its visibility as requested
    if (styleObject) {
		styleObject.display = newVisibility;
		return true;
    } else {
		return false;
    }
}

function switchDiv(div_id)
{
  var style_sheet = getStyleObject(div_id);
  if (style_sheet)
  {
    //hideAll();
    changeObjectVisibility(div_id, "block");
  }
  else 
  {
    alert("sorry, this only works in browsers that do Dynamic HTML");
  }
}
// -->
</script>

<spring:bind path="command">
	<span class="error"><c:out value="${status.errorMessage}" /></span>
</spring:bind>

<FORM method="POST" accept-charset="UTF-8">

<div class="zilverpanel"><c:if test="${command.new}">
	<fmt:message key="New" />
</c:if><fmt:message key="Collection" />
<table>
	<tr>
		<td><fmt:message key="name" /></td>
		<spring:bind path="command.name">
			<td><INPUT type="text" name="name" size="30"
				value="<c:out value="${status.value}"/>" /></td>
			<td><span class="error"><c:out value="${status.errorMessage}" /></span></td>
		</spring:bind>
	</tr>
	<tr>
		<td><fmt:message key="description" /></td>
		<spring:bind path="command.description">
			<td><textarea name="description" /><c:out value="${status.value}" /></textarea></td>
			<td><span class="error"><c:out value="${status.errorMessage}" /></span></td>
		</spring:bind>
	</tr>
	<tr>
		<td><fmt:message key="source" /></td>
		<spring:bind path="command.contentDir">
			<td><INPUT type="text" name="contentDir" size="45"
				title="<fmt:message key="ContentDirectory"/>"
				value="<c:out value="${status.value}"/>" /></td>
			<td><span class="error"><c:out value="${status.errorMessage}" /></span></td>
		</spring:bind>
	</tr>
	<c:if test="${!command.new}">
		<tr>
			<td><fmt:message key="numofdocs" /></td>
			<td><c:out value="${command.numberOfDocs}" /></td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td width="15%"><fmt:message key="lastIndexed" /></td>
			<td><fmt:formatDate type="both" value="${command.lastIndexed}" /></td>
			<td>&nbsp;</td>
		</tr>
	</c:if>
</table>
</div>

<P><c:if test="${command.new}">
	<INPUT type="submit" value="<fmt:message key="Save" />" />
</c:if> <c:if test="${!(command.new)}">
	<div class="zilverpanel"><fmt:message key="LocationInformation" /><span
		class="small" align="right"><input type="radio" value="Edit"
		onclick="switchDiv('magicpanel')" /><fmt:message
		key="collectiondetails" /></span>
	<table>
		<tr>
			<td><fmt:message key="ContentDirectory" /></td>
			<td class="small"><c:out value="${command.contentDir}" /> <fmt:message
				key="mapped" /> <a class="doc" title="<fmt:message key="clicktesthere"/>"
				href="<c:url value="${command.urlDefault}" />"><c:url
				value="${command.urlDefault}" /></a></td>
		</tr>
		<tr>
			<td><fmt:message key="IndexDirectory" /></td>
			<td class="small"><c:out
				value="${command.indexDirWithManagerDefaults}" /></td>
		</tr>
		<tr>
			<td><fmt:message key="Analyzer" /></td>
			<td><SELECT name="analyzer">
				<OPTION value="">-- <fmt:message key="usedefault"/> (<c:out value="${command.manager.analyzer})" />-- 
				<c:forEach items="${command.manager.allAnalyzers}" var="option">
					<OPTION value="<c:out value="${option}"/>"
						<c:if test="${command.analyzer == option}">
			                selected
                		</c:if>>
					<c:out value="${option}" />
				</c:forEach>
			</SELECT></td>
		</tr>
		<tr>
			<td><fmt:message key="CacheDirectory" /></td>
			<td class="small"><c:out
				value="${command.cacheDirWithManagerDefaults}" /> <c:if
				test="${!empty command.cacheUrlWithManagerDefaults}">
				<fmt:message key="mapped" />
				<a class="doc" title="<fmt:message key="clicktesthere"/>"
					href="<c:url value="${command.cacheUrlWithManagerDefaults}" />"><c:url
					value="${command.cacheUrlWithManagerDefaults}" /></a>
			</c:if>&nbsp;</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td class="italic"><c:if test="${!command.keepCache}">
				<fmt:message key="nocacheavailable" />
				<br> <c:if test="${command.existsOnDisk}">
					<fmt:message key="retrievedonthefly" />
					<br> <fmt:message key="flushcachestart" /> <a class="doc" 
						href="<c:url value="/flushCache.htm?collection=${command.name}"/>"><fmt:message
						key="here" /></a> <fmt:message key="flushcacheend" />
				</c:if>
			</c:if></td>
		</tr>
	</table>
	</div>
	<p><INPUT type="submit" value="<fmt:message key="UpdateCollection"/>" />
	<INPUT type="submit" name="delete" value="<fmt:message key="DeleteCollection"/>"
		onclick="return confirm('<fmt:message key="deletecollectionareyousure"/>')" />
</c:if> 
	<INPUT type="submit" name="_cancel" value="<fmt:message key="Cancel"/>"	 />

<div id="magicpanel" style="display:none;">
<div class="zilverpanel" ><fmt:message key="LocationDetails" />
<table>
	<tr>
		<td><fmt:message key="ContentURL"/></td>
		<td><spring:bind path="command.url">
			<INPUT type="text" size="45" name="url" title="<fmt:message key="ContentURL.hint"/>"
				value="<c:out value="${status.value}"/>" />
			<c:if test="${empty status.value}">
				<fmt:message key="Optional"/><fmt:message key="defaultvalue"/> <c:out value="${command.urlDefault}" />
			</c:if>
		</spring:bind></td>
	</tr>

	<tr>
		<td><fmt:message key="IndexDirectory"/></td>
		<td><spring:bind path="command.indexDir">
			<INPUT type="text" size="45" name="indexDir" title="<fmt:message key="IndexDirectory.hint"/>"
				value="<c:out value="${status.value}"/>" />
			<c:if test="${empty status.value}">
				<fmt:message key="Optional"/><fmt:message key="defaultvalue"/> <c:out
					value="${command.indexDirWithManagerDefaults}" />
			</c:if>
		</spring:bind></td>
	</tr>

	<tr>
		<td><fmt:message key="CacheDirectory"/></td>
		<td><spring:bind path="command.cacheDir">
			<INPUT type="text" size="45" name="cacheDir" title="<fmt:message key="CacheDirectory.hint"/>"
				value="<c:out value="${status.value}"/>" />
			<c:if test="${empty status.value}">
				<fmt:message key="Optional"/><fmt:message key="defaultvalue"/> <c:out
					value="${command.cacheDirWithManagerDefaults}" />
			</c:if>
		</spring:bind></td>
	</tr>

	<tr>
		<td><fmt:message key="CacheURL"/></td>
		<td><spring:bind path="command.cacheUrl">
			<INPUT type="text" size="45" name="cacheUrl" title="<fmt:message key="CacheURL.hint"/>"
				value="<c:out value="${status.value}"/>" />
			<c:if test="${empty status.value}">
				<fmt:message key="Optional"/><fmt:message key="defaultvalue"/> <c:out
					value="${command.cacheUrlWithManagerDefaults}" />
			</c:if>
		</spring:bind></td>
	</tr>

	<tr>
		<td><fmt:message key="KeepCache"/></td>
		<td><spring:bind path="command.keepCache">
			<input type="hidden" name="_<%= status.getExpression() %>"
				value="false"> <input type="checkbox"
				name="<%= status.getExpression() %>" value="true" title="<fmt:message key="KeepCache.hint"/>"
				<c:if test="${status.value}">
				checked
			</c:if>>
		</spring:bind></td>
	</tr>
</table>
</div></div>
</FORM>
<%@include file="footer.jsp"%>
