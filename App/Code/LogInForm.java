import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

public class LogInForm extends JFrame {
    private JPanel mainPanel;
    private JButton logInButton;
    private JButton signUpButton;
    private JButton ImporterButton;
    private JLabel Title;
    private JLabel usernameError;
    private JLabel passwordError;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private QueryManager QueryManager;

    public LogInForm(QueryManager queryManager)
    {
        super(Main.Title + " - Log In");
        QueryManager = queryManager;

        InitializeComponents();
        RegisterListeners();
    }

    /*
     * Method: InitializeComponents
     * Purpose: to initialize all components of this form to their defaults.
     */
    private void InitializeComponents()
    {
        this.pack();
        this.setContentPane(mainPanel);
        this.setSize(400, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /*
     * Method: RegisterListeners
     * Purpose: to register all listeners for all components of this form.
     */
    private void RegisterListeners()
    {
        logInButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                LogIn();
            }
        });

        signUpButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ShowSignUpForm();
            }
        });

        ImporterButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ShowImporterForm();
            }
        });
    }

    /*
     * Method: LogIn
     * Purpose: logs the user into the application.
     */
    private void LogIn()
    {
        boolean isOwner;
        String id;
        String username;
        String password;
        JFrame mainForm;
        ResultSet userInfo;

        if (!IsFormValid()) { return; }

        username = usernameField.getText();
        password = new String(passwordField.getPassword());

        if (QueryManager.IsRegistered(username, password))
        {
            try
            {
                userInfo = QueryManager.GetUserInfo(username);

                if (userInfo == null) { return; }

                // Use an if-statement since we will just use the first value returned (prevents duplicates).
                if (userInfo.next())
                {
                    id      = userInfo.getString(1);
                    isOwner = Integer.parseInt(userInfo.getString(2)) == 1;

                    // Set user information for the current session.
                    // ID will be used for making an order and owner status is used to know what buttons to enable.
                    SessionInfo.SetId(id);
                    SessionInfo.SetOwner(isOwner);
                }
            }
            catch (Exception e)
            {
                System.out.println("LogInForm.LogIn - An exception was caught: " + e.getMessage());
            }

            mainForm = new MainForm(this, QueryManager);
            mainForm.setVisible(true);
            this.setVisible(false);
        }
        else
        {
            passwordError.setVisible(true);
            passwordError.setText("Incorrect username or password");
        }
    }

    /*
     * Method: IsFormValid
     * Purpose: validates the login form to check for valid username/password input.
     */
    private boolean IsFormValid()
    {
        boolean isValid = true;

        if (usernameField.getText().isEmpty())
        {
            usernameError.setVisible(true);
            isValid = false;
        }
        else
        {
            usernameError.setVisible(false);
        }

        if (passwordField.getPassword().length == 0)
        {
            passwordError.setVisible(true);
            passwordError.setText("Please enter a password");
            isValid = false;
        }
        else
        {
            passwordError.setVisible(false);
        }

        return isValid;
    }

    /*
     * Method: ShowSignUpForm
     * Purpose: displays the sign up form.
     */
    private void ShowSignUpForm()
    {
        JFrame signUpForm = new SignUpForm(this, QueryManager);
        signUpForm.setVisible(true);
    }

    /*
     * Method: ShowImporterForm
     * Purpose: displays the importer tool.
     */
    private void ShowImporterForm()
    {
        Importer importer = new Importer(this, QueryManager);
        importer.setVisible(true);
    }
}
