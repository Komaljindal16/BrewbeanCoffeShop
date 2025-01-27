package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

public class TotalSpendingReportScreen {

    private Stage primaryStage;
    private TextField shopperIdField;
    private TableView<SpendingReport> spendingTable;

    public TotalSpendingReportScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void show() {
        BorderPane root = new BorderPane();

        // Title
        Label title = new Label("Shopper Total Spending Report");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        root.setTop(title);

        // Form and Table Layout
        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");

        // Input for Shopper ID
        Label shopperIdLabel = new Label("Enter Shopper ID (Leave empty to show all):");
        shopperIdField = new TextField();

        // Button to list spending
        Button listButton = new Button("List Spending");
        listButton.setOnAction(e -> fetchSpendingReport());

        // Back button
        Button backButton = new Button("Back to Main Screen");
        backButton.setOnAction(e -> navigateBackToMainScreen());

        // Table for displaying results
        spendingTable = createSpendingTable();

        content.getChildren().addAll(shopperIdLabel, shopperIdField, listButton, spendingTable, backButton);
        root.setCenter(content);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Shopper Total Spending");
        primaryStage.show();
    }

    // Method to create the table for displaying spending
    private TableView<SpendingReport> createSpendingTable() {
        TableView<SpendingReport> table = new TableView<>();

        TableColumn<SpendingReport, Integer> colShopperId = new TableColumn<>("Shopper ID");
        colShopperId.setCellValueFactory(cellData -> cellData.getValue().shopperIdProperty().asObject());

        TableColumn<SpendingReport, Double> colTotalSpending = new TableColumn<>("Total Spending");
        colTotalSpending.setCellValueFactory(cellData -> cellData.getValue().totalSpendingProperty().asObject());

        table.getColumns().addAll(colShopperId, colTotalSpending);
        return table;
    }

    // Method to fetch spending data from the database
    private void fetchSpendingReport() {
        String shopperIdInput = shopperIdField.getText().trim();

        ObservableList<SpendingReport> reportData = FXCollections.observableArrayList();
        Connection conn = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection("jdbc:oracle:thin:@//199.212.26.208:1521/SQLD", "COMP214_F24_er_2", "password");

            if (shopperIdInput.isEmpty()) {
                // Query for all shoppers
                String query = "SELECT idshopper, TOT_PURCH_SF(idshopper) AS total_spending FROM bb_shopper";
                stmt = conn.prepareCall(query);
            } else {
                // Query for specific shopper
                String query = "SELECT idshopper, TOT_PURCH_SF(?) AS total_spending FROM bb_shopper WHERE idshopper = ?";
                stmt = conn.prepareCall(query);
                int shopperId = Integer.parseInt(shopperIdInput);
                stmt.setInt(1, shopperId);
                stmt.setInt(2, shopperId);
            }

            rs = stmt.executeQuery();

            while (rs.next()) {
                int shopperId = rs.getInt("idshopper");
                double totalSpending = rs.getDouble("total_spending");
                reportData.add(new SpendingReport(shopperId, totalSpending));
            }

            spendingTable.setItems(reportData);

        } catch (SQLException e) {
            showAlert("Database Error", e.getMessage());
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Invalid Shopper ID.");
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ignored) {
            }
        }
    }

    // Navigate back to the main screen
    private void navigateBackToMainScreen() {
        Main mainScreen = new Main();
        mainScreen.start(primaryStage); // Reinitialize and display the main screen
    }

    // Method to show an alert
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
