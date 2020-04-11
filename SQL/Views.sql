-- [ VIEWS ]

/*
 * View: report_sales_vs_expenditures
 * Purpose: to get the total sales and expenditures of books for every month.
 * 
 * Notes:
 * - Expenditures are calculated at 85% of total sales after paying the publisher their profit
 * - Order is used to get the date
 * - JOIN on contain is used to get the sold books
 * - JOIN on book is used to get the price and profit of the sold books to calculate sales and expenditures
 */
CREATE VIEW project.report_sales_vs_expenditures AS
SELECT date, ROUND(SUM(price), 2) AS sales, ROUND(SUM(price - profit)*0.85, 2) AS expenditures
FROM (project.order JOIN project.contain ON id = order_id) NATURAL JOIN project.book
GROUP BY date

--

/*
 * View: report_sales_per_genre
 * Purpose: to get the total sales for each genre.
 *
 * Notes:
 * - Contain is used to get the sold books
 * - NATURAL JOIN on book is used to get the price of the sold books
 */
CREATE VIEW project.report_sales_per_genre AS
SELECT genre, ROUND(SUM(price), 2) AS sales
FROM project.contain NATURAL JOIN project.book
GROUP BY genre

--

/*
 * View: report_sales_per_author
 * Purpose: to get the total sales for each author.
 *
 * Notes:
 * - Contain is used to get the sold books
 * - NATURAL JOIN on write is used to get the author IDs of the sold books
 * - NATURAL JOIN on book is used to get the price of the sold books
 * - JOIN on author is used to get the author name
 */
CREATE VIEW project.report_sales_per_author AS
SELECT name, ROUND(SUM(price), 2) AS sales
FROM ((project.contain NATURAL JOIN project.write) NATURAL JOIN project.book) JOIN project.author ON author_id = id
GROUP BY name

--

/*
 * View: remaining_per_publisher
 * Purpose: to get the remaining copies of books when their book(s) are ordered when the amount is less than 10.
 *
 * Notes:
 * - NATURAL JOIN on book where the ISBNs of books have been ordered (e.g., are in contain) and the amount remaining for that publisher < 10 
 */
CREATE VIEW project.remaining_per_publisher AS
SELECT * FROM

(SELECT publisher_id, SUM(quantity - ordered) AS remaining FROM

(SELECT publisher_id, quantity, COUNT(isbn) AS ordered
FROM project.book NATURAL JOIN
(SELECT publisher_id, COUNT(isbn) AS quantity
FROM project.book GROUP BY publisher_id) AS temp
WHERE isbn IN (SELECT isbn FROM project.contain)
GROUP BY publisher_id, quantity) AS temp1

GROUP BY publisher_id) AS temp2

WHERE remaining < 10

--

/*
 * View: books_to_order
 * Purpose: to get the books for publishers to order.
 *
 * Notes:
 * - Order is used to get last month's date
 * - JOIN book NATURAL JOIN contain is used to get the books where the publisher IDs of sold books are in remaining_per_publisher (e.g., have a quantity < 10)
 * - Substring-ing is used to get last months date
 */
CREATE VIEW project.books_to_order AS
SELECT * FROM project.book
WHERE isbn IN (SELECT isbn FROM project.order JOIN (SELECT * FROM project.book NATURAL JOIN project.contain WHERE publisher_id IN (SELECT publisher_id FROM project.remaining_per_publisher)) AS temp ON id = order_id
WHERE CAST(date AS VARCHAR) LIKE CONCAT(CAST((SELECT CONCAT(SUBSTRING(CAST(CURRENT_DATE AS VARCHAR) FROM 1 FOR 5), CONCAT('0', CAST(SUBSTRING(CAST(CURRENT_DATE AS VARCHAR) FROM 6 FOR 2) AS INTEGER) - 1))) AS VARCHAR), '%'))