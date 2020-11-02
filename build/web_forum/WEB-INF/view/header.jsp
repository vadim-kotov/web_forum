<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<div id="header">
	<h1>Форум о Мэйн-Кунах</h1>

	<div id="log-in-outter">
		<div id="log-in-inner">
			<form>
				<input class="textinput" type="text" name="login" placeholder="Логин" required="true"/><br/>
				<input class="textinput" type="password" name="password" placeholder="Пароль" required="true"/><br/>
				<input class="button" type="submit" value="Войти"/>
			</form>
		</div>
		<a href="<c:url value="/forum/registration.do"/>" onclick="showPopUp();">Регистрация</a>
	</div>
</div>