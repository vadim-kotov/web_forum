<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<head>
    <meta charset="utf-8"/>
    <title>Log in</title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/common.css"/>"/>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/users/login/styles.css"/>"/>
</head>
<body>

	<%@ include file="/WEB-INF/view/header.jsp" %>

    <%@ include file="/WEB-INF/view/nav.jsp" %>

    <div id="wrapper">
        <div id="second-header">
            <h2>Вход</h2>
        </div>
        <div id="second-wrapper">
            <form id="log-in" class="third-wrapper" novalidate="true" action="/web_forum/users/login.do" method="POST">
                <input id="log-in-login" class="textinput" type="text" name="username" placeholder="Логин" required="true"/>
                <span id="log-in-login-error" class="input-error"></span>
                <input id="log-in-password" class="textinput" type="password" name="password" placeholder="Пароль" required="true"/>
                <span id="log-in-password-error" class="input-error"></span>
                
				<input type="hidden" name="${_csrf.parameterName}"   value="${_csrf.token}" />
				
                <div id="log-in-button">
                    <input  class="button" type="submit" value="Вход"/>
                </div>
            </form>
        </div>
    </div>

    <%@ include file="/WEB-INF/view/footer.jsp" %>

    <script type="text/javascript">
        var login = document.getElementById("log-in-login");
        var password = document.getElementById("log-in-password");
        var loginError = document.getElementById("log-in-login-error");
        var passwordError = document.getElementById("log-in-password-error");

        login.addEventListener("focus", function(event) {
            loginError.innerHTML = "";
            login.className = "textinput";
        });
        password.addEventListener("focus", function(event) {
            passwordError.innerHTML = "";
            password.className = "textinput";
        });

        var form = document.getElementById("log-in");
        form.addEventListener("submit", function(event) {
            var flag = false;

            if(!login.value) {
                flag = true;

                login.className = "textinput textinput-error";
                loginError.innerHTML = "введите это поле";
            }
            if(!password.value) {
                flag = true;

                password.className = "textinput textinput-error";
                passwordError.innerHTML = "введите это поле";
            }

            if(flag) {
                event.preventDefault();
            }
        });
    </script>
</body>