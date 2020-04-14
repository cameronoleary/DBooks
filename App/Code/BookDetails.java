import javax.swing.*;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BookDetails extends JFrame
{
    private JPanel mainPanel;
    private JLabel isbnDetail;
    private JLabel yearDetail;
    private JLabel titleDetail;
    private JLabel genreDetail;
    private JLabel priceDetail;
    private JLabel authorsDetail;
    private JLabel pageCountDetail;
    private JLabel publisherDetail;
    private JFrame ParentForm;
    private QueryManager QueryManager;

    public BookDetails(JFrame parent, QueryManager queryManager)
    {
        super(Main.Title + " - Book Details");

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
        this.setContentPane(mainPanel);
        this.setSize(800, 500);
        this.setLocationRelativeTo(ParentForm);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    /*
     * Method: GetDetails
     * Purpose: show the details (i.e., ISBN, authors, publisher, etc.) of a book.
     *
     * Parameters:
     * - isbn (String), the ISBN of the book to get details for
     */
    public void GetDetails(String isbn)
    {
        String year;
        String title;
        String genre;
        String price;
        String pageCount;
        String publisher;
        ArrayList<String> authorDetails;
        StringBuilder authors;
        ResultSet bookDetails;

        try
        {
            bookDetails = QueryManager.GetBookDetailsFromIsbn(isbn);

            if (bookDetails == null) { return; }

            title     = "";
            genre     = "";
            year      = "";
            pageCount = "";
            price     = "";

            while (bookDetails.next())
            {
                title     = bookDetails.getString(2);
                genre     = bookDetails.getString(3);
                year      = bookDetails.getString(4);
                pageCount = bookDetails.getString(5);
                price     = "$" + bookDetails.getString(6);
            }

            // Populate book details.
            isbnDetail.setText(isbn);
            titleDetail.setText(title);
            genreDetail.setText(genre);
            yearDetail.setText(year);
            pageCountDetail.setText(pageCount);
            priceDetail.setText(price);

            authors       = new StringBuilder();
            authorDetails = QueryManager.GetAuthorNameFromIsbn(isbn);

            if (authorDetails != null)
            {
                for (String name : authorDetails)
                {
                    authors.append(name).append(", ");
                }
            }

            authors = new StringBuilder(authors.substring(0, authors.lastIndexOf(",")));
            authorsDetail.setText(authors.toString());

            publisher = QueryManager.GetPublisherNameFromIsbn(isbn);
            publisherDetail.setText(publisher);
        }
        catch (Exception e)
        {
            System.out.println("BookDetails.GetDetails - An exception was caught: " + e.getMessage());
        }
    }
}
