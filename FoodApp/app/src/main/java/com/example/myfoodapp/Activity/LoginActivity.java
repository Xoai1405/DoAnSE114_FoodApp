package com.example.myfoodapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.example.myfoodapp.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import Domain.User;

public class LoginActivity extends BaseActivity {

    ActivityLoginBinding binding;
    private GoogleSignInClient mGoogleSignInClient;

    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    Toast.makeText(this, "Đăng nhập Google thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.example.myfoodapp.R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        setVariable();
    }

    private void setVariable() {
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.userEdt.getText().toString().trim();
                String password = binding.passEdt.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(LoginActivity.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, task -> {
                    if (task.isSuccessful()) {
                        // Reload để lấy trạng thái verified mới nhất từ server
                        mAuth.getCurrentUser().reload().addOnCompleteListener(reloadTask -> {
                            if (mAuth.getCurrentUser().isEmailVerified()) {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this,
                                        "Vui lòng xác thực email trước khi đăng nhập.",
                                        Toast.LENGTH_LONG).show();
                                mAuth.signOut();
                            }
                        });
                    } else {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại. Kiểm tra lại email hoặc mật khẩu.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        binding.googleBtn.setOnClickListener(v -> signInWithGoogle());

        binding.forgotPasswordTv.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class))
        );

        binding.signupTv.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            finish();
        });


    }

    private void signInWithGoogle() {
        googleSignInLauncher.launch(mGoogleSignInClient.getSignInIntent());
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                        // 1. Lấy thông tin từ tài khoản Google vừa đăng nhập
                        String uid = mAuth.getCurrentUser().getUid();
                        String email = mAuth.getCurrentUser().getEmail();
                        String username = mAuth.getCurrentUser().getDisplayName();

                        // 2. Gọi hàm lưu thông tin vào Realtime Database ở dưới
                        saveUserToDatabase(uid, username, email);
                    } else {
                        Toast.makeText(this, "Xác thực Google thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToDatabase(String id, String name, String email)
    {
        User newUser = new User(id, name, "", email);
        reference.child("Users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Nếu nút này CHƯA tồn tại (!snapshot.exists()) -> Người dùng mới tinh -> Tiến hành lưu
                if (!snapshot.exists()) {
                    User newUser = new User(id, name, "", email);
                    reference.child("Users").child(id).setValue(newUser).addOnCompleteListener(writeTask -> {
                        if (writeTask.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công.", Toast.LENGTH_SHORT).show(); // Nhớ thêm .show() nha má nãy thiếu kìa!
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish(); // Nhớ finish để họ không bấm nút Back quay lại màn Login được nữa
                        } else {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Nếu nút đã tồn tại -> Người cũ đăng nhập lại -> Không lưu gì hết, cho qua thẳng MainActivity luôn
                    Toast.makeText(LoginActivity.this, "Chào mừng bạn quay trở lại.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
