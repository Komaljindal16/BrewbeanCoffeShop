package application;

import javafx.beans.property.*;

public class SpendingReport {
    private final IntegerProperty shopperId;
    private final DoubleProperty totalSpending;

    public SpendingReport(int shopperId, double totalSpending) {
        this.shopperId = new SimpleIntegerProperty(shopperId);
        this.totalSpending = new SimpleDoubleProperty(totalSpending);
    }

    public int getShopperId() {
        return shopperId.get();
    }

    public IntegerProperty shopperIdProperty() {
        return shopperId;
    }

    public double getTotalSpending() {
        return totalSpending.get();
    }

    public DoubleProperty totalSpendingProperty() {
        return totalSpending;
    }
}
