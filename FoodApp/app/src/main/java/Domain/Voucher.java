package Domain;

import java.io.Serializable;

public class Voucher implements Serializable {
    private String id;
    private String description;
    private int discountPercentage;
    private double maxDiscount;
    private double minOrderValue;
    private String expiryDate;

    public Voucher() {
    }

    public Voucher(String id, String description, int discountPercentage, double maxDiscount, double minOrderValue, String expiryDate) {
        this.id = id;
        this.description = description;
        this.discountPercentage = discountPercentage;
        this.maxDiscount = maxDiscount;
        this.minOrderValue = minOrderValue;
        this.expiryDate = expiryDate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(int discountPercentage) { this.discountPercentage = discountPercentage; }

    public double getMaxDiscount() { return maxDiscount; }
    public void setMaxDiscount(double maxDiscount) { this.maxDiscount = maxDiscount; }

    public double getMinOrderValue() { return minOrderValue; }
    public void setMinOrderValue(double minOrderValue) { this.minOrderValue = minOrderValue; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
}