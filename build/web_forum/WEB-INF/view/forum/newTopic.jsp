<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<head>
    <meta charset="utf-8"/>
    <title>Log in</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/common.css"/>"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/forum/new_topic/styles.css"/>"/>
</head>
<body>
    <%@ include file="/WEB-INF/view/header.jsp" %>

    <%@ include file="/WEB-INF/view/nav.jsp" %>

    <div id="wrapper">
        
        <div id="second-header">
            <h2><c:out value="${section.name}"/></h2>
        </div>
        <div id="main">
        	<div id="second-wrapper">
            	<h3>Создать тему</h3>
                <form:form id="new-topic" class="third-wrapper" novalidate="true" method="POST" action="/web_forum/forum/${section.sectionId}/new_topic.do" modelAttribute="newTopic" accept-charset="UTF-8">
                    <form:input path="name" id="new-topic-name" class="textinput" type="text" name="name" placeholder="Название темы" required="true"/>
                    <span id="new-topic-name-error" class="input-error"></span>
    
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

                    <div id="new-topic-button">
                        <input class="button" type="submit" value="Создать"/>
                    </div>
                </form:form>
            </div>
        </div>
    </div>

    <%@ include file="/WEB-INF/view/footer.jsp" %>

    <script type="text/javascript">
        var topicName = document.getElementById("new-topic-name");
        var topicNameError = document.getElementById("new-topic-name-error");

        topicName.addEventListener("focus", function(event) {
            topicNameError.innerHTML = "";
            topicName.className = "textinput";
        });
        topicName.addEventListener("blur", function(event) {
            if(!topicName.value) {
                topicNameError.innerHTML = "заполните это поле";
                topicName.className = "textinput textinput-error";
            }
        });
        
        var form = document.getElementById("new-topic");
        form.addEventListener("submit", function(event) {
            if(!topicName.value) {
                topicNameError.innerHTML = "заполните это поле";
                topicName.className = "textinput textinput-error";
                
                event.preventDefault();
            }
        })
        
    </script>
</body>
