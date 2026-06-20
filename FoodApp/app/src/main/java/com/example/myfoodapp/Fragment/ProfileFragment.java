package com.example.myfoodapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.myfoodapp.Activity.LoginActivity;
import com.example.myfoodapp.R;
import com.example.myfoodapp.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import Helper.Cloud_Service;

public class ProfileFragment extends BaseFragment {

    // Folder trên Cloudinary chứa bộ avatar dựng sẵn, tên file: avatar_1, avatar_2, ...
    private static final String PRESET_AVATAR_FOLDER = "PresetAvatars";

    private static final int PRESET_AVATAR_COUNT = 12;

    private FragmentProfileBinding binding;
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

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        userRef = database.getReference("Users").child(user.getUid());

        binding.emailTv.setText(user.getEmail());

        loadUserData();

        binding.saveBtn.setOnClickListener(v -> saveUserData());
        binding.changePasswordBtn.setOnClickListener(v -> changePassword());
        binding.logoutBtn.setOnClickListener(v -> confirmLogout());
        binding.changeAvatarBtn.setOnClickListener(v -> openAvatarPicker());
    }

    // Hiện dialog dạng lưới, mỗi ô là 1 avatar có sẵn trên Cloudinary
    private void openAvatarPicker() {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_avatar_picker, null);
        GridLayout grid = dialogView.findViewById(R.id.avatarGrid);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Choose Profile Picture")
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .create();

        int sizePx = (int) (72 * getResources().getDisplayMetrics().density);

        for (int i = 1; i <= PRESET_AVATAR_COUNT; i++) {
            String avatarId = "avatar_" + i;

            ImageView img = new ImageView(getContext());
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = sizePx;
            params.height = sizePx;
            params.setMargins(8, 8, 8, 8);
            img.setLayoutParams(params);
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);

            Cloud_Service.loadCloudinaryImageWithGlide(avatarId, PRESET_AVATAR_FOLDER, img);

            img.setOnClickListener(v -> {
                selectAvatar(avatarId);
                dialog.dismiss();
            });

            grid.addView(img);
        }

        dialog.show();
    }

    private void selectAvatar(String avatarId) {
        if (userRef == null) return;
        userRef.child("userAvatarId").setValue(avatarId)
                .addOnCompleteListener(task -> {
                    if (binding == null) return;
                    if (task.isSuccessful()) {
                        showSelectedAvatar(avatarId);
                        Toast.makeText(getContext(), "Profile picture updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to update profile picture. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showSelectedAvatar(String avatarId) {
        if (binding == null) return;
        binding.avatarTv.setVisibility(View.GONE);
        binding.avatarImg.setVisibility(View.VISIBLE);
        Cloud_Service.loadCloudinaryImageWithGlide(avatarId, PRESET_AVATAR_FOLDER, binding.avatarImg);
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
                    String avatarId = snapshot.child("userAvatarId").getValue(String.class);

                    if (userName != null && !userName.equals("New User")) {
                        binding.nameEdt.setText(userName);
                    }
                    if (userPhone != null) binding.phoneEdt.setText(userPhone);
                    if (userAddress != null) binding.addressEdt.setText(userAddress);

                    if (avatarId != null && !avatarId.isEmpty()) {
                        showSelectedAvatar(avatarId);
                    } else {
                        updateAvatarInitials(userName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to load profile data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateAvatarInitials(String userName) {
        if (binding == null) return;
        String source = (userName != null && !userName.isEmpty() && !userName.equals("New User"))
                ? userName
                : mAuth.getCurrentUser().getEmail();
        if (source != null && !source.isEmpty()) {
            binding.avatarTv.setText(source.substring(0, Math.min(2, source.length())).toUpperCase());
        }
        binding.avatarImg.setVisibility(View.GONE);
        binding.avatarTv.setVisibility(View.VISIBLE);
    }

    private void saveUserData() {
        String userName = binding.nameEdt.getText().toString().trim();
        String userPhone = binding.phoneEdt.getText().toString().trim();
        String userAddress = binding.addressEdt.getText().toString().trim();

        if (userName.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a display name", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "Changes saved successfully", Toast.LENGTH_SHORT).show();
                if (binding.avatarImg.getVisibility() != View.VISIBLE) {
                    updateAvatarInitials(userName);
                }
            } else {
                Toast.makeText(getContext(), "Failed to save changes. Please try again.", Toast.LENGTH_SHORT).show();
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
                                "Password reset email sent. Please check your inbox.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to send email", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void confirmLogout() {
        if (getContext() == null) return;
        new AlertDialog.Builder(getContext())
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out", (dialog, which) -> logout())
                .setNegativeButton("Cancel", null)
                .show();
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