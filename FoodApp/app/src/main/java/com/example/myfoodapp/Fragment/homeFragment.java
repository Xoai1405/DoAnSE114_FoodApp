package com.example.myfoodapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfoodapp.Activity.CartActivity;
import com.example.myfoodapp.Activity.ListFoodsActivity;
import com.example.myfoodapp.Activity.LoginActivity;
import com.example.myfoodapp.R;
import com.example.myfoodapp.databinding.FragmentHomeBinding; // Import đúng ViewBinding của fragment_home
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Adapter.BestFoodsAdapter;
import Adapter.CategoryAdapter;
import Domain.Category;
import Domain.Foods;
import Domain.Location;
import Domain.Price;
import Domain.Time;

public class homeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    public homeFragment() {
        // Bắt buộc phải có hàm khởi tạo trống này
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 1. Khởi tạo ViewBinding cho Fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // 2. Khởi tạo các biến Firebase (Vì Fragment không kế thừa BaseActivity nên phải gọi trực tiếp)
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // 3. Hiển thị Username
        String email = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : null;
        String username = email != null ? email.split("@")[0] : "User";
        binding.textView4.setText(username);

        // 4. Chạy các hàm load dữ liệu từ Firebase
        initLocation();
        initTime();
        initPrice();
        initBestFood();
        initCategory();
        setVariable();

        return binding.getRoot();
    }

    private void setVariable() {
        binding.logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish(); // Đóng luôn Activity chứa Fragment này
        });

        binding.searchBtn.setOnClickListener(v -> {
            String text = binding.searchEdit.getText().toString();
            if (!text.isEmpty()) {
                Intent intent = new Intent(getActivity(), ListFoodsActivity.class);
                intent.putExtra("text", text);
                intent.putExtra("isSearch", true);
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "Searching for food can not be blank", Toast.LENGTH_SHORT).show();
            }
        });

        binding.cartBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CartActivity.class);
            startActivity(intent);
        });

        binding.viewAllBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ListFoodsActivity.class);
            intent.putExtra("isViewAll", true);
            startActivity(intent);
        });

        binding.filterBtn.setOnClickListener(v -> {
            if (binding.layoutFilter.getVisibility() == View.GONE) {
                binding.layoutFilter.setVisibility(View.VISIBLE);
                binding.layoutFilter.setAlpha(0f);
                binding.layoutFilter.animate().alpha(1f).setDuration(300).start();
            } else {
                binding.layoutFilter.setVisibility(View.GONE);
            }
        });
    }

    private void initBestFood() {
        DatabaseReference myRef = database.getReference("Foods");
        binding.progressBarBestFood.setVisibility(View.VISIBLE);
        ArrayList<Foods> list = new ArrayList<>();
        Query query = myRef.orderByChild("BestFood").equalTo(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        Foods foods = issue.getValue(Foods.class);
                        list.add(foods);
                    }
                    if (list.size() > 0) {
                        // Dùng getActivity() thay cho MainActivity.this
                        binding.bestFoodView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                        RecyclerView.Adapter adapter = new BestFoodsAdapter(list);
                        binding.bestFoodView.setAdapter(adapter);
                    }
                    binding.progressBarBestFood.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void initLocation() {
        DatabaseReference myRef = database.getReference("Location");
        ArrayList<Location> list = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        Location location = issue.getValue(Location.class);
                        list.add(location);
                    }
                }
                // Dùng getActivity() để lấy Context cho ArrayAdapter
                ArrayAdapter<Location> adapter = new ArrayAdapter<>(getActivity(), R.layout.sp_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.locationSp.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void initTime() {
        DatabaseReference myRef = database.getReference("Time");
        ArrayList<Time> list = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        Time time = issue.getValue(Time.class);
                        list.add(time);
                    }
                }
                ArrayAdapter<Time> adapter = new ArrayAdapter<>(getActivity(), R.layout.sp_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.timeSp.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void initPrice() {
        DatabaseReference myRef = database.getReference("Price");
        ArrayList<Price> list = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        Price price = issue.getValue(Price.class);
                        list.add(price);
                    }
                }
                ArrayAdapter<Price> adapter = new ArrayAdapter<>(getActivity(), R.layout.sp_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.priceSp.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void initCategory() {
        DatabaseReference myRef = database.getReference("Category");
        binding.progressBarCategory.setVisibility(View.VISIBLE);
        ArrayList<Category> list = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        Category cat = issue.getValue(Category.class);
                        list.add(cat);
                    }
                    if (list.size() > 0) {
                        binding.categoryView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
                        RecyclerView.Adapter adapter = new CategoryAdapter(list);
                        binding.categoryView.setAdapter(adapter);
                    }
                    binding.progressBarCategory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Tránh rò rỉ bộ nhớ (Memory Leak)
    }
}