package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;

public class AddNewProductScreen {

    private Stage primaryStage;
    private Integer deptid;

    public AddNewProductScreen(Stage primaryStage, Integer deptid) {
        this.primaryStage = primaryStage;
        this.deptid = deptid;
    }

    public void show() {
        // Root Layout
        BorderPane root = new BorderPane();

        // Header (Logo + Welcome text)
        HBox header = createHeader();
        root.setTop(header);

        // Content area
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-padding: 20;");

        Label instruction = new Label("Enter New Product Details:");
        instruction.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Labels and Text Fields for product details
        TextField nameField = createStyledTextField("Product Name:");
        TextField descriptionField = createStyledTextField("Description:");
        TextField imageField = createStyledTextField("Image Filename:");
        TextField priceField = createStyledTextField("Price:");
        TextField activeField = createStyledTextField("Active Status (1=Active, 0=Inactive):");

        Button addProductButton = new Button("Add Product");
        addProductButton.setStyle("-fx-background-color: #4B2C20; -fx-text-fill: white; -fx-font-size: 16px;");

        // Add Product Button Action
        addProductButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();
            String imageFilename = imageField.getText().trim();
            String priceStr = priceField.getText().trim();
            String activeStr = activeField.getText().trim();

            if (name.isEmpty() || description.isEmpty() || imageFilename.isEmpty() || priceStr.isEmpty() || activeStr.isEmpty()) {
                showAlert("Error", "All fields must be filled!");
                return;
            }

            try {
                double price = Double.parseDouble(priceStr);
                int active = Integer.parseInt(activeStr);

                // Add the product to the database
                addProductToDatabase(name, description, imageFilename, price, deptid, active);
                showAlert("Success", "Product added successfully!");

                // Clear the fields after adding
                nameField.clear();
                descriptionField.clear();
                imageField.clear();
                priceField.clear();
                activeField.clear();

            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter valid numeric values for Price and Active Status.");
            }
        });

        // Add components to the form
        content.getChildren().addAll(
                instruction,
                nameField,
                descriptionField,
                imageField,
                priceField,
                activeField,
                addProductButton
        );

        root.setCenter(content);

        // Back Button
        Button backButton = new Button("Back to Products");
        backButton.setStyle("-fx-background-color: #D6B87E; -fx-text-fill: black; -fx-font-size: 16px;");
        backButton.setOnAction(e -> {
            // Navigate back to the department screen
            DepartmentScreen departmentScreen = new DepartmentScreen(primaryStage);
            departmentScreen.show();
        });
        root.setBottom(backButton);
        BorderPane.setAlignment(backButton, Pos.CENTER);

        // Show the screen
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private TextField createStyledTextField(String prompt) {
        TextField textField = new TextField();
        textField.setPromptText(prompt);
        textField.setStyle("-fx-background-color: #f1f1f1; -fx-border-radius: 15; -fx-padding: 10;");
        return textField;
    }

    private void addProductToDatabase(String name, String description, String imageFilename, double price, int deptid, int active) {
        String dbUrl = "jdbc:oracle:thin:@//199.212.26.208:1521/SQLD"; // Update your database URL
        String dbUser = "COMP214_F24_er_2"; // Replace with actual database username
        String dbPassword = "password"; // Replace with actual database password

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             CallableStatement stmt = conn.prepareCall("{CALL SP_ADD_PRODUCT(?, ?, ?, ?, ?, ?)}")) {

            // Set the IN parameters
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setString(3, imageFilename);
            stmt.setDouble(4, price);
            stmt.setInt(5, deptid);
            stmt.setInt(6, active);

            // Execute the procedure
            stmt.execute();
            System.out.println("Product added to the database successfully.");

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Error", "Failed to add product: " + ex.getMessage());
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
