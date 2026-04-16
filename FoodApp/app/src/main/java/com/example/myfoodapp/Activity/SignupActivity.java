package com.example.myfoodapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myfoodapp.R;
import com.example.myfoodapp.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.core.Tag;

public class SignupActivity extends BaseActivity {
ActivitySignupBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);


        setVariable();
    }

    private void setVariable() {
        binding.signupBtn.setOnClickListener(new View.OnClickListener() {
            String email = binding.userEdt.getText().toString();
            String password = binding.passEdt.getText().toString();
            @Override
                    public void onClick(View v) {
                        String email = binding.userEdt.getText().toString();
                        String password = binding.passEdt.getText().toString();

                        if(password.length()<6) {
                            Toast.makeText(SignupActivity.this, "Your password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this,task-> {
                                if(task.isComplete()) {
                                    Log.i(TAG, "onComplete: ");
                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                } else {
                                    Log.i(TAG, "fairlure: " + task.getException());
                                    Toast.makeText(SignupActivity.this, "Authentication failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                                }


                        });
            }

        });
    }
}