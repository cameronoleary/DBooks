import java.sql.*;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class QueryManager
{
    private Connection Conn;
    private Statement Statement;

    public QueryManager(Connection conn)
    {
        Conn = conn;

        CreateStatement();
    }

    /*
     * Method: CreateStatement
     * Purpose: to initialize the Statement class variable.
     */
    private void CreateStatement()
    {
        try
        {
            Statement = Conn.createStatement();
        }
        catch (Exception e)
        {
            System.out.println("An exception was caught: " + e.getMessage());
        }
    }

    /*
     * Method: ImporterAddPublisher
     * Purpose: to insert a publisher into the publisher table in the database.
     *
     * Parameters:
     * - data (String[]), the data for the tuple to be added to the publisher table
     *
     * Note: this should be changed to private after using the Importer tool.
     */
    public void ImporterAddPublisher(String[] data)
    {
        // SQL's NUMERIC type is Java's BigDecimal
        BigDecimal phone;
        BigDecimal account;

        String id;
        String name;
        String address;
        String email;
        String call;
        CallableStatement callSt;

        try
        {
            id      = data[0];
            name    = data[1];
            address = data[2];
            phone   = new BigDecimal(data[3]);
            email   = data[4];
            account = new BigDecimal(data[5]);

            // Initialize the database call to the insert_publisher() SQL function.
            call = "{call " + ConnectionManager.Schema + ".insert_publisher(?, ?, ?, ?, ?, ?)}";

            // Prepare call and initialize its parameters, then execute it.
            callSt = Conn.prepareCall(call);
            callSt.setString(1, id);
            callSt.setString(2, name);
            callSt.setString(3, address);
            callSt.setBigDecimal(4, phone);
            callSt.setString(5, email);
            callSt.setBigDecimal(6, account);
            callSt.execute();
        }
        catch(Exception e)
        {
            System.out.println("An exception was caught: " + e.getMessage());
        }
    }

    /*
     * Method: ImporterAddPublisher
     * Purpose: to insert an author into the author table in the database.
     *
     * Parameters:
     * - data (String[]), the data for the tuple to be added to the author table
     *
     * Note: this should be changed to private after using the Importer tool.
     */
    public void ImporterAddAuthor(String[] data)
    {
        // SQL's NUMERIC type is Java's BigDecimal
        BigDecimal phone;

        String id;
        String name;
        String insertAuthor;
        CallableStatement callSt;

        try
        {
            id    = data[0];
            name  = data[1];
            phone = new BigDecimal(data[2]);

            // Initialize the database call to the insert_publisher() SQL function.
            insertAuthor = "{call " + ConnectionManager.Schema + ".insert_author(?, ?, ?)}";

            // Prepare call and initialize its parameters, then execute it.
            callSt = Conn.prepareCall(insertAuthor);
            callSt.setString(1, id);
            callSt.setString(2, name);
            callSt.setBigDecimal(3, phone);
            callSt.execute();
        }
        catch(Exception e)
        {
            System.out.println("An exception was caught: " + e.getMessage());
        }
    }

    /*
     * Method: IsRegistered
     * Purpose: to check if a user is registered with the bookstore.
     *
     * Parameters:
     * - username (String), the username of the user to check if registered
     * - password (String), the password of the user to check if registered
     *
     * Return: boolean
     * - true, the user is registered with the bookstore
     * - false, the user is not registered with the bookstore
     *
     * Pitfall: this method assumes no duplicate entries for checking the username and password in the database.
     */
    public boolean IsRegistered(String username, String password)
    {
        String query;
        String table;
        String condition;
        ResultSet result;

        try
        {
            table     = ConnectionManager.Schema + ".user";
            condition = " WHERE username = \'" + username + "\' AND password = \'" + password + "\'";
            query     = "SELECT username, password FROM " + table + condition;

            result = Statement.executeQuery(query);

            if (result.next()) { return true; }
        }
        catch (Exception e)
        {
            System.out.println("An exception was caught: " + e.getMessage());
        }

        return false;
    }

    /*
     * Method: Register
     * Purpose: to register a user with the bookstore.
     *
     * Parameters:
     * - username (String), the username of the user to register
     * - password (String), the password of the user to register
     * - name (String), the name of the user to register
     * - address (String), the address of the user to register
     * - email (String), the email of the user to register
     * - owner (boolean), true sets the registered user to a bookstore owner; otherwise false, a normal user
     *
     * Return: boolean
     * - true, the user was registered successfully
     * - false, an error occurred trying to register the user
     */
    public boolean Register(String username, String password, String name, String address, String phone, String email, boolean owner)
    {
        int id;
        int ownerToInt;
        boolean success;
        String table;
        String query;
        String prefix;
        String attributes;
        ResultSet result;

        try
        {
            id         = 0;
            ownerToInt = owner ? 1 : 0;
            table      = ConnectionManager.Schema + ".user";
            attributes = "id, username, password, name, address, phone, email, owner";
            prefix     = "INSERT INTO " + table + " (" + attributes + ") VALUES ";
            query      = "SELECT MAX(CAST(id AS BIGINT)) FROM " + ConnectionManager.Schema + ".user";
            result     = Statement.executeQuery(query);

            if (result.next())
            {
                // Get the latest user ID if one exists.
                if (result.getString(1) != null)
                {
                    id = Integer.parseInt(result.getString(1)) + 1;
                }
            }

            query = prefix + "(\'" + id + "\', \'" + username + "\', \'" + password + "\', \'" + name
                    + "\', \'" + address + "\', \'" + phone + "\', \'" + email + "\', " + ownerToInt + ");";

            Statement.execute(query);

            success = true;
        }
        catch (Exception e)
        {
            System.out.println("An exception was caught: " + e.getMessage());
            success = false;
        }

        return success;
    }

    /*
     * Method: GetUserInfo
     * Purpose: to get information on the current user so their ID and owner status can be stored internally for reference in SessionInfo.
     *
     * Parameters:
     * - username (String), the username of the user get information from
     *
     * Return: ResultSet
     * - A set containing the user's ID and owner status
     */
    public ResultSet GetUserInfo(String username)
    {
        String table;
        String query;
        String condition;
        ResultSet result;

        try
        {
            table     = ConnectionManager.Schema + ".user";
            condition = " WHERE username = \'" + username + "\'";
            query     = "SELECT id, owner FROM " + table + condition;
            result    = Statement.executeQuery(query);
        }
        catch (Exception e)
        {
            System.out.println("An exception was caught: " + e.getMessage());
            result = null;
        }

        return result;
    }

    /*
     * Method: AddBook
     * Purpose: to insert a book into the book table in the database.
     *
     * Parameters:
     * - isbn (String), the ISBN of the book to be inserted
     * - title (String), the title of the book to be inserted
     * - genre (String), the genre of the book to be inserted
     * - year (String), the year of the book to be inserted
     * - pages (String), the page count of the book to be inserted
     * - price (String), the price of the book to be inserted
     * - authors (String[]), the authors of the book to be inserted
     * - publisher (String), the publisher of the book to be inserted
     *
     * Return: boolean
     * - true, the book was inserted successfully
     * - false, an error occurred trying to insert the book
     */
    public boolean AddBook(String isbn, String title, String genre, String year, String pages, String price, String[] authors, String publisher)
    {
        String call;
        String query;
        String insertBook;
        String insertWrite;

        // SQL's NUMERIC type is Java's BigDecimal
        BigDecimal yearToBigDecimal;
        BigDecimal pagesToBigDecimal;
        BigDecimal priceToBigDecimal;
        BigDecimal profitToBigDecimal;

        int authorId;
        int publisherId;
        boolean success;
        ResultSet result;
        double calculatedProfit;
        CallableStatement callSt;
        ArrayList<Integer> authorIds;

        try
        {
            authorIds = new ArrayList<>();

            for (String name : authors)
            {
                authorId = GetIdFromAuthorName(name);

                if (authorId == 0)
                {
                    // This author does not exist yet, so make a new entry for them in the author table.
                    query  = "SELECT MAX(CAST(id AS BIGINT)) FROM " + ConnectionManager.Schema + ".author";
                    result = Statement.executeQuery(query);

                    if (result.next())
                    {
                        if (result.getString(1) != null)
                        {
                            authorId = Integer.parseInt(result.getString(1)) + 1;
                        }
                    }

                    call   = "{call " + ConnectionManager.Schema + ".insert_author(?, ?, ?)}";
                    callSt = Conn.prepareCall(call);
                    callSt.setString(1, Integer.toString(authorId));
                    callSt.setString(2, name);
                    callSt.setBigDecimal(3, BigDecimal.ZERO);
                    callSt.execute();
                }

                authorIds.add(authorId);
            }

            publisherId = GetIdFromPublisherName(publisher);

            if (publisherId == 0)
            {
                // This publisher does not exist yet, so make a new entry for them in the publisher table.
                query  = "SELECT MAX(CAST(id AS BIGINT)) FROM " + ConnectionManager.Schema + ".publisher";
                result = Statement.executeQuery(query);

                if (result.next())
                {
                    if (result.getString(1) != null)
                    {
                        publisherId = Integer.parseInt(result.getString(1)) + 1;
                    }
                }

                call   = "{call " + ConnectionManager.Schema + ".insert_publisher(?, ?, ?, ?, ?, ?)}";
                callSt = Conn.prepareCall(call);
                callSt.setString(1, Integer.toString(publisherId));
                callSt.setString(2, publisher);
                callSt.setString(3, "");
                callSt.setBigDecimal(4, BigDecimal.ZERO);
                callSt.setString(5, publisher.toLowerCase() + "@pub.com");
                callSt.setBigDecimal(6, BigDecimal.ZERO);
                callSt.execute();
            }

            calculatedProfit   = Double.parseDouble(price) * Math.random();
            yearToBigDecimal   = new BigDecimal(year);
            pagesToBigDecimal  = new BigDecimal(pages);
            priceToBigDecimal  = new BigDecimal(price);
            profitToBigDecimal = new BigDecimal(calculatedProfit);

            // Initialize the database call to the insert_book() SQL function.
            insertBook = "{call " + ConnectionManager.Schema + ".insert_book(?, ?, ?, ?, ?, ?, ?, ?)}";

            // Prepare call and initialize its parameters, then execute it.
            callSt = Conn.prepareCall(insertBook);
            callSt.setString(1, isbn);
            callSt.setString(2, title);
            callSt.setString(3, genre);
            callSt.setBigDecimal(4, yearToBigDecimal);
            callSt.setBigDecimal(5, pagesToBigDecimal);
            callSt.setBigDecimal(6, priceToBigDecimal);
            callSt.setBigDecimal(7, profitToBigDecimal);
            callSt.setString(8, Integer.toString(publisherId));
            callSt.execute();

            // Book insertion successful, now insert into the write table for the book authors.
            for (Integer id : authorIds)
            {
                insertWrite = "{call " + ConnectionManager.Schema + ".insert_write(?, ?)}";

                callSt = Conn.prepareCall(insertWrite);
                callSt.setString(1, Integer.toString(id));
                callSt.setString(2, isbn);
                callSt.execute();
            }

            success = true;
        }
        catch (Exception e)
        {
            System.out.println("QueryManager.AddBook - An exception was caught: " + e.getMessage());
            success = false;
        }

        return success;
    }

    /*
     * Method: RemoveBook
     * Purpose: to remove a book from the book table in the database.
     *
     * Parameters:
     * - isbn (String), the ISBN of the book to be removed
     *
     * Return: boolean
     * - true, the book was removed successfully
     * - false, an error occurred trying to remove the book
     */
    public boolean RemoveBook(String isbn)
    {
        boolean success;
        String table;
        String query;
        String condition;

        try
        {
            table     = ConnectionManager.Schema + ".book";
            condition = " WHERE isbn = \'" + isbn + "\'";
            query     = "DELETE FROM " + table + condition;

            Statement.executeQuery(query);

            success = true;
        }
        catch (Exception e)
        {
            System.out.println("QueryManager.RemoveBook - An exception was caught: " + e.getMessage());
            success = false;
        }

        return success;
    }

    /*
     * Method: GetIdFromAuthorName
     * Purpose: to get the ID of an author from their name.
     *
     * Parameters:
     * - name (String), the name of the author
     *
     * Return: int
     * - 0, the author does not exist
     * - > 0, the ID of the author
     */
    private int GetIdFromAuthorName(String name)
    {
        String table;
        String query;
        String condition;
        int id = 0;
        ResultSet result;

        try
        {
            table     = ConnectionManager.Schema + ".author";
            condition = " WHERE name = \'" + name + "\'";
            query     = "SELECT id FROM " + table + condition;
            result    = Statement.executeQuery(query);

            if (result.next())
            {
                if (result.getString(1) != null)
                {
                    id = Integer.parseInt(result.getString(1));
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("QueryManager.GetIdFromAuthorName - An exception was caught: " + e.getMessage());
        }

        return id;
    }

    /*
     * Method: GetIdFromPublisherName
     * Purpose: to get the ID of a publisher from their name.
     *
     * Parameters:
     * - name (String), the name of the publisher
     *
     * Return: int
     * - 0, the publisher does not exist
     * - > 0, the ID of the publisher
     */
    private int GetIdFromPublisherName(String name)
    {
        String table;
        String query;
        String condition;
        int id = 0;
        ResultSet result;

        try
        {
            table     = ConnectionManager.Schema + ".publisher";
            condition = " WHERE name = \'" + name + "\'";
            query     = "SELECT id FROM " + table + condition;
            result    = Statement.executeQuery(query);

            if (result.next())
            {
                if (result.getString(1) != null)
                {
                    id = Integer.parseInt(result.getString(1));
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("QueryManager.GetIdFromPublisherName - An exception was caught: " + e.getMessage());
        }

        return id;
    }

    /*
     * Method: Order
     * Purpose: to insert an order into the order table in the database.
     *
     * Parameters:
     * - quantity (int), the quantity of the order
     * - isbnArray (String[]), the book ISBNs contained in the order to be added to the contain table in the database
     *
     * Return: int
     * - > -1, the order number of the order that was inserted
     * - -1, an error occurred trying to insert the order
     */
    public int Order(int quantity, String[] isbnArray)
    {
        int id;
        String call;
        String query;
        LocalDate date;
        ResultSet result;
        CallableStatement callSt;
        DateTimeFormatter formatter;
        BigDecimal quantityToBigDecimal;

        try
        {
            id     = 0;
            query  = "SELECT MAX(CAST(id AS BIGINT)) FROM " + ConnectionManager.Schema + ".order";
            result = Statement.executeQuery(query);

            if (result.next())
            {
                // Get the latest order ID if one exists.
                if (result.getString(1) != null)
                {
                    id = Integer.parseInt(result.getString(1)) + 1;
                }
            }

            // Initialize insertion parameters for the order.
            date                 = LocalDate.now();
            formatter            = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            quantityToBigDecimal = new BigDecimal(quantity);

            // Insert the order into the order table.
            call   = "{call " + ConnectionManager.Schema + ".insert_order(?, ?, ?, ?)}";
            callSt = Conn.prepareCall(call);
            callSt.setString(1, Integer.toString(id));
            callSt.setBigDecimal(2, quantityToBigDecimal);
            callSt.setDate(3, Date.valueOf(formatter.format(date)));
            callSt.setString(4, SessionInfo.GetId());
            callSt.execute();

            // Insert a tuple into the make table for this order so we can track the location of the order ID.
            call   = "{call " + ConnectionManager.Schema + ".insert_make(?, ?)}";
            callSt = Conn.prepareCall(call);
            callSt.setString(1, Integer.toString(id));
            callSt.setString(2, Utils.GenerateLocation());
            callSt.execute();

            // Insert all ISBNs for this order into the contain table.
            for (String isbn : isbnArray)
            {
                call   = "{call " + ConnectionManager.Schema + ".insert_contain(?, ?)}";
                callSt = Conn.prepareCall(call);
                callSt.setString(1, Integer.toString(id));
                callSt.setString(2, isbn);
                callSt.execute();
            }
        }
        catch (Exception e)
        {
            System.out.println("QueryManager.Order - An exception was caught: " + e.getMessage());
            id = -1;
        }

        return id;
    }

    /*
     * Method: GetOrderDetailsFromId
     * Purpose: to get the details of an order from an order ID (number).
     *
     * Parameters:
     * - id (String), the order number
     *
     * Return: ResultSet
     * - A set containing the details of the order (i.e., location, quantity, date, etc.)
     */
    public ResultSet GetOrderDetailsFromId(String id)
    {
        String joins;
        String query;
        String condition;
        String userTable;
        String makeTable;
        String orderTable;
        String attributes;
        String warehouseTable;
        ResultSet result;

        try
        {
            userTable      = ConnectionManager.Schema + ".user";
            makeTable      = ConnectionManager.Schema + ".make";
            orderTable     = ConnectionManager.Schema + ".order";
            warehouseTable = ConnectionManager.Schema + ".warehouse";
            attributes     = " location, quantity, date, username, code, " + warehouseTable + ".address, " + warehouseTable + ".phone ";
            joins          = "((" + orderTable + " JOIN " + makeTable + " ON id = order_id) LEFT OUTER JOIN " + userTable + " ON user_id = " + userTable + ".id) JOIN " + warehouseTable + " ON warehouse_code = code";
            condition      = " WHERE order_id = \'" + id + "\'";
            query          = "SELECT" + attributes + "FROM" + joins + condition;
            result         = Statement.executeQuery(query);
        }
        catch (Exception e)
        {
            System.out.println("QueryManager.GetOrderDetailsFromId - An exception was caught: " + e.getMessage());
            result = null;
        }

        return result;
    }

    /*
     * Method: GetBookDetailsFromIsbn
     * Purpose: to get the details of a book from an ISBN.
     *
     * Parameters:
     * - isbn (String), the ISBN of the book to get details from
     *
     * Return: ResultSet
     * - A set containing the details of the book (i.e., title, genre, year, etc.)
     */
    public ResultSet GetBookDetailsFromIsbn(String isbn)
    {
        return GetBookDetails(isbn, "", "", "","", "", new String[0], "");
    }

    /*
     * Method: GetBookDetails
     * Purpose: to get the details of a book.
     *
     * Parameters:
     * - isbn (String), the ISBN of the book to get details from
     * - title (String), the title of the book to get details from
     * - genre (String), the genre of the book to get details from
     * - year (String), the year of the book to get details from
     * - pages (String), the page count of the book to get details from
     * - price (String), the price of the book to get details from
     * - authors (String[]), the authors of the book to get details from
     * - publisher (String), the publisher of the book to get details from
     *
     * Return: ResultSet
     * - A set containing the details of the book (i.e., title, genre, year, etc.)
     *
     * Note: book details are found with an 'OR' search directive.
     */
    public ResultSet GetBookDetails(String isbn, String title, String genre, String year, String pages, String price, String[] authors, String publisher)
    {
        int yearToInt;
        int publisherId;
        int pagesCountToInt;
        float priceToFloat;
        String table;
        String query;
        StringBuilder condition;
        StringBuilder isbnFromWriteTable;
        ResultSet result;

        try
        {
            try
            {
                yearToInt = Integer.parseInt(year);
            }
            catch (Exception yearConv)
            {
                yearToInt = 0;
            }

            try
            {
                pagesCountToInt = Integer.parseInt(pages);
            }
            catch (Exception pageCountConv)
            {
                pagesCountToInt = 0;
            }

            try
            {
                priceToFloat = Float.parseFloat(price);
            }
            catch (Exception priceConv)
            {
                priceToFloat = 0;
            }

            isbnFromWriteTable = new StringBuilder();

            if (authors.length > 0)
            {
                // Find the author that wrote the book.
                table     = ConnectionManager.Schema + ".author";
                condition = new StringBuilder(" WHERE ");

                for (String author : authors)
                {
                    condition.append("name = \'").append(author.trim()).append("\' OR ");
                }

                // Get the author IDs from the author table.
                query  = "SELECT id FROM " + table + condition;
                query  = query.substring(0, query.lastIndexOf("OR"));
                result = Statement.executeQuery(query);

                // Now, look in the write table to find the ISBN corresponding to the author IDs.
                table     = ConnectionManager.Schema + ".write";
                condition = new StringBuilder(" WHERE ");

                while (result.next())
                {
                    condition.append("author_id = \'").append(result.getString(1)).append("\' OR ");
                }

                if (!condition.toString().equals(" WHERE "))
                {
                    // Get the ISBNs from the write table.
                    query     = "SELECT isbn FROM " + table + condition;
                    query     = query.substring(0, query.lastIndexOf("OR"));
                    result    = Statement.executeQuery(query);
                    condition = new StringBuilder(" WHERE ");

                    while (result.next())
                    {
                        condition.append("isbn = \'").append(result.getString(1)).append("\' OR ");
                    }

                    isbnFromWriteTable = new StringBuilder(" OR " + condition.substring(condition.lastIndexOf("WHERE") + 5, condition.lastIndexOf("OR")).trim());
                }
            }

            publisherId = 0;

            if (!publisher.isEmpty())
            {
                publisherId = GetIdFromPublisherName(publisher);
            }

            table = ConnectionManager.Schema + ".book";

            if (isbn.isEmpty() && title.isEmpty() && genre.isEmpty() && year.isEmpty() && pages.isEmpty() && price.isEmpty() && authors.length == 0 && publisher.isEmpty())
            {
                result = GetInventory();
            }
            else
            {
                condition = new StringBuilder(" WHERE (isbn = \'" + isbn + "\'" + isbnFromWriteTable + " OR LOWER(title) = \'" + title + "\' OR LOWER(genre) = \'" +
                            genre + "\' OR year = " + yearToInt + " OR pages = " + pagesCountToInt + " OR price = " + priceToFloat + " OR publisher_id = \'" + publisherId + "\') AND isbn NOT IN (SELECT isbn FROM project.contain)");

                query  = "SELECT * FROM " + table + condition;
                result = Statement.executeQuery(query);
            }
        }
        catch (Exception e)
        {
            System.out.println("QueryManager.GetBookDetails - An exception was caught: " + e.getMessage());
            result = null;
        }

        return result;
    }

    /*
     * Method: GetAuthorNameFromIsbn
     * Purpose: to get the authors of a book from an ISBN.
     *
     * Parameters:
     * - isbn (String), the ISBN of the book to get the authors of
     *
     * Return: ArrayList<String>
     * - An ArrayList containing the names of the authors of the book
     */
    public ArrayList<String> GetAuthorNameFromIsbn(String isbn)
    {
        String table;
        String query;
        String condition;
        ArrayList<String> authorIds;
        ArrayList<String> authorNames;
        ResultSet result;

        try
        {
            table     = ConnectionManager.Schema + ".write";
            condition = " WHERE isbn = \'" + isbn + "\'";
            query     = "SELECT author_id FROM " + table + condition;
            result    = Statement.executeQuery(query);
            authorIds = new ArrayList<>();

            while (result.next())
            {
                authorIds.add(result.getString(1));
            }

            // Get the author names from the author ID retrieved by the ISBN.
            // Note: this is a bit hacky since we cannot nest ResultSets.
            authorNames = new ArrayList<>();

            for (String id : authorIds)
            {
                authorNames.add(GetAuthorNameFromId(id));
            }
        }
        catch (Exception e)
        {
            System.out.println("QueryManager.GetAuthorNameFromIsbn - An exception was caught: " + e.getMessage());
            authorNames = null;
        }

        return authorNames;
    }

    /*
     * Method: GetAuthorNameFromId
     * Purpose: to get the name of an author from their ID.
     *
     * Parameters:
     * - id (String), the ID of the author
     *
     * Return: String
     * - null, the author does not exist; otherwise, the name of the author
     */
    public String GetAuthorNameFromId(String id)
    {
        String table;
        String query;
        String condition;
        String name = null;
        ResultSet result;

        try
        {
            table     = ConnectionManager.Schema + ".author";
            condition = " WHERE id = \'" + id + "\'";
            query     = "SELECT name FROM " + table + condition;
            result    = Statement.executeQuery(query);

            if (result.next())
            {
                name = result.getString(1);
            }
        }
        catch (Exception e)
        {
            System.out.println("An exception was caught: " + e.getMessage());
        }

        return name;
    }

    /*
     * Method: GetPublisherNameFromIsbn
     * Purpose: to get the name of a publisher from a book ISBN.
     *
     * Parameters:
     * - isbn (String), the ISBN of the book
     *
     * Return: String
     * - null, the publisher does not exist; otherwise, the name of the publisher
     */
    public String GetPublisherNameFromIsbn(String isbn)
    {
        String table;
        String query;
        String condition;
        String name = null;
        String publisherId;
        ResultSet result;

        try
        {
            table     = ConnectionManager.Schema + ".book";
            condition = " WHERE isbn = \'" + isbn + "\'";
            query     = "SELECT publisher_id FROM " + table + condition;
            result    = Statement.executeQuery(query);

            if (!result.next()) { return null; }

            publisherId = result.getString(1);

            table     = ConnectionManager.Schema + ".publisher";
            condition = " WHERE id = \'" + publisherId + "\'";
            query     = "SELECT name FROM " + table + condition;
            result    = Statement.executeQuery(query);

            if (result.next())
            {
                name = result.getString(1);
            }
        }
        catch (Exception e)
        {
            System.out.println("An exception was caught: " + e.getMessage());
        }

        return name;
    }

    /*
     * Method: GetInventory
     * Purpose: to get all books for sale that have not been ordered yet.
     *
     * Return: ResultSet
     * - A set containing all books for sale that have not been ordered yet
     */
    public ResultSet GetInventory()
    {
        String query;
        String condition;
        String bookTable;
        String containTable;
        ResultSet result;

        try
        {
            // Do not list books for sale that have been ordered.
            bookTable    = ConnectionManager.Schema + ".book";
            containTable = ConnectionManager.Schema + ".contain";
            condition    = " WHERE isbn NOT IN (SELECT isbn FROM " + containTable + ")";
            query        = "SELECT * FROM " + bookTable + condition;

            result = Statement.executeQuery(query);
        }
        catch (Exception e)
        {
            System.out.println("An exception was caught: " + e.getMessage());
            result = null;
        }

        return result;
    }

    /*
     * Method: GetSalesVsExpenditures
     * Purpose: to get the monthly sales and expenditures for the store.
     *
     * Return: ResultSet
     * - A set containing the monthly sales and expenditures
     */
    public ResultSet GetSalesVsExpenditures()
    {
        String view;
        String query;
        ResultSet result;

        try
        {
            // Owners get 15% of the left over sale cost after the publisher gets their cut.
            view   = ConnectionManager.Schema + ".report_sales_vs_expenditures";
            query  = "SELECT * FROM " + view;
            result = Statement.executeQuery(query);
        }
        catch(Exception e)
        {
            System.out.println("QueryManager.GetSalesExpenditures - An exception was caught: " + e.getMessage());
            result = null;
        }

        return result;
    }

    /*
     * Method: GetSalesPerGenre
     * Purpose: to get the total sales per book genre.
     *
     * Return: ResultSet
     * - A set containing the total sales per book genre
     */
    public ResultSet GetSalesPerGenre()
    {
        String view;
        String query;
        ResultSet result;

        try
        {
            view   = ConnectionManager.Schema + ".report_sales_per_genre";
            query  = "SELECT * FROM " + view;
            result = Statement.executeQuery(query);
        }
        catch(Exception e)
        {
            System.out.println("QueryManager.GetSalesPerGenre - An exception was caught: " + e.getMessage());
            result = null;
        }

        return result;
    }

    /*
     * Method: GetSalesPerAuthor
     * Purpose: to get the total sales per book author.
     *
     * Return: ResultSet
     * - A set containing the total sales per book author
     */
    public ResultSet GetSalesPerAuthor()
    {
        String view;
        String query;
        ResultSet result;

        try
        {
            view   = ConnectionManager.Schema + ".report_sales_per_author";
            query  = "SELECT * FROM " + view;
            result = Statement.executeQuery(query);
        }
        catch(Exception e)
        {
            System.out.println("QueryManager.GetSalesPerAuthor - An exception was caught: " + e.getMessage());
            result = null;
        }

        return result;
    }

    /*
     * Method: GetAllAuthors
     * Purpose: to get information from all authors of books in the store.
     *
     * Return: ResultSet
     * - A set containing the information of the authors
     */
    public ResultSet GetAllAuthors()
    {
        String table;
        String query;
        ResultSet result;

        try
        {
            table  = ConnectionManager.Schema + ".author";
            query  = "SELECT * FROM " + table;
            result = Statement.executeQuery(query);
        }
        catch (Exception e)
        {
            System.out.println("QueryManager.GetAllAuthors - An exception was caught: " + e.getMessage());
            result = null;
        }

        return result;
    }

    /*
     * Method: GetAllPublishers
     * Purpose: to get information from all publishers of books in the store.
     *
     * Return: ResultSet
     * - A set containing the information of the publishers
     */
    public ResultSet GetAllPublishers()
    {
        String table;
        String query;
        ResultSet result;

        try
        {
            table  = ConnectionManager.Schema + ".publisher";
            query  = "SELECT * FROM " + table;
            result = Statement.executeQuery(query);
        }
        catch (Exception e)
        {
            System.out.println("QueryManager.GetAllPublishers - An exception was caught: " + e.getMessage());
            result = null;
        }

        return result;
    }
}
