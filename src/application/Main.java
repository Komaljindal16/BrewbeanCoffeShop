package application;


/*
 * Creators: Komal (3012421306), Utsav Mistry (301476898)
 * Project: Brewbeans Coffee Shop using Oracle for my sql and Javafx for GUI
 * Group 8
 */
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;

public class Main extends Application {

    private Stage primaryStage;
    private ObservableList<ProductSelection> productTableData = FXCollections.observableArrayList();
    private TableView<ProductSelection> productTable;
    private Label subtotalLabel;  // Label reference to show subtotal
    private TextField basketIdField; // To accept basket ID for checking stock status
    private Label stockStatusLabel; // To display the stock check result

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Root Layout
        BorderPane root = new BorderPane();

        // Header (Logo + Welcome text)
        HBox header = createHeader();
        root.setTop(header);

        // Sidebar Navigation
        VBox sidebar = createSidebar();
        root.setLeft(sidebar);

        // Main Content
        VBox contentArea = createContentArea();
        root.setCenter(contentArea);

        // Scene
        Scene scene = new Scene(root);
        primaryStage.setTitle("Brewbean's Coffee Shop");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Header with logo and welcome text
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

    //Sidebar
    private VBox createSidebar() {
        VBox sidebar = new VBox(20);
        sidebar.setStyle("-fx-padding: 10; ");
        sidebar.setAlignment(Pos.CENTER_LEFT);

        // Create buttons with images
        Button departments = createButtonWithImage("Departments","file:src/resources/images/department.png");
        departments.setOnAction(e -> showDepartmentScreen());//department screen
        

        Button basket = createButtonWithImage("Basket", "file:src/resources/images/basket.png");
        basket.setOnAction(e -> showStockReportScreen()); // Navigate to stock report screen


        Button checkout = createButtonWithImage("Check Out", "file:src/resources/images/checkout.png");
        checkout.setOnAction(e -> showCheckoutScreen());
        
        Button search = createButtonWithImage("Search", "file:src/resources/images/search.png");
        search.setOnAction(e -> openSaleStatusReportScreen(primaryStage)); // Navigate to Sale Status Report Screen

        Button account = createButtonWithImage("Account", "file:src/resources/images/account.png");
        account.setOnAction(e -> showTotalSpendingReportScreen()); // Navigate to Total Spending Report Screen
        
        Button orderStatus = createButtonWithImage("Order Status", "file:src/resources/images/orderStatus.png");
        orderStatus.setOnAction(e -> showOrderStatusScreen()); // Show Order Status Screen
        
        // Link to select products
        Hyperlink selectProductsLink = new Hyperlink("Select Products");
        selectProductsLink.setOnAction(e -> showDepartmentProductSelectionScreen());  // Open the product selection screen

        // Add buttons to the sidebar
        sidebar.getChildren().addAll(departments, basket, checkout, search, account, orderStatus, selectProductsLink);
        return sidebar;
    }


    

	// Helper method to create a button with an image
    private Button createButtonWithImage(String text, String imagePath) {
        Image image = new Image(imagePath);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(20);
        imageView.setFitHeight(20);

        Button button = new Button(text, imageView);
        button.setStyle("-fx-font-size: 14px; -fx-border-color: #4B2C20; -fx-border-width: 1px;");
        return button;
    }

    // Main Content Area
    private VBox createContentArea() {
        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");

        Label title = new Label("Products");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Initialize the subtotal label and reference it
        subtotalLabel = new Label("Subtotal: $0.00");
        subtotalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        productTable = createProductTable();
        HBox actions = createActions();

        content.getChildren().addAll(title, productTable, subtotalLabel, actions);
        return content;
    }


 // Table for displaying products
    private TableView<ProductSelection> createProductTable() {
        TableView<ProductSelection> table = new TableView<>();
        table.setItems(productTableData);

        TableColumn<ProductSelection, String> colItemCode = new TableColumn<>("Item Code");
        colItemCode.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        TableColumn<ProductSelection, Double> colPrice = new TableColumn<>("Price");
        colPrice.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());  // Correct way to reference priceProperty()

        TableColumn<ProductSelection, Integer> colQty = new TableColumn<>("Qty");
        colQty.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());  // Correct way to reference quantityProperty()

        TableColumn<ProductSelection, Integer> colSize = new TableColumn<>("Size Code");
        colSize.setCellValueFactory(cellData -> cellData.getValue().sizeCodeProperty().asObject());  // Correct way to reference sizeCodeProperty()

        TableColumn<ProductSelection, Integer> colForm = new TableColumn<>("Form Code");
        colForm.setCellValueFactory(cellData -> cellData.getValue().formCodeProperty().asObject());  // Correct way to reference formCodeProperty()

        TableColumn<ProductSelection, Double> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTotal()));

     // Delete Button Column
        TableColumn<ProductSelection, Void> colDelete = new TableColumn<>("Action");
        colDelete.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(e -> {
                    ProductSelection product = getTableView().getItems().get(getIndex());
                    productTableData.remove(product); // Remove the product from the ObservableList
                    updateSubtotal(); // Recalculate and update the subtotal
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        table.getColumns().addAll(colItemCode, colPrice, colQty, colSize, colForm, colTotal, colDelete);
        return table;
    }


    // Actions at the bottom
    private HBox createActions() {
        HBox actions = new HBox(10);
        Button checkOut = new Button("Check Out");
        checkOut.setOnAction(e -> showCheckoutScreen());
        actions.getChildren().addAll(checkOut);
        return actions;
    }

    
    // Method to show the department screen
    private void showDepartmentScreen() {
        DepartmentScreen departmentScreen = new DepartmentScreen(primaryStage);
        departmentScreen.show();
    }

    // Method to show the stock report screen
    private void showStockReportScreen() {
        // Create a new screen to show stock status
        StockReportScreen stockReportScreen = new StockReportScreen(primaryStage);
        stockReportScreen.show();
    }
    
    private void showDepartmentProductSelectionScreen() {
        DepartmentProductSelectionScreen departmentProductSelectionScreen = new DepartmentProductSelectionScreen(primaryStage, this);
        departmentProductSelectionScreen.show();
    }



    // Update the product table with selected items
    public void updateProductTable(ObservableList<ProductSelection> products) {
       // productTableData.clear();
        productTableData.addAll(products);

        // Calculate the subtotal dynamically based on the items in the product table
        double subtotal = 0;
        for (ProductSelection product : products) {
            subtotal += product.getTotal();  // Add the total for each product
        }

        // Update the subtotal label
        subtotalLabel.setText("Subtotal: $" + String.format("%.2f", subtotal));
    }

    
    private void showCheckoutScreen() {
        // Show checkout screen with subtotal passed to it
        CheckoutScreen checkoutScreen = new CheckoutScreen(primaryStage, getSubtotal());
        checkoutScreen.show();
    }
    
    
    // Method to get the subtotal
    private double getSubtotal() {
        double subtotal = 0.0;
        for (ProductSelection product : productTableData) {
            subtotal += product.getTotal();  // Assuming total is price * quantity
        }
        return subtotal;
    }
    
    
    private void updateSubtotal() {
        double subtotal = productTableData.stream()
            .mapToDouble(ProductSelection::getTotal)
            .sum();

        subtotalLabel.setText("Subtotal: $" + String.format("%.2f", subtotal));
    }
    // Method to show the order status screen
    private void showOrderStatusScreen() {
        OrderStatusScreen orderStatusScreen = new OrderStatusScreen(primaryStage);
        orderStatusScreen.show();
    }
    // Method to handle order status update
    private void updateOrderStatus(int orderId, String trackingNumber, String shipper) {
        // Simulate the database update for order status
        // This is where you would call your stored procedure STATUS_SHIP_SP.
        // Assuming you are using JDBC for database interaction, you would perform an insert like this:

        String query = "BEGIN STATUS_SHIP_SP(:order_id, :tracking_number, :shipper); END;";
        // You would use a JDBC CallableStatement to execute this query with the provided parameters.
        
        // Simulate success for this example
        System.out.println("Order Status Updated: Order " + orderId + " has been shipped.");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order Status Updated");
        alert.setHeaderText("Order " + orderId + " has been shipped!");
        alert.setContentText("Tracking Number: " + trackingNumber + "\nShipper: " + shipper);
        alert.showAndWait();
    }
    private void showTotalSpendingReportScreen() {
        TotalSpendingReportScreen reportScreen = new TotalSpendingReportScreen(primaryStage);
        reportScreen.show();
    }

    
    // Method to open the SaleStatusReportScreen
    private void openSaleStatusReportScreen(Stage primaryStage) {
        SaleStatusReportScreen saleStatusReportScreen = new SaleStatusReportScreen(primaryStage, this);
        saleStatusReportScreen.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
