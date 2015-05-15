<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<%@ page import="java.util.List" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Gmail message search</title>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
  </head>

  <body>
    <h1>Gmail message search</h1>
    
    <%
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    
    if(user != null){
    	pageContext.setAttribute("user", user);
    %>
    <p>Hello, ${fn:escapeXml(user.nickname)}! (You can <a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">sign out</a>.)</p>
    <%
    }
    %>
    
    <p>
		<h1>Search:</h1>
		<form action="search" method="POST">
		<input type="text" name="keyword">
		<input type="submit" value="Search">
		</form>
	</p>
	
	<p>
		<h1>Database content:</h1>
		<div id="db_content"></div>
	</p>
	
	<script type="text/javascript">
		$(document).ready(function(){
			$("#db_content").load("/db");
		});
	</script>
    
  </body>
</html>
