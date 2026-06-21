package com.example.myfoodapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myfoodapp.R;
import com.example.myfoodapp.databinding.ActivityCheckoutBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jspecify.annotations.NonNull;

import Domain.Voucher;
import Helper.ManagmentCart;

public class CheckoutActivity extends AppCompatActivity {
    private ActivityCheckoutBinding binding;
    private ManagmentCart managementCart;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private double TaxPercent = 0.02f;
    private double DeliveryFee = 10.0f;
    private double TotalTax;
    private double TotalMoney;
    private double Subtotal;
    private Voucher selectedVoucher = null;
    private double discount = 0.0;

    private final ActivityResultLauncher<Intent> voucherLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedVoucher = (Voucher) result.getData().getSerializableExtra("selectedVoucher");
                    if (selectedVoucher != null) {
                        binding.selectedVoucherTxt.setText(selectedVoucher.getDescription());
                        binding.selectedVoucherTxt.setTextColor(ContextCompat.getColor(this, R.color.red));
                        calculateTotals();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        managementCart = new ManagmentCart(this);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        setupSpinner();
        setupClickListeners();
        calculateTotals();
        loadUserProfile();
    }

    private void setupSpinner() {
        String[] paymentMethods = {"Cash on Delivery (COD)", "Bank Transfer"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentMethods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.paymentSpinner.setAdapter(adapter);
    }

    private void loadUserProfile() {
        if (auth.getCurrentUser() == null) return;
        String uid = auth.getCurrentUser().getUid();
        DatabaseReference userRef = database.getReference("Users").child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);

                    if (name == null || name.isEmpty()) name = "User";
                    if (phone == null || phone.isEmpty()) phone = "No Phone Number";

                    binding.userNameAddressTxt.setText(name + " | " + phone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setupClickListeners() {
        binding.backBtn.setOnClickListener(v -> finish());

        binding.voucherSection.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, VoucherActivity.class);
            voucherLauncher.launch(intent);
        });

        binding.placeOrderBtn.setOnClickListener(v -> {
            Toast.makeText(CheckoutActivity.this, "Order Placed Successfully!", Toast.LENGTH_SHORT).show();
            managementCart.getListCart().clear();
            Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void calculateTotals() {
        Subtotal = Math.round(managementCart.getTotalFee() * 100.0) / 100.0;
        TotalTax = Math.round(TaxPercent * Subtotal * 100.0) / 100.0;
        discount = 0.0;

        if (selectedVoucher != null) {
            if (Subtotal >= selectedVoucher.getMinOrderValue()) {
                if (selectedVoucher.getDiscountPercentage() > 0) {
                    discount = Subtotal * (selectedVoucher.getDiscountPercentage() / 100.0);
                    if (discount > selectedVoucher.getMaxDiscount()) {
                        discount = selectedVoucher.getMaxDiscount();
                    }
                } else {
                    discount = selectedVoucher.getMaxDiscount();
                }
                binding.discountLayout.setVisibility(View.VISIBLE);
                binding.discountTxt.setText("-$" + Math.round(discount * 100.0) / 100.0);
            } else {
                selectedVoucher = null;
                binding.selectedVoucherTxt.setText("Select FoodApp Voucher");
                binding.selectedVoucherTxt.setTextColor(ContextCompat.getColor(this, R.color.black));
                binding.discountLayout.setVisibility(View.GONE);
                Toast.makeText(this, "Not eligible for this voucher!", Toast.LENGTH_SHORT).show();
            }
        } else {
            binding.discountLayout.setVisibility(View.GONE);
        }

        TotalMoney = Math.round((Subtotal + TotalTax + DeliveryFee - discount) * 100.0) / 100.0;
        if (TotalMoney < 0) TotalMoney = 0;

        binding.subtotalTxt.setText("$" + Subtotal);
        binding.taxTxt.setText("$" + TotalTax);
        binding.deliveryTxt.setText("$" + DeliveryFee);
        binding.totalPaymentTxt.setText("$" + TotalMoney);
    }
}