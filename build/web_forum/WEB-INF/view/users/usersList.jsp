<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ include file="../includes.jsp" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Пользователи</title>
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/common.css"/>"/> 
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/users/usersList/styles.css"/>"/>
</head>
<body>

	<%@ include file="/WEB-INF/view/header.jsp" %>
	
	<%@ include file="/WEB-INF/view/nav.jsp" %>
	
	<div id="wrapper">
        <div id="second-header">
            <h2><c:out value="${section.name}"/></h2>
        </div>
        <c:if test="${empty section.sectionId}">
            <c:set var="formAction" value="/web_forum/users/users_list/section_${section.sectionId}.do"/> 
        </c:if> 
        <c:if test="${not empty section.sectionId}>">
            <c:set var="formAction" value="/web_forum/users/users_list.do"/>
        </c:if>
        <div id="main">
            <div id="second-wrapper">
                <h3 id="filter-header">Фильтры:</h3>
                <form:form modelAttriute="filter" id="filters" class="third-wrapper" method="GET" action="formAction">
                    <div class="filter-div">
                        <p class="second-line">Период активности:</p>
                        <label class="second-line" for="startDate">С:</label>
                        <form:input path="startDate" class="textinput" type="date" name="startDate"/>
                        <label class="second-line" for="endDate">По:</label>
                        <form:input path="endDate" class="textinput" type="date" name="endDate"/>
                    </div>
                    <div class="filter-div">
                        <p class="second-line">Количество сообщений:</p>
                        <label class="second-line" for="minMessageNum">От:</label>
                        <form:input path="minMessageNum" class="textinput" type="number" name="minMessageNum"/>
                        <label class="second-line" for="maxMessageNum">До:</label>
                        <form:input path="maxMessageNum" class="textinput" type="number" name="maxMessageNum"/>
                    </div>
                    <div class="filter-div"> 
                        <p class="second-line">Дата регистрации:</p>
                        <label class="second-line" for="startRegistDate">С:</label>
                        <form:input path="startRegistDate" class="textinput" type="date" name="startRegistDate"/>
                        <label class="second-line" for="endRegistDate">По:</label>
                        <form:input path="endRegistDate" class="textinput" type="date" name="endRegistDate"/>
                    </div>
                    <input type="hidden" name="filterEnabled" value="true"/>
                    <input class="button" type="submit" value="Применить">
                </form:form>
            
                <h3 id="users-header">Пользователи:</h3>
                <table id="users-table">
                    <tbody>
                    	<c:forEach var="user" items="${usersList}">
	                        <tr class="third-wrapper">
	                            <td class="users-avatar">
	                                <a class="mes-table-img" href="<c:url value="/users/user_${user.userInfo.userId}.do"/>"><img src="<c:url value="/img/${user.userInfo.avatar}"/>" alt="avatar"></a>
	                            </td>
	                            <td class="users-info">
	                                <div id="login">
	                                    <span class="first-line"><c:out value="${user.userInfo.login}"/></span><br/>
	                                </div>
	                                <span class="second-line">Дата регистрации: </span><time class="first-line" datetime=""><c:out value="${user.userInfo.registDate}"/></time><br/>
	                                <span class="second-line">Статус: </span><span class="first-line">Пользователь</span>
	                                <c:if test="${not empty user.userInfo.rights}">
					                	<span class="first-line">Администратор</span>
					                </c:if>
					                <c:if test="${empty user.userInfo.rights}">
					                	<span class="first-line">Пользователь</span>
					                </c:if>
	                            </td>
	                            <td class="users-mes-num">
	                                <span class="first-line"><c:out value="${user.messageNum}"/></span><br/><span class="second-line">Ответы</span>
	                            </td>
	                            <td class="users-top-num">
	                                <span class="first-line"><c:out value="${user.topicNum}"/></span><br/><span class="second-line">Темы</span>
	                            </td>
	                            <td class="users-created-top-num">
	                                <span class="first-line"><c:out value="${user.createdTopicNum}"/></span><br/><span class="second-line">Создано тем</span>
	                            </td>
	                            <td class="empty-col">
	
	                            </td>
	                        </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
	
</body>