package com.example.myfoodapp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myfoodapp.R;
import com.example.myfoodapp.databinding.FragmentCartBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.CartAdapter;
import Domain.CartItem;
import Domain.Foods;
import Helper.CartManager;


public class CartFragment extends BaseFragment {

    private FragmentCartBinding binding;

    private RecyclerView.Adapter adapter;
    private DatabaseReference foodRefs;

    private double TaxPercent = 0.02f;
    private double DeliveryFee = 10.0f;
    private double TotalTax;
    private double TotalMoney;
    private double Subtotal;


    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater, container, false);
        setVariable();
        return binding.getRoot();
    }

    private void setVariable()
    {
        binding.EmptyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "Bấm vào là xóa toàn bộ.", Toast.LENGTH_SHORT).show();
                CartManager.getInstance().clearCart();
                if (adapter != null)  adapter.notifyDataSetChanged();
                List<CartItem> emptyList = CartManager.getInstance().getCartItems();
                checkEmptyCart(emptyList);
            }
        });
    }

    private void initList() {
        foodRefs = database.getReference("Foods");

        // Sử dụng addListenerForSingleValueEvent là đúng (chỉ lấy 1 lần),
        // nhưng hãy kiểm tra kỹ điều kiện UI trước khi xử lý
        foodRefs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Kiểm tra an toàn bảo vệ Fragment
                if (binding == null || getContext() == null) return;

                List<Foods> allFoodsList = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Foods food = data.getValue(Foods.class);
                        if (food != null) {
                            allFoodsList.add(food);
                        }
                    }
                }

                // Chỉ map thông tin món ăn trên RAM, KHÔNG gọi lệnh setValue() ngược lên Firebase ở đây
                CartManager.getInstance().convertAndSyncDetails(allFoodsList);
                List<CartItem> list = CartManager.getInstance().getCartItems();

                if (checkEmptyCart(list)) return;

                LinearLayoutManager layout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                binding.CartView.setLayoutManager(layout);

                adapter = new CartAdapter(list, () -> {
                    calculatedCart();
                    SetUpUI();
                    checkEmptyCart(list);
                });

                binding.CartView.setAdapter(adapter);

                // Tính toán tiền và hiển thị UI
                if (list != null && !list.isEmpty()) {
                    calculatedCart();
                    SetUpUI();
                } else {
                    Subtotal = 0;
                    TotalTax = 0;
                    TotalMoney = 0;
                    SetUpUI();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FragmentCart", "Lỗi lấy danh sách Foods: " + error.getMessage());
            }
        });
    }

    private void calculatedCart() {
        Subtotal = CartManager.getInstance().getTotalFee();
        TotalTax = Math.round(Subtotal * TaxPercent * 100.0)/ 100.0;
        TotalMoney = Subtotal + TotalTax + DeliveryFee;
    }

    private void SetUpUI()
    {
        binding.SubtotalText.setText("$" + Subtotal);
        binding.TaxText.setText("$" + TotalTax);
        binding.DeliveryText.setText("$" + DeliveryFee);
        binding.TotalText.setText("$" + TotalMoney);
    }

    private boolean checkEmptyCart(List<CartItem> list)
    {
        if (list.size()<=0)
        {
            binding.CartScrollview.setVisibility(View.GONE);
            binding.emptyCartText.setVisibility(View.VISIBLE);
            return true;
        }  else {

            binding.CartScrollview.setVisibility(View.VISIBLE);
            binding.emptyCartText.setVisibility(View.GONE);
            return false;
        }
    }

    public void Reload()
    {
        initList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Giải phóng bộ nhớ tránh rò rỉ memory leak
    }

}