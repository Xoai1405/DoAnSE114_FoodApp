package com.example.myfoodapp.Activity;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.myfoodapp.databinding.ActivityVoucherBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import Adapter.VoucherAdapter;
import Domain.Voucher;

public class VoucherActivity extends BaseActivity {
    ActivityVoucherBinding binding;
    private ArrayList<Voucher> voucherList = new ArrayList<>();
    private VoucherAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVoucherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v -> finish());

        setupRecyclerView();
        loadUserVouchers();
    }

    private void setupRecyclerView() {
        binding.voucherRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VoucherAdapter(voucherList, this);
        binding.voucherRecyclerView.setAdapter(adapter);
    }

    private void loadUserVouchers() {
        if (mAuth.getCurrentUser() == null) return;
        String currentUserId = mAuth.getCurrentUser().getUid();

        binding.progressBar.setVisibility(View.VISIBLE);
        DatabaseReference userVouchRef = database.getReference("UserVouchers");
        DatabaseReference vouchRef = database.getReference("Vouchers");

        userVouchRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                voucherList.clear(); // Xóa list cũ trước khi nạp mới

                if (snapshot.exists()) {
                    ArrayList<String> userVoucherIds = new ArrayList<>();
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        String userIdOnDb = issue.child("userId").getValue(String.class);
                        String status = issue.child("status").getValue(String.class);
                        if (userIdOnDb != null && userIdOnDb.equals(currentUserId) && "unused".equals(status)) {
                            String voucherId = issue.child("voucherId").getValue(String.class);
                            if (voucherId != null) {
                                userVoucherIds.add(voucherId);
                            }
                        }
                    }

                    // SỬA LỖI 1: Nếu user không có voucher nào, tắt progress và cập nhật giao diện trống ngay
                    if (userVoucherIds.isEmpty()) {
                        binding.progressBar.setVisibility(View.GONE);
                        checkEmptyState();
                        return;
                    }

                    vouchRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapVouch) {
                            if (snapVouch.exists()) {
                                for (DataSnapshot issueVouch : snapVouch.getChildren()) {
                                    Voucher voucher = issueVouch.getValue(Voucher.class);
                                    if (voucher != null && userVoucherIds.contains(voucher.getId())) {
                                        voucherList.add(voucher);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                            // Đưa ra ngoài vòng if(snapVouch.exists) để luôn kiểm tra giao diện và tắt progress
                            binding.progressBar.setVisibility(View.GONE);
                            checkEmptyState();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            binding.progressBar.setVisibility(View.GONE);
                            checkEmptyState();
                        }
                    });
                } else {
                    // SỬA LỖI 2: Node UserVouchers hoàn toàn trống trên DB
                    binding.progressBar.setVisibility(View.GONE);
                    checkEmptyState();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progressBar.setVisibility(View.GONE);
                checkEmptyState();
            }
        });
    }

    // Hàm hỗ trợ kiểm tra danh sách độc lập, viết rất gọn và không bị sót trường hợp
    private void checkEmptyState() {
        if (voucherList.isEmpty()) {
            binding.emptyVoucherText.setVisibility(View.VISIBLE);
        } else {
            binding.emptyVoucherText.setVisibility(View.GONE);
        }
    }
}