<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<head>
    <meta charset="utf-8"/>
    <title>Сообщение</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/common.css"/>"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/forum/topic/new_message/styles.css"/>"/>
</head>
<body>
    <%@ include file="/WEB-INF/view/header.jsp" %>

    <%@ include file="/WEB-INF/view/nav.jsp" %>

    <div id="wrapper">
    	<div id="second-header">
    		<h2><c:out value="${topic.name}"/></h2>
    	</div>
	    <div id="main">
	        <div id="second-wrapper">
	            <h3>Сообщение</h3>
	            <form:form modelAttribute="newMessage" action="/web_forum/forum/${section.sectionId}/topic_${topic.topicId}/new_message.do?${_csrf.parameterName}=${_csrf.token}" id="new-message" class="third-wrapper" novalidate="true" method="POST" enctype="multipart/form-data" accept-charset="UTF-8">
	                <form:input path="header" id="new-message-header" class="textinput" type="text" name="header" placeholder="Заголовок сообщения"/><br/>
	                <textarea name="messageText" id="new-message-text" class="textinput" placeholder="Текст сообщения"></textarea>
	                <span id="new-message-text-error" class="input-error"></span>
	                
	                <input id="new-message-button" name="messageFile" type="file" accept="image/*"/>

	                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	
	                <div id="new-message-button">
	                    <input class="button" type="submit" value="Опубликовать"/>
	                </div>
	            </form:form>
	        </div>
	    </div>
	</div>

    <%@ include file="/WEB-INF/view/footer.jsp" %>

    <script type="text/javascript">
        var messageText = document.getElementById("new-message-text");
        var messageTextError = document.getElementById("new-message-text-error");

        messageText.addEventListener("focus", function(event) {
            messageTextError.innerHTML = "";
            messageText.className = "textinput";
        } );

        messageText.addEventListener("blur", function(event) {
            if(!messageText.value) {
                messageTextError.innerHTML = "заполните это поле";
                messageText.className = "textinput textinput-error";
            }
        } );

        var form = document.getElementById("new-message");
        form.addEventListener("submit", function(event) {
            if(!messageText.value) {
                messageTextError.innerHTML = "заполните это поле";
                messageText.className = "textinput textinput-error";
                
                event.preventDefault();
            }
        } );
    </script>

</body>