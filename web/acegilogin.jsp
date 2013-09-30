<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page session="true"%>
<%@ page isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html"%>
<%@ taglib prefix="app" tagdir="/WEB-INF/tags/app"%>

<%@ page import="net.sf.acegisecurity.ui.AbstractProcessingFilter" %>
<%@ page import="net.sf.acegisecurity.ui.webapp.AuthenticationProcessingFilter" %>
<%@ page import="net.sf.acegisecurity.AuthenticationException" %>

<html>
<head>
<meta HTTP-EQUIV="content-type" CONTENT="text/html; charset=UTF-8">
	<link rel="stylesheet" href="<c:url value="/themes/ns/main.css"/>" type="text/css" />
<title>login</title>
</head>

  <body>
    <form action="<c:url value='j_acegi_security_check'/>" method="POST">
	<table align="center" cellpadding="4" cellspacing="0" border="0" class="loginform">
	<th colspan="2">login to Zilverline</th>
        <tr><td>User:</td><td><input type='text' name='j_username' <c:if test="${not empty param.login_error}">value='<%= session.getAttribute(AuthenticationProcessingFilter.ACEGI_SECURITY_LAST_USERNAME_KEY) %>'</c:if>></td></tr>
        <tr><td>Password:</td><td><input type='password' name='j_password'></td></tr>
        <tr><td><input type="checkbox" name="_acegi_security_remember_me"></td><td>Don't ask for my password for two weeks</td></tr>

        <tr><td colspan='2' align="center"><input name="submit" type="submit" value="login"></td></tr>
      </table>
    </form>

    <%-- this form-login-page form is also used as the 
         form-error-page to ask for a login again.
         --%>
    <c:if test="${not empty param.login_error}">
      <span class="error">
        Your login attempt was not successful, try again.<BR><BR>
        Reason: <%= ((AuthenticationException) session.getAttribute(AbstractProcessingFilter.ACEGI_SECURITY_LAST_EXCEPTION_KEY)).getMessage() %>
      </span>
    </c:if>

  </body>
</html>
</body></html>