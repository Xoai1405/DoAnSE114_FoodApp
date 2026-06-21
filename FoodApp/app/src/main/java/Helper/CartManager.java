package  Helper;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import Domain.CartItem;
import Domain.Foods;

public class CartManager {

    private static volatile CartManager instance = null;
    private final List<CartItem> cartItems;
    private final DatabaseReference cartRef;
    private String currentUserId = ""; // Thay bằng ID của User hiện tại khi đăng nhập

    // Private Constructor
    private CartManager() {
        cartItems = new ArrayList<>();
        // Trỏ sẵn đến Node "Cart" trên Firebase Realtime Database
        cartRef = FirebaseDatabase.getInstance().getReference("Cart");
    }

    public static CartManager getInstance() {
        if (instance == null) {
            synchronized (CartManager.class) {
                if (instance == null) {
                    instance = new CartManager();
                }
            }
        }
        return instance;
    }

    public void initCartData(String userId) {
        this.currentUserId = userId;

        cartRef.child(userId).child("items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartItems.clear(); // Xóa dữ liệu cũ trên RAM trước
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        CartItem item = data.getValue(CartItem.class);
                        if (item != null) {
                            cartItems.add(item); // Nạp dữ liệu từ Firebase vào RAM
                        }
                    }
                }
                Log.d("CartManager", "Đã nạp xong " + cartItems.size() + " món từ Firebase về RAM.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CartManager", "Lỗi tải giỏ hàng: " + error.getMessage());
            }
        });
    }


    public void addToCart(int foodId, int quantity) {
        CartItem existingItem = null;

        // Tìm xem trên RAM đã có món này chưa
        for (CartItem item : cartItems) {
            if (item.getFoodID() == foodId) {
                existingItem = item;
                break;
            }
        }

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            cartItems.add(new CartItem(foodId, quantity));
        }

        // Cập nhật trên RAM xong thì đẩy thẳng lên Firebase đồng bộ luôn
        syncWithFirebase();
    }

    public void removeFromCart(int foodId) {
        CartItem itemToRemove = null;

        // 1. Tìm xem món cần xóa đang nằm ở đâu trong danh sách gốc (cartItems) trên RAM
        for (CartItem item : cartItems) {
            if (item.getFoodID() == foodId) {
                itemToRemove = item;
                break;
            }
        }

        // 2. Nếu tìm thấy thì tiến hành xóa sạch khỏi RAM và đồng bộ lên Firebase
        if (itemToRemove != null) {
            cartItems.remove(itemToRemove);
            syncWithFirebase(); // Đẩy list mới đã xóa món lên Firebase luôn
            Log.d("CartManager", "Đã xóa món ID " + foodId + " khỏi CartManager và Firebase.");
        }
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public double getTotalFee()
    {
        double total = 0.0;
        for (CartItem item : cartItems)
        {
            if (item.getFoodDetails() != null)
            {
                total = total + ( Math.round(item.getFoodDetails().getPrice() * item.getQuantity() * 100.0)/ 100.0);
            }

        }
        return total;
    }

    public void convertAndSyncDetails(List<Foods> allFoodsList) {
        if (allFoodsList == null || allFoodsList.isEmpty()) return;

        for (CartItem item : cartItems) {
            for (Foods food : allFoodsList) {
                if (food.getID() == item.getFoodID()) {
                    item.setFoodDetails(food);
                    break; // Tìm thấy rồi thì dừng vòng lặp trong, qua item tiếp theo
                }
            }
        }
    }

    public void clearCart() {
        cartItems.clear(); // Xóa trên RAM
        syncWithFirebase(); // Đồng bộ xóa sạch mảng items trên Firebase
        Log.d("CartManager", "Đã xóa sạch giỏ hàng cả trên RAM và Firebase");
    }

    private void syncWithFirebase() {
        if (currentUserId.isEmpty()) {
            Log.e("CartManager", "Chưa có UserId, không thể đồng bộ Firebase!");
            return;
        }

        // Đẩy thẳng nguyên List lên Firebase JSON
        cartRef.child(currentUserId).child("items").setValue(cartItems)
                .addOnSuccessListener(aVoid -> Log.d("CartManager", "Đồng bộ Firebase thành công!"))
                .addOnFailureListener(e -> Log.e("CartManager", "Lỗi đồng bộ Firebase: " + e.getMessage()));
    }
}