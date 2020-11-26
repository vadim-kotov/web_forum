<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div id="nav">
	<ul>
		<li><a href="<c:url value="/forum.do"/>">Форум</a></li
		><li><a href="<c:url value="/users/users_list.do"/>">Пользователи</a></li
		><li><a href="<c:url value="/users/account.do"/>">Личный кабинет</a></li>
	</ul>
	<div id="auth">
		<sec:authorize access="!isAuthenticated()">
			<a href="/web_forum/users/login.do">Вход</a><a href="/web_forum/users/registration.do">Регистрация</a>
		</sec:authorize>
		<sec:authorize access="isAuthenticated()">
			<span>Добро пожаловать, <sec:authentication property="principal.username" htmlEscape="false"/></span><a href="#" onclick="document.getElementById('log-out').submit();">Выход</a>
			<form id="log-out" action="<c:url value="/users/logout.do"/>" method="POST">
			    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
			</form>
		</sec:authorize>
	</div>
</div>