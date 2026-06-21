package com.example.myfoodapp.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.myfoodapp.R;
import com.example.myfoodapp.databinding.ActivityDetailBinding;

import Domain.Foods;
import Helper.CartManager;
import Helper.Cloud_Service;
import Helper.ManagmentCart;

public class DetailActivity extends BaseActivity {
    ActivityDetailBinding binding;
    private Foods object;
    private int num = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        getIntentExtra();
        setVariable();
    }

    private void setVariable() {


        binding.backBtn.setOnClickListener(v -> finish());

        /*Glide.with(DetailActivity.this)
                .load(object.getImagePath())
                .into(binding.pic);*/

        Cloud_Service.loadCloudinaryImageWithGlide(object.getTitle(), binding.pic);

        binding.priceTxt.setText("$" + object.getPrice());
        binding.titleTxt.setText((object.getTitle()));
        binding.descriptionTxt.setText(object.getDescription());
        binding.rateTxt.setText(object.getStar() + " Rating");
        binding.ratingBar.setRating((float) object.getStar());
        binding.totalTxt.setText((num * object.getPrice() + "$"));

        binding.numTxt.setText(String.valueOf(num));

        binding.plusBtn.setOnClickListener(v -> {
            num = num + 1;
            binding.numTxt.setText(String.valueOf(num));
            binding.totalTxt.setText((num * object.getPrice()) + "$");
        });

        binding.minusBtn.setOnClickListener(v -> {
            if (num > 1) {
                num = num - 1;
                binding.numTxt.setText(String.valueOf(num));
                binding.totalTxt.setText((num * object.getPrice()) + "$");
            }
        });

        binding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(DetailActivity.this, "ID Food: " + object.getID(), Toast.LENGTH_SHORT).show();
                CartManager.getInstance().addToCart(object.getID(), num);
                Toast.makeText(DetailActivity.this, "Added this product to your cart.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getIntentExtra() {
        object = (Foods) getIntent().getSerializableExtra("object");
    }
}