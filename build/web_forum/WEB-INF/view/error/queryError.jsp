<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Error</title>
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/common.css"/>"/>
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/error/styles.css"/>"/>
</head>
<body>

	<%@ include file="/WEB-INF/view/header.jsp" %>

	<%@ include file="/WEB-INF/view/nav.jsp" %>

	<div id="wrapper">	
		<div id="second-header">
			<h2>Ошибка!!!</h2>
		</div>
		
		<div id="second-wrapper">
			<span id="error-message"><c:out value="${error.errorMessage}"/></span>
		</div>
		
	</div>

	<%@ include file="/WEB-INF/view/footer.jsp" %>

</body>
</html>