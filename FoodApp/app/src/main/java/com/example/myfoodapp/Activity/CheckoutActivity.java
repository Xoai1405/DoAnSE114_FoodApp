package com.example.myfoodapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfoodapp.R;
import com.example.myfoodapp.databinding.ActivityCheckoutBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jspecify.annotations.NonNull;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.CheckoutAdapter;
import Domain.CartItem;
import Domain.Order;
import Domain.Voucher;
import Helper.CartManager;
import Helper.ManagmentCart;

public class CheckoutActivity extends BaseActivity {
    private ActivityCheckoutBinding binding;
    private double TaxPercent = 0.02f;
    private double DeliveryFee = 10.0f;
    private double TotalTax;
    private double TotalMoney;
    private double Subtotal;
    private Voucher selectedVoucher = null;
    private double discount = 0.0;


    private final ActivityResultLauncher<Intent> voucherLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedVoucher = (Voucher) result.getData().getSerializableExtra("selectedVoucher");
                    if (selectedVoucher != null) {
                        binding.selectedVoucherTxt.setText(selectedVoucher.getDescription());
                        binding.selectedVoucherTxt.setTextColor(ContextCompat.getColor(this, R.color.red));
                        calculateTotals();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        InitList();
        setupSpinner();
        calculateTotals();
        loadUserProfile();
        setupClickListeners();
    }

    private void setupSpinner() {
        String[] paymentMethods = {"Cash on Delivery (COD)", "Bank Transfer"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentMethods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.paymentSpinner.setAdapter(adapter);
    }

    private void loadUserProfile() {
        if (mAuth.getCurrentUser() == null) return;
        String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = database.getReference("Users").child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("userName").getValue(String.class);
                    String phone = snapshot.child("userPhoneNumber").getValue(String.class);
                    String address = snapshot.child("userAddress").getValue(String.class);

                    if (name == null || name.isEmpty()) name = "User";
                    if (phone == null || phone.isEmpty()) phone = "No Phone Number";
                    if (address == null || address.isEmpty()) address ="University of Information Technology, Linh Trung, Thu Duc, Ho Chi Minh City";

                    binding.addressDetailTxt.setText(address);
                    binding.userNameAddressTxt.setText(name + " | " + phone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setupClickListeners() {
        binding.backBtn.setOnClickListener(v -> finish());

        binding.voucherSection.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, VoucherActivity.class);
            voucherLauncher.launch(intent);
        });

        binding.placeOrderBtn.setOnClickListener(v -> {
            // 1. Kiểm tra xem người dùng đã đăng nhập chưa để tránh Null UID
            if (mAuth.getCurrentUser() == null) {
                Toast.makeText(CheckoutActivity.this, "User not authenticated!", Toast.LENGTH_SHORT).show();
                return;
            }

            String orderID = generateID();
            String voucherid = "None";
            String payment = "Cash on Delivery (COD)";

            if (selectedVoucher != null) {
                voucherid = selectedVoucher.getId();
            }
            if (binding.paymentSpinner.getSelectedItem() != null) {
                payment = binding.paymentSpinner.getSelectedItem().toString();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = sdf.format(new Date());

            // Tạo đối tượng Order
            Order newOrder = new Order(orderID, mAuth.getCurrentUser().getUid(), dateString, CartManager.getInstance().getCartItems(), voucherid, payment, TotalMoney);

            // BẬT ĐIỀU HƯỚNG HIỂN THỊ LOG ĐỂ BIẾT NÚT CÓ ĐƯỢC BẤM HAY KHÔNG
            Log.d("Firebase_Debug", "Đang tiến hành đặt hàng với ID: " + orderID);

            // 2. Tiến hành lưu vào bảng Orders
            reference.child("Orders").child(orderID).setValue(newOrder)
                    .addOnCompleteListener(task -> {
                        // Sử dụng addOnCompleteListener để ĐẢM BẢO luôn có phản hồi trả về
                        if (!task.isSuccessful()) {
                            Log.e("Firebase_Debug", "Thất bại hoàn toàn khi ghi Order!", task.getException());
                            Toast.makeText(CheckoutActivity.this, "Database Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnSuccessListener(av -> {
                        Log.d("Firebase_Debug", "Ghi Order thành công! Đang check Voucher...");
                        Toast.makeText(CheckoutActivity.this, "Order has been created successfully.", Toast.LENGTH_SHORT).show();
                        CartManager.getInstance().clearCart();

                        // 3. Xử lý Voucher
                        if (selectedVoucher != null) {
                            String currentUserId = mAuth.getCurrentUser().getUid();
                            String currentVoucherId = selectedVoucher.getId();

                            reference.child("UserVouchers")
                                    .orderByChild("userId")
                                    .equalTo(currentUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot ds : snapshot.getChildren()) {
                                                String vId = ds.child("voucherId").getValue(String.class);
                                                String status = ds.child("status").getValue(String.class);

                                                if (vId != null && vId.equals(currentVoucherId) && "unused".equals(status)) {
                                                    String recordKey = ds.getKey();
                                                    Map<String, Object> updates = new HashMap<>();
                                                    updates.put("status", "used");
                                                    updates.put("usedAt", dateString);

                                                    if (recordKey != null) {
                                                        reference.child("UserVouchers").child(recordKey).updateChildren(updates);
                                                    }
                                                    break;
                                                }
                                            }
                                            Log.d("Firebase_Debug", "Hoàn thành xử lý Voucher -> Đóng màn hình.");
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e("Firebase_Debug", "Lỗi truy vấn UserVouchers: " + error.getMessage());
                                            finish();
                                        }
                                    });
                        } else {
                            Log.d("Firebase_Debug", "Không dùng voucher -> Đóng màn hình.");
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firebase_Debug", "Lỗi trong addOnFailureListener: ", e);
                        Toast.makeText(CheckoutActivity.this, "Can not create new order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void InitList()
    {
        List<CartItem> list = CartManager.getInstance().getCartItems();
        LinearLayoutManager layout = new LinearLayoutManager(CheckoutActivity.this, LinearLayoutManager.VERTICAL, false);
        RecyclerView.Adapter adapter = new CheckoutAdapter(list);
        binding.CheckOutView.setLayoutManager(layout);
        binding.CheckOutView.setAdapter(adapter);
    }

    private void calculateTotals() {
        // 1. Tính toán các chi phí cơ bản ban đầu
        Subtotal = Math.round(CartManager.getInstance().getTotalFee() * 100.0) / 100.0;
        TotalTax = Math.round(TaxPercent * Subtotal * 100.0) / 100.0;
        discount = 0.0;

        // 2. Tính Tổng tiền đơn hàng khi CHƯA áp voucher (Dùng làm gốc để tính và xét điều kiện Voucher)
        double totalBeforeDiscount = Subtotal + TotalTax + DeliveryFee;

        if (selectedVoucher != null) {
            // Kiểm tra điều kiện dựa trên TỔNG ĐƠN (totalBeforeDiscount) thay vì Subtotal
            if (totalBeforeDiscount >= selectedVoucher.getMinOrderValue()) {

                if (selectedVoucher.getDiscountPercentage() > 0) {
                    // Giảm theo % của TỔNG ĐƠN
                    discount = totalBeforeDiscount * (selectedVoucher.getDiscountPercentage() / 100.0);

                    // Giới hạn số tiền giảm tối đa nếu vượt mức
                    if (discount > selectedVoucher.getMaxDiscount()) {
                        discount = selectedVoucher.getMaxDiscount();
                    }
                } else {
                    // Giảm thẳng số tiền cố định (MaxDiscount)
                    discount = selectedVoucher.getMaxDiscount();
                }

                // Làm tròn số tiền discount về 2 chữ số thập phân
                discount = Math.round(discount * 100.0) / 100.0;

                binding.discountLayout.setVisibility(View.VISIBLE);
                binding.discountTxt.setText("-$" + discount);
            } else {
                // Nếu tổng đơn không đủ điều kiện, tự động nhả voucher ra
                selectedVoucher = null;
                binding.selectedVoucherTxt.setText("Select FoodApp Voucher");
                binding.selectedVoucherTxt.setTextColor(ContextCompat.getColor(this, R.color.black));
                binding.discountLayout.setVisibility(View.GONE);
                Toast.makeText(this, "Total amount not eligible for this voucher!", Toast.LENGTH_SHORT).show();
            }
        } else {
            binding.discountLayout.setVisibility(View.GONE);
        }

        // 3. Tính Tổng tiền cuối cùng khách phải trả sau khi trừ discount
        TotalMoney = Math.round((totalBeforeDiscount - discount) * 100.0) / 100.0;
        if (TotalMoney < 0) TotalMoney = 0;

        // 4. Hiển thị lên giao diện (UI)
        binding.subtotalTxt.setText("$" + Subtotal);
        binding.taxTxt.setText("$" + TotalTax);
        binding.deliveryTxt.setText("$" + DeliveryFee);
        binding.totalPaymentTxt.setText("$" + TotalMoney);
    }

    private String generateID()
    {
        String firebaseId = reference.child("Orders").push().getKey();

        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String datePart = sdf.format(new Date());

        return "OD-" + datePart + "-" + firebaseId;
    }
}