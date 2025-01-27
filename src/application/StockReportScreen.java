package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

public class StockReportScreen {

    private Stage primaryStage;
    private TextField basketIdField;
    private Button checkStockButton;
    private Label resultLabel;
    private Button backButton;

    public StockReportScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void show() {
        BorderPane root = new BorderPane();

        // Title
        Label title = new Label("Check Stock Availability");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        root.setTop(title);

        // Form for basket ID input
        VBox form = new VBox(10);
        form.setStyle("-fx-padding: 20;");

        Label basketIdLabel = new Label("Enter Basket ID:");
        basketIdField = new TextField();

        // Result label to display the stock status
        resultLabel = new Label();

        // Button to check stock
        checkStockButton = new Button("Check Stock");
        checkStockButton.setOnAction(e -> checkStockStatus());

        // Back button
        backButton = new Button("Back to Main Screen");
        backButton.setOnAction(e -> navigateBackToMainScreen());

        form.getChildren().addAll(basketIdLabel, basketIdField, checkStockButton, resultLabel, backButton);
        root.setCenter(form);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Stock Report");
        primaryStage.show();
    }

    // Method to check the stock status
    private void checkStockStatus() {
        String basketIdText = basketIdField.getText().trim();

        if (basketIdText.isEmpty()) {
            showAlert("Error", "Please enter a Basket ID.");
            return;
        }

        try {
            int basketId = Integer.parseInt(basketIdText);
            String result = checkBasketStock(basketId);
            resultLabel.setText(result);
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid Basket ID.");
        }
    }

    // Method to call the stored procedure and check stock
    private String checkBasketStock(int basketId) {
        String result = "";
        String sql = "{call CHECK_BASKET_IN_STOCK(?, ?)}"; // Call stored procedure

        try (Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//199.212.26.208:1521/SQLD", "COMP214_F24_er_2", "password");
             CallableStatement stmt = conn.prepareCall(sql)) {

            // Set input parameter
            stmt.setInt(1, basketId);

            // Register output parameter
            stmt.registerOutParameter(2, Types.VARCHAR);

            // Execute procedure
            stmt.executeUpdate();

            // Get the result
            result = stmt.getString(2); // Return result from procedure
        } catch (SQLException e) {
            showAlert("Error", "Error checking basket stock: " + e.getMessage());
            result = "Error checking stock!";
        }

        return result;
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