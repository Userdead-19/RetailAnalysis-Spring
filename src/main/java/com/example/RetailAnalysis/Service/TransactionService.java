package com.example.RetailAnalysis.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.RetailAnalysis.Model.Transaction;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

@Service
public class TransactionService {

    private static final String CSV_FILE = "src\\main\\resources\\transactions.csv";

    // Get all transactions
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = null;

        try (Reader reader = new BufferedReader(new FileReader(CSV_FILE))) {
            CsvToBean<Transaction> csvToBean = new CsvToBeanBuilder<Transaction>(reader)
                    .withType(Transaction.class) // Specify the type of beans to be created
                    .withIgnoreLeadingWhiteSpace(true) // Optional: Ignore leading whitespace
                    .build();

            System.out.println("Reading transactions from CSV file");
            System.out.println("=====================================");
            transactions = csvToBean.parse();

            // Log the parsed transactions
            for (Transaction transaction : transactions) {
                System.out.println(transaction);
            }

        } catch (IOException e) {
            System.err.println("Error reading the CSV file: " + e.getMessage());
        }

        return transactions;
    }

    // Add new transaction
    public void addTransaction(Transaction transaction) throws IOException {
        // Generate a unique transaction ID
        String transactionId = UUID.randomUUID().toString();
        transaction.setTransactionId(transactionId); // Set the generated ID

        List<Transaction> transactions = getAllTransactions();
        for (Transaction t : transactions) {
            System.out.println(t);
        }
        transactions.add(transaction);
        writeTransactionsToCSV(transactions);
    }

    private void writeTransactionsToCSV(List<Transaction> transactions) throws IOException {

        try (FileWriter writer = new FileWriter(CSV_FILE)) {
            // Write the header in the required order
            writer.append("Transaction_ID,Date,Customer_Name,Product,Total_Items,Total_Cost,Payment_Method,City,Store_Type,Discount_Applied,Customer_Category,Season,Promotion\n");

            // Loop through the transactions and write each one in the correct order
            for (Transaction transaction : transactions) {
                writer.append(transaction.getTransactionId())
                        .append(",")
                        .append(transaction.getDate())
                        .append(",")
                        .append(transaction.getCustomerName())
                        .append(",")
                        .append(transaction.getProduct())
                        .append(",")
                        .append(String.valueOf(transaction.getTotalItems()))
                        .append(",")
                        .append(String.valueOf(transaction.getTotalCost()))
                        .append(",")
                        .append(transaction.getPaymentMethod())
                        .append(",")
                        .append(transaction.getCity())
                        .append(",")
                        .append(transaction.getStoreType())
                        .append(",")
                        .append(String.valueOf(transaction.isDiscountApplied()))
                        .append(",")
                        .append(transaction.getCustomerCategory())
                        .append(",")
                        .append(transaction.getSeason())
                        .append(",")
                        .append(transaction.getPromotion())
                        .append("\n");
            }
        } catch (IOException e) {
            throw new IOException("Error writing transactions to CSV file", e);
        }
    }

    // Update transaction
    public void updateTransaction(Transaction updatedTransaction) throws IOException {
        List<Transaction> transactions = getAllTransactions();
        for (Transaction t : transactions) {
            if (t.getTransactionId().equals(updatedTransaction.getTransactionId())) {
                transactions.remove(t);
                transactions.add(updatedTransaction);
                break;
            }
        }
        writeTransactionsToCSV(transactions);
    }

    // Delete transaction
    public void deleteTransaction(String transactionID) throws IOException {
        List<Transaction> transactions = getAllTransactions();
        transactions.removeIf(t -> t.getTransactionId().equals(transactionID));
        writeTransactionsToCSV(transactions);
    }

}
