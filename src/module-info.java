module BrewbeanCoffeShop {
	requires javafx.controls;
	requires java.desktop;
	requires javafx.graphics;
	requires javafx.base;
	requires java.sql;
	exports application;
	opens application to javafx.graphics, javafx.fxml;
}
