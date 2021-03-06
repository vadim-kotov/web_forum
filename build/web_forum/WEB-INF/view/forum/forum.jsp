<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ include file="../includes.jsp" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
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
            	<sec:authorize access="hasRole('ADMIN')">
	            	<c:if test="${not empty section.sectionId}">
		            	<form action="<c:url value="/forum/${section.sectionId}/delete_section.do"/>" method="POST">
		            		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
		                    <input type="submit" class="button" value="Удалить"/>
		                </form>
		        	</c:if>
	        	</sec:authorize>
            </h2>
            <div class="buttons">
                <ul>
                	<sec:authorize access="hasRole('ADMIN')">
                		<c:if test="${empty section.sectionId}">
           					<c:set var="newSectionButton" value="/forum/new_section.do"/> 
				        </c:if> 
				        <c:if test="${not empty section.sectionId}">
				            <c:set var="newSectionButton" value="/forum/${section.sectionId}/new_section.do"/>
				        </c:if>
                		<li><a href="<c:url value="${newSectionButton}"/>">Создать раздел</a></li>
                	</sec:authorize>
                	<sec:authorize access="isAuthenticated()">
						<c:if test="${not empty section.sectionId}">
							<li><a href="<c:url value="/forum/${section.sectionId}/new_topic.do"/>">Создать тему</a></li>
						</c:if>
					</sec:authorize>
                    <li>
                    	<c:if test="${empty section.sectionId}">
                    		<a href="<c:url value="/users/users_list.do"/>">Пользователи</a>
                    	</c:if>
                    	<c:if test="${not empty section.sectionId}">
                    		<a href="<c:url value="/users/users_list/section_${section.sectionId}.do"/>">Пользователи</a>
                    	</c:if>	
                    </li>
                </ul>
            </div>
        </div>
        <c:if test="${not empty path}">
	        <div id="path">
	        	<c:if test="${not empty upsection.sectionId}">
	            	<a href="<c:url value="/forum/${upsection.sectionId}.do"/>"><img id="path-up" class="string-img" src="<c:url value="/resources/forum/up.png"/>" alt="go up"/></a>
	            </c:if>
	            <c:if test="${empty upsection.sectionId}">
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
		                        	<c:if test="${not empty subSect.lastMessage}">
			                        	<a class="sec-table-img" href="<c:url value="/users/user_${subSect.lastMessage.author.userId}.do"/>"><img src="<c:url value="/img/${subSect.lastMessage.author.avatar}"/>" alt="user_avatar"/></a>
			                        	<a class="first-line" href="<c:url value="/forum/${subSect.lastMessage.topic.section.sectionId}/topic_${subSect.lastMessage.topic.topicId}.do#message_${subSect.lastMessage.messageId}"/>"><c:out value="${subSect.lastMessage.topic.name}"/></a><br>
			                        	<time class="second-line" datetime=""><c:out value="${subSect.lastMessage.date}"/></time>
			                        	<a class="first-line" href="<c:url value="/users/user_${subSect.lastMessage.author.userId}.do"/>"><c:out value="${subSect.lastMessage.author.login}"/></a>
			                    	</c:if>			                       
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
		                            <td class="top-header clearfix"><a class="top-table-img" href="<c:url value="/users/user_${subTopic.topic.creator.userId}.do"/>"><img src="<c:url value="/img/${subTopic.topic.creator.avatar}"/>" alt="avatar"></a>
		                                <div class="div-pre-wrap"><a class="second-line" href="<c:url value="/forum/${section.sectionId}/topic_${subTopic.topic.topicId}.do"/>"><c:out value="${subTopic.topic.name}"/></a></div>
		                                <div class="div-nowrap"><a class="first-line" href="<c:url value="/users/user_${subTopic.topic.creator.userId}.do"/>"><c:out value="${subTopic.topic.creator.login}"/></a><time class="second-line" datetime=""><c:out value="${subTopic.topic.date}"/></time></div></td>
		                            <td class="top-user-num"><div class="top-text"><a class="first-line" href="#"><c:out value="${subTopic.userNum}"/></a><br><span class="second-line">Участники</span></div></td>
		                            <td class="top-mes-num"><div class="top-text"><span class="first-line"><c:out value="${subTopic.messageNum}"/></span><br><span class="second-line">Ответы</span></div></td>
		                            <td class="top-last-mes clearfix">
		                            	<c:if test="${not empty subTopic.lastMessage}">
			                            	<a class="top-table-img" href="<c:url value="/users/user_${subTopic.lastMessage.author.userId}.do"/>"><img src="<c:url value="/img/${subTopic.lastMessage.author.avatar}"/>" alt="avatar"></a>
			                                <div class="div-pre-wrap"><a class="second-line" href="<c:url value="/forum/${section.sectionId}/topic_${subTopic.topic.topicId}.do#message_${subTopic.lastMessage.messageId}"/>"><c:out value="${subTopic.lastMessage.header}"/></a></div>
			                                <div class="div-nowrap"><a class="first-line" href="<c:url value="/users/user_${subTopic.lastMessage.author.userId}.do"/>"><c:out value="${subTopic.lastMessage.author.login}"/></a><time class="second-line" datetime=""><c:out value="${subTopic.lastMessage.date}"/></time></div>
			                        	</c:if>
		                            </td>
		                        </tr>
	                        </c:forEach>
	                    </tbody>
	                </table>
	            </div>
            </c:if>
        </div>
	</div>
	
	<%@ include file="/WEB-INF/view/footer.jsp" %>
	
</body>
</html>