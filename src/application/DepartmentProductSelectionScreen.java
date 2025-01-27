package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.sql.*;

public class DepartmentProductSelectionScreen {

    private Stage primaryStage;
    private ObservableList<ProductSelection> availableProducts = FXCollections.observableArrayList();
    private Main mainApp;

    public DepartmentProductSelectionScreen(Stage primaryStage, Main mainApp) {
        this.primaryStage = primaryStage;
        this.mainApp = mainApp;
        fetchAvailableProducts(); // Dynamically fetch products from the database
    }

    public void show() {
        BorderPane root = new BorderPane();

        // Header
        Label header = new Label("Select Products from Department");
        header.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        root.setTop(header);
        BorderPane.setAlignment(header, Pos.CENTER);

        // Product List
        ListView<ProductSelection> productListView = new ListView<>();
        productListView.setItems(availableProducts);
        productListView.setCellFactory(param -> new ProductCell());

        // Add to Basket Button
        Button addToBasketButton = new Button("Add to Basket");
        addToBasketButton.setOnAction(e -> {
            boolean allValid = true;
            ObservableList<ProductSelection> selectedProducts = FXCollections.observableArrayList();
            for (ProductSelection product : productListView.getItems()) {
                if (product.isSelected()) {
                    if (product.getSizeCode() == 0 || product.getFormCode() == 0) {
                        allValid = false;
                        showAlert("Error", "Please select size and form for all selected products.");
                        break;
                    }
                    try {
                        // Dynamically add product to the basket using the procedure
                        addProductToBasket(product);
                        selectedProducts.add(product);
                    } catch (SQLException ex) {
                        showAlert("Error", "Failed to add product to basket: " + ex.getMessage());
                        return;
                    }
                }
            }

            if (allValid) {
                mainApp.updateProductTable(selectedProducts);
                mainApp.start(primaryStage); // Return to main screen
            }
        });

        VBox layout = new VBox(20, productListView, addToBasketButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20;");
        root.setCenter(layout);

        // Scene
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Department Product Selection");
        primaryStage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Fetch available products from the database dynamically
    private void fetchAvailableProducts() {
    	String dbUrl = "jdbc:oracle:thin:@//199.212.26.208:1521/SQLD"; // Update with your database URL
        String dbUser = "COMP214_F24_er_2"; // Replace with your database username
        String dbPassword = "password"; // Replace with your database password

        String query = "SELECT idProduct, productName, price FROM bb_product";
        String saleStatusQuery = "{? = call CK_SALE_SF(SYSDATE, ?)}"; // Call CK_SALE_SF function
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query);
            CallableStatement cstmt = conn.prepareCall(saleStatusQuery)) {

        	while (rs.next()) {
                int id = rs.getInt("idProduct");
                String name = rs.getString("productName");
                double price = rs.getDouble("price");

                ProductSelection product = new ProductSelection(name, price);

                // Call CK_SALE_SF to get the sale status
                cstmt.registerOutParameter(1, Types.VARCHAR);
                cstmt.setInt(2, id);
                cstmt.execute();

                String saleStatus = cstmt.getString(1);
                product.setSaleStatus(saleStatus);

                availableProducts.add(product);
            }

        } catch (SQLException e) {
            showAlert("Error", "Failed to fetch products: " + e.getMessage());
        }
    }

    // Dynamically call the stored procedure to add product to the basket
    private void addProductToBasket(ProductSelection product) throws SQLException {
        String dbUrl = "jdbc:oracle:thin:@//199.212.26.208:1521/SQLD"; // Update with your database URL
        String dbUser = "COMP214_F24_er_2"; // Replace with your database username
        String dbPassword = "password"; // Replace with your database password

        String procedureCall = "{call BASKET_ADD_SP(?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             CallableStatement stmt = conn.prepareCall(procedureCall)) {

            // Set input parameters
            stmt.setInt(1, product.getProductId()); // Product ID
            stmt.setInt(2, getCurrentBasketId());  // Dynamically fetch Basket ID
            stmt.setDouble(3, product.getPrice()); // Product Price
            stmt.setInt(4, product.getQuantity()); // Quantity
            stmt.setInt(5, product.getSizeCode()); // Size Code
            stmt.setInt(6, product.getFormCode()); // Form Code

            // Execute the procedure
            stmt.executeUpdate();
        }
    }

    // Dynamically fetch the current basket ID
    private int getCurrentBasketId() throws SQLException {
        String dbUrl = "jdbc:oracle:thin:@//199.212.26.208:1521/SQLD";// Update with your database URL
        String dbUser = "COMP214_F24_er_2"; // Replace with your database username
        String dbPassword = "password"; // Replace with your database password

        String query = "SELECT idBasket FROM bb_basket WHERE ROWNUM = 1 ORDER BY idBasket DESC"; // Example query

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt("idBasket");
            } else {
                throw new SQLException("No basket ID found.");
            }
        }
    }

    private static class ProductCell extends ListCell<ProductSelection> {
        @Override
        protected void updateItem(ProductSelection product, boolean empty) {
            super.updateItem(product, empty);

            if (empty || product == null) {
                setGraphic(null);
            } else {
                CheckBox selectBox = new CheckBox();
                selectBox.selectedProperty().bindBidirectional(product.selectedProperty());

                Label nameLabel = new Label(product.getName() + " - $" + product.getPrice());
                nameLabel.setStyle("-fx-font-size: 14px;");

                ToggleGroup sizeGroup = new ToggleGroup();
                RadioButton size1 = new RadioButton("Size 1");
                RadioButton size2 = new RadioButton("Size 2");
                size1.setToggleGroup(sizeGroup);
                size2.setToggleGroup(sizeGroup);
                sizeGroup.selectedToggleProperty().addListener((obs, old, newValue) -> {
                    if (newValue == size1) product.setSizeCode(1);
                    if (newValue == size2) product.setSizeCode(2);
                });

                ToggleGroup formGroup = new ToggleGroup();
                RadioButton form3 = new RadioButton("Form 3");
                RadioButton form4 = new RadioButton("Form 4");
                form3.setToggleGroup(formGroup);
                form4.setToggleGroup(formGroup);
                formGroup.selectedToggleProperty().addListener((obs, old, newValue) -> {
                    if (newValue == form3) product.setFormCode(3);
                    if (newValue == form4) product.setFormCode(4);
                });

                Spinner<Integer> quantitySpinner = new Spinner<>(1, 10, 1);
                quantitySpinner.valueProperty().addListener((obs, old, newValue) -> product.setQuantity(newValue));

                VBox sizeBox = new VBox(new Label("Size:"), size1, size2);
                VBox formBox = new VBox(new Label("Form:"), form3, form4);

                HBox layout = new HBox(10, selectBox, nameLabel, sizeBox, formBox, new Label("Quantity:"), quantitySpinner);
                layout.setAlignment(Pos.CENTER_LEFT);
                setGraphic(layout);
            }
        }
    }
}