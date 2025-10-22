package library;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SuppressWarnings("unused")
public class GenerateReports {

    public Node getView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #e8f5e9, #ffffff);");
        
        Label header = new Label("Generate Reports");
        header.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");
        
        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        
        Button generateButton = new Button("Generate Report");
        generateButton.setStyle("-fx-font-size: 16px; -fx-background-color: linear-gradient(to right, #42a5f5, #1e88e5);" +
                                " -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 8px 16px;");
        generateButton.setOnAction(e -> {
            String report = generateReport();
            reportArea.setText(report);
        });
        
        root.getChildren().addAll(header, generateButton, reportArea);
        return root;
    }
    
    @SuppressWarnings("unchecked")
	private String generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("Report Generated on: ").append(LocalDate.now().toString()).append("\n\n");
        int totalBorrowed = 0;
        int totalOverdue = 0;
        Map<String, Integer> bookBorrowCount = new HashMap<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("borrowedBooks.dat"))) {
            List<BorrowedBookRecord> records = (List<BorrowedBookRecord>) in.readObject();
            totalBorrowed = records.size();
            for (BorrowedBookRecord rec : records) {
                bookBorrowCount.put(rec.getBookTitle(), bookBorrowCount.getOrDefault(rec.getBookTitle(), 0) + 1);
                if ((!rec.isReturned() && LocalDate.now().isAfter(rec.getDueDate())) ||
                    (rec.isReturned() && rec.getReturnDate() != null && rec.getReturnDate().isAfter(rec.getDueDate()))) {
                    totalOverdue++;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        sb.append("Total Borrowed Books: ").append(totalBorrowed).append("\n");
        sb.append("Total Overdue Books: ").append(totalOverdue).append("\n\n");
        String mostBorrowed = "N/A";
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : bookBorrowCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostBorrowed = entry.getKey();
            }
        }
        sb.append("Most Borrowed Book: ").append(mostBorrowed)
          .append(" (").append(maxCount).append(" times)").append("\n");
        return sb.toString();
    }
}
