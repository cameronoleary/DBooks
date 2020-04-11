import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.border.EtchedBorder;
import java.sql.ResultSet;

public class MainForm extends JFrame
{
    private JPanel MainPanel;
    private JTable BookTable;
    private JFrame ParentForm;
    private JButton FilterButton;
    private JButton LogOutButton;
    private JButton TrackOrderButton;
    private JButton ViewDetailsButton;
    private JButton ViewReportsButton;
    private JTextField IsbnField;
    private JTextField YearField;
    private JTextField TitleField;
    private JTextField GenreField;
    private JTextField PriceField;
    private JTextField AuthorsField;
    private JTextField PageCountField;
    private JTextField PublisherField;
    private JButton AddButton;
    private JButton RemoveButton;
    private JButton ProceedToCheckoutButton;
    private JLabel AddBookError;
    private JButton ClearButton;
    private JButton SeeAllAuthorsButton;
    private JButton SeeAllPublishersButton;
    private BookModel Model;
    private HeaderRenderer Renderer;
    private QueryManager QueryManager;

    public MainForm(JFrame parent, QueryManager queryManager)
    {
        super(Main.Title + " - Main Menu");

        ParentForm   = parent;
        QueryManager = queryManager;

        InitializeComponents();
        RegisterListeners();
    }

    private void InitializeComponents()
    {
        this.pack();
        this.setContentPane(MainPanel);
        this.setSize(1000, 800);
        this.setLocationRelativeTo(ParentForm);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Model = new BookModel(BookTable, ProceedToCheckoutButton, RemoveButton)
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

        Model.addColumn("Add to Order");
        Model.addColumn("ISBN");
        Model.addColumn("Title");
        Model.addColumn("Genre");
        Model.addColumn("Year");
        Model.addColumn("Page Count");
        Model.addColumn("Price");

        BookTable.setModel(Model);
        BookTable.getTableHeader().setDefaultRenderer(Renderer);

        PopulateTable();

        if (SessionInfo.IsOwner())
        {
            AddButton.setVisible(true);
            RemoveButton.setVisible(true);
            ViewReportsButton.setVisible(true);
        }
    }

    private void RegisterListeners()
    {
        LogOutButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                LogOut();
            }
        });

        TrackOrderButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ShowTrackOrderForm();
            }
        });

        ViewDetailsButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ShowBookDetailsForm();
            }
        });

        ViewReportsButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ShowReportsForm();
            }
        });

        RemoveButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                RemoveBooks();
            }
        });

        SeeAllAuthorsButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ShowMoreForm(1);
            }
        });

        SeeAllPublishersButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ShowMoreForm(2);
            }
        });

        AddButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                AddBook();
            }
        });

        ClearButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Clear();
            }
        });

        FilterButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Filter();
            }
        });

        ProceedToCheckoutButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ShowTransactionForm();
            }
        });

        BookTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            @Override
            public void valueChanged(ListSelectionEvent e)
            {
                if (BookTable.getSelectionModel().isSelectionEmpty() || BookTable.getSelectedRowCount() > 1)
                {
                    ViewDetailsButton.setEnabled(false);
                }
                else
                {
                    ViewDetailsButton.setEnabled(true);
                }
            }
        });
    }

    public void PopulateTable()
    {
        String isbn;
        String year;
        String title;
        String genre;
        String price;
        String pageCount;
        ResultSet books;

        try
        {
            books = QueryManager.GetInventory();

            if (books == null) { return; }

            // Clear the table.
            Model.setRowCount(0);

            while (books.next())
            {
                isbn      = books.getString(1);
                title     = books.getString(2);
                genre     = books.getString(3);
                year      = books.getString(4);
                pageCount = books.getString(5);
                price     = "$" + books.getString(6);

                Object[] bookEntry = new Object[]{Boolean.FALSE, isbn, title, genre, year, pageCount, price};

                Model.addRow(bookEntry);
            }
        }
        catch (Exception e)
        {
            System.out.println("An exception was caught: " + e.getMessage());
        }
    }

    private void LogOut()
    {
        this.dispose();
        ParentForm.setVisible(true);
    }

    private void ShowTrackOrderForm()
    {
        TrackOrderForm trackOrderForm = new TrackOrderForm(this, QueryManager);
        trackOrderForm.setVisible(true);
    }

    private void ShowBookDetailsForm()
    {
        int selectedRow = BookTable.getSelectedRow();
        String isbn     = BookTable.getValueAt(selectedRow, 1).toString();

        BookDetails bookDetails = new BookDetails(this, QueryManager);
        bookDetails.setVisible(true);
        bookDetails.GetDetails(isbn);
    }

    private void ShowReportsForm()
    {
        ReportsForm reportsForm = new ReportsForm(this, QueryManager);
        reportsForm.setVisible(true);
    }

    private void ShowMoreForm(int type)
    {
        ShowMoreForm showMoreForm = new ShowMoreForm(this, QueryManager, type);
        showMoreForm.setVisible(true);
    }

    private void RemoveBooks()
    {
        int selection = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the selected book(s)?", "Remove Books", JOptionPane.YES_NO_OPTION);
        boolean isSelected;
        String isbn;

        if (selection == 0)
        {
            // Yes, delete the selected books.
            for (int i = 0; i < BookTable.getRowCount(); i++)
            {
                isSelected = Boolean.parseBoolean(BookTable.getValueAt(i, 0).toString());

                if (isSelected)
                {
                    // Make capturing of ISBN explicit. This makes it easier to read and understand.
                    isbn = BookTable.getValueAt(i, 1).toString();

                    QueryManager.RemoveBook(isbn);
                }
            }

            PopulateTable();

            RemoveButton.setEnabled(false);
            ViewDetailsButton.setEnabled(false);
            ProceedToCheckoutButton.setEnabled(false);
        }
    }

    private void AddBook()
    {
        boolean success;
        String isbn;
        String year;
        String title;
        String genre;
        String price;
        String pageCount;
        String publisher;
        String[] authors;
        String[] tempAuthors;

        if (!IsFormValid()) { return; }

        isbn        = IsbnField.getText().trim();
        year        = YearField.getText().trim();
        title       = TitleField.getText().trim();
        genre       = GenreField.getText().trim();
        price       = PriceField.getText().trim();
        pageCount   = PageCountField.getText().trim();
        tempAuthors = AuthorsField.getText().split(",");
        authors     = new String[tempAuthors.length];

        for (int i = 0; i < authors.length; i++)
        {
            authors[i] = tempAuthors[i].trim();
        }

        publisher = PublisherField.getText().trim();

        success = QueryManager.AddBook(isbn, title, genre, year, pageCount, price, authors, publisher);

        if (success)
        {
            AddBookError.setText("Book Added Successfully");
            AddBookError.setForeground(Color.GREEN);
        }
        else
        {
            AddBookError.setText("Book Add Failure");
            AddBookError.setForeground(Color.RED);
        }

        AddBookError.setVisible(true);

        // Re-populate the table so the newly added book shows up.
        PopulateTable();
    }

    private boolean IsFormValid()
    {
        boolean isValid = true;

        if (IsbnField.getText().isEmpty())
        {
            IsbnField.setText("Please enter an ISBN");
            IsbnField.setForeground(Color.RED);
            isValid = false;
        }

        if (TitleField.getText().isEmpty())
        {
            TitleField.setText("Please enter a title");
            TitleField.setForeground(Color.RED);
            isValid = false;
        }

        if (GenreField.getText().isEmpty())
        {
            GenreField.setText("Please enter a genre");
            GenreField.setForeground(Color.RED);
            isValid = false;
        }

        if (YearField.getText().isEmpty())
        {
            YearField.setText("Please enter a year");
            YearField.setForeground(Color.RED);
            isValid = false;
        }

        if (PageCountField.getText().isEmpty())
        {
            PageCountField.setText("Please enter a page count");
            PageCountField.setForeground(Color.RED);
            isValid = false;
        }

        if (PriceField.getText().isEmpty())
        {
            PriceField.setText("Please enter a price");
            PriceField.setForeground(Color.RED);
            isValid = false;
        }

        if (AuthorsField.getText().isEmpty())
        {
            AuthorsField.setText("Please enter an author(s)");
            AuthorsField.setForeground(Color.RED);
            isValid = false;
        }

        if (PublisherField.getText().isEmpty())
        {
            PublisherField.setText("Please enter a publisher");
            PublisherField.setForeground(Color.RED);
            isValid = false;
        }

        return isValid;
    }

    private void Clear()
    {
        AddBookError.setVisible(false);
        IsbnField.setText("");
        TitleField.setText("");
        GenreField.setText("");
        YearField.setText("");
        PageCountField.setText("");
        PriceField.setText("");
        AuthorsField.setText("");
        PublisherField.setText("");
    }

    private void Filter()
    {
        String isbn;
        String year;
        String title;
        String genre;
        String price;
        String pageCount;
        String publisher;
        String[] authors;
        ResultSet filteredBooks;

        isbn      = IsbnField.getText();
        title     = TitleField.getText().toLowerCase();
        genre     = GenreField.getText().toLowerCase();
        year      = YearField.getText();
        pageCount = PageCountField.getText();
        price     = PriceField.getText();
        authors   = new String[0];
        publisher = PublisherField.getText();

        if (!AuthorsField.getText().isEmpty())
        {
            authors = AuthorsField.getText().split(",");
        }

        try
        {
            filteredBooks = QueryManager.GetBookDetails(isbn, title, genre, year, pageCount, price, authors, publisher);

            if (filteredBooks == null) { return; }

            // Clear the table.
            Model.setRowCount(0);

            while (filteredBooks.next())
            {
                isbn      = filteredBooks.getString(1);
                title     = filteredBooks.getString(2);
                genre     = filteredBooks.getString(3);
                year      = filteredBooks.getString(4);
                pageCount = filteredBooks.getString(5);
                price     = "$" + filteredBooks.getString(6);

                Object[] bookEntry = new Object[]{false, isbn, title, genre, year, pageCount, price};

                Model.addRow(bookEntry);
            }
        }
        catch (Exception e)
        {
            System.out.println("MainForm.Filter - An exception was caught: " + e.getMessage());
        }
    }

    private void ShowTransactionForm()
    {
        TransactionForm transactionForm = new TransactionForm(this, QueryManager);
        transactionForm.setVisible(true);
    }

    public JTable GetBookTable()
    {
        return BookTable;
    }
}

class HeaderRenderer extends DefaultTableCellRenderer
{
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column)
    {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        setBorder(new EtchedBorder());
        setHorizontalAlignment(SwingConstants.CENTER);

        return this;
    }
}

class BookModel extends DefaultTableModel
{
    private JTable Table;
    private JButton Button1;
    private JButton Button2;

    public BookModel(JTable table, JButton button)
    {
        Table   = table;
        Button1 = button;
    }

    public BookModel(JTable table, JButton button1, JButton button2)
    {
        Table  = table;
        Button1 = button1;
        Button2 = button2;
    }

    @Override
    public boolean isCellEditable(int row, int column)
    {
        return column == 0;
    }

    @Override
    public void setValueAt(Object aValue, int row, int column)
    {
        super.setValueAt(aValue, row, column);

        if (column == 0)
        {
            for (int i = 0; i < Table.getRowCount(); i++)
            {
                boolean isSelected = Boolean.parseBoolean(Table.getValueAt(i, 0).toString());

                if (isSelected)
                {
                    if (Button1 != null)
                    {
                        Button1.setEnabled(true);
                    }

                    if (Button2 != null)
                    {
                        Button2.setEnabled(true);
                    }

                    return;
                }
            }

            Button1.setEnabled(false);
            Button2.setEnabled(false);
        }
    }
}
