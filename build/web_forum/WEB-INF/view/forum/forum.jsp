<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<% 
	request.setCharacterEncoding("UTF-8"); 
	response.setCharacterEncoding("UTF-8"); 
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Forum</title>
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/common.css"/>"/> 
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/forum/styles.css"/>"/>
</head>
<body>

	<%@ include file="/resources/header.html" %>
	
	<div id="wrapper">
	
		<%@ include file="/resources/nav.html" %>
		
		<div id="forum-header">
            <h2><c:out value="${section.name}" /></h2>
            <div class="buttons">
                <ul>
                    <li><a href="#">Создать тему</a></li>
                    <li><a href="#">Пользователи</a></li>
                </ul>
            </div>
        </div>
        <div id="path">
            <a href="#"><img id="path-up" class="string-img" src="<c:out value="/web_forum/resources/forum/up.png"/>" alt="go up"/></a
            ><ul
                ><li><a href="#">Форум</a></li
                ><img id="path-next" class="string-img" src="<c:out value="/web_forum/resources/forum/right.png"/>"/><li
                ><a href="#">Котята Мейн-Куны</a></li
            ></ul>
        </div>
        <div id="main">
            <div id="sections">
                <div id="sections-tools" class="tools"></div>
                <table class="sections-table">
                    <tr>
                        <td class="sec-header"><a class="sec-table-img" href="#"><img src="<c:out value="/web_forum/resources/forum/section.png"/>" alt="section"/></a><a class="first-line" href="#">Котята брид и шоу-классов</a><br><span class="second-line sec-description">Нефиговые котятки</span></td>
                        <td class="sec-user-num"><a class="first-line" href="#">70</a><br><span class="second-line">Участники</span></td>
                        <td class="sec-topic-num"><span class="first-line">21</span><br><span class="second-line">Темы</span></td>
                        <td class="sec-mes-num"><span class="first-line">123</span><br><span class="second-line">Ответы</span></td>
                        <td class="sec-last-mes"><a class="sec-table-img" href="#"><img src="<c:out value="/web_forum/resources/forum/avatar.png"/>" alt="user_avatar"/></a><a class="first-line" href="#">Черный солид Faydark Jet Black, 02.04.20 питомник Faydark (Украина, Харьков)</a><br><time class="second-line" datetime="2020-08-21T17:45">21.08.2020 17:45</time><a class="first-line" href="#">Eytar</a></td>
                    </tr>
                    <tr>
                        <td class="sec-header"><a class="sec-table-img" href="#"><img src="<c:out value="/web_forum/resources/forum/section.png"/>" alt="section"/></a><a class="first-line" href="#">Котята - домашние любимцы. Цена до 15000 р.</a><br><span class="second-line sec-description">С пивом потянет</span></td>
                        <td class="sec-user-num"><a class="first-line" href="#">81</a><br><span class="second-line">Участники</span></td>
                        <td class="sec-topic-num"><span class="first-line">30</span><br><span class="second-line">Темы</span></td>
                        <td class="sec-mes-num"><span class="first-line">162</span><br><span class="second-line">Ответы</span></td>
                        <td class="sec-last-mes"><a class="sec-table-img" href="#"><img src="<c:out value="/web_forum/resources/forum/avatar.png"/>" alt="user_avatar"/></a><a class="first-line" href="#">Ximena JokerCoon, черная тикированная</a><br><time class="second-line" datetime="2020-04-11T20:18">11.04.2020 20:18</time><a class="first-line" href="#">JokerCoon</a></td>
                    </tr>
                    <tr>
                        <td class="sec-header"><a class="sec-table-img" href="#"><img src="<c:out value="/web_forum/resources/forum/section.png"/>" alt="section"/></a><a class="first-line" href="#">Котята - домашние любимцы. Цена 15000-25000 р.</a><br><span class="second-line sec-description">Ну такое, не рыба не мясо</span></td>
                        <td class="sec-user-num"><a class="first-line" href="#">112</a><br><span class="second-line">Участники</span></td>
                        <td class="sec-topic-num"><span class="first-line">30</span><br><span class="second-line">Темы</span></td>
                        <td class="sec-mes-num"><span class="first-line">182</span><br><span class="second-line">Ответы</span></td>
                        <td class="sec-last-mes"><a class="sec-table-img" href="#"><img src="<c:out value="/web_forum/resources/forum/avatar.png"/>" alt="user_avatar"/></a><a class="first-line" href="#">Питомник Great Lion. Franc - Шкода с большой буквы.</a><br><time class="second-line" datetime="2020-04-28T16:32">28.04.2020 16:32</time><a class="first-line" href="#">ЕленаС</a></td>
                    </tr>
                    <tr>
                        <td class="sec-header"><a class="sec-table-img" href="#"><img src="<c:out value="/web_forum/resources/forum/section.png"/>" alt="section"/></a><a class="first-line" href="#">Котята - домашние любимцы. Цена от 25000 р.</a><br><span class="second-line sec-description">Нормальные котятки</span></td>
                        <td class="sec-user-num"><a class="first-line" href="#">23</a><br><span class="second-line">Участники</span></td>
                        <td class="sec-topic-num"><span class="first-line">11</span><br><span class="second-line">Темы</span></td>
                        <td class="sec-mes-num"><span class="first-line">99</span><br><span class="second-line">Ответы</span></td>
                        <td class="sec-last-mes"><a class="sec-table-img" href="#"><img src="<c:out value="/web_forum/resources/forum/avatar.png"/>" alt="user_avatar"/></a><a class="first-line" href="#">Megatherion Leo Minor — Малый Лев</a><br><time class="second-line" datetime="2020-08-26T17:25">26.08.2020 17:25</time><a class="first-line" href="#">Усатая</a></td>
                    </tr>
                </table>
            </div>
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
                        <tr>
                            <td class="top-header"><a class="top-table-img" href="#"><img src="<c:out value="/web_forum/resources/forum/avatar.png"/>" alt="avatar"></a>
                                <div class="div-pre-wrap"><a class="second-line" href="#">Как правильно выбрать котёнка мейн куна!</a></div>
                                <div class="div-nowrap"><a class="first-line" href="#">КуноПаПа</a><time class="second-line" datetime="2009-12-03T13:53">03.12.2009 13:53</time></div></td>
                            <td class="top-user-num"><div class="top-text"><a class="first-line" href="#">5</a><br><span class="second-line">Участники</span></div></td>
                            <td class="top-mes-num"><div class="top-text"><span class="first-line">15</span><br><span class="second-line">Ответы</span></div></td>
                            <td class="top-last-mes"><a class="top-table-img" href="#"><img src="<c:out value="/web_forum/resources/forum/avatar.png"/>" alt="avatar"></a>
                                <div class="div-pre-wrap"><a class="second-line">Сам по себе форум - не гарантия качества котят которых предлагают к продаже участники форума и питомники зарегистрированные на форуме.</a></div>
                                <div class="div-nowrap"><a class="first-line" href="#">КуноПапа</a><time class="second-line" datetime="2012-09-11T19:42">2012.09.11 19:42</time></div>
                            </td>
                        </tr>
                        <tr>
                            <td class="top-header"><a class="top-table-img" href="#"><img src="<c:out value="/web_forum/resources/forum/avatar.png"/>" alt="avatar"></a>
                                <div class="div-pre-wrap"><a class="second-line" href="#">В Вашем доме скоро появится котёнок мейн кун!</a></div>
                                <div class="div-nowrap"><a class="first-line" href="#">КуноПаПа</a><time class="second-line" datetime="2009-04-28T14:41">28.04.2009 14:41</time></div></td>
                            <td class="top-user-num"><div class="top-text"><a class="first-line" href="#">10</a><br><span class="second-line">Участники</span></div></td>
                            <td class="top-mes-num"><div class="top-text"><span class="first-line">30</span><br><span class="second-line">Ответы</span></div></td>
                            <td class="top-last-mes"><a class="top-table-img" href="#"><img src="<c:out value="/web_forum/resources/forum/avatar.png"/>" alt="avatar"></a>
                                <div class="div-pre-wrap"><a class="second-line">Сам по себе форум - не гарантия качества котят которых предлагают к продаже участники форума и питомники зарегистрированные на форуме.</a></div>
                                <div class="div-nowrap"><a class="first-line" href="#">КуноПапа</a><time class="second-line" datetime="2012-09-11T19:42">2012.09.11 19:42</time></div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
	</div>
	
	<%@ include file="/resources/footer.html" %>
	
</body>
</html>