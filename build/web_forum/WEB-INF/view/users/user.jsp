<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>User</title>
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/common.css"/>"/>
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/users/user/styles.css"/>"/>
</head>
<body>

	<%@ include file="/WEB-INF/view/header.jsp" %>

    <%@ include file="/WEB-INF/view/nav.jsp" %>
    
    <div id="wrapper">
        <div id="second-header">
            <h2>Личный кабинет</h2>
        </div>
        <div id="second-wrapper" class="clearfix">
            <div id="avatar" >
            	<c:if test="${not empty user.avatar}">
                	<img src="<c:url value="/img/${user.avatar}"/>" alt="avatar"/>
                </c:if>
                <c:if test="${empty user.avatar}">
                	<img src="<c:url value="/resources/forum/avatar.png"/>" alt="avatar"/>
                </c:if>
            </div>
            <div id="info" class="third-wrapper">
                <div id="login">
                    <span class="first-line"><c:out value="${user.login}"/></span><br/>
                </div>
                <span class="second-line">Дата регистрации: </span><time class="first-line" datetime=""><c:out value="${user.registDate}"/></time><br/>
                <span class="second-line">Статус: </span>
                <c:if test="${not empty user.rights}">
                	<span class="first-line">Администратор</span>
                </c:if>
                <c:if test="${empty user.rights}">
                	<span class="first-line">Пользователь</span>
                </c:if>
            </div>
        </div>
    </div>
    
    <%@ include file="/WEB-INF/view/footer.jsp" %>
    
</body>