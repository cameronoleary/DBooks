/*
 *  Author: Cameron O'Leary
 * Contact: cameron.oleary@carleton.ca
 *   Title: DBooks: An Online Bookstore Application
 */

import javax.swing.*;
import java.sql.Connection;

public class Main
{
    public static String Title = "DBooks";

    public static void main(String[] args)
    {
        // Initialize database communication.
        Connection dbConnection = new ConnectionManager().EstablishConnection();
        QueryManager queryManager = new QueryManager(dbConnection);

        if (dbConnection == null) { return; }

        // Show the login form.
        JFrame logInForm = new LogInForm(queryManager);
        logInForm.setVisible(true);
    }
}
