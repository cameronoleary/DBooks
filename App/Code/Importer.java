import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Class: Importer
 * Purpose: to provide a simple interface for inserting data into the database
 *
 * Note: this is not part of the project spec; just something I made to make my testing a little easier.
 */
public class Importer extends JFrame
{
    private JPanel MainPanel;
    private JButton ImportButton;
    private JComboBox FilesBox;
    private JFrame ParentForm;
    private QueryManager QueryManager;
    private ArrayList<String> ValidFiles;

    public Importer(JFrame parent, QueryManager queryManager)
    {
        ParentForm   = parent;
        QueryManager = queryManager;
        ValidFiles   = new ArrayList<>();

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

        ValidFiles.add("books.txt");
        ValidFiles.add("authors.txt");
        ValidFiles.add("publishers.txt");

        InitializeFileList();
    }

    private void InitializeFileList()
    {
        String filesDirPath = System.getProperty("user.dir") + "/src/files/";
        File filesDir       = new File(filesDirPath);

        if (!filesDir.exists()) { return; }

        if (filesDir.isDirectory() && filesDir.list().length > 0)
        {
            for (File file : filesDir.listFiles())
            {
                if (ValidFiles.contains(file.getName()))
                {
                    FilesBox.addItem(file.getPath());
                }
            }
        }
    }

    private void RegisterListeners()
    {
        ImportButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Import();
            }
        });
    }

    private void Import()
    {
        String fileName;
        String filePath;
        String[] data;
        String[] authors;
        Scanner buffer;
        File selectedFile;

        try
        {
            filePath = (String)FilesBox.getSelectedItem();

            if (filePath.isEmpty()) { return; }

            selectedFile = new File(filePath);
            buffer       = new Scanner(selectedFile);
            fileName     = filePath.substring(filePath.lastIndexOf('/') + 1);

            while (buffer.hasNextLine())
            {
                if (fileName.toLowerCase().equals("books.txt"))
                {
                    data = buffer.nextLine().split(";");
                    authors = data[6].split(",");

                    QueryManager.AddBook(data[0], data[1], data[2], data[3], data[4], data[5], authors, data[7]);
                }
                else if(fileName.toLowerCase().equals("authors.txt"))
                {
                    data = buffer.nextLine().split(",");
                    QueryManager.ImporterAddAuthor(data);
                }
                else if (fileName.toLowerCase().equals("publishers.txt"))
                {
                    data = buffer.nextLine().split(",");
                    QueryManager.ImporterAddPublisher(data);
                }
            }

            buffer.close();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("An exception was caught: " + e.getMessage());
        }
    }
}
