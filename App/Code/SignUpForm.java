import java.awt.*;
import javax.swing.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignUpForm extends JFrame
{
    private JPanel mainPanel;
    private JButton submitButton;
    private JFrame ParentForm;
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField addressField;
    private JTextField usernameField;
    private JRadioButton noRadioButton;
    private JRadioButton yesRadioButton;
    private JPasswordField passwordField;
    private JLabel nameError;
    private JLabel phoneError;
    private JLabel emailError;
    private JLabel addressError;
    private JLabel usernameError;
    private JLabel passwordError;
    private JLabel registeredMessage;
    private JButton cancelButton;
    private ButtonGroup OwnerGroup;
    private QueryManager QueryManager;

    public SignUpForm(LogInForm parent, QueryManager queryManager)
    {
        super(Main.Title + " - Sign Up");
        QueryManager = queryManager;
        ParentForm = parent;

        InitializeComponents();
        RegisterListeners();
    }

    private void InitializeComponents()
    {
        this.pack();
        this.setContentPane(mainPanel);
        this.setSize(400, 500);
        this.setLocationRelativeTo(ParentForm);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        OwnerGroup = new ButtonGroup();
        noRadioButton.setActionCommand("false");
        yesRadioButton.setActionCommand("true");
        OwnerGroup.add(noRadioButton);
        OwnerGroup.add(yesRadioButton);
    }

    private void RegisterListeners()
    {
        submitButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                SubmitForm();
            }
        });

        cancelButton.addActionListener(this::actionPerformed);
    }

    private void actionPerformed(ActionEvent e)
    {
        this.dispose();
    }

    private void SubmitForm()
    {
        String username;
        String password;
        String name;
        String address;
        String phone;
        String email;
        boolean isOwner;

        if (!IsFormValid()) { return; }

        username = usernameField.getText();
        password = new String(passwordField.getPassword());
        name = nameField.getText();
        address = addressField.getText();
        phone = phoneField.getText();
        email = emailField.getText();
        isOwner = Boolean.parseBoolean(OwnerGroup.getSelection().getActionCommand());

        boolean success = QueryManager.Register(username, password, name, address, phone, email, isOwner);

        if (success)
        {
            registeredMessage.setText("Register Successful");
            registeredMessage.setForeground(Color.GREEN);
        }
        else
        {
            registeredMessage.setText("Register Failure");
            registeredMessage.setForeground(Color.RED);
        }

        registeredMessage.setVisible(true);
    }

    private boolean IsFormValid()
    {
        boolean isValid = true;
        String emailRegex;
        Matcher matcher;
        Pattern pattern;


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
            isValid = false;
        }
        else
        {
            passwordError.setVisible(false);
        }

        if (nameField.getText().isEmpty())
        {
            nameError.setVisible(true);
            isValid = false;
        }
        else
        {
            nameError.setVisible(false);
        }

        if (addressField.getText().isEmpty())
        {
            addressError.setVisible(true);
            isValid = false;
        }
        else
        {
            addressError.setVisible(false);
        }

        if (phoneField.getText().isEmpty())
        {
            phoneError.setText("Please enter a phone number");
            phoneError.setVisible(true);
            isValid = false;
        }
        else
        {
            try
            {
                Long.parseLong(phoneField.getText());
                phoneError.setVisible(false);
            }
            catch (Exception e)
            {
                phoneError.setText("Please enter a valid phone number");
                phoneError.setVisible(true);
                isValid = false;
            }
        }

        emailRegex = "^(.+)@(.+)$";
        pattern = Pattern.compile(emailRegex);
        matcher = pattern.matcher(emailField.getText());

        if (emailField.getText().isEmpty())
        {
            emailError.setText("Please enter an email");
            emailError.setVisible(true);
            isValid = false;
        }
        else if (!matcher.matches())
        {
            emailError.setText("Please enter a valid email");
            emailError.setVisible(true);
            isValid = false;
        }
        else
        {
            emailError.setVisible(false);
        }

        return isValid;
    }
}
