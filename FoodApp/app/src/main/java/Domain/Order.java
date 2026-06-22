package Domain;

import java.util.List;

public class Order {
    // Đổi toàn bộ tên biến về dạng camelCase chuẩn mã hóa JSON của Firebase
    private String id;
    private String userId;
    private String createdAt;
    private String status;
    private List<CartItem> listItems;
    private String voucherId;
    private String paymentMethod;
    private double orderValue;

    // Constructor trống bắt buộc phải có
    public Order() {
    }

    // Constructor có tham số (Cập nhật theo tên biến mới)
    public Order(String id, String userId, String createdAt, List<CartItem> listItems, String voucherId, String paymentMethod, double orderValue) {
        this.id = id;
        this.userId = userId;
        this.createdAt = createdAt;
        this.listItems = listItems;
        this.status = "Delivering";
        this.voucherId = voucherId;
        this.paymentMethod = paymentMethod;
        this.orderValue = orderValue;
    }

    // --- TOÀN BỘ GETTER / SETTER ĐÃ ĐƯỢC CHUẨN HÓA KHỚP 100% VỚI FIREBASE ---
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CartItem> getListItems() {
        return listItems;
    }

    public void setListItems(List<CartItem> listItems) {
        this.listItems = listItems;
    }

    public String getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(String voucherId) {
        this.voucherId = voucherId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getOrderValue() {
        return orderValue;
    }

    public void setOrderValue(double orderValue) {
        this.orderValue = orderValue;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}