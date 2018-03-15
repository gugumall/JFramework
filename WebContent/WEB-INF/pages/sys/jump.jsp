<%@ page contentType="text/html; charset=UTF-8" %>
<%
String content=(String)request.getAttribute(j.app.Constants.J_PAGE_CONTENT);
out.print(content);
request.removeAttribute(j.app.Constants.J_PAGE_CONTENT);
%>