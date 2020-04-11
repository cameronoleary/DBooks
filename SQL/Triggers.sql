-- [ TRIGGERS ]

--

/*
 * Trigger: cap_warehouse
 * Purpose: to prevent more than 1 warehouse from being inserted, per requirement specs.
 */
CREATE TRIGGER cap_warehouse BEFORE INSERT ON project.warehouse
	FOR EACH ROW EXECUTE PROCEDURE project.cap_warehouse();

--

/*
 * Trigger: update_publisher_account
 * Purpose: to update the account of publishers with the profit amount of a book when their book is sold (e.g., inserted into contain).
 */
CREATE TRIGGER update_publisher_account AFTER INSERT ON project.contain
	FOR EACH ROW EXECUTE PROCEDURE project.update_publisher_account();

--

/*
 * Trigger: order_new_books
 * Purpose: to trigger an order from publisher for new books if their remaining quantity < 10 when a tuple has been inserted into contain.
 *
 * Notes:
 * - Trigger function does the looping, no need to execute function for every insertion
 */
CREATE TRIGGER order_new_books AFTER INSERT ON project.contain
	EXECUTE PROCEDURE project.order_new_books();