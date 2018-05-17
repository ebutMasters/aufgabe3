<%@ page session="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
<title>eBusiness Framework Demo - Login</title>
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="pragma" content="no-cache">
<link rel="stylesheet" type="text/css" href="default.css">
</head>
<body>

<%@ include file="header.jsp" %>
<%@ include file="error.jsp" %>

<h1>Import Productkatalog</h1>



 
  <h3> Choose File to Upload in Server </h3>

            <form action="<%= response.encodeURL("controllerservlet?action=saveImportedProducts") %>" method="post" enctype="multipart/form-data">

                <input type="file" name="file" />

                <input type="submit" value="upload" />

            </form>         
 








<!-- <form name="loginForm" method="post" action="<%= response.encodeURL("controllerservlet") %>" >

<b>Internal user:</b><br>
Username: <a href="<%= response.encodeURL("controllerservlet?action=login&role=1&username=internal1&password=internal1") %>"><i>internal1</i></a> and password: <i>internal1</i><br>
<br>
<b>Customer:</b><br>
Username: <a href="<%= response.encodeURL("controllerservlet?action=login&role=2&username=customer1&password=customer1") %>"><i>customer1</i></a> and password: <i>customer1</i><br>
Username: <a href="<%= response.encodeURL("controllerservlet?action=login&role=2&username=customer2&password=customer2") %>"><i>customer2</i></a> and password: <i>customer2</i><br>
<br>
<b>Supplier:</b><br>
Username: <a href="<%= response.encodeURL("controllerservlet?action=login&role=3&username=supplier1&password=supplier1") %>"><i>supplier1</i></a> and password: <i>supplier1</i><br>
Username: <a href="<%= response.encodeURL("controllerservlet?action=login&role=3&username=supplier2&password=supplier2") %>"><i>supplier2</i></a> and password: <i>supplier2</i><br>

</form>-->
</body>
</html>
