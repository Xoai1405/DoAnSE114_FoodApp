package com.example.myfoodapp.Activity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Patterns;
import android.view.View;
import android.widget.Toast;


import com.example.myfoodapp.databinding.ActivitySignupBinding;
import com.google.firebase.auth.FirebaseUser;

import Domain.User;

public class SignupActivity extends BaseActivity {

    ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setVariable();
    }

    private void setVariable() {
        binding.loginTv.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
        binding.signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpProcess();
            }
        });
    }

    private void SignUpProcess()
    {
        // lay thong tin
        String email = binding.userEdt.getText().toString().trim();
        String password = binding.passEdt.getText().toString();
        String confirmPassword = binding.confirmPassEdt.getText().toString();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(SignupActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(SignupActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(SignupActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this, task -> {
            if (task.isSuccessful()) {

                FirebaseUser current = mAuth.getCurrentUser();

                if (current != null)
                {
                    String userid = current.getUid();
                    User newUser = new User(userid, "New User", "", email);

                    reference.child("Users").child(userid).setValue(newUser).addOnCompleteListener(writeTask ->
                    {
                        if (writeTask.isSuccessful()) {
                            mAuth.getCurrentUser().sendEmailVerification();
                            Toast.makeText(SignupActivity.this,
                                    "Registration successful! Please check your email to verify your account.",
                                    Toast.LENGTH_LONG).show();
                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(SignupActivity.this, "Failed to save user data!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            } else
            {

                if (task.getException() instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                    Toast.makeText(SignupActivity.this, "This email is already registered!", Toast.LENGTH_LONG).show();
                } else {
                    // Các lỗi khác (như mất mạng, lỗi server...)
                    String err = task.getException() != null ? task.getException().getMessage() : "Registration failed!";
                    Toast.makeText(SignupActivity.this, err, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}