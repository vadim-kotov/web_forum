<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Error</title>
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/common.css"/>"/>
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/users/registration/styles.css"/>"/>
</head>
<body>
	<%@ include file="/WEB-INF/view/header.jsp" %>

    <%@ include file="/WEB-INF/view/nav.jsp" %>
    
    <div id="wrapper">
        <div id="second-header">
            <h2>Регистрация</h2>
        </div>
        <div id="second-wrapper">
            <form:form id="registration" class="third-wrapper" novalidate="true" action="/web_forum/users/registration.do" modelAttribute="user" method="POST">
                <form:input path="login" id="registration-login" class="textinput" type="text" name="login" placeholder="Логин" required="true"/>
                <span id="registration-login-error" class="input-error"></span>
                <form:input path="password" id="registration-password" class="textinput" type="password" name="password" placeholder="Пароль" required="true" minlength="8"/>
                <span id="registration-password-error" class="input-error"></span>
                <input id="registration-password-confirm" class="textinput" type="password" name="passwordConfirm" placeholder="Подтвердите пароль" required="true"/>
                <span id="registration-password-confirm-error" class="input-error"></span>
                    
                <div id="registration-button">
                    <input  class="button" type="submit" value="Регистрация"/>
                </div>
            </form:form>
        </div>
    </div>

    <%@ include file="/WEB-INF/view/footer.jsp" %>
    
    <script type="text/javascript">
        var login = document.getElementById("registration-login");
        var loginError = document.getElementById("registration-login-error");
        var password = document.getElementById("registration-password");
        var passwordError = document.getElementById("registration-password-error");
        var passwordConfirm = document.getElementById("registration-password-confirm");
        var passwordConfirmError = document.getElementById("registration-password-confirm-error");
        
        login.addEventListener("focus", function(event) {
            loginError.innerHTML = "";
            login.className = "textinput";
        });
        password.addEventListener("blur", function(event) {
            if(!password.validity.valid) {
                passwordError.innerHTML = "введите пароль не короче 8 символов";
            }
            if(!password.validity.valid || passwordConfirm.value != password.value) {
                password.className = "textinput textinput-error";
            }
        });
        password.addEventListener("focus", function(event) {
            passwordError.innerHTML = "";
            password.className = "textinput";
        });

        passwordConfirm.addEventListener("blur", function(event) {
            if(passwordConfirm.value != password.value)
            {
                passwordConfirmError.innerHTML = "пароли не совпадают";
                passwordConfirm.className = "textinput textinput-error";

                password.className = "textinput textinput-error";
            }
        });
        passwordConfirm.addEventListener("focus", function(event) {
            passwordConfirmError.innerHTML = "";
            passwordConfirm.className = "textinput";

            if(password.validity.valid){
                password.className = "textinput";
            }
        });

        var form = document.querySelector("#registration");
        form.addEventListener("submit", function(event) {
            var flag = false;
            var inputs = [login, password, passwordConfirm];
            var errors = [loginError, passwordError, passwordConfirmError];

            inputs.forEach((input, index) => {
                if(!input.value) {
                    flag = true;

                    input.className = "textinput textinput-error";
                    errors[index].innerHTML = "введите это поле";
                }
            })
            if(!password.validity.valid && password.value) {
                flag = true;

                password.className = "textinput textinput-error";
                passwordError.innerHTML = "введите пароль не короче 8 символов";
            }
            if(passwordConfirm.value != password.value)
            {
                flag = true;

                passwordConfirmError.innerHTML = "пароли не совпадают";
                passwordConfirm.className = "textinput textinput-error";

                password.className = "textinput textinput-error";
            }

            if(flag) {
                event.preventDefault();
            }
        });
    </script>
</body>