<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<head>
    <meta charset="utf-8"/>
    <title>Новый раздел</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/common.css"/>"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/forum/new_section/styles.css"/>"/>
</head>
<body>
    <%@ include file="/WEB-INF/view/header.jsp" %>

    <%@ include file="/WEB-INF/view/nav.jsp" %>

    <div id="wrapper">
        
        <div id="second-header">
            <h2><c:out value="${section.name}"/></h2>
        </div>
        <c:if test="${empty section.sectionId}">
            <c:set var="formAction" value="/web_forum/forum/new_section.do"/> 
        </c:if> 
        <c:if test="${not empty section.sectionId}>">
            <c:set var="formAction" value="/web_forum/forum/${section.sectionId}/new_section.do"/>
        </c:if>
        <div id="main">
            <div id="second-wrapper">
                <h3>Создать раздел</h3>
                <form:form id="new-section" class="third-wrapper" novalidate="true" action="${formAction}" modelAttribute="newSection" method="POST" accept-charset="UTF-8">
                    <form:input path="name" id="new-section-name" class="textinput" type="text" name="name" placeholder="Название раздела" required="true"/>
                    <span id="new-section-name-error" class="input-error"></span>
                    <form:textarea path="description" id="new-section-description" class="textinput" name="description" placeholder="Описание раздела" required="false"></form:textarea>

                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

                    <div id="new-section-button">
                        <input class="button" type="submit" value="Создать"/>
                    </div>
                </form:form>
            </div>
        </div>
    </div>

    <%@ include file="/WEB-INF/view/footer.jsp" %>

    <script type="text/javascript">
        var sectionName = document.getElementById("new-section-name");
        var sectionNameError = document.getElementById("new-section-name-error");

        sectionName.addEventListener("focus", function(event) {
            sectionNameError.innerHTML = "";
            sectionName.className = "textinput";
        });
        sectionName.addEventListener("blur", function(event) {
            if(!sectionName.value) {
                sectionNameError.innerHTML = "заполните это поле";
                sectionName.className = "textinput textinput-error";
            }
        });
        
        var form = document.getElementById("new-section");
        form.addEventListener("submit", function(event) {
            if(!sectionName.value) {
                sectionNameError.innerHTML = "заполните это поле";
                sectionName.className = "textinput textinput-error";
                
                event.preventDefault();
            }
        })
        
    </script>
</body>