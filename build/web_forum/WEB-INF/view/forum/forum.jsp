<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ include file="../includes.jsp" %>
<!-- 
<% 
	request.setCharacterEncoding("UTF-8"); 
	response.setCharacterEncoding("UTF-8"); 
%>
-->

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<c:if test="${empty section.sectionId}">
		<title>Форум</title>
	</c:if>
	<c:if test="${not empty section.sectionId}">
		<title><c:out value="${section.name}"/></title>
	</c:if>
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/common.css"/>"/> 
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/forum/styles.css"/>"/>
</head>
<body>

	<%@ include file="/WEB-INF/view/header.jsp" %>
	
	<%@ include file="/WEB-INF/view/nav.jsp" %>
	
	<div id="wrapper">
		<div id="second-header">
            <h2>
            	<c:out value="${section.name}"/>
            	<form action="<c:url value="/forum/${section.sectionId}/delete_section.do"/>" method="POST">
            		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <input type="submit" class="button" method="POST" value="Удалить"/>
                </form>
            </h2>
            <div class="buttons">
                <ul>
                	<sec:authorize access="hasRole('ROLE_ADMIN')">
                		<li><a href="<c:url value="/forum/${section.sectionId}/new_section.do"/>">Создать раздел</a></li>
                	</sec:authorize>
                	<c:if test="${not empty section.sectionId}">
                    	<li><a href="#">Создать тему</a></li>
                    </c:if>
                    <li><a href="#">Пользователи</a></li>
                </ul>
            </div>
        </div>
        <c:if test="${not empty path}">
	        <div id="path">
	        	<c:if test="${not empty upsection}">
	            	<a href="<c:url value="/forum/${upsection.sectionId}.do"/>"><img id="path-up" class="string-img" src="<c:url value="/resources/forum/up.png"/>" alt="go up"/></a>
	            </c:if>
	            <c:if test="${empty upsection}">
	            	<a href="<c:url value="/forum.do"/>"><img id="path-up" class="string-img" src="<c:url value="/resources/forum/up.png"/>" alt="go up"/></a>
	            </c:if>
	            <ul>
	            	<li><a href="<c:url value="/forum/${path[0].sectionId}.do"/>"><c:out value="${path[0].name}"/></a></li>
	            	<c:forEach var="pathElem" items="${path}" begin="1">
	            			<img id="path-next" class="string-img" src="<c:url value="/resources/forum/right.png"/>"/>
	            		<li><a href="<c:url value="/forum/${pathElem.sectionId}.do"/>"><c:out value="${pathElem.name}"/></a></li>
	            	</c:forEach>
	            </ul>
	        </div>
	    </c:if>
        <div id="main">
        	<c:if test="${not empty subSects}">
	            <div id="sections">
	                <div id="sections-tools" class="tools"></div>
	                <table class="sections-table">
	                	<c:forEach var="subSect" items="${subSects}">
	                		<tr>
		                        <td class="sec-header">
		                        	<a class="sec-table-img" href="<c:url value="/forum/${subSect.section.sectionId}.do"/>"/><img src="<c:url value="/resources/forum/section.png"/>" alt="section"/></a>
		                        	<a class="first-line" href="<c:url value="/forum/${subSect.section.sectionId}.do"/>"/><c:out value="${subSect.section.name}"/></a><br>
		                        	<span class="second-line sec-description"><c:out value="${subSect.section.description}"/></span>
		                        </td>
		                        <td class="sec-user-num"><a class="first-line" href="#"><c:out value="${subSect.userNum}"/></a><br><span class="second-line">Участники</span></td>
		                        <td class="sec-topic-num"><span class="first-line"><c:out value="${subSect.topicNum}"/></span><br><span class="second-line">Темы</span></td>
		                        <td class="sec-mes-num"><span class="first-line"><c:out value="${subSect.messageNum}"/></span><br><span class="second-line">Ответы</span></td>
		                        <td class="sec-last-mes">
		                        	<a class="sec-table-img" href="#"><img src="<c:url value="/resources/forum/avatar.png"/>" alt="user_avatar"/></a>
		                        	<a class="first-line" href="#"><c:out value="${subSect.lastMessage.topic.name}"/></a><br>
		                        	<time class="second-line" datetime=""><c:out value="${subSect.lastMessage.date}"/></time>
		                        	<a class="first-line" href="#"><c:out value="${subSect.lastMessage.author.login}"/></a>
		                        </td>
	                    	</tr>
	                	</c:forEach>
	                </table>
	            </div>
	        </c:if>
	        <c:if test="${not empty subTopics}">
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
	                    	<c:forEach var="subTopic" items="${subTopics}">
		                        <tr>
		                            <td class="top-header clearfix"><a class="top-table-img" href="#"><img src="<c:url value="/resources/forum/avatar.png"/>" alt="avatar"></a>
		                                <div class="div-pre-wrap"><a class="second-line" href="#"><c:out value="${subTopic.topic.name}"/></a></div>
		                                <div class="div-nowrap"><a class="first-line" href="#"><c:out value="${subTopic.topic.creator.login}"/></a><time class="second-line" datetime=""><c:out value="${subTopic.topic.date}"/></time></div></td>
		                            <td class="top-user-num"><div class="top-text"><a class="first-line" href="#"><c:out value="${subTopic.userNum}"/></a><br><span class="second-line">Участники</span></div></td>
		                            <td class="top-mes-num"><div class="top-text"><span class="first-line"><c:out value="${subTopic.messageNum}"/></span><br><span class="second-line">Ответы</span></div></td>
		                            <td class="top-last-mes clearfix"><a class="top-table-img" href="#"><img src="<c:url value="/resources/forum/avatar.png"/>" alt="avatar"></a>
		                                <div class="div-pre-wrap"><a class="second-line"><c:out value="${subTopic.lastMessage.header}"/></a></div>
		                                <div class="div-nowrap"><a class="first-line" href="#"><c:out value="${subTopic.lastMessage.author.login}"/></a><time class="second-line" datetime=""><c:out value="${subTopic.lastMessage.date}"/></time></div>
		                            </td>
		                        </tr>
	                        </c:forEach>
	                        <tr>
	                            <td class="top-header"><a class="top-table-img" href="#"><img src="<c:out value="/web_forum/resources/forum/avatar.png"/>" alt="avatar"></a>
	                                <div class="div-pre-wrap"><a class="second-line" href="#">В Вашем доме скоро появится котёнок мейн кун!</a></div>
	                                <div class="div-nowrap"><a class="first-line" href="#">КуноПаПа</a><time class="second-line" datetime="2009-04-28T14:41">28.04.2009 14:41</time></div></td>
	                            <td class="top-user-num"><div class="top-text"><a class="first-line" href="#">10</a><br><span class="second-line">Участники</span></div></td>
	                            <td class="top-mes-num"><div class="top-text"><span class="first-line">30</span><br><span class="second-line">Ответы</span></div></td>
	                            <td class="top-last-mes clearfix"><a class="top-table-img" href="#"><img src="<c:out value="/web_forum/resources/forum/avatar.png"/>" alt="avatar"></a>
	                                <div class="div-pre-wrap"><a class="second-line">Сам по себе форум - не гарантия качества котят которых предлагают к продаже участники форума и питомники зарегистрированные на форуме.</a></div>
	                                <div class="div-nowrap"><a class="first-line" href="#">КуноПапа</a><time class="second-line" datetime="2012-09-11T19:42">2012.09.11 19:42</time></div>
	                            </td>
	                        </tr>
	                    </tbody>
	                </table>
	            </div>
            </c:if>
        </div>
	</div>
	
	<%@ include file="/WEB-INF/view/footer.jsp" %>
	
</body>
</html>