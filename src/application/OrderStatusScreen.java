package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.text.ParseException;




import javafx.geometry.Pos;

public class OrderStatusScreen {

    private Stage primaryStage;

    public OrderStatusScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void show() {
        // Root Layout
        BorderPane root = new BorderPane();

        // Header (Logo + Welcome text)
        HBox header = createHeader();
        root.setTop(header);

        // Content area with smooth style
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-padding: 20;");

        Label instruction = new Label("Update Order Status (Shipping Information):");
        instruction.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Labels for Order information fields
        Label basketIdLabel = new Label("Basket ID:");
        TextField basketIdField = new TextField();
        basketIdField.setStyle("-fx-background-color: #f1f1f1; -fx-border-radius: 15; -fx-padding: 10;");

        Label dateShippedLabel = new Label("Date Shipped (e.g., 20-FEB-12):");
        TextField dateShippedField = new TextField();
        dateShippedField.setStyle("-fx-background-color: #f1f1f1; -fx-border-radius: 15; -fx-padding: 10;");

        Label shipperLabel = new Label("Shipper:");
        TextField shipperField = new TextField();
        shipperField.setStyle("-fx-background-color: #f1f1f1; -fx-border-radius: 15; -fx-padding: 10;");

        Label trackingNumberLabel = new Label("Tracking Number:");
        TextField trackingNumberField = new TextField();
        trackingNumberField.setStyle("-fx-background-color: #f1f1f1; -fx-border-radius: 15; -fx-padding: 10;");

        Button updateButton = new Button("Update Status");
        updateButton.setStyle("-fx-background-color: #4B2C20; -fx-text-fill: white; -fx-font-size: 16px;");

        // Button click handler to update the order status
        updateButton.setOnAction(e -> {
            String basketId = basketIdField.getText();
            String dateShipped = dateShippedField.getText();
            String shipper = shipperField.getText();
            String trackingNumber = trackingNumberField.getText();

            if (basketId.isEmpty() || dateShipped.isEmpty() || shipper.isEmpty() || trackingNumber.isEmpty()) {
                showAlert("Error", "Please fill all fields.");
            } else {
                // Call the procedure to update the order status
                updateOrderStatus(basketId, dateShipped, shipper, trackingNumber);
            }
        });

        content.getChildren().addAll(instruction, basketIdLabel, basketIdField, dateShippedLabel, dateShippedField,
                shipperLabel, shipperField, trackingNumberLabel, trackingNumberField, updateButton);
        root.setCenter(content);

        // Back Button
        Button backButton = new Button("Back to Orders");
        backButton.setStyle("-fx-background-color: #D6B87E; -fx-text-fill: black; -fx-font-size: 16px;");
        backButton.setOnAction(e -> {
            // Go back to the previous screen (orders screen or main menu)
            OrderStatusScreen ordersScreen = new OrderStatusScreen(primaryStage);
            ordersScreen.show();
        });
        root.setBottom(backButton);
        BorderPane.setAlignment(backButton, Pos.CENTER);

        // Show the screen
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void updateOrderStatus(String basketId, String dateShipped, String shipper, String trackingNumber) {
        // This should call the stored procedure to update the order status
        // You can add your JDBC code here to interact with the database

        String dbUrl = "jdbc:oracle:thin:@//199.212.26.208:1521/SQLD"; // Update your database URL
        String dbUser = "COMP214_F24_er_2"; // Replace with actual database username
        String dbPassword = "password"; // Replace with actual database password

       
        String query = "{ call STATUS_SHIP_SP(?, ?, ?, ?) }";

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                 CallableStatement stmt = connection.prepareCall(query)) {

                stmt.setInt(1, Integer.parseInt(basketId)); // Basket ID
                stmt.setString(2, dateShipped); // Date shipped
                stmt.setString(3, shipper); // Shipper
                stmt.setString(4, trackingNumber); // Tracking number

                stmt.execute(); // Execute the procedure
                showAlert("Success", "Order status updated successfully!");
            }
        } catch (Exception e) {
            System.err.println("Error updating order status: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to update order status.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private HBox createHeader() {
        HBox header = new HBox(30); 
        header.setAlignment(Pos.TOP_CENTER);      
        header.setStyle("-fx-background-color: #EDDCD6; -fx-border-color: #4B2C20; -fx-border-width: 3px; -fx-padding: 10px;"); 
        
        // Logo
        Image mainImage = new Image("file:src/resources/images/coffeeLogo.png"); 
        ImageView mainImageView = new ImageView(mainImage);
        mainImageView.setFitWidth(75);  
        mainImageView.setFitHeight(75); 

        // Welcome text
        Label welcomeText = new Label("Brewbean's Coffee Shop");
        welcomeText.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        welcomeText.setAlignment(Pos.CENTER);

        // Add logo and welcome text to the header
        header.getChildren().addAll(mainImageView, welcomeText);
        return header;
    }
}
