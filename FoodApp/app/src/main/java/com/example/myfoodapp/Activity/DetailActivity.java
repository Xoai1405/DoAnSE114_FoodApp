package com.example.myfoodapp.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

import com.example.myfoodapp.R;
import com.example.myfoodapp.databinding.ActivityDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Domain.Foods;
import Helper.CartManager;
import Helper.Cloud_Service;

public class DetailActivity extends BaseActivity {
    ActivityDetailBinding binding;
    private Foods object;
    private int num = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        getIntentExtra();
        setVariable();
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());

        Cloud_Service.loadCloudinaryImageWithGlide(object.getTitle(), binding.pic);

        binding.priceTxt.setText("$" + object.getPrice());
        binding.titleTxt.setText(object.getTitle());
        binding.descriptionTxt.setText(object.getDescription());
        binding.rateTxt.setText(object.getStar() + " Rating");
        binding.ratingBar.setRating((float) object.getStar());
        binding.totalTxt.setText((num * object.getPrice() + "$"));
        binding.numTxt.setText(String.valueOf(num));

        binding.plusBtn.setOnClickListener(v -> {
            num = num + 1;
            binding.numTxt.setText(String.valueOf(num));
            binding.totalTxt.setText((num * object.getPrice()) + "$");
        });

        binding.minusBtn.setOnClickListener(v -> {
            if (num > 1) {
                num = num - 1;
                binding.numTxt.setText(String.valueOf(num));
                binding.totalTxt.setText((num * object.getPrice()) + "$");
            }
        });

        binding.addBtn.setOnClickListener(v -> {
            CartManager.getInstance().addToCart(object.getID(), num);
            Toast.makeText(DetailActivity.this, "Added this product to your cart.", Toast.LENGTH_SHORT).show();
        });

        // ==========================================
        // KHU VỰC THÊM MỚI: XỬ LÝ NÚT TRÁI TIM (FAVBTN)
        // ==========================================
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserID = currentUser.getUid();
            String foodID = String.valueOf(object.getID()).trim();

            // Trỏ thẳng đến đường dẫn Favorites/UID/FoodID trên Firebase
            DatabaseReference favRef = FirebaseDatabase.getInstance().getReference("Favorites")
                    .child(currentUserID)
                    .child(foodID);

            // 1. Kiểm tra xem món này đã được thích từ trước chưa để hiển thị màu trái tim thích hợp
            favRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        binding.favBtn.setSelected(true); // Đã thích -> Kích hoạt trạng thái selected (Đỏ)
                    } else {
                        binding.favBtn.setSelected(false); // Chưa thích -> Trắng
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });

            // 2. Bắt sự kiện click vào nút Trái tim để thêm/xóa yêu thích động
            binding.favBtn.setOnClickListener(v -> {
                boolean isCurrentlySelected = binding.favBtn.isSelected();

                if (!isCurrentlySelected) {
                    // Nếu chưa thích -> Tiến hành THÍCH món ăn
                    favRef.setValue(true).addOnSuccessListener(aVoid -> {
                        binding.favBtn.setSelected(true); // Đổi màu trái tim sang đỏ
                        Toast.makeText(DetailActivity.this, "Added this product to your favorite list.", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(DetailActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                } else {
                    // Nếu đã thích rồi -> Tiến hành BỎ THÍCH món ăn
                    favRef.removeValue().addOnSuccessListener(aVoid -> {
                        binding.favBtn.setSelected(false); // Đổi màu trái tim về trắng
                        Toast.makeText(DetailActivity.this, "Remove this product from your favorite list.", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(DetailActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } else {
            // Trường hợp chưa đăng nhập mà ấn trái tim
            binding.favBtn.setOnClickListener(v ->
                    Toast.makeText(DetailActivity.this, "Vui lòng đăng nhập để sử dụng tính năng này!", Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void getIntentExtra() {
        object = (Foods) getIntent().getSerializableExtra("object");
    }
}