import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionManager
{
    public static String Schema;
    private String Url;
    private String Username;
    private String Password;

    public ConnectionManager()
    {
        Schema   = "project";
        Url      = "jdbc:postgresql://localhost:5432/postgres?currentSchema=" + Schema;
        Username = "postgres";
        Password = "";
    }

    /*
     * Method: EstablishConnection
     * Purpose: to establish a connection to the database.
     *
     * Return: Connection
     * - The connection to the database; can be used for querying or inserting data.
     */
    public Connection EstablishConnection()
    {
        Connection conn;

        try
        {
            System.out.println("Attempting to establish database connection...");
            conn = DriverManager.getConnection(Url, Username, Password);
            System.out.println("Connection established successfully.");
        }
        catch (Exception e)
        {
            System.out.println("ConnectionManager.EstablishConnection - An exception was caught: " + e.getMessage());
            return null;
        }

        return conn;
    }
}
