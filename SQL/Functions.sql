-- [ FUNCTIONS ]

/*
 * Function: insert_author
 * Purpose: to insert an author into the author table.
 */
CREATE FUNCTION project.insert_author(_id VARCHAR(5), _name VARCHAR(100), _phone NUMERIC(10,0))
RETURNS VOID AS
$$
BEGIN
	INSERT INTO project.author (id, name, phone)
	VALUES (_id, _name, _phone);
END;
$$
LANGUAGE PLPGSQL;

--

/*
 * Function: insert_publisher
 * Purpose: to insert a publisher into the publisher table.
 */
CREATE FUNCTION project.insert_publisher(_id VARCHAR(5), _name VARCHAR(100), _address VARCHAR(60), _phone NUMERIC(10,0), _email VARCHAR(60), _account NUMERIC(8,2))
RETURNS VOID AS
$$
BEGIN
	INSERT INTO project.publisher (id, name, address, phone, email, account)
	VALUES (_id, _name, _address, _phone, _email, _account);
END;
$$
LANGUAGE PLPGSQL;

--

/*
 * Function: insert_book
 * Purpose: to insert a book into the book table.
 */
CREATE FUNCTION project.insert_book(_isbn VARCHAR(13), _title VARCHAR(100), _genre VARCHAR(40), _year NUMERIC(4,0), _pages NUMERIC(4,0), _price NUMERIC(5,2), _profit NUMERIC(8,2), _publisher_id VARCHAR(5))
RETURNS VOID AS
$$
BEGIN
	INSERT INTO project.book (isbn, title, genre, year, pages, price, profit, publisher_id, warehouse_code)
	VALUES (_isbn, _title, _genre, _year, _pages, _price, _profit, _publisher_id, (SELECT code FROM project.warehouse));
END;
$$
LANGUAGE PLPGSQL;

--

/*
 * Function: insert_write
 * Purpose: to insert a tuple into the write table for an author (_author_id) that wrote a book (_isbn).
 */
CREATE FUNCTION project.insert_write(_author_id VARCHAR(5), _isbn VARCHAR(13))
RETURNS VOID AS
$$
BEGIN
	INSERT INTO project.write (author_id, isbn)
	VALUES (_author_id, _isbn);
END;
$$
LANGUAGE PLPGSQL;

--

/*
 * Function: insert_make
 * Purpose: to insert a tuple into the make table for an order that was made.
 */
CREATE FUNCTION project.insert_make(_order_id VARCHAR(5), _location VARCHAR(30))
RETURNS VOID AS
$$
BEGIN
	INSERT INTO project.make (order_id, location)
	VALUES (_order_id, _location);
END;
$$
LANGUAGE PLPGSQL;

--

/*
 * Function: insert_order
 * Purpose: to insert an order into the order table.
 */
CREATE FUNCTION project.insert_order(_id VARCHAR(5), _quantity NUMERIC(2,0), _date DATE, _user_id VARCHAR(5))
RETURNS VOID AS
$$
BEGIN
	INSERT INTO project.order (id, quantity, date, user_id, warehouse_code)
	VALUES (_id, _quantity, _date, _user_id, (SELECT code FROM project.warehouse));
END;
$$
LANGUAGE PLPGSQL;

--

/*
 * Function: insert_contain
 * Purpose: to insert a tuple into the contain table for an order that was made.
 */
CREATE FUNCTION project.insert_contain(_order_id VARCHAR(5), _isbn VARCHAR(13)) RETURNS VOID AS
$$
BEGIN
	INSERT INTO project.contain (order_id, isbn)
	VALUES (_order_id, _isbn);
END;
$$
LANGUAGE PLPGSQL;

--

-- [ TRIGGER FUNCTIONS ]

/*
 * Function: update_publisher_account
 * Purpose: to update the account value of a publisher when their book has been ordered (e.g., inserted into contain) by adding the profit of the book to the existing amount.
 */
CREATE FUNCTION project.update_publisher_account() RETURNS TRIGGER AS $update_publisher_account$
DECLARE
pub_id VARCHAR;
pub_acc NUMERIC;
acc_val NUMERIC;
BEGIN
	pub_id  = (SELECT publisher_id FROM project.book      WHERE isbn = NEW.isbn);
	acc_val = (SELECT profit       FROM project.book      WHERE isbn = NEW.isbn);
	pub_acc = (SELECT account      FROM project.publisher WHERE id   = pub_id);
	UPDATE project.publisher
	SET account = pub_acc + acc_val
	WHERE id = pub_id;
	RETURN NEW;
END;
$update_publisher_account$
LANGUAGE PLPGSQL;

--

/*
 * Function: cap_warehouse
 * Purpose: to prevent more than 1 warehouse from being inserted, per requirement specs.
 */
CREATE FUNCTION project.cap_warehouse() RETURNS TRIGGER AS $cap_warehouse$
DECLARE
_count INTEGER;
BEGIN
	_count = (SELECT COUNT(*) FROM project.warehouse);
	IF (_count = 1) THEN
		RETURN NULL;
	END IF;
	RETURN NEW;
END;
$cap_warehouse$
LANGUAGE PLPGSQL;

--

/*
 * Function: order_new_books
 * Purpose: to place orders for publishers whos remaining books for sale is a quantity < 10.
 *
 * Note:
 * - Since this function loops over the contain table, publisher orders will not be inserted (e.g., infinite recursion)
 *   - To remedy this, orders placed by publishers are marked automatically as 'Delivered' and placed in the book table
 * - An order for the publisher is placed in order and is marked as an ID of NULL (since ID comes from user)
 * - Respective tuple in make table is inserted as well for the order ID
 */
CREATE FUNCTION project.order_new_books() RETURNS TRIGGER AS $order_new_books$
DECLARE
_row RECORD;
_id VARCHAR;
_isbn VARCHAR;
_profit NUMERIC;
_quantity NUMERIC;
_temp_pub_id VARCHAR;
BEGIN
	_temp_pub_id = '';
	FOR _row IN
		SELECT * FROM project.books_to_order
	LOOP
		_id          = (SELECT CAST(MAX(CAST(id AS BIGINT)) + 1 AS VARCHAR) FROM project.order);
		_isbn        = (SELECT CAST(CAST(MAX(isbn) AS BIGINT) + 1 AS VARCHAR) FROM project.book);
		_profit      = (ROUND(CAST(RANDOM() * _row.price AS NUMERIC), 2));
		_quantity    = (SELECT CAST(COUNT(*) AS NUMERIC) FROM project.books_to_order GROUP BY _row.publisher_id);
		-- Every row we want to insert a book; but, not every row do we want to create an order for the books.
		PERFORM project.insert_book(_isbn, _row.title, _row.genre, _row.year, _row.pages, _row.price, _profit, _row.publisher_id);
		IF (_row.publisher_id != _temp_pub_id) THEN
			PERFORM project.insert_order(_id, _quantity, CURRENT_DATE, NULL);
			PERFORM project.insert_make(_id, 'Delivered');
			_temp_pub_id = _row.publisher_id;
		END IF;
	END LOOP;
	RETURN NEW;
END;
$order_new_books$
LANGUAGE PLPGSQL;