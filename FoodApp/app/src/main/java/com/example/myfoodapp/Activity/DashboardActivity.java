package com.example.myfoodapp.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myfoodapp.Fragment.ProfileFragment;
import com.example.myfoodapp.Fragment.homeFragment;
import com.example.myfoodapp.Fragment.CartFragment;
import com.example.myfoodapp.Fragment.DealFragment;
import com.example.myfoodapp.Fragment.FavoriteFragment;

import com.example.myfoodapp.R;
import com.example.myfoodapp.databinding.ActivityDashboardBinding;
import com.google.firebase.auth.FirebaseUser;

import java.util.Set;

import Helper.CartManager;

public class DashboardActivity extends BaseActivity {

    private ActivityDashboardBinding binding;

    private final homeFragment homeFragment = new homeFragment();
    private final CartFragment cartFragment = new CartFragment();
    private final DealFragment dealFragment = new DealFragment();
    private final FavoriteFragment favoriteFragment = new FavoriteFragment();
    private final ProfileFragment profileFragment = new ProfileFragment();

    private Fragment activeFragment = homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Mặc định khi mở App lên lần đầu -> Load màn hình Home mới tinh
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, profileFragment, "5").hide(profileFragment)
                    .add(R.id.fragment_container, favoriteFragment, "4").hide(favoriteFragment)
                    .add(R.id.fragment_container, dealFragment, "3").hide(dealFragment)
                    .add(R.id.fragment_container, cartFragment, "2").hide(cartFragment)
                    .add(R.id.fragment_container, homeFragment, "1") // Thằng Home được thêm cuối cùng và không bị ẩn -> tự động hiển thị lên trên
                    .commit();
        }

        // Gọi hàm bắt sự kiện click cho các nút menu
        setVariable();

        SetUpData();
    }

    private void setVariable() {
        // CỨ BẤM NÚT LÀ KHỞI TẠO NEW FRAGMENT MỚI -> ÉP CHẠY LẠI LOGIC LOAD DATABASE 100%

        // Bấm nút Home -> Tạo mới và load lại màn hình Home
        binding.HomeBtn.setOnClickListener(v -> switchFragment(new homeFragment()));

        // Bấm nút Giỏ hàng -> Hiện màn hình Giỏ hàng
        binding.CartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartFragment.Reload();
                switchFragment(cartFragment);
            }

        });

        // Bấm nút Khuyến mãi -> Tạo mới và load lại màn hình Deal
        binding.DealBtn.setOnClickListener(v -> switchFragment(new DealFragment()));

        // Bấm nút Yêu thích -> Tạo mới và load lại màn hình Favorite (Quét lại Firebase ngay lập tức)
        binding.FavoriteBtn.setOnClickListener(v -> switchFragment(new FavoriteFragment()));

        // Bấm nút Tài khoản -> Tạo mới và load lại màn hình Profile
        binding.UserBtn.setOnClickListener(v ->switchFragment(new ProfileFragment()));
    }

    // HÀM THẦN THÁNH: Thay thế hoàn toàn Fragment cũ bằng Fragment mới tinh để kích hoạt reload dữ liệu
    private void loadNewFragment(Fragment targetFragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, targetFragment)
                .commit();
    }
    private void switchFragment(Fragment targetFragment) {

        if (activeFragment == targetFragment) return;
        getSupportFragmentManager().beginTransaction()
                .hide(activeFragment)
                .show(targetFragment)
                .replace(R.id.fragment_container, targetFragment)
                .commit();

        activeFragment = targetFragment;
    }

    private void SetUpData()
    {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
        {
            if(CartManager.getInstance() != null)
            {
                CartManager.getInstance().initCartData(currentUser.getUid());
                //Toast.makeText( DashboardActivity.this,"Cart đã lấy được id thành công " + currentUser.getUid() , Toast.LENGTH_SHORT).show();
            }
        }
    }

}