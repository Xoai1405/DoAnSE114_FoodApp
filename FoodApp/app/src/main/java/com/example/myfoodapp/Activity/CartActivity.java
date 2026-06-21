package com.example.myfoodapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfoodapp.R;
import com.example.myfoodapp.databinding.ActivityCartBinding;

import Adapter.CartAdapter;
import Helper.ManagmentCart;

public class CartActivity extends AppCompatActivity {
    private ActivityCartBinding binding;
    private RecyclerView.Adapter adapter;
    private ManagmentCart managementCart;
    private double Subtotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Init();
        setVariable();
        calculatedCart();
    }

    private void calculatedCart() {
        Subtotal = Math.round(managementCart.getTotalFee() * 100.0) / 100.0;
        binding.SubtotalText.setText("$" + Subtotal);

        if (Subtotal == 0) {
            binding.CartScrollview.setVisibility(View.GONE);
            binding.emptyCartText.setVisibility(View.VISIBLE);
        }
    }

    private void setVariable() {
        binding.Backbtn.setOnClickListener(v -> finish());

        binding.checkoutBtn.setOnClickListener(v -> {
            if (Subtotal > 0) {
                startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
            } else {
                Toast.makeText(CartActivity.this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Init() {
        managementCart = new ManagmentCart(this);

        if (managementCart.getListCart().isEmpty()) {
            binding.CartScrollview.setVisibility(View.GONE);
            binding.emptyCartText.setVisibility(View.VISIBLE);
        } else {
            binding.CartScrollview.setVisibility(View.VISIBLE);
            binding.emptyCartText.setVisibility(View.GONE);
        }

        LinearLayoutManager layout = new LinearLayoutManager(CartActivity.this, LinearLayoutManager.VERTICAL, false);
        binding.CartView.setLayoutManager(layout);

        adapter = new CartAdapter(managementCart.getListCart(), CartActivity.this, () -> calculatedCart());
        binding.CartView.setAdapter(adapter);
    }
}