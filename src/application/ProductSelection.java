package application;

import javafx.beans.property.*;

public class ProductSelection {
    private StringProperty name = new SimpleStringProperty();
    private DoubleProperty price = new SimpleDoubleProperty();
    private BooleanProperty selected = new SimpleBooleanProperty(false);
    private IntegerProperty sizeCode = new SimpleIntegerProperty(0);
    private IntegerProperty formCode = new SimpleIntegerProperty(0);
    private IntegerProperty quantity = new SimpleIntegerProperty(1);
    private StringProperty saleStatus = new SimpleStringProperty("Great Deal!"); // New property for sale status

    public ProductSelection(String name, double price) {
        this.name.set(name);
        this.price.set(price);
    }

    // Getters and setters for all properties including saleStatus
    public String getSaleStatus() { return saleStatus.get(); }
    public void setSaleStatus(String saleStatus) { this.saleStatus.set(saleStatus); }
    public StringProperty saleStatusProperty() { return saleStatus; }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }

    public double getPrice() { return price.get(); }
    public void setPrice(double price) { this.price.set(price); }
    public DoubleProperty priceProperty() { return price; }

    public boolean isSelected() { return selected.get(); }
    public void setSelected(boolean selected) { this.selected.set(selected); }
    public BooleanProperty selectedProperty() { return selected; }

    public int getSizeCode() { return sizeCode.get(); }
    public void setSizeCode(int sizeCode) { this.sizeCode.set(sizeCode); }
    public IntegerProperty sizeCodeProperty() { return sizeCode; }

    public int getFormCode() { return formCode.get(); }
    public void setFormCode(int formCode) { this.formCode.set(formCode); }
    public IntegerProperty formCodeProperty() { return formCode; }

    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int quantity) { this.quantity.set(quantity); }
    public IntegerProperty quantityProperty() { return quantity; }

    public double getTotal() {
        return price.get() * quantity.get();  
    }

	public int getProductId() {
		// TODO Auto-generated method stub
		return 0;
	}
}