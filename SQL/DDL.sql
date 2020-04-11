create table user
(
	id		VARCHAR(5)  NOT NULL,
	username	VARCHAR(20) NOT NULL,
	password	VARCHAR(10) NOT NULL,
	name		VARCHAR(20) NOT NULL,
	address		VARCHAR(30),
	phone		NUMERIC(10,0),
	email		VARCHAR(60) NOT NULL,
	owner		NUMERIC(1,0) CHECK (owner IN (1,0)),
	PRIMARY KEY (id, username)
);

create table publisher
(
	id		VARCHAR(5)   NOT NULL,
	name		VARCHAR(100) NOT NULL,
	address		VARCHAR(60),
	phone		NUMERIC(10,0),
	email		VARCHAR(60)  NOT NULL,
	account		NUMERIC(8,2) CHECK (account >= 0),
	PRIMARY KEY (id)
);

create table author
(
	id		VARCHAR(5)  NOT NULL,
	name		VARCHAR(100) NOT NULL,
	phone		NUMERIC(10,0),
	PRIMARY KEY (id)
);

create table warehouse
(
	code		VARCHAR(10) NOT NULL,
	address		VARCHAR(30),
	phone		NUMERIC(10,0),
	PRIMARY KEY (code)
);

create table book
(
	isbn		VARCHAR(13)  NOT NULL,
	title		VARCHAR(100) NOT NULL,
	genre		VARCHAR(40)  NOT NULL,
	year		NUMERIC(4,0),
	pages		NUMERIC(4,0) CHECK (pages > 0),
	price		NUMERIC(5,2) CHECK (price >= 0),
	profit		NUMERIC(8,2),
	publisher_id	VARCHAR(5)   NOT NULL,
	warehouse_code	VARCHAR(10),
	PRIMARY KEY (isbn),
	FOREIGN KEY (publisher_id) REFERENCES publisher (id) ON DELETE CASCADE,
	FOREIGN KEY (warehouse_code) REFERENCES warehouse (code) ON DELETE SET NULL
);

create table write
(
	author_id	VARCHAR(5)  NOT NULL,
	isbn		VARCHAR(13) NOT NULL,
	PRIMARY KEY (author_id, isbn),
	FOREIGN KEY (author_id) REFERENCES author (id) ON DELETE CASCADE,
	FOREIGN KEY (isbn) REFERENCES book ON DELETE CASCADE
);

create table order
(
	id		VARCHAR(5) NOT NULL,
	quantity	NUMERIC(2,0) CHECK (quantity > 0),
	date		DATE,
	user_id		VARCHAR(5),
	warehouse_code	VARCHAR(10),
	PRIMARY KEY (id),
	FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,
	FOREIGN KEY (warehouse_code) REFERENCES warehouse (code) ON DELETE SET NULL
);

create table make
(
	order_id	VARCHAR(5) NOT NULL,
	location	VARCHAR(30),
	PRIMARY KEY (order_id),
	FOREIGN KEY (order_id) REFERENCES order (id) ON DELETE CASCADE
);

create table contain
(
	order_id	VARCHAR(5)  NOT NULL,
	isbn		VARCHAR(13) NOT NULL,
	PRIMARY KEY (order_id, isbn),
	FOREIGN KEY (order_id) REFERENCES order (id) ON DELETE CASCADE,
	FOREIGN KEY (isbn) REFERENCES book
);