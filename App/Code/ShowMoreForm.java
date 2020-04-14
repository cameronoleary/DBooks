import javax.swing.*;
import java.sql.ResultSet;

public class ShowMoreForm extends JFrame
{
    private int Type;
    private JPanel MainPanel;
    private JFrame ParentForm;
    private JTable ShowAllTable;
    private JLabel DisplayLabel;
    private ReportModel ShowAllModel;
    private QueryManager QueryManager;

    public ShowMoreForm(JFrame parent, QueryManager queryManager, int type)
    {
        Type         = type;
        ParentForm   = parent;
        QueryManager = queryManager;

        InitializeComponents();
    }

    /*
     * Method: InitializeComponents
     * Purpose: to initialize all components of this form to their defaults.
     */
    private void InitializeComponents()
    {
        this.pack();
        this.setContentPane(MainPanel);
        this.setSize(700, 500);
        this.setLocationRelativeTo(ParentForm);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ShowAllModel = new ReportModel();

        System.out.println(Type);

        switch (Type)
        {
            case 1:
                DisplayLabel.setText("Displaying All Authors");
                ShowAllModel.addColumn("ID");
                ShowAllModel.addColumn("Name");
                ShowAllModel.addColumn("Phone");
                PopulateAuthors();
                break;
            case 2:
                DisplayLabel.setText("Displaying All Publishers");
                ShowAllModel.addColumn("ID");
                ShowAllModel.addColumn("Name");
                ShowAllModel.addColumn("Address");
                ShowAllModel.addColumn("Phone");
                ShowAllModel.addColumn("Email");
                PopulatePublishers();
                break;
            default:
                break;
        }

        ShowAllTable.setModel(ShowAllModel);
    }

    /*
     * Method: PopulateAuthors
     * Purpose: populates the form with all the authors of books in the bookstore.
     */
    private void PopulateAuthors()
    {
        String id;
        String name;
        String temp;
        String phone;
        ResultSet authors;

        try
        {
            authors = QueryManager.GetAllAuthors();

            if (authors != null)
            {
                while (authors.next())
                {
                    id    = authors.getString(1);
                    name  = authors.getString(2);

                    temp  = authors.getString(3);
                    phone = (Long.parseLong(temp) == 0) ? "CONTACT FOR INFO" : temp;

                    Object[] authorEntry = new Object[]{id, name, phone};

                    ShowAllModel.addRow(authorEntry);
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("ShowMoreForm.PopulateAuthors - An exception was caught: " + e.getMessage());
        }
    }

    /*
     * Method: PopulatePublishers
     * Purpose: populates the form with all the publishers of books in the bookstore.
     */
    private void PopulatePublishers()
    {
        String id;
        String name;
        String temp;
        String phone;
        String email;
        String address;
        ResultSet publishers;

        try
        {
            publishers = QueryManager.GetAllPublishers();

            if (publishers != null)
            {
                while (publishers.next())
                {
                    id   = publishers.getString(1);
                    name = publishers.getString(2);

                    temp    = publishers.getString(3);
                    address = (temp.isEmpty()) ? "CONTACT FOR INFO" : temp;

                    temp  = publishers.getString(4);
                    phone = (Long.parseLong(temp) == 0) ? "CONTACT FOR INFO" : temp;

                    email   = publishers.getString(5);

                    Object[] publisherEntry = new Object[]{id, name, address, phone, email};

                    ShowAllModel.addRow(publisherEntry);
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("ShowMoreForm.PopulateAuthors - An exception was caught: " + e.getMessage());
        }
    }
}
