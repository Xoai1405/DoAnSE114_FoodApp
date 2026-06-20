package com.example.myfoodapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myfoodapp.Activity.LoginActivity;
import com.example.myfoodapp.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        binding.emailTv.setText(user.getEmail());

        loadUserData();

        binding.saveBtn.setOnClickListener(v -> saveUserData());
        binding.changePasswordBtn.setOnClickListener(v -> changePassword());
        binding.logoutBtn.setOnClickListener(v -> logout());
    }

    private void loadUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded() || binding == null) return;

                if (snapshot.exists()) {
                    String userName = snapshot.child("userName").getValue(String.class);
                    String userPhone = snapshot.child("userPhoneNumber").getValue(String.class);
                    String userAddress = snapshot.child("userAddress").getValue(String.class);

                    if (userName != null && !userName.equals("New User")) {
                        binding.nameEdt.setText(userName);
                    }
                    if (userPhone != null) binding.phoneEdt.setText(userPhone);
                    if (userAddress != null) binding.addressEdt.setText(userAddress);

                    updateAvatar(userName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Không tải được dữ liệu hồ sơ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateAvatar(String userName) {
        String source = (userName != null && !userName.isEmpty() && !userName.equals("New User"))
                ? userName
                : mAuth.getCurrentUser().getEmail();
        if (source != null && !source.isEmpty()) {
            binding.avatarTv.setText(source.substring(0, Math.min(2, source.length())).toUpperCase());
        }
    }

    private void saveUserData() {
        String userName = binding.nameEdt.getText().toString().trim();
        String userPhone = binding.phoneEdt.getText().toString().trim();
        String userAddress = binding.addressEdt.getText().toString().trim();

        if (userName.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập tên hiển thị", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.saveBtn.setEnabled(false);

        Map<String, Object> updates = new HashMap<>();
        updates.put("userName", userName);
        updates.put("userPhoneNumber", userPhone);
        updates.put("userAddress", userAddress);

        userRef.updateChildren(updates).addOnCompleteListener(task -> {
            if (binding == null) return;
            binding.saveBtn.setEnabled(true);
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Đã lưu thay đổi", Toast.LENGTH_SHORT).show();
                updateAvatar(userName);
            } else {
                Toast.makeText(getContext(), "Lưu thất bại, thử lại sau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changePassword() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || user.getEmail() == null) return;

        mAuth.sendPasswordResetEmail(user.getEmail())
                .addOnCompleteListener(task -> {
                    if (getContext() == null) return;
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(),
                                "Email đổi mật khẩu đã được gửi. Kiểm tra hộp thư của bạn.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Gửi email thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}