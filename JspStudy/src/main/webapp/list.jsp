<%@page import="java.net.URLEncoder" %>
<%@page import="java.net.URLDecoder" %>
<%-- <%@page import="com.util.MyUtil" %> --%>
<%@page import="com.board.BoardDTO" %>
<%@page import="java.util.List" %>
<%@page import="com.board.BoardDAO" %>
<%-- <%@page import="com.util.DBConn" %> --%>
<%@page import="java.sql.Connection" %>
<%@page contentType="text/html; charset=UTF-8" %>
<%
	request.setCharacterEncoding("UTF-8");
	String cp = request.getContextPath();
	
	/* Connection conn = DBConn.getConnection(); */
	//import 주석 지우면 가능
	BoardDAO dao = new BoardDAO(conn);
	
	/* MyUtil myUtil = new MyUtil(); */
	//import 주석 지우면 가능
	
	


%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>list page</title>
</head>
<body>

</body>
</html>