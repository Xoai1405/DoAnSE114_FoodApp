package com.example.myfoodapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.myfoodapp.Fragment.homeFragment;       // Tên đúng chuẩn nên viết hoa chữ cái đầu nha bạn
import com.example.myfoodapp.Fragment.CartFragment;       // File Fragment giỏ hàng
import com.example.myfoodapp.Fragment.DealFragment;       // File Fragment khuyến mãi/đãi ngộ
import com.example.myfoodapp.Fragment.FavoriteFragment;   // File Fragment yêu thích
import com.example.myfoodapp.Fragment.UserFragment;    // File Fragment tài khoản

import com.example.myfoodapp.R;
import com.example.myfoodapp.databinding.ActivityDashboardBinding;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends BaseActivity {

    private ActivityDashboardBinding binding;

    private final Fragment homeFragment = new homeFragment();
    private final Fragment cartFragment = new CartFragment();
    private final Fragment dealFragment = new DealFragment();
    private final Fragment favoriteFragment = new FavoriteFragment();
    private final Fragment profileFragment = new UserFragment();

    private Fragment activeFragment = homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // nạp sẵn 5 Fragment vào
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, profileFragment, "5").hide(profileFragment)
                    .add(R.id.fragment_container, favoriteFragment, "4").hide(favoriteFragment)
                    .add(R.id.fragment_container, dealFragment, "3").hide(dealFragment)
                    .add(R.id.fragment_container, cartFragment, "2").hide(cartFragment)
                    .add(R.id.fragment_container, homeFragment, "1") // Thằng Home để lộ ra, không giấu ngầm
                    .commit();
        }

        // Gọi hàm bắt sự kiện click cho các nút menu
        setVariable();

        SetUpData();
    }

    private void setVariable() {

        // Bấm nút Home -> Hiện màn hình Home
        binding.HomeBtn.setOnClickListener(v -> switchFragment(homeFragment));

        // Bấm nút Giỏ hàng -> Hiện màn hình Giỏ hàng
        binding.CartBtn.setOnClickListener(v -> switchFragment(cartFragment));

        // Bấm nút Khuyến mãi -> Hiện màn hình Deal
        binding.DealBtn.setOnClickListener(v -> switchFragment(dealFragment)); // (Tên ID cũ của bạn đang là favoriteBtn cho nút deal)

        // Bấm nút Yêu thích -> Hiện màn hình Favorite
        binding.FavoriteBtn.setOnClickListener(v -> switchFragment(favoriteFragment));

        // Bấm nút Tài khoản -> Hiện màn hình Profile
        binding.UserBtn.setOnClickListener(v -> switchFragment(profileFragment));

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                finish();
            }
        });
    }


    private void switchFragment(Fragment targetFragment) {

        if (activeFragment == targetFragment) return;

        getSupportFragmentManager().beginTransaction()
                .hide(activeFragment)
                .show(targetFragment)
                .commit();

        activeFragment = targetFragment;
    }

    private void SetUpData()
    {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
        {
            String email = currentUser.getEmail();
            String username = email != null ? email.split("@")[0] : "User";
            binding.UserText.setText(username);
        }
    }
}