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


public class ExpenseManager {
    private final Map<Integer, Expense> expenses = new HashMap<>();
    private final DefaultTableModel expenseTableModel = new DefaultTableModel(new Object[]{"ID", "Description", "Amount"}, 0);
    private int expenseId = 0;


    public double getTotalExpenses() {
        return expenses.values().stream().mapToDouble(Expense::getAmount).sum();
    }
   


    public JPanel createExpensePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField descriptionField = new JTextField();
        JTextField amountField = new JTextField();


        formPanel.add(new JLabel("Description:"));
        formPanel.add(descriptionField);
        formPanel.add(new JLabel("Amount:"));
        formPanel.add(amountField);


        JTable expenseTable = new JTable(expenseTableModel);
        JScrollPane tableScrollPane = new JScrollPane(expenseTable);


        JButton addExpenseButton = StyledButton.createStyledButton("Add Expense", new Color(76, 175, 80), Color.WHITE);
        addExpenseButton.addActionListener(e -> {
            try {
                String description = descriptionField.getText();
                double amount = Double.parseDouble(amountField.getText());


                Expense expense = new Expense(description, amount);
                expenses.put(++expenseId, expense);
                expenseTableModel.addRow(new Object[]{expenseId, description, amount});


                descriptionField.setText("");
                amountField.setText("");


                JOptionPane.showMessageDialog(panel, "Expense added successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid input for amount!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        JButton deleteExpenseButton = StyledButton.createStyledButton("Delete Expense", new Color(220, 53, 69), Color.WHITE);
        deleteExpenseButton.addActionListener(e -> {
            int selectedRow = expenseTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(panel, "Please select an expense to delete!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }


            int expenseId = (int) expenseTableModel.getValueAt(selectedRow, 0);
            expenses.remove(expenseId);
            expenseTableModel.removeRow(selectedRow);


            JOptionPane.showMessageDialog(panel, "Expense deleted successfully!");
        });


        JButton saveExpensesButton = StyledButton.createStyledButton("Save Expenses", new Color(255, 193, 7), Color.BLACK);
        saveExpensesButton.addActionListener(e -> saveExpensesToFile());


        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addExpenseButton);
        buttonPanel.add(deleteExpenseButton);
        buttonPanel.add(saveExpensesButton);


        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);


        return panel;
    }


    private void saveExpensesToFile() {
        try {
            // Generate a unique filename with the current date and time
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "expenses_" + timestamp + ".txt";
   
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                writer.write("ID\tDescription\tAmount\n");
                for (Map.Entry<Integer, Expense> entry : expenses.entrySet()) {
                    Expense expense = entry.getValue();
                    writer.write(String.format("%d\t%s\t%.2f\n", entry.getKey(), expense.getDescription(), expense.getAmount()));
                }
            }
   
            JOptionPane.showMessageDialog(null, "Expenses saved successfully to '" + filename + "'!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving expenses: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}    
