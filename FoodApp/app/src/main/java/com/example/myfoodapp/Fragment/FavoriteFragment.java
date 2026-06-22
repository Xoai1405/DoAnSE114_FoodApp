package com.example.myfoodapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.myfoodapp.databinding.FragmentFavoriteBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Adapter.FavoriteAdapter;
import Domain.Foods;

public class FavoriteFragment extends Fragment {

    private FragmentFavoriteBinding binding;
    private FirebaseDatabase database;
    private ArrayList<Foods> favoriteFoodsList;
    private FavoriteAdapter favoriteAdapter;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();



        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        loadFavoriteFoods();
    }

    private void loadFavoriteFoods() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            //Toast.makeText(getActivity(), "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserID = currentUser.getUid();
        binding.progressBarFavorite.setVisibility(View.VISIBLE);

        favoriteFoodsList = new ArrayList<>();

        // 1. Vào bảng Favorites bốc các ID đã thích
        DatabaseReference favRef = database.getReference("Favorites").child(currentUserID);
        favRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    binding.progressBarFavorite.setVisibility(View.GONE);
                    toggleEmptyState(true);
                    return;
                }

                ArrayList<String> favFoodIds = new ArrayList<>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    if (item.getValue() != null) {
                        favFoodIds.add(item.getKey().trim());
                    }
                }

                // 2. Đi tìm chi tiết thông tin từ bảng "Foods"
                DatabaseReference foodsRef = database.getReference("Foods");
                foodsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot foodSnapshot) {
                        favoriteFoodsList.clear();

                        for (DataSnapshot issue : foodSnapshot.getChildren()) {
                            String foodKeyInFirebase = issue.getKey() != null ? issue.getKey().trim() : "";

                            if (favFoodIds.contains(foodKeyInFirebase)) {
                                Foods food = issue.getValue(Foods.class);
                                if (food != null) {
                                    try {
                                        food.setID(Integer.parseInt(foodKeyInFirebase));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    favoriteFoodsList.add(food);
                                }
                            }
                        }

                        // 3. Khởi tạo RecyclerView và đổ dữ liệu
                        if (getActivity() != null) {
                            binding.favoriteListView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

                            // THAY ĐỔI Ở ĐÂY: Khi bên Adapter bấm nút xóa thành công,
                            // nó sẽ kích hoạt lệnh này để Fragment chạy lại hàm loadFavoriteFoods() từ đầu!
                            favoriteAdapter = new FavoriteAdapter(favoriteFoodsList, () -> {
                                loadFavoriteFoods(); // <--- Lệnh ép Fragment load lại dữ liệu từ Firebase
                            });

                            binding.favoriteListView.setAdapter(favoriteAdapter);
                        }

                        binding.progressBarFavorite.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressBarFavorite.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progressBarFavorite.setVisibility(View.GONE);
            }
        });
    }

    // Hàm tiện ích ẩn hiện danh sách trống động
    private void toggleEmptyState(boolean isEmpty) {
        if (isEmpty) {
            binding.favoriteListView.setVisibility(View.GONE);
            binding.emptyFavoriteText.setVisibility(View.VISIBLE);
            //Toast.makeText(getActivity(), "Danh sách yêu thích trống!", Toast.LENGTH_SHORT).show();
            // Nếu trong layout fragment_favorite của bạn có TextView báo trống, bạn có thể gán hiển thị ở đây
        } else {
            binding.favoriteListView.setVisibility(View.VISIBLE);
            binding.emptyFavoriteText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}