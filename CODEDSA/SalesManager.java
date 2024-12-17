import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class SalesManager {
    private final Map<Integer, Sale> sales = new HashMap<>();
    private final DefaultTableModel salesTableModel = new DefaultTableModel(new Object[]{"ID", "Product", "Quantity", "Total Price"}, 0);
    private int saleId = 0;
    private final InventoryManager inventoryManager;


    public SalesManager(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }
   
    public double getTotalRevenue() {
        return sales.values().stream().mapToDouble(Sale::getTotalPrice).sum();
    }
   


    public JPanel createSalesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JList<String> productList = new JList<>(inventoryManager.getProductListModel());
        JScrollPane productScrollPane = new JScrollPane(productList);
        JTextField quantityField = new JTextField();


        formPanel.add(new JLabel("Products:"));
        formPanel.add(productScrollPane);
        formPanel.add(new JLabel("Quantity:"));
        formPanel.add(quantityField);


        JTable salesTable = new JTable(salesTableModel);
        JScrollPane tableScrollPane = new JScrollPane(salesTable);


        JButton recordSaleButton = StyledButton.createStyledButton("Record Sale", new Color(76, 175, 80), Color.WHITE);
        recordSaleButton.addActionListener(e -> {
            String selectedProduct = productList.getSelectedValue();
            if (selectedProduct == null) {
                JOptionPane.showMessageDialog(panel, "Please select a product!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }


            try {
                int quantity = Integer.parseInt(quantityField.getText());
                Product product = inventoryManager.getInventory().get(selectedProduct);
                if (product.getQuantity() < quantity) {
                    JOptionPane.showMessageDialog(panel, "Insufficient stock!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                product.setQuantity(product.getQuantity() - quantity);
                double totalPrice = product.getPrice() * quantity;


                Sale sale = new Sale(selectedProduct, quantity, totalPrice);
                sales.put(++saleId, sale);
                salesTableModel.addRow(new Object[]{saleId, selectedProduct, quantity, totalPrice});


                inventoryManager.getInventoryTableModel().setRowCount(0); // Refresh inventory table
                for (Product p : inventoryManager.getInventory().values()) {
                    inventoryManager.getInventoryTableModel().addRow(new Object[]{p.getName(), p.getCategory(), p.getPrice(), p.getQuantity()});
                }


                JOptionPane.showMessageDialog(panel, "Sale recorded successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid input for quantity!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        JButton deleteSaleButton = StyledButton.createStyledButton("Delete Sale", new Color(220, 53, 69), Color.WHITE);
        deleteSaleButton.addActionListener(e -> {
            int selectedRow = salesTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(panel, "Please select a sale to delete!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }


            int saleId = (int) salesTableModel.getValueAt(selectedRow, 0);
            sales.remove(saleId);
            salesTableModel.removeRow(selectedRow);


            JOptionPane.showMessageDialog(panel, "Sale deleted successfully!");
        });


        JButton saveSalesButton = StyledButton.createStyledButton("Save Sales", new Color(255, 193, 7), Color.BLACK);
        saveSalesButton.addActionListener(e -> saveSalesToFile());


        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(recordSaleButton);
        buttonPanel.add(deleteSaleButton);
        buttonPanel.add(saveSalesButton); // Add Save Sales button to the panel


        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);


        return panel;
    }


    private void saveSalesToFile() {
        try {
            // Generate a unique filename with the current date and time
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "sales_" + timestamp + ".txt";
   
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                writer.write("ID\tProduct\tQuantity\tTotal Price\n");
                for (Map.Entry<Integer, Sale> entry : sales.entrySet()) {
                    Sale sale = entry.getValue();
                    writer.write(String.format("%d\t%s\t%d\t%.2f\n", entry.getKey(), sale.getProductName(), sale.getQuantity(), sale.getTotalPrice()));
                }
            }
   
            JOptionPane.showMessageDialog(null, "Sales saved successfully to '" + filename + "'!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving sales: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
