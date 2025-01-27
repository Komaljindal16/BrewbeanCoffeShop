package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductScreen {

    private Stage primaryStage;
    private String department;
    private Integer deptid;

    public ProductScreen(Stage primaryStage, String department) {
        this.primaryStage = primaryStage;
        this.department = department;
    }

    public void show() {
        // Root Layout
        BorderPane root = new BorderPane();

        // Header (Logo + Welcome text)
        HBox header = createHeader();
        root.setTop(header);

        // Content area with modern design
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-padding: 30;");

        Label instruction = new Label("Manage Products for " + department);
        instruction.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Display the list of products for the selected department
        ListView<String> productListView = new ListView<>();
        List<String> products = fetchProductsFromDatabase(department);
        if (!products.isEmpty()) {
            productListView.getItems().addAll(products);
        } else {
            productListView.getItems().add("No products found in this department.");
        }

        // Product actions
        Button updateDescriptionButton = new Button("Update Product Description");
        updateDescriptionButton.setStyle("-fx-background-color: #4B2C20; -fx-text-fill: white; -fx-font-size: 16px;");
        updateDescriptionButton.setOnAction(e -> {
            String selectedProduct = productListView.getSelectionModel().getSelectedItem();
            if (selectedProduct != null && !selectedProduct.equals("No products found in this department.")) {
                // Extract product ID from the selection (e.g., "101 - Product 1")
                String productId = selectedProduct.split(" - ")[0];
                showUpdateProductDescriptionScreen(productId); // Show description update screen
            } else {
                showAlert("Error", "Please select a product to update.");
            }
        });

        Button addNewProductButton = new Button("Add New Product");
        addNewProductButton.setStyle("-fx-background-color: #4B2C20; -fx-text-fill: white; -fx-font-size: 16px;");
        addNewProductButton.setOnAction(e -> showAddNewProductScreen()); // Show the add product screen

        content.getChildren().addAll(instruction, productListView, updateDescriptionButton, addNewProductButton);
        root.setCenter(content);

        // Scene
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Manage Products");
        primaryStage.show();
    }
    
    private int fetchDepartmentId(String departmentName) {
        int departmentId = -1; // Default value if department is not found
        String dbUrl = "jdbc:oracle:thin:@//199.212.26.208:1521/SQLD"; // Update your database URL
        String dbUser = "COMP214_F24_er_2"; // Replace with actual database username
        String dbPassword = "password"; // Replace with actual database password

        String query = "SELECT IDDEPARTMENT FROM bb_department WHERE deptname = '" + departmentName + "'";

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                 Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                if (rs.next()) {
                    departmentId = rs.getInt("IDDEPARTMENT");
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching department ID: " + e.getMessage());
            e.printStackTrace();
        }

        deptid=departmentId;
        return departmentId;
    }


    private List<String> fetchProductsFromDatabase(String departmentName) {
        List<String> products = new ArrayList<>();
        int departmentId = fetchDepartmentId(departmentName); // Fetch the department ID

        if (departmentId == -1) {
            System.err.println("No department ID found for department: " + departmentName);
            return products; // Return empty list if department ID is invalid
        }

        String dbUrl = "jdbc:oracle:thin:@//199.212.26.208:1521/SQLD"; // Update your database URL
        String dbUser = "COMP214_F24_er_2"; // Replace with actual database username
        String dbPassword = "password"; // Replace with actual database password

        String query = "SELECT idproduct, productname FROM bb_product WHERE IDDEPARTMENT = " + departmentId;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                 Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    String productId = rs.getString("idproduct");
                    String productName = rs.getString("productname");
                    products.add(productId + " - " + productName);
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching products: " + e.getMessage());
            e.printStackTrace();
        }

        return products;
    }


    private void showUpdateProductDescriptionScreen(String productId) {
        // Create a new screen for updating the product description
        UpdateProductDescriptionScreen updateScreen = new UpdateProductDescriptionScreen(primaryStage, productId);
        updateScreen.show();
    }

    private void showAddNewProductScreen() {
        // Create a new screen for adding a new product
        AddNewProductScreen addProductScreen = new AddNewProductScreen(primaryStage, deptid);
        addProductScreen.show();
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
