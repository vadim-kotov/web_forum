<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ include file="/WEB-INF/view/includes.jsp" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title><c:out value="${topic.name}"/></title>
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/common.css"/>"/> 
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/forum/topic/styles.css"/>"/>
</head>
<body>

	<%@ include file="/WEB-INF/view/header.jsp" %>
	
    <%@ include file="/WEB-INF/view/nav.jsp" %>
    
    <div id="wrapper">
        <div id="second-header">
            <h2>
                <c:out value="${topic.name}"/>
                <sec:authorize access="hasRole('ADMIN')">
		            <form action="<c:url value="/forum/${section.sectionId}/topic_${topic.topicId}/delete_topic.do"/>" method="POST">
		            	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
		                <input type="submit" class="button" value="Удалить"/>
		            </form>
	        	</sec:authorize>
            </h2>
            <div class="buttons">
                <ul>
                	<sec:authorize access="isAuthenticated()">
						<li><a href="<c:url value="/forum/${section.sectionId}/topic_${topic.topicId}/new_message.do"/>">Сообщение</a></li>
					</sec:authorize>
                </ul>
            </div>
        </div>
        <div id="topic-info">
            <a href="<c:url value="/users/user_${topic.creator.userId}.do"/>"><c:out value="${topic.creator.login}"/></a><time datetime=""><c:out value="${topic.date}"/></time>
        </div>
	    <div id="path">
	        <a href="<c:url value="/forum/${section.sectionId}.do"/>"><img id="path-up" class="string-img" src="<c:url value="/resources/forum/up.png"/>" alt="go up"/></a>
	        <ul>
	        	<li><a href="<c:url value="/forum/${path[0].sectionId}.do"/>"><c:out value="${path[0].name}"/></a></li>
	        	<c:forEach var="pathElem" items="${path}" begin="1">
	    			<img id="path-next" class="string-img" src="<c:url value="/resources/forum/right.png"/>"/>
	        		<li><a href="<c:url value="/forum/${pathElem.sectionId}.do"/>"><c:out value="${pathElem.name}"/></a></li>
	        	</c:forEach>
	        </ul>
        </div>
        <div id="main">
            <div class="tools"></div>
            <c:if test="${not empty topicMessages}">
                <table id="second-wrapper">
                    <tbody>
                        <c:forEach var="topicMessage" items="${topicMessages}">
                        	
                            <tr class="third-wrapper">
                            	<c:set var="objects" value="${topicMessage.message.messageObjects}"/>
                                <td class="mes-user-info">
                                	<a name="<c:out value="message_${topicMessage.message.messageId}"/>"></a>
                                    <a class="mes-table-img" href="<c:url value="/users/user_${topicMessage.message.author.userId}.do"/>"><img src="<c:url value="/img/${topicMessage.message.author.avatar}"/>" alt="avatar"></a>
                                    <div class="login-time">
                                        <a class="first-line" href="<c:url value="/users/user_${topicMessage.message.author.userId}.do"/>"><c:out value="${topicMessage.message.author.login}"/></a><br/>
                                        <time class="second-line" datetime=""><c:out value="${topicMessage.message.date}"/></time>
                                    </div>
                                </td>
                                <td class="mes-body">
                                    <div class="mes-header">
                                    	<c:if test="${not empty topicMessage.message.header}">
                                        	<span class="first-line"><c:out value="${topicMessage.message.header}"/></span>
                                        </c:if>
                                        <c:if test="${empty topicMessage.message.header}">
                                        	<span class="first-line"><c:out value=" "/></span>
                                        </c:if>
                                        <sec:authorize access="hasRole('ADMIN')">
                                        	<form method="POST" action="<c:url value="/forum/${section.sectionId}/topic_${topic.topicId}/delete_message.do"/>">
                                        		<input type="hidden" name="messageId" value="${topicMessage.message.messageId}"/>
                                        		<c:if test="${objects.size() gt 1}">
                                        			<input type="hidden" name="filename" value="<c:out value="${objects[1].value}"/>"/>
                                        		</c:if>
                                        		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                        		<input type="submit" class="button" value="Удалить"/>
                                        	</form>
                                        </sec:authorize>
                                    </div>
                                    <div class="mes-text">
                                        <span class="second-line"><c:out value="${objects[0].value}"/></span><br/>
                                    	<c:if test="${objects.size() gt 1}">
                                    		<img class="attached-file" src="<c:url value="/img/${objects[1].value}"/>"/>
                                    	</c:if>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
        </div>
    </div>

    <%@ include file="/WEB-INF/view/footer.jsp" %>

</body>