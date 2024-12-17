import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class NayramStoreManager extends JFrame {
    private final InventoryManager inventoryManager = new InventoryManager();
    private final SalesManager salesManager = new SalesManager(inventoryManager);
    private final ExpenseManager expenseManager = new ExpenseManager();


    public NayramStoreManager() {
        setTitle("Minimal Nayram Store Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


        // Header
        JLabel headerLabel = new JLabel("Nayram Store Manager", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerLabel.setOpaque(true);
        headerLabel.setBackground(new Color(60, 90, 153));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(headerLabel, BorderLayout.NORTH);


        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Inventory", inventoryManager.createInventoryPanel());
        tabbedPane.addTab("Sales", salesManager.createSalesPanel());
        tabbedPane.addTab("Expenses", expenseManager.createExpensePanel());
        add(tabbedPane, BorderLayout.CENTER);


        // Footer: Summary Buttons
        JPanel footerPanel = new JPanel(new FlowLayout());
        JButton viewSummaryButton = StyledButton.createStyledButton("View Summary", new Color(102, 153, 255), Color.WHITE);
        JButton saveSummaryButton = StyledButton.createStyledButton("Save Summary", new Color(255, 193, 7), Color.BLACK);


        viewSummaryButton.addActionListener(e -> displaySummary());
        saveSummaryButton.addActionListener(e -> saveSummaryToFile());


        footerPanel.add(viewSummaryButton);
        footerPanel.add(saveSummaryButton);
        add(footerPanel, BorderLayout.SOUTH);
    }


    private void displaySummary() {
        double totalRevenue = salesManager.getTotalRevenue();
        double totalExpenses = expenseManager.getTotalExpenses();
        double netProfit = totalRevenue - totalExpenses;


        String summary = String.format(
            "Business Summary:\n\nTotal Revenue: $%.2f\nTotal Expenses: $%.2f\nNet Profit: $%.2f",
            totalRevenue, totalExpenses, netProfit
        );


        JOptionPane.showMessageDialog(this, summary, "Summary", JOptionPane.INFORMATION_MESSAGE);
    }


    private void saveSummaryToFile() {
        double totalRevenue = salesManager.getTotalRevenue();
        double totalExpenses = expenseManager.getTotalExpenses();
        double netProfit = totalRevenue - totalExpenses;


        String summary = String.format(
            "Business Summary:\n\nTotal Revenue: $%.2f\nTotal Expenses: $%.2f\nNet Profit: $%.2f",
            totalRevenue, totalExpenses, netProfit
        );


        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "summary_" + timestamp + ".txt";


            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                writer.write(summary);
            }


            JOptionPane.showMessageDialog(this, "Summary saved successfully to '" + filename + "'!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving summary: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NayramStoreManager manager = new NayramStoreManager();
            manager.setVisible(true);
        });
    }
}
