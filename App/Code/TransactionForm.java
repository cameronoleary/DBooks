import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TransactionForm extends JFrame
{
    private JPanel mainPanel;
    private JTextField nameField;
    private JTextField cityField;
    private JTextField phoneField;
    private JTextField addressField;
    private JTextField postalCodeField;
    private JButton cancelButton;
    private JButton continueButton;
    private JLabel nameError;
    private JLabel cityError;
    private JLabel phoneError;
    private JLabel addressError;
    private JLabel postalCodeError;
    private JFrame ParentForm;
    private QueryManager QueryManager;

    public TransactionForm(JFrame parent, QueryManager queryManager)
    {
        super(Main.Title + " - Checkout");

        ParentForm   = parent;
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
        this.setLocationRelativeTo(ParentForm);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    /*
     * Method: RegisterListeners
     * Purpose: to register all listeners for all components of this form.
     */
    private void RegisterListeners()
    {
        continueButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ShowOrderForm();
            }
        });

        cancelButton.addActionListener(this::actionPerformed);
    }

    /*
     * Method: actionPerformed
     * Purpose: closes this form.
     */
    private void actionPerformed(ActionEvent e)
    {
        this.dispose();
    }

    /*
     * Method: ShowOrderForm
     * Purpose: shows the last form for checking out an order (e.g., the order review and placement).
     */
    private void ShowOrderForm()
    {
        if (!IsFormValid()) { return; }

        OrderForm orderForm = new OrderForm(ParentForm, QueryManager);
        orderForm.setVisible(true);
        this.dispose();
    }

    /*
     * Method: IsFormValid
     * Purpose: validates the form for invalid input for all input fields.
     */
    private boolean IsFormValid()
    {
        boolean isValid = true;

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

        if (cityField.getText().isEmpty())
        {
            cityError.setVisible(true);
            isValid = false;
        }
        else
        {
            cityError.setVisible(false);
        }

        if (postalCodeField.getText().isEmpty())
        {
            postalCodeError.setVisible(true);
            isValid = false;
        }
        else
        {
            postalCodeError.setVisible(false);
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

        return isValid;
    }
}
