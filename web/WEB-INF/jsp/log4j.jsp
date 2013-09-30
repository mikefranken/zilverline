<%@ include file="/WEB-INF/jsp/header.jsp"%>
<%@ page language="java" import="org.apache.log4j.*,java.util.*"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML>
<HEAD>
<TITLE>Log4J</TITLE>
</HEAD>

<BODY>

<div class="zilverpanel">
<H3>Log4J Runtime Configuration</H3>

<%// do we have a name to start with
            String logger = "";
            if (request.getParameter("logger") != null && !((String) request.getParameter("logger")).equals("")) {
                logger = (String) request.getParameter("logger");
            }

            // are we editing?
            if (request.getParameter("action") != null && ((String) request.getParameter("action")).equals("save")) {
                // Creates a new logger if it doesn't exist
                Category cat = Category.getInstance(logger);
                // level
                String level = (String) request.getParameter("level");
                if (level.equals(""))
                    cat.setLevel(null);
                else
                    cat.setLevel(Level.toLevel(level));
                // additivity
                String additivity = (String) request.getParameter("additivity");
                cat.setAdditivity(additivity.equals("false") ? false : true);
            }

            // get sorted list of all logger names
            TreeSet names = new TreeSet();
            for (Enumeration e = LogManager.getCurrentLoggers(); e.hasMoreElements();) {
                Logger cat = (Logger) e.nextElement();
                names.add(cat.getName());
            }

            %>

<FORM>
Select a currently configured logger
<br>
<SELECT NAME="logger" SIZE=20 onChange="this.form.submit()">
	<%for (Iterator i = names.iterator(); i.hasNext();) {
                String option = (String) i.next();
                if (option.equals(logger)) {
%>
	<OPTION SELECTED><%=option%> <%} else {
%>
	<OPTION><%=option%> <%}
            }
%>
</SELECT></FORM>


<FORM>
Or enter a logger name to configure a new one
<br>
<INPUT NAME="logger" value="<%= logger %>" SIZE=60> <INPUT
	TYPE="submit" VALUE="Submit"></FORM>

<%// display logger info
            if (!logger.equals("")) {
                // Creates a new logger if it doesn't exist
                Logger log = Logger.getLogger(logger);
                String level = "";
                String chained = "none";
                String additivity = "none";
                String appender = "none";
                if (logger != null) {
                    level = log.getLevel() != null ? log.getLevel().toString() : "none";
                    chained = log.getEffectiveLevel() != null ? log.getEffectiveLevel().toString() : "none";
                    additivity = log.getAdditivity() + "";
                    while (log.getAllAppenders() != null && log.getAllAppenders().hasMoreElements()) {
                        Appender app = (Appender) log.getAllAppenders().nextElement();
                        appender = appender + ", " + app.getName();
                    }
                }
%>
<FORM><INPUT TYPE="hidden" NAME="action" VALUE="save">
</div>
<p>

<div class="zilverpanel">
<%=logger%>
<TABLE CELLPADDING=3 CELLSPACING=0 BORDER=1>
	<TR>
		<TD ALIGN="right"><B>Name</B></TD>
		<TD><INPUT NAME="logger" VALUE="<%= logger %>" SIZE=60></TD>
	</TR>
	<TR>
		<TD ALIGN="right"><B>Level</B></TD>
		<TD><SELECT NAME="level">
			<OPTION VALUE="">none <%Priority[] parray = Level.getAllPossiblePriorities();
                for (int i = 0; i < parray.length; i++) {
                    if (level.equals(parray[i].toString())) {
%>
			<OPTION SELECTED><%=parray[i]%> <%} else {
%>
			<OPTION><%=parray[i]%> <%}
                }
%>
		</SELECT></TD>
	</TR>
	<TR>
		<TD ALIGN="right"><B>Chained Level</B></TD>
		<TD><%=chained%></TD>
	</TR>
	<TR>
		<TD ALIGN="right"><B>Appenders</B></TD>
		<TD><%=appender%></TD>
	</TR>
	<TR>
		<TD ALIGN="right"><B>Additivity</B></TD>
		<TD><%if (additivity.equals("false")) {

                %> <INPUT NAME="additivity" TYPE="radio" VALUE="true">true
		<INPUT NAME="additivity" TYPE="radio" VALUE="false" CHECKED>false <%} else {

                %> <INPUT NAME="additivity" TYPE="radio" VALUE="true"
			CHECKED>true <INPUT NAME="additivity" TYPE="radio" VALUE="false">false
		<%}

            %></TD>
	</TR>
	<TR>
		<TD ALIGN="right">&nbsp;</TD>
		<TD><INPUT TYPE="submit" VALUE="Save Changes"></TD>
	</TR>
</TABLE>
</div>
<%}

        %> <%@ include file="/WEB-INF/jsp/footer.jsp"%>