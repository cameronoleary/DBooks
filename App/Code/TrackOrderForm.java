import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

public class TrackOrderForm extends JFrame
{
    private JPanel MainPanel;
    private JFrame ParentForm;
    private JButton TrackButton;
    private JLabel LocationLabel;
    private JTextField OrderNumberField;
    private JLabel QuantityLabel;
    private JLabel DateLabel;
    private JLabel UsernameLabel;
    private JLabel WarehouseLabel;
    private QueryManager QueryManager;

    public TrackOrderForm(JFrame parent, QueryManager queryManager)
    {
        ParentForm   = parent;
        QueryManager = queryManager;

        InitializeComponents();
        RegisterListeners();
    }

    private void InitializeComponents()
    {
        this.pack();
        this.setContentPane(MainPanel);
        this.setSize(800, 600);
        this.setLocationRelativeTo(ParentForm);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void RegisterListeners()
    {
        TrackButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                TrackOrder();
            }
        });
    }

    private void TrackOrder()
    {
        String date             = "N/A";
        String location         = "N/A";
        String quantity         = "N/A";
        String username         = "N/A";
        String warehouseDetails = "N/A";
        ResultSet orderDetails  = QueryManager.GetOrderDetailsFromId(OrderNumberField.getText());

        if (orderDetails != null)
        {
            try
            {
                while (orderDetails.next())
                {
                    location = orderDetails.getString(1);
                    quantity = orderDetails.getString(2);
                    date = orderDetails.getString(3);
                    username = orderDetails.getString(4);

                    if (username == null)
                    {
                        username = "PUBLISHER ORDER";
                    }

                    warehouseDetails = orderDetails.getString(5) + ", " + orderDetails.getString(6) + ", " + orderDetails.getString(7);
                }
            }
            catch (Exception e)
            {
                System.out.println("TrackOrderForm.TrackOrder - An exception was caught: " + e.getMessage());
            }
        }

        DateLabel.setText(date);
        LocationLabel.setText(location);
        QuantityLabel.setText(quantity);
        UsernameLabel.setText(username);
        WarehouseLabel.setText(warehouseDetails);

        DateLabel.setVisible(true);
        LocationLabel.setVisible(true);
        QuantityLabel.setVisible(true);
        UsernameLabel.setVisible(true);
        WarehouseLabel.setVisible(true);
    }
}
