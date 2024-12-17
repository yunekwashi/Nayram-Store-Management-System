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


public class InventoryManager {
    private final Map<String, Product> inventory = new HashMap<>();
    private final DefaultTableModel inventoryTableModel = new DefaultTableModel(new Object[]{"Name", "Category", "Price", "Quantity"}, 0);
    private final DefaultListModel<String> productListModel = new DefaultListModel<>();


    public JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField nameField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField quantityField = new JTextField();


        formPanel.add(new JLabel("Product Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryField);
        formPanel.add(new JLabel("Price:"));
        formPanel.add(priceField);
        formPanel.add(new JLabel("Quantity:"));
        formPanel.add(quantityField);


        JButton addButton = StyledButton.createStyledButton("Add Product", new Color(76, 175, 80), Color.WHITE);
        formPanel.add(addButton);


        JTable inventoryTable = new JTable(inventoryTableModel);
        JScrollPane tableScrollPane = new JScrollPane(inventoryTable);


        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String category = categoryField.getText();
                double price = Double.parseDouble(priceField.getText());
                int quantity = Integer.parseInt(quantityField.getText());


                if (inventory.containsKey(name)) {
                    JOptionPane.showMessageDialog(panel, "Product already exists. Please update it instead.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                Product product = new Product(name, category, price, quantity);
                inventory.put(name, product);
                inventoryTableModel.addRow(new Object[]{name, category, price, quantity});
                productListModel.addElement(name);


                nameField.setText("");
                categoryField.setText("");
                priceField.setText("");
                quantityField.setText("");


                JOptionPane.showMessageDialog(panel, "Product added successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid input. Please check price and quantity fields.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        JButton deleteButton = StyledButton.createStyledButton("Delete Product", new Color(220, 53, 69), Color.WHITE);
        deleteButton.addActionListener(e -> {
            int selectedRow = inventoryTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(panel, "Please select a product to delete!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String productName = (String) inventoryTableModel.getValueAt(selectedRow, 0);
            inventory.remove(productName);
            inventoryTableModel.removeRow(selectedRow);
            productListModel.removeElement(productName);


            JOptionPane.showMessageDialog(panel, "Product deleted successfully!");
        });


        JButton saveButton = StyledButton.createStyledButton("Save Inventory", new Color(255, 193, 7), Color.BLACK);
        saveButton.addActionListener(e -> saveInventoryToFile());


        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(deleteButton);
        buttonPanel.add(saveButton);


        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);


        return panel;
    }


    private void saveInventoryToFile() {
        try {
            // Generate a unique filename with the current date and time
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "inventory_" + timestamp + ".txt";
   
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                writer.write("Name\tCategory\tPrice\tQuantity\n");
                for (Product product : inventory.values()) {
                    writer.write(String.format("%s\t%s\t%.2f\t%d\n", product.getName(), product.getCategory(), product.getPrice(), product.getQuantity()));
                }
            }
   
            JOptionPane.showMessageDialog(null, "Inventory saved successfully to '" + filename + "'!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving inventory: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public DefaultListModel<String> getProductListModel() {
        return productListModel;
    }


    public Map<String, Product> getInventory() {
        return inventory;
    }


    public DefaultTableModel getInventoryTableModel() {
        return inventoryTableModel;
    }
}
