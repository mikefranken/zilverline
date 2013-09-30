<%@ page isErrorPage="true"  %>
<%@ include file="/WEB-INF/jsp/header.jsp" %>

<H2><fmt:message key="error" /></H2>
<fmt:message key="errormessage" />
<P><%try {
    // The Servlet spec guarantees this attribute will be available
    exception = (Exception) request.getAttribute("javax.servlet.error.exception");
    Throwable cause = null;

    if (exception != null) {
        if (exception instanceof ServletException) {
            // It's a ServletException: we should extract the root cause
            ServletException sex = (ServletException) exception;
            Throwable rootCause = sex.getRootCause();
            if (rootCause == null) {
                rootCause = sex;
            }
            String message = rootCause.getMessage();
            if (message == null) {
                message = "unknown";
            }
            out.println("<div class=\"error\">Root cause is: " + rootCause.getMessage() + "</div><p>");

            cause = rootCause.getCause();
            //rootCause.printStackTrace(new java.io.PrintWriter(out)); 
        } else {
            // It's not a ServletException, so we'll just show it
            cause = exception;
        }
        while (cause != null) {
            out.println("<div class=\"error\">Cause is: " + cause.getMessage() + "</div><p>");
            cause = cause.getCause();
        }
    } else {
        out.println("<div class=\"error\">No error information available</div><p>");
    }

} catch (Exception ex) {
    ex.printStackTrace(new java.io.PrintWriter(out));
}
%>

<P><BR>


<%@ include file="/WEB-INF/jsp/footer.jsp" %>