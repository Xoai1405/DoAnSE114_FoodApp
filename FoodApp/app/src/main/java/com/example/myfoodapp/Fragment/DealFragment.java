package com.example.myfoodapp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myfoodapp.R;
import com.example.myfoodapp.databinding.FragmentDealBinding;

import java.util.ArrayList;
import java.util.List;

import Adapter.OrderAdapter;
import Domain.Foods;
import Domain.Order;

public class DealFragment extends BaseFragment {


    private FragmentDealBinding binding;

    private OrderAdapter adapter;

    private List<Order> list;

    public DealFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();

        InitList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDealBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    private void InitList() {
        if (mAuth.getCurrentUser() == null) return;

        String currentUserId = mAuth.getCurrentUser().getUid();

        // Khởi tạo 2 danh sách trống
        list = new ArrayList<>();
        List<Domain.Foods> listFoods = new ArrayList<>();

        // Khởi tạo Adapter và gắn vào RecyclerView (Truyền cả 2 list vào)
        adapter = new OrderAdapter(list, listFoods);
        LinearLayoutManager layout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.OrderView.setLayoutManager(layout);
        binding.OrderView.setAdapter(adapter);

        // BƯỚC 1: Lấy toàn bộ bảng Foods về máy trước để làm "Bộ Từ Điển" tra cứu
        reference.child("Foods").addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshotFoods) {
                listFoods.clear();
                for (com.google.firebase.database.DataSnapshot dsFood : snapshotFoods.getChildren()) {
                    Domain.Foods food = dsFood.getValue(Domain.Foods.class);
                    if (food != null) {
                        listFoods.add(food); // Nạp tất cả món ăn trong hệ thống vào listFoods
                    }
                }

                // BƯỚC 2: Khi chắc chắn đã có listFoods rồi, mới tiến hành lắng nghe bảng Orders
                reference.child("Orders")
                        .orderByChild("userId")
                        .equalTo(currentUserId)
                        .addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshotOrders) {
                                list.clear();
                                for (com.google.firebase.database.DataSnapshot dsOrder : snapshotOrders.getChildren()) {
                                    Order order = dsOrder.getValue(Order.class);
                                    if (order != null) {
                                        list.add(order);
                                    }
                                }

                                // Cập nhật giao diện sau khi cả 2 danh sách dữ liệu đã đồng bộ
                                adapter.notifyDataSetChanged();

                                if (list.isEmpty()) {
                                    binding.emptyOrderText.setVisibility(View.VISIBLE);
                                    binding.OrderScrollView.setVisibility(View.GONE);
                                } else {
                                    binding.emptyOrderText.setVisibility(View.GONE);
                                    binding.OrderScrollView.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
                                android.util.Log.e("DealFragment", "Lỗi tải bảng Orders: " + error.getMessage());
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
                android.util.Log.e("DealFragment", "Lỗi tải bảng Foods: " + error.getMessage());
            }
        });
    }
}