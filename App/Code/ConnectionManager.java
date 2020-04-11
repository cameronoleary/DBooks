import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionManager
{
    public static String Schema;
    private String Username;
    private String Password;
    private String Url;

    public ConnectionManager()
    {
        Username = "postgres";
        Password = "";
        Schema = "project";
        Url = "jdbc:postgresql://localhost:5432/postgres?currentSchema=" + Schema;
    }

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
            System.out.println("An exception was caught: " + e.getMessage());
            return null;
        }

        return conn;
    }
}
