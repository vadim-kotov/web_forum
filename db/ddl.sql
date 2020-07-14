DROP TABLE IF EXISTS Banlist;
DROP TABLE IF EXISTS [Object];
DROP TABLE IF EXISTS Enums;
DROP TABLE IF EXISTS [Message];
DROP TABLE IF EXISTS Topic;
DROP TABLE IF EXISTS Section;
DROP TABLE IF EXISTS [User];
GO

CREATE TABLE [User]
(
	[user_id] int IDENTITY(1,1) NOT NULL PRIMARY KEY,
	[login] varchar(50) NOT NULL UNIQUE,						/*check simbols*/
	[password] varchar(20) NOT NULL CHECK(LEN(password) >= 8),		/*check simbols*/
	regist_date datetime2 NOT NULL,							
	rights bit NOT NULL,										/*0 - user, 1 - moderator*/								
	avatar varchar(100)
);

CREATE TABLE Section
(
	section_id int IDENTITY(1,1) NOT NULL PRIMARY KEY,
	[name] nvarchar(200) NOT NULL,
	upsection_id int NULL											/*id of parent section*/
		REFERENCES Section(section_id),
	[description] nvarchar(500) NOT NULL
);

CREATE TABLE Topic
(
	topic_id int IDENTITY(1,1) NOT NULL PRIMARY KEY,
	[name] nvarchar(200) NOT NULL,
	section_id int NOT NULL
		REFERENCES Section(section_id),
	creator_id int NOT NULL
		REFERENCES [User]([user_id]),
	[date] datetime2 NOT NULL
);

CREATE TABLE [Message]
(
	message_id int IDENTITY(1,1) NOT NULL PRIMARY KEY,
	topic_id int NOT NULL
		REFERENCES Topic(topic_id)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION,
	upmessage_id int NULL										/*id of parent message*/
		REFERENCES [Message](message_id),
	author_id int NOT NULL
		REFERENCES [User]([user_id]),
	[date] datetime2 NOT NULL,
	header nvarchar(200)										/*header of message*/
);

CREATE TABLE Enums
(
	enums_id tinyint /*IDENTITY(1,1)*/ NOT NULL PRIMARY KEY,
	[type] varchar(10),
	[name] varchar(10) NOT NULL
);

CREATE TABLE [Object]											/*every message consists of several objects*/
(
	[object_id] int IDENTITY(1,1) NOT NULL PRIMARY KEY,
	message_id int NOT NULL
		REFERENCES [Message](message_id) 
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	object_num tinyint /*NOT*/ NULL,								/*number of object in message*/
	[type_id] tinyint NOT NULL									/*type of object(text, image, etc.)*/
		REFERENCES Enums(enums_id),
	[value] nvarchar(max) NOT NULL,								/*main content of object, might have different view for different object types*/
	quote_author_id int NULL									/*NOT NULL only for object with type 'quote'.*/
		REFERENCES [User]([user_id]),
	quote_id int NULL											/*NOT NULL only for object with type 'quote'.*/
		REFERENCES [Message](message_id),
	width smallint NULL,										/*width and height of object(image, video, etc.)*/
	height smallint NULL

);

CREATE TABLE Banlist
(
	banlist_id int IDENTITY(1,1) NOT NULL PRIMARY KEY,
	[user_id] int NOT NULL
		REFERENCES [User]([user_id]),
	[type_id] tinyint NOT NULL									/*ban type*/
		REFERENCES Enums(enums_id),
	topic_id int NULL
		REFERENCES Topic(topic_id)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	moder_id int NOT NULL										
		REFERENCES [User]([user_id]),
	ban_date datetime2 NOT NULL,
	unban_date datetime2 NOT NULL,
	[source] nvarchar(400) NOT NULL,

	CHECK (unban_date > ban_date)
);
GO

DROP TRIGGER IF EXISTS DeleteMessageThread
DROP TRIGGER IF EXISTS RelocateMessageThread
DROP TRIGGER IF EXISTS TopicDelete
DROP TRIGGER IF EXISTS SectionDelete
DROP TRIGGER IF EXISTS ObjectInsert
DROP TRIGGER IF EXISTS BanlistInsert
DROP TRIGGER IF EXISTS MessageBanlist
DROP TRIGGER IF EXISTS TopicBanlist
GO

/*
* deletes all thread
* updates all matching quotes
*/
CREATE TRIGGER DeleteMessageThread									
ON [Message]
INSTEAD OF DELETE
AS
	IF (ROWCOUNT_BIG() = 0)
	RETURN;
	
	WITH temp(id) AS
	(SELECT message_id
	FROM deleted

	UNION ALL
	SELECT message_id
	FROM [Message], temp
	WHERE upmessage_id = id)
	SELECT DISTINCT * INTO #Thread FROM temp
	
	UPDATE [Message] SET upmessage_id = NULL WHERE message_id IN (SELECT * FROM #Thread)

	DELETE FROM [Message] WHERE message_id IN (SELECT * FROM #Thread)

	UPDATE [Object] SET quote_id = NULL 
		FROM [Object] JOIN Enums ON ([Object].[type_id] = enums_id) 
			JOIN #Thread ON (message_id = id)
		WHERE Enums.[name] = 'quote'

	DELETE FROM #Thread
	DROP TABLE #Thread;
GO

/*
* set upmessage_id of root message to null
* updates topic_id in all mesage thread
*/
CREATE TRIGGER RelocateMessageThread
ON [Message]
AFTER UPDATE
AS
	IF (ROWCOUNT_BIG() = 0 OR ROWCOUNT_BIG() > 1 OR NOT UPDATE(topic_id))
	RETURN;

	IF(NOT UPDATE(upmessage_id))
	UPDATE [Message] SET upmessage_id = NULL WHERE message_id IN (SELECT message_id FROM inserted);
	
	WITH temp(id) AS
	(SELECT message_id
	FROM deleted

	UNION ALL
	SELECT message_id
	FROM [Message], temp
	WHERE upmessage_id = id)
	SELECT DISTINCT * INTO #Thread FROM temp

	UPDATE [Message] SET topic_id = (SELECT topic_id FROM inserted) WHERE message_id IN (SELECT * FROM #Thread);
	
	DELETE FROM #Thread
	DROP TABLE #Thread
GO


/*
* deletes all messages of topic
*/
CREATE TRIGGER TopicDelete
ON Topic 
INSTEAD OF DELETE
AS
	IF(ROWCOUNT_BIG() = 0)
	RETURN;

	DELETE FROM [Message] WHERE topic_id IN (SELECT topic_id FROM deleted)
	DELETE FROM Topic WHERE topic_id IN (SELECT topic_id FROM deleted);
GO

/*
* deletes all topics of deleting sections
* deletes all threads of sections 
*/
CREATE TRIGGER SectionDelete
ON Section
INSTEAD OF DELETE
AS
	IF (ROWCOUNT_BIG() = 0)
	RETURN;
	
	WITH temp(id) AS
	(SELECT section_id
	FROM deleted

	UNION ALL
	SELECT section_id
	FROM Section, temp
	WHERE upsection_id = id)
	SELECT DISTINCT * INTO #Thread FROM temp

	DELETE FROM Topic WHERE section_id IN (SELECT * FROM #Thread)
	UPDATE Section SET upsection_id = NULL WHERE section_id IN (SELECT * FROM #Thread)
	DELETE FROM Section WHERE section_id IN (SELECT * FROM #Thread);

	DELETE FROM #Thread
	DROP TABLE #Thread
GO

/*
* checks field 'type'
*/
CREATE TRIGGER ObjectInsert
ON [Object]
AFTER INSERT
AS
	IF(ROWCOUNT_BIG() = 0)
	RETURN;

	IF EXISTS 
		(SELECT * FROM inserted JOIN Enums ON (inserted.[type_id] = enums_id)
		WHERE Enums.[type] != 'Object')
	BEGIN
		ROLLBACK TRANSACTION
		RETURN
	END;
GO

/*
* checks fields
* checks for "intersecting" bans
*/

CREATE TRIGGER BanlistInsert
ON Banlist
AFTER INSERT
AS
	IF(ROWCOUNT_BIG() = 0)
	RETURN;

	IF EXISTS										/*	check fields*/
		(SELECT * FROM inserted JOIN Enums ON (inserted.[type_id] = enums_id)
		WHERE Enums.[type] != 'Banlist' OR
			[name] = 'topic' AND topic_id IS NOT NULL)
	BEGIN
		ROLLBACK TRANSACTION
		RETURN
	END;

	IF EXISTS										/*	double ban check*/
		(SELECT * FROM inserted JOIN ((SELECT * FROM Banlist) EXCEPT (SELECT * FROM inserted)) AS B
			ON (inserted.[type_id] = B.[type_id] AND inserted.[user_id] = B.[user_id]) JOIN Enums ON (inserted.[type_id] = enums_id)
		WHERE (([name] = 'message' AND inserted.topic_id = B.topic_id) OR [name] != 'message') AND
			NOT (inserted.unban_date <= B.ban_date OR inserted.ban_date >= B.unban_date))
	BEGIN
		ROLLBACK TRANSACTION
		RETURN
	END;
	
GO

/*
* checks that user is not banned
*/
CREATE TRIGGER MessageBanlist
ON [Message]
AFTER INSERT
AS
	IF(ROWCOUNT_BIG() = 0)
	RETURN;

	IF EXISTS
		(SELECT * FROM inserted JOIN Banlist ON (inserted.[author_id] = Banlist.[user_id] 
			AND inserted.topic_id = Banlist.topic_id) JOIN Enums ON (Banlist.[type_id] = Enums.[enums_id])
		WHERE [name] = 'message'
			AND inserted.[date] BETWEEN ban_date AND unban_date)
	BEGIN
		ROLLBACK TRANSACTION
		RETURN
	END;
GO

/*
* checks that user is not banned
*/
CREATE TRIGGER TopicBanlist
ON Topic 
AFTER INSERT
AS
	IF(ROWCOUNT_BIG() = 0)
	RETURN;

	IF EXISTS
		(SELECT * FROM inserted JOIN Banlist ON (creator_id = [user_id]) JOIN Enums ON (Banlist.[type_id] = Enums.[enums_id])
		WHERE Enums.[name] = 'topic'
			AND inserted.[date] BETWEEN ban_date AND unban_date)
	BEGIN
		ROLLBACK TRANSACTION
		RETURN
	END;
GO

DELETE FROM Banlist;
DELETE FROM [Object];
DELETE FROM Enums;
DELETE FROM [Message];
DELETE FROM Topic;
DELETE FROM Section;
DELETE FROM [User];
GO

SET IDENTITY_INSERT [User] ON
INSERT [User]([user_id], [login], [password], regist_date, rights, avatar) VALUES (1, 'KunoPaPa', '12345678', convert(datetime, '2007-10-01 00:00', 102), 0, 'http://web-forum.ru/avatars/818173.jpg')
INSERT [User]([user_id], [login], [password], regist_date, rights, avatar) VALUES (2, 'Kisye Mamko', '12345678', convert(datetime, '2006-05-23 14:15', 102), 0, 'http://web-forum.ru/avatars/123456.jpg')
INSERT [User]([user_id], [login], [password], regist_date, rights, avatar) VALUES (3, 'alina13160', '12345678', convert(datetime, '2007-11-13 13:14', 102), 0, 'http://web-forum.ru/avatars/784332.jpg')
INSERT [User]([user_id], [login], [password], regist_date, rights, avatar) VALUES (4, 'whisky70', '12345678', convert(datetime, '2007-03-02 22:15', 102), 0, 'http://web-forum.ru/avatars/023473.jpg')
INSERT [User]([user_id], [login], [password], regist_date, rights, avatar) VALUES (5, 'MamaYana', '12345678', convert(datetime, '2007-05-09 17:40', 102), 0, 'http://web-forum.ru/avatars/456872.jpg')
INSERT [User]([user_id], [login], [password], regist_date, rights, avatar) VALUES (6, 'diesel_rus', '12345678', convert(datetime, '2007-06-25 18:16', 102), 0, 'http://web-forum.ru/avatars/321058.jpg')
INSERT [User]([user_id], [login], [password], regist_date, rights, avatar) VALUES (7, 'Monterini', '12345678', convert(datetime, '2007-06-15 04:00', 102), 0, 'http://web-forum.ru/avatars/021648.jpg')
INSERT [User]([user_id], [login], [password], regist_date, rights, avatar) VALUES (8, 'Gautz', '12345678', convert(datetime, '2007-04-14 17:42', 102), 0, 'http://web-forum.ru/avatars/540670.jpg')
INSERT [User]([user_id], [login], [password], regist_date, rights, avatar) VALUES (9, 'Busena', '12345678', convert(datetime, '2006-02-01 22:25', 102), 0, 'http://web-forum.ru/avatars/011021.jpg')
INSERT [User]([user_id], [login], [password], regist_date, rights, avatar) VALUES (10, 'Yana1298', '12345678', convert(datetime, '2007-06-21 19:30', 102), 0, 'http://web-forum.ru/avatars/348982.jpg')
INSERT [User]([user_id], [login], [password], regist_date, rights, avatar) VALUES (11, 'Bonita', '12345678', convert(datetime, '2007-03-02 15:30', 102), 0, 'http://web-forum.ru/avatars/121687.jpg')
INSERT [User]([user_id], [login], [password], regist_date, rights, avatar) VALUES (12, 'LEV', '12345678', convert(datetime, '2007-06-13 11:00', 102), 0, 'http://web-forum.ru/avatars/148865.jpg')
INSERT [User]([user_id], [login], [password], regist_date, rights, avatar) VALUES (13, '!Chp', '12345678', convert(datetime, '2012-04-21 7:00',102), 1, 'http://web-forum.ru/avatars/122854.jpg')
INSERT [User]([user_id], [login], [password], regist_date, rights, avatar) VALUES (14, '!Tatyana!', '12345678', convert(datetime, '2015-09-04 15:22', 102), 0, 'http://web-forum.ru/avatars/025836.jpg')
INSERT [User]([user_id], [login], [password], regist_date, rights, avatar) VALUES (15, '$vetik', '12345678', convert(datetime, '2013-02-28 21:20', 102), 0, 'http://web-forum.ru/avatars/789456.jpg')
INSERT [User]([user_id], [login], [password], regist_date, rights, avatar) VALUES (16, 'Luna', '12345678', convert(datetime, '2013-07-27 19:03', 102), 0, 'http://web-forum.ru/avatars/326553.jpg')
SET IDENTITY_INSERT [User] OFF
GO

SET IDENTITY_INSERT Section ON
INSERT Section(section_id, [name], upsection_id, [description]) VALUES (1, 'СОДЕРЖАНИЕ МЕЙН КУНА', NULL, '')
INSERT Section(section_id, [name], upsection_id, [description]) VALUES (2, 'ПИТАНИЕ', NULL, '')
INSERT Section(section_id, [name], upsection_id, [description]) VALUES (8, '', NULL, '')
INSERT Section(section_id, [name], upsection_id, [description]) VALUES (3, 'ДОМ МЕЙН КУНА', 1, 'Как обустроить свой дом, сделав его комфортным для кунов без ущерба для себя.')
INSERT Section(section_id, [name], upsection_id, [description]) VALUES (4, 'Поведение и прочие вопросы содержания', 1, '')
INSERT Section(section_id, [name], upsection_id, [description]) VALUES (5, 'Натуральное питание', 2, 'Это вкусное слово натура :) всё о том, как кормить натуральными продуктами - личный опыт, советы, вопросы - о вреде, о пользе, о качестве.')
INSERT Section(section_id, [name], upsection_id, [description]) VALUES (6, 'Питание промышленными кормами', 2, 'Всё о сухих кормах и кошачьих консервах, личный опыт, советы, вопросы - о вреде, о пользе, о качестве.')
INSERT Section(section_id, [name], upsection_id, [description]) VALUES (7, 'HAPPY CAT', 6, 'Раздел представляет поставщик данного корма в Россию.')
SET IDENTITY_INSERT Section OFF
GO

SET IDENTITY_INSERT Topic ON
INSERT Topic(topic_id, [name], section_id, creator_id, [date]) VALUES(1, 'Как предотвратить выпадение мейн куна из окна.', 3, 1, convert(datetime,'2008-03-03 12:57',120))
INSERT Topic(topic_id, [name], section_id, creator_id, [date]) VALUES(2, 'Наполнители', 3, 2, convert(datetime, '2010-04-09 05:04', 120))
INSERT Topic(topic_id, [name], section_id, creator_id, [date]) VALUES(3, 'Кун и беременность.', 4, 3, convert(datetime, '2019-11-09 12:42', 120))
INSERT Topic(topic_id, [name], section_id, creator_id, [date]) VALUES(4, 'Зачем Вы завели котея?', 4, 4, convert(datetime, '2018-09-14 23:20', 120))
INSERT Topic(topic_id, [name], section_id, creator_id, [date]) VALUES(5, 'Натуральный корм Hati', 5, 5, convert(datetime, '2019-01-16 17:07', 120))
INSERT Topic(topic_id, [name], section_id, creator_id, [date]) VALUES(6, 'Кормление суточными цыплятами', 5, 6, convert(datetime, '2010-12-09 03:02', 120))
INSERT Topic(topic_id, [name], section_id, creator_id, [date]) VALUES(7, 'Выбор консерв', 6, 7, convert(datetime, '2009-11-11 12:30', 120))
INSERT Topic(topic_id, [name], section_id, creator_id, [date]) VALUES(8, 'Какие консервы выбрать в качестве лакомства с идеальным соотношением цена / качество.', 6, 8, convert(datetime, '2020-01-16 22:59', 120))
INSERT Topic(topic_id, [name], section_id, creator_id, [date]) VALUES(9, 'Акции и скидки (корма HAPPY CAT)', 7, 9, convert(datetime, '2016-03-17 11:48', 120))
INSERT Topic(topic_id, [name], section_id, creator_id, [date]) VALUES(10, 'Корм Happy Cat', 7, 10, convert(datetime, '2012-10-19 17:33', 120))
SET IDENTITY_INSERT Topic OFF
GO

INSERT Enums VALUES (1, 'Object','text')
INSERT Enums VALUES (2, 'Object', 'quote')
INSERT Enums VALUES (3, 'Object', 'image')

INSERT Enums VALUES (21, 'Banlist', 'message')
INSERT Enums VALUES (22, 'Banlist', 'topic')
GO

SET IDENTITY_INSERT Banlist ON
INSERT Banlist(banlist_id, [user_id], [type_id], topic_id, moder_id, ban_date, unban_date, [source]) VALUES (1, 14, 21, 3, 13, convert(datetime, '2018-03-14 19:00', 102), convert(datetime, '2018-03-14 21:00', 102), 'offtop')
INSERT Banlist(banlist_id, [user_id], [type_id], topic_id, moder_id, ban_date, unban_date, [source]) VALUES (2, 15, 21, 8, 13, convert(datetime, '2019-03-08 14:00', 102), convert(datetime, '2020-03-08 14:00', 102), 'Нецензурная брань, оскорбления')
INSERT Banlist(banlist_id, [user_id], [type_id], topic_id, moder_id, ban_date, unban_date, [source]) VALUES (3, 16, 22, NULL, 13, convert(datetime, '2020-02-07 03:50', 102), convert(datetime, '2020-03-07 03:50', 102), 'Нарушения при создании темы')
SET IDENTITY_INSERT Banlist OFF
GO

SET IDENTITY_INSERT [Message] ON
INSERT [Message](message_id, topic_id, upmessage_id, author_id, [date], header) VALUES (1, 1, NULL, 1, convert(datetime,'2008-03-03 12:57',120), 'Как предотвратить выпадение мейн куна из окна.')
INSERT [Object](message_id, [type_id], [value], object_num) VALUES (1, 1, 'Мейн кун - очень подвижный и любопытный кот, к тому же очень мощный и хорошо прыгающий.
В азарте погони за пролетающей птичкой, он, к сожалению, может и не заметить, что выпрыгнул из окна. Хорошо если у Вас перывый этаж, и если Вы заметили это и помогли котегу вернутся домой - а если не первый или это произошло без Вас...
Поэтому - необходимо обезопасить мейн куна, исключить любую возможность покидания квартиры через окно.
Даже если Вы сами не открываете окна, и следите за кунами постоянно, никто не гарантирует, что это беспечно и бездумно не сделает кто то из Ваших гостей или просто людей оказавшихся у Вас дома, как говорят на грех и грабли стреляют.
Обычные москитные сетки, которыми комплектуются пластиковые окна - для мейн куна не преграда, разорвать или просто выбить такую сетку ему вполне по силам.
Есть много способов, какой Вы выберете для себя, решать Вам, но продумать и решить этот вопрос в любом случае необходимо.
Как сделал я:
У меня 14-й этаж - поэтому проблема актуальна.
Решал так:
Сделали москитные сетки - специальные - Антикошка, заказывали из Москвы.
Более плотная и прочная чем обычные, специално предназначенная для этих целей.
Сначала привезли в дом - дал куням поигратся, поставил на балконе - не порвали.
Начали ставить - оказалось, что штатное крепление не годится, слишком хлипкое.
Пообещал оконщикам - что прочность крепления буду проверять на них, выдержит их сетка или нет, если их попробовать из окна вытолкнуть.
Уехали думать...
В итоге - зашили квартиру наглухо...
Сетки крепили с наружи с четырёх сторон, саморезами в армировку, через Z - образный профиль.
Низ верх по всей длинне, боковины по вертикали 50% длинны.
Обошлись без альпинистов - просто вынимали соседние глухие пакеты и крепили.
Плохо получилось только в одном распашном угловом окне - одна сторона слабая, подумаю чем укрепить.
Но это окно практически не открываю.
Давить пробовал держит вроде бы хорошо.
Так же на окнах стоят дополнительные блокираторы на открывание и ограничители угла откидывания.
Окна открываю только в режиме откидывания, с ограничителем на малый угол.
Но всё равно при этом - куней без присмотра стараюсь не оставлять при откинутом окне.
Зиму прожили нормально ТТТ.', 1)

INSERT [Message](message_id, topic_id, upmessage_id, author_id, [date], header) VALUES (2, 1, 1, 11, convert(datetime, '2008-03-03 16:49', 120), NULL)
INSERT [Object](message_id, [type_id], [value], object_num) VALUES (2, 1, 'Совет отличный! проблема та же. Дай, плиз, адрес компании,в которой ты заказывал сетки.', 1)

INSERT [Message](message_id, topic_id, upmessage_id, author_id, [date], header) VALUES (3, 1, 2, 1, convert(datetime, '2008-03-03 18:09', 102), NULL)
INSERT [Object](message_id, [type_id], [value], quote_author_id, quote_id, object_num) VALUES (3, 2, 'Совет отличный! проблема та же. Дай, плиз, адрес компании,в которой ты заказывал сетки.', 11, 2, 1)
INSERT [Object](message_id, [type_id], [value], object_num) VALUES (3, 1, '+79178180410 Павел, ему от меня привет 
В Самаре наверное кроме как у него, больше ни у кого нет сетки.', 2)

INSERT [Message](message_id, topic_id, upmessage_id, author_id, [date], header) VALUES (4, 1, 1, 12, convert(datetime, '2008-03-04 00:58', 102), NULL)
INSERT [Object](message_id, [type_id], [value], object_num) VALUES (4, 1, 'Очень нужная тема!!!!
Спасибо, Георгий! Меньше будет работы при консультировании!
Нельзя для Москвичей уточнить название фирмы и другие контакты или координаты. Не очень удобно заказывать сетки из Москвы через Самару, живя с первопрестольной!', 1)

INSERT [Message](message_id, topic_id, upmessage_id, author_id, [date], header) VALUES (5, 1, 4, 1, convert(datetime, '2008-03-04 08:57', 102), 'координаты московских фирм')
INSERT [Object](message_id, [type_id], [value], quote_author_id, quote_id, object_num) VALUES (5, 2, 'Очень нужная тема!!!!
Спасибо, Георгий! Меньше будет работы при консультировании!
Нельзя для Москвичей уточнить название фирмы и другие контакты или координаты. Не очень удобно заказывать сетки из Москвы через Самару, живя с первопрестольной!', 12, 4, 1)
INSERT [Object](message_id, [type_id], [value], object_num) VALUES (5, 1, 'Вообще в Москве такие услуги представляют многие фирмы - Москва более продвинутый город, чем Самара  , я первоначально брал информацию здесь - okna-spas.
Но ещё раз повторяю - ШТАТНЫЕ КРЕПЛЕНИЯ НЕОБХОДИМО УСИЛИВАТЬ - несмотря на все заверения оконных дел мастеров.', 2)

INSERT [Message](message_id, topic_id, upmessage_id, author_id, [date], header) VALUES (6, 1, 1, 1, convert(datetime, '2008-10-17 09:25', 102), 'Страшно')
INSERT [Object](message_id, [type_id], [value], object_num) VALUES (6, 1, 'В очередно раз сеть наводнили грустные сообщения - куны продолжают покидать этот мир выпадая из окон!
Стандартны окна ПВХ без доработок опасны для кунов на высоких этажах.
Во первых - может быть открыто окно всегда случайно и забыто.
Во вторых - положение откидное - так же опасно - особенно на высоком окне, так как кошка может не только выпасть но и застрять.', 1)
INSERT [Object](message_id, [type_id], [value], width, height, object_num) VALUES (6, 3, 'http://img-fotki.yandex.ru/get/1/kunopapa.25/0_f8fc_4c164224_XL.jpg', 800, 534, 2)
INSERT [Object](message_id, [type_id], [value], object_num) VALUES (6, 1, 'В правом верхнем углу фотографии видно дополнительный блокировщик установленный на окно - он затрудняет открытие окна на распашку и ограничивает угол откидывания - так же позволяет открыть окно в распашном режиме с просветом 2 см.
Можно заблокировать ключём, в этом случае снять блокировку случайно не возможно.
Стоит - копейки - работает пока надёжно.', 3)
SET IDENTITY_INSERT [Message] OFF

GO