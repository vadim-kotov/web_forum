Сборка:

1) В файле /src/java/ru/webforum/hibernate.cfg.xml указать порт, имя пользователи и пароль для доступа к экземпляру MS SQL Server;
2) Создать базу данных с помощью скрипта /db/ddl.sql;
3) В файле /build/web_forum/WEB-INF/application.properties указать путь к директории, где будут храниться загружаемые файлы: <Путь к проекту>/uploads;
4) В файле build.properties указать путь к glassfish в свойстве appserver.home;
5) Выполнить цель ant deploywar;
6) Запустить сервер приложений glassfish;
7) Набрать в браузере URL http://localhost:8080/web_forum/forum.do.
