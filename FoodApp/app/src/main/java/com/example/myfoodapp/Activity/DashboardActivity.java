package com.example.myfoodapp.Activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myfoodapp.Fragment.homeFragment; // Import đúng file homeFragment của bạn
import com.example.myfoodapp.R;
import com.example.myfoodapp.databinding.ActivityDashboardBinding; // ViewBinding của file XML Dashboard

public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. CHỖ QUAN TRỌNG NHẤT: Vừa mở Dashboard lên là phải nạp NGAY homeFragment vào khung trống
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new homeFragment())
                    .commit();
        }

        // 2. Bắt sự kiện Click cho nút Home (Khi người dùng đang ở tab khác bấm quay lại Home)
        binding.homeBtn.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new homeFragment())
                    .commit();
        });

        // 3. Các nút khác (Favorite, Basket, Profile...) bạn sẽ làm tương tự sau khi tạo xong Fragment cho tụi nó
        binding.favoriteBtn.setOnClickListener(v -> {
            // Sau này tạo FavoriteFragment thì thay vào đây nhé:
            // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FavoriteFragment()).commit();
        });
    }
}