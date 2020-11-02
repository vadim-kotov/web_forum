<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<div id="popup" onclick="hidePopUp()">
    <div id="registration">
        
        <div id="registration-header">
            <h2>Регистрация</h2>
        </div>
        <div id="registration-form">
            <form:form action="users/registration.do" modelAttribute="user" method="POST">
                <form:input path="login" class="textinput" type="text" name="login" placeholder="Логин" required="true"/><br/>
                <form:input path="password" class="textinput" type="password" name="password" placeholder="Пароль" required="true"/><br/>
                <input class="textinput" type="password" name="passwordConfirm" placeholder="Подтвердите пароль" required="true"/>
                <div id="registration-button-div">
                    <input class="button" type="submit" value="Регистрация"/>
                </div>
            </form:form>
        </div>
    </div>
</div>

<script type="text/javascript">
    function showPopUp() {
        var div = document.getElementById("popup");
        div.style.display = "block";
        var body = document.getElementsByTagName("body")[0]
        body.style.overflow = "hidden";
    }

    function hidePopUp() {
        var div = document.getElementById("popup");
        div.style.display = "none";
        var body = document.getElementsByTagName("body")[0]
        body.style.overflow = "auto";
    }

    var regist = document.getElementById("registration");
    regist.addEventListener("click", function(e){
        e.stopPropagation();
    });
</script>