<%@ include file="/WEB-INF/jsp/include.jsp"%>
<html>
<head>
<meta HTTP-EQUIV="content-type" CONTENT="text/html; charset=UTF-8"><c:set
	var="css">
	<spring:theme code="css" />
</c:set> 
<c:if test="${not empty css}">
	<link rel="stylesheet" href="<c:url value="${css}"/>" type="text/css" />
</c:if>
<title><fmt:message key="heading" /></title>
</head>
<body>
<table id="header">
	<tr>
		<td><img src="<c:url value="/images/ZilverlineSearchEngine.gif"/>"></td>
		<td align="right">
		<table>
			<tr class="menuitem">
				<td align="right"><a href="<c:url value="/search.htm"/>"
					title="<fmt:message key="resetquery"/>"><fmt:message key="search" /></a></td>
				<authz:authorize ifAllGranted="ROLE_INDEXER">				
				<td><a href="<c:url value="/collections.htm"/>"
					title="<fmt:message key="tocollections"/>"><fmt:message
					key="Collections" /></a></td>
				<td><a href="<c:url value="/upload.htm"/>"
					title="<fmt:message key="upload.hint"/>"><fmt:message
					key="Upload" /></a></td>
				</authz:authorize>
				<authz:authorize ifAllGranted="ROLE_MANAGER">				
				<td><a href="<c:url value="/searchDefaults.htm"/>"
					title="<fmt:message key="topreferences"/>"><fmt:message
					key="Preferences" /></a></td>
				<td><a href="<c:url value="/admin/log4j.htm"/>"
					title="set loglevels">Set loglevels</a></td>
				</authz:authorize>
				<authz:authorize ifNotGranted="ROLE_USER">				
				<td><a href="<c:url value="/acegilogin.jsp"/>"
					title="log on">Log on</a></td>
				</authz:authorize>
				<authz:authorize ifAnyGranted="ROLE_USER">				
				<td><a href="<c:url value="/logoff.jsp"/>"
					title="log off">Log off <authz:authentication operation="username"/></a></td>
				</authz:authorize>
			</tr>
		</table>
		</td>
	</tr>
</table>