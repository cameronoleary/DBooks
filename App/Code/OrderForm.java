import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;

public class OrderForm extends JFrame
{
    private JLabel Title;
    private JLabel OrderError;
    private JTable BookTable;
    private JTable BookOrderTable;
    private JButton CancelButton;
    private JButton RefreshButton;
    private JButton PlaceOrderButton;
    private BookModel Model;
    private JPanel MainPanel;
    private MainForm ParentForm;
    private JScrollPane ScrollPane;
    private JLabel OrderNumberLabel;
    private HeaderRenderer Renderer;
    private QueryManager QueryManager;

    public OrderForm(JFrame parent, QueryManager queryManager)
    {
        super(Main.Title + " - Checkout");

        ParentForm   = (MainForm) parent;
        QueryManager = queryManager;

        InitializeComponents();
        RegisterListeners();
    }

    private void InitializeComponents()
    {
        this.pack();
        this.setContentPane(MainPanel);
        this.setSize(800, 500);
        this.setLocationRelativeTo(ParentForm);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        BookTable = ParentForm.GetBookTable();

        Model = new BookModel(BookOrderTable, PlaceOrderButton)
        {
            public Class<?> getColumnClass(int column)
            {
                if (column == 0)
                {
                    return Boolean.class;
                }
                else
                {
                    return String.class;
                }
            }
        };

        Renderer = new HeaderRenderer();

        Model.addColumn("Order");
        Model.addColumn("ISBN");
        Model.addColumn("Title");
        Model.addColumn("Genre");
        Model.addColumn("Year");
        Model.addColumn("Page Count");
        Model.addColumn("Price");

        BookOrderTable.setModel(Model);
        BookOrderTable.getTableHeader().setDefaultRenderer(Renderer);

        PopulateTable();
    }

    private void RegisterListeners()
    {
        RefreshButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Refresh();
            }
        });

        PlaceOrderButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                PlaceOrder();
            }
        });

        CancelButton.addActionListener(this::actionPerformed);
    }

    private void actionPerformed(ActionEvent e)
    {
        this.dispose();
    }

    private void PopulateTable()
    {
        String isbn;
        String title;
        String genre;
        String year;
        String pageCount;
        String price;

        for (int i = 0; i < BookTable.getRowCount(); i++)
        {
            boolean isSelected = Boolean.parseBoolean(BookTable.getValueAt(i, 0).toString());

            if (isSelected)
            {
                isbn      = BookTable.getValueAt(i, 1).toString();
                title     = BookTable.getValueAt(i, 2).toString();
                genre     = BookTable.getValueAt(i, 3).toString();
                year      = BookTable.getValueAt(i, 4).toString();
                pageCount = BookTable.getValueAt(i, 5).toString();
                price     = BookTable.getValueAt(i, 6).toString();

                Object[] bookEntry = new Object[]{true, isbn, title, genre, year, pageCount, price};

                Model.addRow(bookEntry);
            }
        }
    }

    private void Refresh()
    {
        for (int i = 0; i < BookOrderTable.getRowCount(); i++)
        {
            boolean isSelected = Boolean.parseBoolean(BookOrderTable.getValueAt(i, 0).toString());

            if (!isSelected)
            {
                Model.removeRow(i);
            }
        }
    }

    private void PlaceOrder()
    {
        String isbn;
        String title;
        int orderNumber;
        int orderQuantity = 0;
        boolean isSelected;
        String[] isbnArray;
        HashSet<String> titles = new HashSet<>();
        ArrayList<String> isbnList = new ArrayList<>();

        for (int i = 0; i < BookOrderTable.getRowCount(); i++)
        {
            isSelected = Boolean.parseBoolean(BookOrderTable.getValueAt(i, 0).toString());

            if (isSelected)
            {
                orderQuantity++;

                isbn  = BookOrderTable.getValueAt(i, 1).toString();
                title = BookOrderTable.getValueAt(i, 2).toString();

                titles.add(title);
                isbnList.add(isbn);
            }
        }

        isbnArray = new String[isbnList.size()];
        isbnList.toArray(isbnArray);

        orderNumber = QueryManager.Order(orderQuantity, isbnArray);

        if (orderNumber == -1)
        {
            OrderError.setText("Order Placement Failure");
            OrderError.setForeground(Color.RED);
        }
        else
        {
            Title.setText("Thank you for your order!");
            OrderNumberLabel.setText(OrderNumberLabel.getText() + orderNumber);
            OrderNumberLabel.setVisible(true);

            ScrollPane.setVisible(false);
            PlaceOrderButton.setVisible(false);
            RefreshButton.setVisible(false);
            CancelButton.setText("Close");

            OrderError.setText("Order Placement Successful");
            OrderError.setForeground(Color.GREEN);

            // Refresh the books to browse since the ones in this order should not be marked for sale anymore.
            ParentForm.PopulateTable();
        }

        OrderError.setVisible(true);
    }
}
