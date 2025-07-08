package io.broessl.treesql.gui;

import com.formdev.flatlaf.FlatLightLaf;
import io.broessl.treesql.core.NavigableTreeNode;
import io.broessl.treesql.core.types.TreeString;
import io.broessl.treesql.core.types.TreeValue;
import io.broessl.treesql.file.NavigableDirectory;
import io.broessl.treesql.spi.NavigableTree;
import io.broessl.treesql.sql.QueryParser;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 * Main entry point for the TreeSQL Swing GUI application. This application provides a graphical
 * user interface for TreeSQL operations.
 */
public class Main extends JFrame {

  // Supported text file extensions for content display
  private static final Set<String> SUPPORTED_TEXT_EXTENSIONS =
      new HashSet<>(Arrays.asList("json", "yaml", "yml", "csv", "txt", "log", "xml"));

  private static final Map<String, String> EXTENSION_TO_DIRECTIVE_MAP =
      Map.of(
          "json", "~JSON", "yaml", "~YAML", "yml", "~YAML", "csv", "~CSV", "txt", "~LINES", "log",
          "~LINES", "xml", "~XML");

  private RSyntaxTextArea queryTextArea;
  private JTable resultTable;
  private DefaultTableModel tableModel;
  private RSyntaxTextArea fileContentArea;
  private JScrollPane resultScrollPane;
  private RTextScrollPane fileContentScrollPane;
  private RTextScrollPane queryScrollPane;
  private JSplitPane rightSplitPane;
  private JLabel statusLabel;
  private JTree directoryTree;
  private DefaultTreeModel treeModel;
  private File currentDirectory;
  private File selectedRoot; // Currently selected root file or directory
  private DefaultMutableTreeNode selectedRootNode; // Tree node representing the selected root
  private boolean isShowingFileContent = false;

  public Main() {
    initializeUI();
    setupComponents();
    setupEventHandlers();

    // Open folder dialog on startup
    SwingUtilities.invokeLater(() -> openFolder());
  }

  private void initializeUI() {
    setTitle("TreeSQL GUI - Query Tool");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1200, 800);
    setLocationRelativeTo(null);

    // Create menu bar
    JMenuBar menuBar = new JMenuBar();

    // File menu
    JMenu fileMenu = new JMenu("File");
    JMenuItem openFolderItem = new JMenuItem("Open Folder...");
    JMenuItem saveAsItem = new JMenuItem("Save As...");
    JMenuItem showSelectedRootItem = new JMenuItem("Show Selected Root");
    JMenuItem exitItem = new JMenuItem("Exit");

    fileMenu.add(openFolderItem);
    fileMenu.add(saveAsItem);
    fileMenu.addSeparator();
    fileMenu.add(showSelectedRootItem);
    fileMenu.addSeparator();
    fileMenu.add(exitItem);

    // Query menu
    JMenu queryMenu = new JMenu("Query");
    JMenuItem executeItem = new JMenuItem("Execute Query (Ctrl+Enter)");
    JMenuItem clearResultsItem = new JMenuItem("Clear Results");

    queryMenu.add(executeItem);
    queryMenu.add(clearResultsItem);

    // Help menu
    JMenu helpMenu = new JMenu("Help");
    JMenuItem aboutItem = new JMenuItem("About");
    helpMenu.add(aboutItem);

    menuBar.add(fileMenu);
    menuBar.add(queryMenu);
    menuBar.add(helpMenu);

    setJMenuBar(menuBar);

    // Setup exit action
    exitItem.addActionListener(e -> System.exit(0));
    aboutItem.addActionListener(e -> showAboutDialog());
    openFolderItem.addActionListener(e -> openFolder());
    showSelectedRootItem.addActionListener(e -> showSelectedRootInfo());
    executeItem.addActionListener(e -> executeQuery());
    clearResultsItem.addActionListener(e -> clearResults());
  }

  private void setupComponents() {
    setLayout(new BorderLayout());

    // Create main panel with padding
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

    // Create toolbar
    JPanel toolbarPanel = new JPanel(new BorderLayout());

    toolbarPanel.add(new JLabel("TreeSQL Query Interface"), BorderLayout.CENTER);

    // Create query input panel with text area and execute button
    JPanel queryPanel = new JPanel(new BorderLayout(5, 0));

    // Create SQL syntax-highlighted query text area
    queryTextArea = new RSyntaxTextArea("SELECT foo, @foo, ~foo FROM \"/~foo\" WHERE TRUE");
    queryTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
    queryTextArea.setCodeFoldingEnabled(false);
    queryTextArea.setAntiAliasingEnabled(true);
    queryTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
    queryTextArea.setRows(1);
    queryTextArea.setLineWrap(true);
    queryTextArea.setWrapStyleWord(true);

    // Create scroll pane for the query text area
    queryScrollPane = new RTextScrollPane(queryTextArea);
    queryScrollPane.setFoldIndicatorEnabled(false);
    queryScrollPane.setLineNumbersEnabled(false);
    queryScrollPane.setPreferredSize(new Dimension(0, 30));

    // Add keyboard shortcut for Ctrl+Enter to execute query
    queryTextArea.getInputMap().put(KeyStroke.getKeyStroke("ctrl ENTER"), "executeQuery");
    queryTextArea
        .getActionMap()
        .put(
            "executeQuery",
            new AbstractAction() {
              @Override
              public void actionPerformed(ActionEvent e) {
                executeQuery();
              }
            });

    // Add Enter key to execute query as well
    queryTextArea.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "executeQuery");

    // Add document listener for real-time syntax validation
    queryTextArea
        .getDocument()
        .addDocumentListener(
            new DocumentListener() {
              @Override
              public void insertUpdate(DocumentEvent e) {
                Main.this.validateQuerySyntax();
              }

              @Override
              public void removeUpdate(DocumentEvent e) {
                Main.this.validateQuerySyntax();
              }

              @Override
              public void changedUpdate(DocumentEvent e) {
                Main.this.validateQuerySyntax();
              }
            });

    // Perform initial validation
    validateQuerySyntax();

    // Create small execute button
    JButton executeButton = new JButton("▶");
    executeButton.setToolTipText("Execute Query (Ctrl+Enter)");
    executeButton.setPreferredSize(new Dimension(30, 25));
    executeButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
    executeButton.addActionListener(e -> executeQuery());

    // Create a panel for the execute button to give it proper sizing
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    buttonPanel.add(executeButton);

    queryPanel.add(queryScrollPane, BorderLayout.CENTER);
    queryPanel.add(buttonPanel, BorderLayout.EAST);

    // Set a fixed height for the query panel
    queryPanel.setPreferredSize(new Dimension(queryPanel.getPreferredSize().width, 40));
    queryPanel.setMinimumSize(new Dimension(queryPanel.getPreferredSize().width, 40));

    // Initialize result table and model
    tableModel = new DefaultTableModel();
    resultTable = new JTable(tableModel);
    resultTable.getTableHeader().setBackground(Color.LIGHT_GRAY);
    resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    resultTable.setRowSelectionAllowed(true);
    resultTable.setColumnSelectionAllowed(false);
    resultTable.getTableHeader().setReorderingAllowed(false);

    // Set up alternating colors for better readability
    resultTable.setDefaultRenderer(Object.class, new AlternatingColorRenderer());

    // Set initial empty state
    clearResultTable("Query results will appear here...");

    resultScrollPane = new JScrollPane(resultTable);

    // Create file content area for displaying selected files
    fileContentArea = new RSyntaxTextArea();
    fileContentArea.setEditable(false);
    fileContentArea.setCodeFoldingEnabled(true);
    fileContentArea.setAntiAliasingEnabled(true);
    fileContentArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

    fileContentScrollPane = new RTextScrollPane(fileContentArea);
    fileContentScrollPane.setFoldIndicatorEnabled(true);

    // Create directory tree
    setupDirectoryTree();

    // Create horizontal split pane with tree on left and results on right (no query
    // area here)
    rightSplitPane =
        new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createDirectoryTreePanel(), resultScrollPane);
    rightSplitPane.setDividerLocation(250);
    rightSplitPane.setResizeWeight(0.2);

    // Create main vertical split pane with query at top and tree/results below
    JSplitPane mainSplitPane =
        new JSplitPane(JSplitPane.VERTICAL_SPLIT, queryPanel, rightSplitPane);
    mainSplitPane.setDividerLocation(50); // Fixed position to accommodate the compact query panel
    mainSplitPane.setResizeWeight(0.0); // Don't resize the query area when window is resized

    // Create status bar
    statusLabel = new JLabel("Ready");
    statusLabel.setBorder(new EmptyBorder(5, 5, 5, 5));

    // Add components to main panel
    mainPanel.add(toolbarPanel, BorderLayout.NORTH);
    mainPanel.add(mainSplitPane, BorderLayout.CENTER);
    mainPanel.add(statusLabel, BorderLayout.SOUTH);

    add(mainPanel);
  }

  private void setupEventHandlers() {
    // Add tree selection listener to handle file clicks
    directoryTree.addTreeSelectionListener(
        new TreeSelectionListener() {
          @Override
          public void valueChanged(TreeSelectionEvent e) {
            TreePath path = e.getNewLeadSelectionPath();
            if (path != null) {
              DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
              Object userObject = node.getUserObject();

              System.out.println("Tree selection changed. User object: " + userObject);
              System.out.println(
                  "User object class: "
                      + (userObject != null ? userObject.getClass().getName() : "null"));

              if (userObject instanceof FileTreeNode) {
                FileTreeNode fileNode = (FileTreeNode) userObject;
                File selectedFile = fileNode.getFile();

                System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                System.out.println("Is file: " + selectedFile.isFile());
                System.out.println("Is text file: " + isTextFile(selectedFile));

                if (selectedFile.isFile() && isTextFile(selectedFile)) {
                  System.out.println("Displaying file content for: " + selectedFile.getName());
                  displayFileContent(selectedFile);
                } else if (selectedFile.isFile()) {
                  System.out.println(
                      "File is not a supported text file: " + selectedFile.getName());
                  statusLabel.setText("File type not supported: " + selectedFile.getName());
                }
              } else if (userObject instanceof String) {
                System.out.println("Selected root node: " + userObject);
              }
            }
          }
        });

    // Add mouse listener for context menu (right-click) and double-click selection
    directoryTree.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
              showContextMenu(e);
            }
          }

          @Override
          public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
              showContextMenu(e);
            }
          }

          @Override
          public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
              // Handle double-click for root selection
              TreePath path = directoryTree.getPathForLocation(e.getX(), e.getY());
              if (path != null) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                Object userObject = node.getUserObject();

                if (userObject instanceof FileTreeNode) {
                  FileTreeNode fileNode = (FileTreeNode) userObject;
                  File file = fileNode.getFile();
                  selectAsRoot(node, file);
                }
              }
            }
          }
        });
  }

  // Helper methods for result table management
  private void clearResultTable(String message) {
    tableModel.setRowCount(0);
    tableModel.setColumnCount(0);

    // Add a single column and row to show the message
    if (message != null && !message.isEmpty()) {
      tableModel.addColumn("Status");
      tableModel.addRow(new Object[] {message});
    }
  }

  private void setResultTableError(String errorMessage) {
    clearResultTable("Error: " + errorMessage);
  }

  private void setupResultTable(List<String> headers) {
    tableModel.setRowCount(0);
    tableModel.setColumnCount(0);

    // Add columns based on headers
    for (String header : headers) {
      tableModel.addColumn(header);
    }
  }

  private void addResultTableRow(List<String> rowData) {
    Object[] row = rowData.toArray(new Object[0]);
    tableModel.addRow(row);
  }

  private void executeQuery() {
    String query = queryTextArea.getText().trim();

    if (query.isEmpty()) {
      showQueryResults();
      clearResultTable("Please enter a query to execute.");
      statusLabel.setText("Error: Empty query");
      return;
    }

    QueryParser parsedQuery = null;
    try {
      parsedQuery = QueryParser.parseStatement(query);
    } catch (Exception e) {
      setResultTableError("Error parsing query: " + e.getMessage());
      statusLabel.setText("Error: " + e.getMessage());
      return;
    }

    showQueryResults();
    statusLabel.setText("Executing query...");

    NavigableTreeNode rootAsNavigableTree = null;
    if (selectedRoot.isDirectory()) {
      rootAsNavigableTree = new NavigableDirectory(selectedRoot.toPath(), null);
    } else if (EXTENSION_TO_DIRECTIVE_MAP.containsKey(getFileExtension(selectedRoot))) {
      try {
        String directive = EXTENSION_TO_DIRECTIVE_MAP.get(getFileExtension(selectedRoot));
        var provider = NavigableTree.providerFor(directive);
        rootAsNavigableTree =
            provider
                .buildTreeRoot(new TreeString(Files.readString(selectedRoot.toPath())))
                .orElseThrow();
      } catch (IOException e) {
        setResultTableError("Error reading JSON file: " + e.getMessage());
        statusLabel.setText("Error: " + e.getMessage());
        return;
      }
    } else {
      setResultTableError("Unknown provider for file type.");
      statusLabel.setText("Error: Unsupported file type");
      return;
    }

    try {
      List<String> tableHeader = parsedQuery.getColumnNames();

      // Setup table with headers
      setupResultTable(tableHeader);

      // Execute query and populate results
      List<List<TreeValue>> results = parsedQuery.execute(rootAsNavigableTree).toList();

      SwingUtilities.invokeLater(
          () -> {
            for (List<TreeValue> row : results) {
              // Convert each result row to string values
              List<String> rowData =
                  row.stream()
                      .map(primitive -> primitive != null ? primitive.toString() : "")
                      .toList();

              addResultTableRow(rowData);
            }

            statusLabel.setText("Query executed successfully - " + results.size() + " row(s)");
          });
    } catch (Exception e) {
      setResultTableError("Error executing query: " + e.getMessage());
      statusLabel.setText("Error: " + e.getMessage());
    }
  }

  /**
   * Validates the current query syntax and updates the background color accordingly. Light green
   * for valid queries, light red for invalid queries.
   */
  private void validateQuerySyntax() {
    String query = queryTextArea.getText().trim();

    // Define colors for validation feedback
    Color validColor = new Color(230, 255, 230); // Light green
    Color invalidColor = new Color(255, 230, 230); // Light red
    Color defaultColor = Color.WHITE; // Default white

    if (query.isEmpty()) {
      // Empty query - use default background
      queryTextArea.setBackground(defaultColor);
      return;
    }

    try {
      // Try to parse the query
      QueryParser.parseStatement(query);
      // If parsing succeeds, set light green background
      queryTextArea.setBackground(validColor);
    } catch (Exception e) {
      // If parsing fails, set light red background
      queryTextArea.setBackground(invalidColor);
    }
  }

  private void clearResults() {
    showQueryResults();
    clearResultTable("Query results will appear here...");
    statusLabel.setText("Results cleared");
  }

  private void showQueryResults() {
    if (isShowingFileContent) {
      // Switch back to query results view
      rightSplitPane.setRightComponent(resultScrollPane);
      isShowingFileContent = false;
    }
  }

  private void displayFileContent(File file) {
    System.out.println("displayFileContent called for: " + file.getAbsolutePath());
    try {
      // Read file content
      String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
      System.out.println("File content read successfully. Length: " + content.length());

      // Set syntax highlighting based on file extension
      String extension = getFileExtension(file);
      String syntaxStyle = getSyntaxStyleForExtension(extension);
      System.out.println("File extension: " + extension + ", Syntax style: " + syntaxStyle);
      fileContentArea.setSyntaxEditingStyle(syntaxStyle);

      // Set content
      fileContentArea.setText(content);
      fileContentArea.setCaretPosition(0); // Scroll to top
      System.out.println("Content set in fileContentArea");

      // Switch to file content view
      System.out.println("Switching to file content view...");
      rightSplitPane.setRightComponent(fileContentScrollPane);
      isShowingFileContent = true;
      System.out.println("View switched successfully");

      statusLabel.setText(
          "Viewing file: " + file.getName() + " (" + content.length() + " characters)");

    } catch (IOException e) {
      System.err.println("Error reading file: " + e.getMessage());
      e.printStackTrace();
      // Don't change the result table when there's a file reading error
      statusLabel.setText("Error reading file: " + file.getName());
    }
  }

  private boolean isTextFile(File file) {
    String extension = getFileExtension(file);
    return SUPPORTED_TEXT_EXTENSIONS.contains(extension.toLowerCase());
  }

  private String getFileExtension(File file) {
    String fileName = file.getName();
    int lastDotIndex = fileName.lastIndexOf('.');
    if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
      return fileName.substring(lastDotIndex + 1);
    }
    return "";
  }

  private String getSyntaxStyleForExtension(String extension) {
    switch (extension.toLowerCase()) {
      case "json":
        return SyntaxConstants.SYNTAX_STYLE_JSON;
      case "xml":
        return SyntaxConstants.SYNTAX_STYLE_XML;
      case "yaml":
      case "yml":
        return SyntaxConstants.SYNTAX_STYLE_YAML;
      case "csv":
        return SyntaxConstants.SYNTAX_STYLE_CSV;
      case "log":
      case "txt":
      default:
        return SyntaxConstants.SYNTAX_STYLE_NONE;
    }
  }

  private void showContextMenu(MouseEvent e) {
    // Get the tree path at the mouse position
    TreePath path = directoryTree.getPathForLocation(e.getX(), e.getY());
    if (path != null) {
      // Select the node under the mouse
      directoryTree.setSelectionPath(path);

      DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
      Object userObject = node.getUserObject();

      if (userObject instanceof FileTreeNode) {
        FileTreeNode fileNode = (FileTreeNode) userObject;
        File file = fileNode.getFile();

        // Create context menu
        JPopupMenu contextMenu = new JPopupMenu();

        // Allow selecting both files and directories as root
        JMenuItem selectRootItem = new JMenuItem("Select as Root (or double-click)");
        selectRootItem.addActionListener(evt -> selectAsRoot(node, file));
        contextMenu.add(selectRootItem);

        // Show the context menu
        contextMenu.show(directoryTree, e.getX(), e.getY());
      }
    }
  }

  private void selectAsRoot(DefaultMutableTreeNode node, File file) {
    if (file != null) {
      selectedRoot = file;
      selectedRootNode = node;

      // Update the status to show the selected root
      String fileType = file.isDirectory() ? "directory" : "file";
      statusLabel.setText("Selected root " + fileType + ": " + file.getAbsolutePath());

      // Refresh the tree with custom renderer to highlight the selected root
      updateTreeRenderer();

      System.out.println("Selected as root " + fileType + ": " + file.getAbsolutePath());
    }
  }

  private void updateTreeRenderer() {
    directoryTree.setCellRenderer(
        new DefaultTreeCellRenderer() {
          @Override
          public Component getTreeCellRendererComponent(
              JTree tree,
              Object value,
              boolean selected,
              boolean expanded,
              boolean leaf,
              int row,
              boolean hasFocus) {

            Component component =
                super.getTreeCellRendererComponent(
                    tree, value, selected, expanded, leaf, row, hasFocus);

            // Check if this node is the selected root
            if (value == selectedRootNode) {
              // Much more prominent highlighting for selected root
              setBackgroundSelectionColor(
                  new Color(255, 165, 0, 200)); // Bright orange background when selected
              setBackgroundNonSelectionColor(
                  new Color(255, 140, 0, 150)); // Orange background when not selected
              setTextSelectionColor(Color.WHITE); // White text when selected
              setTextNonSelectionColor(Color.BLACK); // Black text when not selected
              setBorderSelectionColor(new Color(255, 100, 0)); // Dark orange border
              setFont(getFont().deriveFont(Font.BOLD)); // Bold font for selected root
            } else {
              // Reset to default colors for other nodes
              setBackgroundSelectionColor(UIManager.getColor("Tree.selectionBackground"));
              setBackgroundNonSelectionColor(UIManager.getColor("Tree.background"));
              setTextSelectionColor(UIManager.getColor("Tree.selectionForeground"));
              setTextNonSelectionColor(UIManager.getColor("Tree.foreground"));
              setBorderSelectionColor(UIManager.getColor("Tree.selectionBorderColor"));
              setFont(getFont().deriveFont(Font.PLAIN)); // Normal font for other nodes
            }

            return component;
          }
        });

    directoryTree.repaint();
  }

  private void showAboutDialog() {
    javax.swing.JOptionPane.showMessageDialog(
        this,
        "TreeSQL GUI v0.0.1-SNAPSHOT\n\n"
            + "A graphical user interface for TreeSQL operations.\n"
            + "© 2025 TreeSQL Project",
        "About TreeSQL GUI",
        javax.swing.JOptionPane.INFORMATION_MESSAGE);
  }

  private void showSelectedRootInfo() {
    if (selectedRoot != null) {
      String fileType = selectedRoot.isDirectory() ? "Directory" : "File";
      String additionalInfo =
          selectedRoot.isDirectory()
              ? "Right-click on any directory or file in the tree to select it as root, or simply double-click."
              : "Right-click on any directory or file in the tree to select it as root, or simply double-click.";

      javax.swing.JOptionPane.showMessageDialog(
          this,
          "Currently Selected Root "
              + fileType
              + ":\n\n"
              + "Path: "
              + selectedRoot.getAbsolutePath()
              + "\n"
              + "Name: "
              + selectedRoot.getName()
              + "\n"
              + "Type: "
              + fileType
              + "\n\n"
              + additionalInfo,
          "Selected Root Information",
          javax.swing.JOptionPane.INFORMATION_MESSAGE);
    } else {
      javax.swing.JOptionPane.showMessageDialog(
          this,
          "No root selected.\n\n"
              + "Open a folder first, then right-click on any directory or file\n"
              + "in the tree to select it as root, or simply double-click on it.",
          "No Root Selected",
          javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
  }

  private void setupDirectoryTree() {
    // Create empty tree model initially
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("No folder selected");
    treeModel = new DefaultTreeModel(root);
    directoryTree = new JTree(treeModel);
    directoryTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    directoryTree.setRootVisible(true);
    directoryTree.setShowsRootHandles(true);
  }

  private JPanel createDirectoryTreePanel() {
    JPanel treePanel = new JPanel(new BorderLayout());
    treePanel.setBorder(new TitledBorder("Directory Tree"));

    JScrollPane treeScrollPane = new JScrollPane(directoryTree);
    treeScrollPane.setPreferredSize(new Dimension(250, 0));
    treePanel.add(treeScrollPane, BorderLayout.CENTER);

    return treePanel;
  }

  private void openFolder() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fileChooser.setDialogTitle("Select Directory");

    // Set current directory if one was previously selected
    if (currentDirectory != null) {
      fileChooser.setCurrentDirectory(currentDirectory);
    }

    int result = fileChooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
      currentDirectory = fileChooser.getSelectedFile();
      loadDirectoryTree(currentDirectory);
      statusLabel.setText(
          "Loaded directory and selected as root: " + currentDirectory.getAbsolutePath());
    }
  }

  private void loadDirectoryTree(File directory) {
    if (directory == null || !directory.exists() || !directory.isDirectory()) {
      return;
    }

    // Create new tree model with the selected directory as root
    DefaultMutableTreeNode root = new DefaultMutableTreeNode(new FileTreeNode(directory));
    treeModel = new DefaultTreeModel(root);

    // Load directory contents recursively
    loadDirectoryNode(root, directory);

    // Update the tree
    directoryTree.setModel(treeModel);

    // Set the opened directory as the default selected root
    selectedRoot = directory;
    selectedRootNode = root;

    // Update the tree renderer to highlight the selected root
    updateTreeRenderer();

    // Expand all nodes in the tree
    expandAllNodes();
  }

  private void loadDirectoryNode(DefaultMutableTreeNode parentNode, File directory) {
    File[] files = directory.listFiles();
    if (files == null) {
      return;
    }

    // Sort files: directories first, then files, both alphabetically
    java.util.Arrays.sort(
        files,
        (f1, f2) -> {
          if (f1.isDirectory() && !f2.isDirectory()) {
            return -1;
          } else if (!f1.isDirectory() && f2.isDirectory()) {
            return 1;
          } else {
            return f1.getName().compareToIgnoreCase(f2.getName());
          }
        });

    for (File file : files) {
      // Skip hidden files and directories
      if (file.isHidden()) {
        continue;
      }

      DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(new FileTreeNode(file));
      parentNode.add(fileNode);

      // If it's a directory, recursively add its contents (but limit depth for
      // performance)
      if (file.isDirectory() && parentNode.getLevel() < 12) {
        loadDirectoryNode(fileNode, file);
      }
    }
  }

  // Helper class to represent files in the tree
  private static class FileTreeNode {
    private File file;

    public FileTreeNode(File file) {
      this.file = file;
    }

    public File getFile() {
      return file;
    }

    @Override
    public String toString() {
      String name = file.getName();
      if (file.isDirectory()) {
        return "📁 " + name;
      } else {
        return "📄 " + name;
      }
    }
  }

  private void expandAllNodes() {
    DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
    expandAllNodes(root, 0);
  }

  private void expandAllNodes(DefaultMutableTreeNode node, int currentRow) {
    if (node.getChildCount() >= 0) {
      for (int i = 0; i < node.getChildCount(); i++) {
        DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
        expandAllNodes(child, currentRow + i + 1);
      }
    }

    // Expand this node
    directoryTree.expandPath(new javax.swing.tree.TreePath(node.getPath()));
  }

  public File getSelectedRoot() {
    return selectedRoot;
  }

  /** Custom cell renderer for alternating row and column colors */
  private static class AlternatingColorRenderer extends DefaultTableCellRenderer {
    // Define colors for alternating pattern

    @Override
    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

      Component c =
          super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

      if (!isSelected) {
        // Create a checkerboard pattern with alternating row and column colors
        boolean evenRow = (row % 2 == 0);
        boolean evenColumn = (column % 2 == 0);

        if (evenRow && evenColumn) {
          c.setBackground(Color.WHITE);
        } else if (evenRow && !evenColumn) {
          c.setBackground(Color.LIGHT_GRAY);
        } else if (!evenRow && evenColumn) {
          c.setBackground(Color.LIGHT_GRAY);
        } else {
          c.setBackground(Color.GRAY);
        }
      }

      return c;
    }
  }

  public static void main(String[] args) {
    // Set up FlatLaf Look and Feel
    try {
      UIManager.setLookAndFeel(new FlatLightLaf());
    } catch (Exception e) {
      System.err.println("Failed to initialize FlatLaf LaF, using default");
    }

    SwingUtilities.invokeLater(
        () -> {
          new Main().setVisible(true);
        });
  }
}
