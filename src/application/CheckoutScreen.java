package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

public class CheckoutScreen {

    private Stage primaryStage;
    private Label subtotalLabel;
    private Label taxLabel;
    private Label totalLabel;
    private ComboBox<String> stateComboBox;
    private Button calculateButton;
    private Button confirmButton;
    private Button backButton;
    private double subtotal;

    public CheckoutScreen(Stage primaryStage, double subtotal) {
        this.primaryStage = primaryStage;
        this.subtotal = subtotal;
    }

    public void show() {
        BorderPane root = new BorderPane();

        // Title
        Label title = new Label("Checkout - Order Summary");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        root.setTop(title);

        // Form for state selection and subtotal
        VBox form = new VBox(10);
        form.setStyle("-fx-padding: 20;");

        Label stateLabel = new Label("Select your State:");
        stateComboBox = new ComboBox<>();
        stateComboBox.getItems().addAll("Select State", "VA", "NC", "SC");
        stateComboBox.setValue("Select State");

        // Labels for subtotal, tax, and total
        subtotalLabel = new Label("Subtotal: $" + String.format("%.2f", subtotal));
        taxLabel = new Label("Tax: $0.00");
        totalLabel = new Label("Total: $0.00");

        // Calculate Total Button
        calculateButton = new Button("Calculate Total");
        calculateButton.setOnAction(e -> calculateTaxAndTotal());

        // Confirm Order Button
        confirmButton = new Button("Confirm Order");
        confirmButton.setOnAction(e -> placeOrder());

        // Back Button
        backButton = new Button("Back");
        backButton.setOnAction(e -> goBackToMainScreen());

        // Add components to the form
        form.getChildren().addAll(subtotalLabel, stateLabel, stateComboBox, calculateButton, taxLabel, totalLabel, confirmButton, backButton);

        root.setCenter(form);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Order Confirmation");
        primaryStage.show();
    }

    // Method to calculate tax and total when the user selects the state and clicks Calculate Total
    private void calculateTaxAndTotal() {
        String state = stateComboBox.getValue();

        if (state.equals("Select State")) {
            showAlert("Error", "Please select a state.");
            return;
        }

        // Call the TAX_COST_SP procedure with state and subtotal
        try {
            double tax = getTaxFromDatabase(state, subtotal);
            double total = subtotal + tax;

            // Update tax and total labels
            taxLabel.setText("Tax: $" + String.format("%.2f", tax));
            totalLabel.setText("Total: $" + String.format("%.2f", total));
        } catch (Exception e) {
            showAlert("Error", "Failed to calculate tax: " + e.getMessage());
        }
    }

    // Method to call the SP_TAX_CALCULATE procedure and calculate tax
    private double getTaxFromDatabase(String state, double subtotal) throws SQLException {
        double tax = 0.0;

        // JDBC connection
        String dbUrl = "jdbc:oracle:thin:@//199.212.26.208:1521/SQLD"; // Update with your database URL
        String dbUser = "COMP214_F24_er_2"; // Replace with your database username
        String dbPassword = "password"; // Replace with your database password

        String procedureCall = "{call SP_TAX_CACLCULATE(?, ?, ?)}";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             CallableStatement stmt = conn.prepareCall(procedureCall)) {

            // Set input parameters
            stmt.setString(1, state);
            stmt.setDouble(2, subtotal);

            // Register output parameter
            stmt.registerOutParameter(3, Types.NUMERIC);

            // Execute the procedure
            stmt.execute();

            // Retrieve the tax amount
            tax = stmt.getDouble(3);
        }

        return tax;
    }

    // Method to place the order
    private void placeOrder() {
        // Simulate placing the order (you can replace this with actual order submission logic)
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order Confirmation");
        alert.setHeaderText("Order Confirmed");
        alert.setContentText("Your order has been placed successfully!\n" +
                "Subtotal: $" + String.format("%.2f", subtotal) + "\n" +
                taxLabel.getText() + "\n" +
                totalLabel.getText());
        alert.showAndWait();

        // Navigate back to the main screen after placing the order
        Main mainScreen = new Main();
        mainScreen.start(primaryStage); // Reinitialize and display the main screen
    }

    // Method to navigate back to the main screen
    private void goBackToMainScreen() {
        Main mainScreen = new Main();
        mainScreen.start(primaryStage); // Navigate back to the main screen
    }

    // Method to show alert messages
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}