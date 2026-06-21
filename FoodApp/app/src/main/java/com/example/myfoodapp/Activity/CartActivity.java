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

    private Voucher selectedVoucher = null;
    private double discount = 0.0;

    private final ActivityResultLauncher<Intent> voucherLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedVoucher = (Voucher) result.getData().getSerializableExtra("selectedVoucher");
                    if (selectedVoucher != null) {
                        //binding.selectedVoucherTxt.setText(selectedVoucher.getDescription());
                        //binding.selectedVoucherTxt.setTextColor(ContextCompat.getColor(this, R.color.red));

                        calculatedCart();
                        SetUI();
                    }
                }
            });

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

        //Init();
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

    /*private void Init() {
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
    }*/

    private void SetUI() {
        binding.SubtotalText.setText("$" + Subtotal);
        binding.DeliveryText.setText("$" + DeliveryFee);
        binding.TaxText.setText("$" + TotalTax);
        binding.TotalText.setText("$" + TotalMoney);

        if (selectedVoucher != null) {
            binding.discountLabel.setVisibility(View.VISIBLE);
            binding.discountTxt.setVisibility(View.VISIBLE);
            binding.discountTxt.setText("-$" + Math.round(discount * 100.0) / 100.0);
        } else {
            //binding.selectedVoucherTxt.setText("Select voucher");
            //binding.selectedVoucherTxt.setTextColor(ContextCompat.getColor(this, R.color.black));
            binding.discountLabel.setVisibility(View.GONE);
            binding.discountTxt.setVisibility(View.GONE);
        }
    }
}