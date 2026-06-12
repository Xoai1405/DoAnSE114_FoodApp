package com.example.myfoodapp.Activity;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import com.example.myfoodapp.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends BaseActivity {

    private ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setVariable();
    }

    private void setVariable() {
        // Nút back
        binding.backBtn.setOnClickListener(v -> finish());

        // Nút gửi email reset mật khẩu
        binding.sendBtn.setOnClickListener(v -> {
            String email = binding.emailEdt.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.sendBtn.setEnabled(false);
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        binding.sendBtn.setEnabled(true);
                        if (task.isSuccessful()) {
                            Toast.makeText(this,
                                    "Email đặt lại mật khẩu đã được gửi. Kiểm tra hộp thư của bạn.",
                                    Toast.LENGTH_LONG).show();
                            finish(); // Quay về màn Login
                        } else {
                            String errorMsg = task.getException() != null
                                    ? task.getException().getMessage()
                                    : "Gửi email thất bại";
                            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}
