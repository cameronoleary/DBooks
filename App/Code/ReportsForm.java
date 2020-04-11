import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ReportsForm extends JFrame
{
    private JPanel MainPanel;
    private JFrame ParentForm;
    private JButton HelpButton3;
    private JTabbedPane TabbedPane;
    private JPanel SalesPerGenrePanel;
    private JPanel SalesPerAuthorPanel;
    private JPanel SalesExpendituresPanel;
    private JTable SalesPerGenreTable;
    private JTable SalesPerAuthorTable;
    private JTable SalesExpendituresTable;
    private JButton HelpButton2;
    private JButton HelpButton1;
    private ReportModel SalesPerGenreModel;
    private ReportModel SalesPerAuthorModel;
    private ReportModel SalesExpendituresModel;
    private QueryManager QueryManager;

    public ReportsForm(JFrame parent, QueryManager queryManager)
    {
        super(Main.Title + " - Reports");

        ParentForm   = parent;
        QueryManager = queryManager;

        InitializeComponents();
        RegisterListeners();
    }

    private void InitializeComponents()
    {
        this.pack();
        this.setContentPane(MainPanel);
        this.setSize(700, 500);
        this.setLocationRelativeTo(ParentForm);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        SalesPerGenreModel = new ReportModel();
        SalesPerGenreModel.addColumn("Genre");
        SalesPerGenreModel.addColumn("Sales");

        SalesPerAuthorModel = new ReportModel();
        SalesPerAuthorModel.addColumn("Author");
        SalesPerAuthorModel.addColumn("Sales");

        SalesExpendituresModel = new ReportModel();
        SalesExpendituresModel.addColumn("Date");
        SalesExpendituresModel.addColumn("Sales");
        SalesExpendituresModel.addColumn("Expenditures");

        SalesPerGenreTable.setModel(SalesPerGenreModel);
        SalesPerAuthorTable.setModel(SalesPerAuthorModel);
        SalesExpendituresTable.setModel(SalesExpendituresModel);

        PopulateTables();
    }

    private void RegisterListeners()
    {
        HelpButton1.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ShowHelpMessage(1);
            }
        });

        HelpButton2.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ShowHelpMessage(2);
            }
        });

        HelpButton3.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ShowHelpMessage(3);
            }
        });
    }

    private void PopulateTables()
    {
        String date;
        String sale;
        String genre;
        String author;
        String expenditure;
        ResultSet report;

        try
        {
            report = QueryManager.GetSalesVsExpenditures();

            if (report != null)
            {
                while (report.next())
                {
                    date        = report.getString(1);
                    sale        = "$" + report.getString(2);
                    expenditure = "$" + report.getString(3);

                    Object[] reportEntry = new Object[]{date, sale, expenditure};

                    SalesExpendituresModel.addRow(reportEntry);
                }
            }

            report = QueryManager.GetSalesPerGenre();

            if (report != null)
            {
                while (report.next())
                {
                    genre       = report.getString(1);
                    sale        = "$" + report.getString(2);

                    Object[] reportEntry = new Object[]{genre, sale};

                    SalesPerGenreModel.addRow(reportEntry);
                }
            }

            report = QueryManager.GetSalesPerAuthor();

            if (report != null)
            {
                while (report.next())
                {
                    author      = report.getString(1);
                    sale        = "$" + report.getString(2);

                    Object[] reportEntry = new Object[]{author, sale};

                    SalesPerAuthorModel.addRow(reportEntry);
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("ReportsForm.PopulateTables - An exception was caught: " + e.getMessage());
        }
    }

    private void ShowHelpMessage(int type)
    {
        String message;

        switch (type)
        {
            case 1:
                message = "Sales vs. Expenditures shows your total sales cost of books \nfor each month against the " +
                          "total amount paid for the books.\n\nMoreover, your net profit is calculated at 15% after " +
                          "paying the \npublishers of the books. A simple calculation is shown below.\n\nFor every " +
                          "month:\n- Sales = Sum(Books Sold at Retail Price)\n- Expenditures = (Sales - Publisher " +
                          "Profit) * 0.85\n- Net Profit = (Sales - Publisher Profit) * 0.15";
                JOptionPane.showMessageDialog(this, message, "Sales vs. Expenditures - Help",
                                              JOptionPane.INFORMATION_MESSAGE, null);
                break;
            case 2:
                message = "Sales Per Genre show the total sales for each genre of books in your store.";
                JOptionPane.showMessageDialog(this, message, "Sales Per Genre - Help",
                        JOptionPane.INFORMATION_MESSAGE, null);
                break;
            case 3:
                message = "Sales Per Author show the total sales for each author of a book in your store.";
                JOptionPane.showMessageDialog(this, message, "Sales Per Author - Help",
                        JOptionPane.INFORMATION_MESSAGE, null);
                break;
            default:
                break;
        }
    }
}

class ReportModel extends DefaultTableModel
{
    @Override
    public boolean isCellEditable(int row, int column)
    {
        return false;
    }
}
