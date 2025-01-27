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

public class DepartmentScreen {

    private Stage primaryStage;

    public DepartmentScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void show() {
        // Root Layout
        BorderPane root = new BorderPane();

        // Header (Logo + Welcome text)
        HBox header = createHeader();
        root.setTop(header);

        // Content Area with modern design
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-padding: 30;");

        Label instruction = new Label("Select a department to manage products:");
        instruction.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        ComboBox<String> departmentDropdown = new ComboBox<>();
        departmentDropdown.setPrefWidth(250);
        departmentDropdown.setStyle("-fx-background-color: #f1f1f1; -fx-border-radius: 15; -fx-padding: 10;");
        departmentDropdown.setPromptText("--Choose a department--");

        // Fetch departments from the database and populate the ComboBox
        List<String> departments = fetchDepartmentsFromDatabase();
        if (!departments.isEmpty()) {
            departmentDropdown.getItems().addAll(departments);
        } else {
            departmentDropdown.setPromptText("No departments found");
        }

        Button viewProductsButton = new Button("View Products");
        viewProductsButton.setStyle("-fx-background-color: #4B2C20; -fx-text-fill: white; -fx-font-size: 16px;");
        viewProductsButton.setOnAction(e -> {
            String selectedDepartment = departmentDropdown.getValue();
            if (selectedDepartment != null) {
                showProductScreen(selectedDepartment);
            } else {
                showAlert("Error", "Please select a department.");
            }
        });

        content.getChildren().addAll(instruction, departmentDropdown, viewProductsButton);
        root.setCenter(content);

        // Back Button with hover effect
        Button backButton = new Button("Back to Home");
        backButton.setStyle("-fx-background-color: #D6B87E; -fx-text-fill: black; -fx-font-size: 16px;");
        backButton.setOnAction(e -> new Main().start(primaryStage)); // Go back to home
        backButton.setOnMouseEntered(event -> backButton.setStyle("-fx-background-color: #9E7A46; -fx-text-fill: black; -fx-font-size: 16px;"));
        backButton.setOnMouseExited(event -> backButton.setStyle("-fx-background-color: #D6B87E; -fx-text-fill: black; -fx-font-size: 16px;"));
        root.setBottom(backButton);
        BorderPane.setAlignment(backButton, Pos.CENTER);

        // Scene
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Departments");
    }

    private List<String> fetchDepartmentsFromDatabase() {
        List<String> departments = new ArrayList<>();
        String dbUrl = "jdbc:oracle:thin:@//199.212.26.208:1521/SQLD"; // Update your database URL
        String dbUser = "COMP214_F24_er_2"; // Replace with actual database username
        String dbPassword = "password"; // Replace with actual database password

        String query = "SELECT DEPTNAME FROM bb_department"; // Replace with your table and column names

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                 Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    departments.add(rs.getString("DEPTNAME"));
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching departments: " + e.getMessage());
            e.printStackTrace();
        }

        return departments;
    }

    private void showProductScreen(String department) {
        // Create new screen for displaying products for the selected department
        ProductScreen productScreen = new ProductScreen(primaryStage, department);
        productScreen.show();
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
