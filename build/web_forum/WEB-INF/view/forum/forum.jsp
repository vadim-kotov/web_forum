<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<% 
	request.setCharacterEncoding("UTF-8"); 
	response.setCharacterEncoding("UTF-8"); 
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Forum</title>
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/common.css"/>"/> 
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/forum/styles.css"/>"/>
</head>
<body>

	<%@ include file="/resources/header.html" %>
	
	<%@ include file="/resources/nav.html" %>
	
	<div id="wrapper">
		<div id="forum-header">
            <h2><c:out value="${section.name}"/></h2>
            <div class="buttons">
                <ul>
                    <li><a href="#">Создать тему</a></li>
                    <li><a href="#">Пользователи</a></li>
                </ul>
            </div>
        </div>
        <div id="path">
            <a href="#"><img id="path-up" class="string-img" src="<c:out value="/web_forum/resources/forum/up.png"/>" alt="go up"/></a
            ><ul
                ><li><a href="#">Форум</a></li
                ><img id="path-next" class="string-img" src="<c:out value="/web_forum/resources/forum/right.png"/>"/><li
                ><a href="#">Котята Мейн-Куны</a></li
            ></ul>
        </div>
        <div id="main">
        	<c:if test="${not empty subSects}">
	            <div id="sections">
	                <div id="sections-tools" class="tools"></div>
	                <table class="sections-table">
	                	<c:forEach var="subSect" items="${subSects}">
	                		<tr>
		                        <td class="sec-header">
		                        	<a class="sec-table-img" href="<c:url value="${subSect.section.sectionId}.do"/>"/><img src="<c:out value="/web_forum/resources/forum/section.png"/>" alt="section"/></a>
		                        	<a class="first-line" href="<c:url value="${subSect.section.sectionId}.do"/>"/><c:out value="${subSect.section.name}"/></a><br>
		                        	<span class="second-line sec-description"><c:out value="${subSect.section.description}"/></span>
		                        </td>
		                        <td class="sec-user-num"><a class="first-line" href="#"><c:out value="${subSect.userNum}"/></a><br><span class="second-line">Участники</span></td>
		                        <td class="sec-topic-num"><span class="first-line"><c:out value="${subSect.topicNum}"/></span><br><span class="second-line">Темы</span></td>
		                        <td class="sec-mes-num"><span class="first-line"><c:out value="${subSect.messageNum}"/></span><br><span class="second-line">Ответы</span></td>
		                        <td class="sec-last-mes">
		                        	<a class="sec-table-img" href="#"><img src="<c:out value="/web_forum/resources/forum/avatar.png"/>" alt="user_avatar"/></a>
		                        	<a class="first-line" href="#"><c:out value="${subSect.lastMessage.topic.name}"/></a><br>
		                        	<time class="second-line" datetime="2020-08-21T17:45"><c:out value="${subSect.lastMessage.date}"/></time>
		                        	<a class="first-line" href="#"><c:out value="${subSect.lastMessage.author.login}"/></a>
		                        </td>
	                    	</tr>
	                	</c:forEach>
	                </table>
	            </div>
	        </c:if>
            <div id="topics">
                <table class="topics-table">
                    <thead>
                        <tr>
                            <th>Заголовок</th>
                            <th>Участники</th>
                            <th>Ответы</th>
                            <th>Последнее сообщение</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td class="top-header"><a class="top-table-img" href="#"><img src="<c:out value="/web_forum/resources/forum/avatar.png"/>" alt="avatar"></a>
                                <div class="div-pre-wrap"><a class="second-line" href="#">Как правильно выбрать котёнка мейн куна!</a></div>
                                <div class="div-nowrap"><a class="first-line" href="#">КуноПаПа</a><time class="second-line" datetime="2009-12-03T13:53">03.12.2009 13:53</time></div></td>
                            <td class="top-user-num"><div class="top-text"><a class="first-line" href="#">5</a><br><span class="second-line">Участники</span></div></td>
                            <td class="top-mes-num"><div class="top-text"><span class="first-line">15</span><br><span class="second-line">Ответы</span></div></td>
                            <td class="top-last-mes"><a class="top-table-img" href="#"><img src="<c:out value="/web_forum/resources/forum/avatar.png"/>" alt="avatar"></a>
                                <div class="div-pre-wrap"><a class="second-line">Сам по себе форум - не гарантия качества котят которых предлагают к продаже участники форума и питомники зарегистрированные на форуме.</a></div>
                                <div class="div-nowrap"><a class="first-line" href="#">КуноПапа</a><time class="second-line" datetime="2012-09-11T19:42">2012.09.11 19:42</time></div>
                            </td>
                        </tr>
                        <tr>
                            <td class="top-header"><a class="top-table-img" href="#"><img src="<c:out value="/web_forum/resources/forum/avatar.png"/>" alt="avatar"></a>
                                <div class="div-pre-wrap"><a class="second-line" href="#">В Вашем доме скоро появится котёнок мейн кун!</a></div>
                                <div class="div-nowrap"><a class="first-line" href="#">КуноПаПа</a><time class="second-line" datetime="2009-04-28T14:41">28.04.2009 14:41</time></div></td>
                            <td class="top-user-num"><div class="top-text"><a class="first-line" href="#">10</a><br><span class="second-line">Участники</span></div></td>
                            <td class="top-mes-num"><div class="top-text"><span class="first-line">30</span><br><span class="second-line">Ответы</span></div></td>
                            <td class="top-last-mes"><a class="top-table-img" href="#"><img src="<c:out value="/web_forum/resources/forum/avatar.png"/>" alt="avatar"></a>
                                <div class="div-pre-wrap"><a class="second-line">Сам по себе форум - не гарантия качества котят которых предлагают к продаже участники форума и питомники зарегистрированные на форуме.</a></div>
                                <div class="div-nowrap"><a class="first-line" href="#">КуноПапа</a><time class="second-line" datetime="2012-09-11T19:42">2012.09.11 19:42</time></div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
	</div>
	
	<%@ include file="/resources/footer.html" %>
	
</body>
</html>