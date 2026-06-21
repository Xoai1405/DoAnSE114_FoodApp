package com.example.myfoodapp.Activity;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.example.myfoodapp.Fragment.ProfileFragment;
import com.example.myfoodapp.Fragment.homeFragment;
import com.example.myfoodapp.Fragment.CartFragment;
import com.example.myfoodapp.Fragment.DealFragment;
import com.example.myfoodapp.Fragment.FavoriteFragment;

import com.example.myfoodapp.R;
import com.example.myfoodapp.databinding.ActivityDashboardBinding;

public class DashboardActivity extends BaseActivity {

    private ActivityDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Mặc định khi mở App lên lần đầu -> Load màn hình Home mới tinh
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new homeFragment())
                    .commit();
        }

        // Gọi hàm bắt sự kiện click cho các nút menu
        setVariable();
    }

    private void setVariable() {
        // CỨ BẤM NÚT LÀ KHỞI TẠO NEW FRAGMENT MỚI -> ÉP CHẠY LẠI LOGIC LOAD DATABASE 100%

        // Bấm nút Home -> Tạo mới và load lại màn hình Home
        binding.HomeBtn.setOnClickListener(v -> loadNewFragment(new homeFragment()));

        // Bấm nút Giỏ hàng -> Tạo mới và load lại màn hình Giỏ hàng
        binding.CartBtn.setOnClickListener(v -> loadNewFragment(new CartFragment()));

        // Bấm nút Khuyến mãi -> Tạo mới và load lại màn hình Deal
        binding.DealBtn.setOnClickListener(v -> loadNewFragment(new DealFragment()));

        // Bấm nút Yêu thích -> Tạo mới và load lại màn hình Favorite (Quét lại Firebase ngay lập tức)
        binding.FavoriteBtn.setOnClickListener(v -> loadNewFragment(new FavoriteFragment()));

        // Bấm nút Tài khoản -> Tạo mới và load lại màn hình Profile
        binding.UserBtn.setOnClickListener(v -> loadNewFragment(new ProfileFragment()));
    }

    // HÀM THẦN THÁNH: Thay thế hoàn toàn Fragment cũ bằng Fragment mới tinh để kích hoạt reload dữ liệu
    private void loadNewFragment(Fragment targetFragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, targetFragment)
                .commit();
    }
}