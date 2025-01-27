package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;

import javafx.geometry.Pos;

public class UpdateProductDescriptionScreen {

    private Stage primaryStage;
    private String productId;

    public UpdateProductDescriptionScreen(Stage primaryStage, String productId) {
        this.primaryStage = primaryStage;
        this.productId = productId;
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

        Label instruction = new Label("Update Product Description:");
        instruction.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Labels for Product ID and Description fields
        Label productLabel = new Label("Product ID: " + productId);
        productLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: normal;");

        Label newDescriptionLabel = new Label("New Description:");
        TextField newDescriptionField = new TextField();
        newDescriptionField.setStyle("-fx-background-color: #f1f1f1; -fx-border-radius: 15; -fx-padding: 10;");

        Button updateButton = new Button("Update Description");
        updateButton.setStyle("-fx-background-color: #4B2C20; -fx-text-fill: white; -fx-font-size: 16px;");

        // Button click handler to update the Product Description
        updateButton.setOnAction(e -> {
            String newDescription = newDescriptionField.getText();

            if (newDescription.isEmpty()) {
                showAlert("Error", "Please enter a new description.");
            } else {
                // Call the procedure to update the product description
                updateProductDescription(productId, newDescription);
            }
        });

        content.getChildren().addAll(instruction, productLabel, newDescriptionLabel, newDescriptionField, updateButton);
        root.setCenter(content);

        // Back Button
        Button backButton = new Button("Back to Products");
        backButton.setStyle("-fx-background-color: #D6B87E; -fx-text-fill: black; -fx-font-size: 16px;");
        backButton.setOnAction(e -> {
            // Go back to the department screen
            DepartmentScreen departmentScreen = new DepartmentScreen(primaryStage);
            departmentScreen.show();
        });
        root.setBottom(backButton);
        BorderPane.setAlignment(backButton, Pos.CENTER);

        // Show the screen
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void updateProductDescription(String productId, String newDescription) {
        // This should call the stored procedure to update the product description
        // You can add your JDBC code here to interact with the database

        String dbUrl = "jdbc:oracle:thin:@//199.212.26.208:1521/SQLD"; // Update your database URL
        String dbUser = "COMP214_F24_er_2"; // Replace with actual database username
        String dbPassword = "password"; // Replace with actual database password

        String query = "{ call update_product_description(?, ?) }";

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                 CallableStatement stmt = connection.prepareCall(query)) {

                stmt.setInt(1, Integer.parseInt(productId)); // Product ID
                stmt.setString(2, newDescription); // New Description

                stmt.execute(); // Execute the procedure
                showAlert("Success", "Product description updated successfully!");
            }
        } catch (Exception e) {
            System.err.println("Error updating product description: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to update product description.");
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
