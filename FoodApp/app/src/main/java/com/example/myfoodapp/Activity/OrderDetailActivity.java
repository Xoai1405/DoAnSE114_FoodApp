package com.example.myfoodapp.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfoodapp.R;
import com.example.myfoodapp.databinding.ActivityOrderDetailBinding;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.CheckoutAdapter;
import Domain.CartItem;
import Domain.Foods;
import Domain.Order;
import Domain.Voucher;
import Helper.CartManager;

public class OrderDetailActivity extends BaseActivity {

    private ActivityOrderDetailBinding binding;

    private Order order;
    private Voucher selectedVoucher;
    private List<Foods> listFoods;

    private List<Foods> orderFoodsDetailsList = new ArrayList<>();

    private double TaxPercent = 0.02f;
    private double DeliveryFee = 10.0f;
    private double TotalTax;
    private double TotalMoney;
    private double Subtotal;

    private double discount = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setVariable();
        getIntentExtra();
        SetUpData();
    }

    private void setVariable()
    {
        binding.BackDetailBtn.setOnClickListener(v -> finish());
    }

    private void getIntentExtra() {
        order = (Order) getIntent().getSerializableExtra("Order");
        listFoods = (List<Foods>) getIntent().getSerializableExtra("listFood");
    }

    private void SetUpData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Chỉ xử lý nếu có dữ liệu Đơn hàng truyền sang
        if (order != null) {

            // 1. Lấy dữ liệu User hiện tại về
            if (currentUser != null) {
                getUserInformation(currentUser.getUid());
            }

            // 2. Trích xuất thông tin chi tiết các món ăn từ listFoods dựa vào danh sách FoodID trong Order
            mapOrderItemsToFoodDetails();

            // 3. Lấy ra thông tin Voucher tương ứng dựa trên VoucherID của Order
            if (order.getVoucherId() != null && !order.getVoucherId().equals("None")) {
                getVoucherInformation(order.getVoucherId());
            } else {
                // Đơn hàng không áp dụng voucher
                this.selectedVoucher = null;
                // Bạn có thể set ẩn phần hiển thị voucher ở đây...
                calculateMoney();
            }
        }
    }

    private void getUserInformation(String uid) {


        // Giả sử bảng lưu User của bạn trên Firebase đặt tên là "Users"
        reference.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("userName").getValue(String.class);
                    String phone = snapshot.child("userPhoneNumber").getValue(String.class);
                    String address = snapshot.child("userAddress").getValue(String.class);

                    if (name == null || name.isEmpty()) name = "User";
                    if (phone == null || phone.isEmpty()) phone = "No Phone Number";
                    if (address == null || address.isEmpty()) address ="University of Information Technology, Linh Trung, Thu Duc, Ho Chi Minh City";

                    binding.AddressDetailText.setText(address);
                    binding.NameAddressDetailText.setText(name + " | " + phone);
                    binding.OrderDateText.setText(order.getCreatedAt());
                    binding.OrderIdText.setText(order.getId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("OrderDetail", "Lỗi lấy thông tin User: " + error.getMessage());
            }
        });
    }

    private void mapOrderItemsToFoodDetails() {
        if (order.getListItems() == null || listFoods == null) return;

        orderFoodsDetailsList.clear();

        // Duyệt qua từng Item giỏ hàng nằm trong Đơn hàng hiện tại
        for (CartItem item : order.getListItems()) {
            int targetFoodId = item.getFoodID();

            // Tìm kiếm thông tin món ăn khớp ID trong kho dữ liệu listFoods truyền sang
            for (Foods food : listFoods) {
                if (food.getID() == targetFoodId) { // Giả sử id trong Foods của bạn là kiểu int

                    // Thêm đối tượng Foods đầy đủ thuộc tính vào danh sách cục bộ của đơn hàng
                    item.setFoodDetails(food);

                    // 🔥 PHẦN BỔ SUNG 1: Thêm món ăn tìm thấy vào list toàn cục để phục vụ tính toán an toàn
                    orderFoodsDetailsList.add(food);
                    break; // Đã tìm thấy món, chuyển sang duyệt Item tiếp theo
                }
            }
        }

        LinearLayoutManager layout = new LinearLayoutManager(OrderDetailActivity.this, LinearLayoutManager.VERTICAL, false);
        RecyclerView.Adapter adapter = new CheckoutAdapter(order.getListItems());
        binding.OrderDetailView.setLayoutManager(layout);
        binding.OrderDetailView.setAdapter(adapter);
        Log.d("OrderDetail", "Đã tìm thấy " + orderFoodsDetailsList.size() + " món ăn chi tiết cho đơn hàng này.");
    }


    private void getVoucherInformation(String voucherId) {
        // 1. Trỏ thẳng vào nút tổng "Vouchers" thay vì đi sâu vào một con cụ thể
        reference.child("Vouchers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    selectedVoucher = null; // Reset lại biến toàn cục trước khi tìm

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Voucher v = ds.getValue(Voucher.class);

                        if (v != null && v.getId() != null && v.getId().equals(voucherId)) {
                            selectedVoucher = v; // Đã tìm thấy đúng đối tượng Voucher!
                            break; // Tìm thấy rồi thì thoát vòng lặp ngay lập tức cho nhẹ máy
                        }
                    }

                    // 3. Xử lý sau khi vòng lặp kết thúc
                    if (selectedVoucher != null) {
                        Toast.makeText(OrderDetailActivity.this, "Đã tìm thấy voucher: " + selectedVoucher.getId(), Toast.LENGTH_SHORT).show();
                        calculateMoney(); // Tiến hành tính tiền dựa trên voucher vừa tìm được
                    } else {
                        Toast.makeText(OrderDetailActivity.this, "Không có voucher nào trong danh sách có ID: " + voucherId, Toast.LENGTH_SHORT).show();
                        selectedVoucher = null;
                        calculateMoney(); // Tính tiền gốc bình thường
                    }
                } else {
                    selectedVoucher = null;
                    calculateMoney();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("OrderDetail", "Lỗi lấy danh sách Voucher: " + error.getMessage());
                selectedVoucher = null;
                calculateMoney();
            }
        });
    }

    private void calculateMoney()
    {
        // 1. Tính toán các chi phí cơ bản ban đầu
        Subtotal = Math.round(getTotalFee(order.getListItems()) * 100.0) / 100.0;
        TotalTax = Math.round(TaxPercent * Subtotal * 100.0) / 100.0;
        discount = 0.0;

        // 2. Tính Tổng tiền đơn hàng khi CHƯA áp voucher (Dùng làm gốc để tính và xét điều kiện Voucher)
        double totalBeforeDiscount = Subtotal + TotalTax + DeliveryFee;

        if ( selectedVoucher!= null) {
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

            } else {
                // Nếu tổng đơn không đủ điều kiện, tự động nhả voucher ra
                selectedVoucher = null;
                Toast.makeText(this, "Total amount not eligible for this voucher!", Toast.LENGTH_SHORT).show();
            }
        }

        // 3. Tính Tổng tiền cuối cùng khách phải trả sau khi trừ discount
        TotalMoney = Math.round((totalBeforeDiscount - discount) * 100.0) / 100.0;
        if (TotalMoney < 0) TotalMoney = 0;

        // 4. Hiển thị lên giao diện (UI)
        binding.SubtotalDetailText.setText("$" + Subtotal);
        binding.TaxDetailtext.setText("$" + TotalTax);
        binding.DeliveryDetailtext.setText("$" + DeliveryFee);
        binding.TotalDetailText.setText("$" + TotalMoney);
        binding.DiscountDetailText.setText("-$" + discount);
    }

    // 🔥 PHẦN BỔ SUNG ĐỔI LOGIC: Hàm tính tiền dựa vào orderFoodsDetailsList đã cô lập dữ liệu chắc chắn trên RAM
    private double getTotalFee(List<CartItem> list)
    {
        double total = 0.0;
        if (list == null || orderFoodsDetailsList.isEmpty()) return 0.0;

        for (CartItem item: list)
        {
            // Duyệt đối chiếu tìm giá tiền trực tiếp từ list món ăn cục bộ
            for (Foods food : orderFoodsDetailsList) {
                if (food.getID() == item.getFoodID()) {
                    double itemTotal = food.getPrice() * item.getQuantity();
                    total = total + (Math.round(itemTotal * 100.0)/ 100.0);
                    break;
                }
            }
        }
        return total;
    }
}