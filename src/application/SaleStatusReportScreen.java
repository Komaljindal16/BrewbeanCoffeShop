package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;

public class SaleStatusReportScreen {

    private Stage primaryStage;
    private TextField productIdField;
    private DatePicker datePicker;
    private Label resultLabel;
    private Main mainScreen;  // Reference to Main class

    public SaleStatusReportScreen(Stage primaryStage, Main mainScreen) {
        this.primaryStage = primaryStage;
        this.mainScreen = mainScreen;
    }

    public void show() {
        BorderPane root = new BorderPane();

        // Title
        Label title = new Label("Sale Status Report");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        root.setTop(title);

        // Form
        VBox form = new VBox(10);
        form.setStyle("-fx-padding: 20;");

        Label productIdLabel = new Label("Enter Product ID:");
        productIdField = new TextField();

        Label dateLabel = new Label("Select Date:");
        datePicker = new DatePicker();

        Button checkButton = new Button("Check Sale Status");
        checkButton.setOnAction(e -> checkSaleStatus());

        resultLabel = new Label("Result will be displayed here.");
        resultLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Back button
        Button backButton = new Button("Back to Main Screen");
        backButton.setOnAction(e -> navigateBackToMainScreen());

        form.getChildren().addAll(productIdLabel, productIdField, dateLabel, datePicker, checkButton, resultLabel, backButton);

        root.setCenter(form);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sale Status Report");
        primaryStage.show();
    }

    // Method to check sale status from the database
    private void checkSaleStatus() {
        String productIdInput = productIdField.getText().trim();
        if (productIdInput.isEmpty() || datePicker.getValue() == null) {
            resultLabel.setText("Please enter Product ID and select a Date.");
            return;
        }

        int productId;
        try {
            productId = Integer.parseInt(productIdInput);
        } catch (NumberFormatException e) {
            resultLabel.setText("Invalid Product ID. Please enter a number.");
            return;
        }

        // Get selected date
        java.sql.Date selectedDate = java.sql.Date.valueOf(datePicker.getValue());

        // Call the CK_SALE_SF function to check sale status
        String saleStatus = getSaleStatusFromDatabase(selectedDate, productId);
        resultLabel.setText(saleStatus);
    }

    // Method to call the CK_SALE_SF function in the database
    private String getSaleStatusFromDatabase(java.sql.Date selectedDate, int productId) {
        String saleStatus = "";
        try (Connection connection = DriverManager.getConnection(
        		"jdbc:oracle:thin:@//199.212.26.208:1521/SQLD", "COMP214_F24_er_2", "password")) {
            
            CallableStatement statement = connection.prepareCall("{? = call CK_SALE_SF(?, ?)}");
            statement.registerOutParameter(1, Types.VARCHAR);
            statement.setDate(2, selectedDate);
            statement.setInt(3, productId);
            statement.execute();

            saleStatus = statement.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
            saleStatus = "Error while fetching sale status.";
        }
        return saleStatus;
    }

    // Navigate back to the main screen
    private void navigateBackToMainScreen() {
        mainScreen.start(primaryStage);  // Show the main screen using the existing instance
    }
}
