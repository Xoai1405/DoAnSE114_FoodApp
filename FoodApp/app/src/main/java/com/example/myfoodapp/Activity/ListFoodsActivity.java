package com.example.myfoodapp.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfoodapp.R;
import com.example.myfoodapp.databinding.ActivityListFoodsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Adapter.FoodListAdapter;
import Domain.Foods;

public class ListFoodsActivity extends BaseActivity {
ActivityListFoodsBinding binding;
private RecyclerView.Adapter adapterListFood;
private int categoryId;
private String categoryName;
private String searchText;
private boolean isSearch;
private boolean isViewAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityListFoodsBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getIntentExtra();
        initList();
        setVariable();
    }

    private void setVariable(){

    }
    private void initList(){
        DatabaseReference myRef = database.getReference("Foods");
        binding.progressBar.setVisibility(View.VISIBLE);
        ArrayList<Foods> list = new ArrayList<>();
        if (isViewAll) {
            // Câu lệnh này sẽ lọc đúng những món ăn có "BestFood: true" trong bảng Foods giống như trên ảnh của bạn
            Query query = myRef.orderByChild("BestFood").equalTo(true);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot issue : snapshot.getChildren()) {
                            list.add(issue.getValue(Foods.class));
                        }
                        setupRecyclerView(list);
                    }
                    binding.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    binding.progressBar.setVisibility(View.GONE);
                }
            });
        }

        // TRƯỜNG HỢP 1: CÓ SEARCH (Lọc bằng code ở Client sau khi lấy hết data)
        else if (isSearch) {
            // Ép chuỗi tìm kiếm về chữ thường trước để tối ưu, tránh ép kiểu nhiều lần trong vòng lặp
            String lowerSearchText = searchText.toLowerCase().trim();

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot issue : snapshot.getChildren()) {
                            Foods food = issue.getValue(Foods.class);

                            if (food != null && food.getTitle() != null) {
                                String lowerTitle = food.getTitle().toLowerCase();

                                // Sử dụng contains nếu muốn tìm kiếm gần đúng (chứa từ khóa)
                                if (lowerTitle.contains(lowerSearchText)) {
                                    list.add(food);
                                }
                            }
                        }
                        // Hiển thị dữ liệu lên giao diện
                        setupRecyclerView(list);
                    }
                    binding.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    binding.progressBar.setVisibility(View.GONE);
                }
            });

            // TRƯỜNG HỢP 2: KHÔNG SEARCH (Query trực tiếp bằng CategoryId như cũ)
        } else {
            Query query = myRef.orderByChild("CategoryId").equalTo(categoryId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot issue : snapshot.getChildren()) {
                            list.add(issue.getValue(Foods.class));
                        }
                        // Hiển thị dữ liệu lên giao diện
                        setupRecyclerView(list);
                    }
                    binding.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    binding.progressBar.setVisibility(View.GONE);
                }
            });
        }
    }


    private void setupRecyclerView(ArrayList<Foods> list) {
        if (list.size() > 0) {
            binding.foodListView.setLayoutManager(new GridLayoutManager(ListFoodsActivity.this, 2));
            adapterListFood = new FoodListAdapter(list);
            binding.foodListView.setAdapter(adapterListFood);
        } else {
            binding.foodListView.setAdapter(null);
        }
    }
    private void getIntentExtra(){
        categoryId=getIntent().getIntExtra("CategoryId",0);
        categoryName=getIntent().getStringExtra("CategoryName");
        searchText=getIntent().getStringExtra("text");
        isSearch=getIntent().getBooleanExtra("isSearch",false);
        isViewAll=getIntent().getBooleanExtra("isViewAll",false);
        // nếu là chế độ xem hết thì đổi tiêu đề
        if (isViewAll) {
            binding.titleTxt.setText("Today's Best Foods");
        } else {
            binding.titleTxt.setText(categoryName);
        }
        binding.backBtn.setOnClickListener(v->finish());
    }
}