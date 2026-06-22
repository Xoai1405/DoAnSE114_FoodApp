package Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfoodapp.Activity.DetailActivity;
import com.example.myfoodapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Domain.Foods;
import Helper.Cloud_Service;

public class BestFoodsAdapter extends RecyclerView.Adapter<BestFoodsAdapter.ViewHolder> {
    ArrayList<Foods> items;
    Context context;

    public BestFoodsAdapter(ArrayList<Foods> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public BestFoodsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_best_deal, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull BestFoodsAdapter.ViewHolder holder, int position) {
        Foods foodItem = items.get(position);

        holder.titleTxt.setText(foodItem.getTitle());
        holder.priceTxt.setText("$" + foodItem.getPrice());
        holder.starTxt.setText("" + foodItem.getStar());
        holder.timeTxt.setText(foodItem.getTimeValue() + " min");

        Cloud_Service.loadCloudinaryImageWithGlide(foodItem.getTitle(), holder.pic);

        // Click vào món ăn để xem chi tiết
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", foodItem);
            context.startActivity(intent);
        });

        // ==========================================
        // KHU VỰC ĐÃ FIX: XỬ LÝ NÚT TRÁI TIM (FAVORITES)
        // ==========================================
        /*FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserID = currentUser.getUid();

            // Ép kiểu ID về String chuẩn và loại bỏ khoảng trắng thừa
            String foodID = String.valueOf(foodItem.getID()).trim();

            DatabaseReference favRef = FirebaseDatabase.getInstance().getReference("Favorites")
                    .child(currentUserID)
                    .child(foodID);

            // 1. Kiểm tra trạng thái khi hiển thị món ăn ban đầu (ĐÃ FIX XUÔI LOGIC)
            favRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        holder.favBtn.setSelected(true);  // ĐÃ SỬA: Có tồn tại trên DB -> ĐÃ THÍCH (Màu đỏ)
                    } else {
                        holder.favBtn.setSelected(false); // ĐÃ SỬA: Không tồn tại trên DB -> CHƯA THÍCH (Màu trắng)
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });

            // 2. Click vào nút trái tim xử lý động theo vị trí click thực tế
            holder.favBtn.setOnClickListener(v -> {
                int clickedPosition = holder.getAdapterPosition();
                if (clickedPosition == RecyclerView.NO_POSITION) return;

                // Bốc chính xác đối tượng món ăn tại thời điểm click
                Foods currentFood = items.get(clickedPosition);
                String currentFoodID = String.valueOf(currentFood.getID()).trim();

                DatabaseReference clickFavRef = FirebaseDatabase.getInstance().getReference("Favorites")
                        .child(currentUserID)
                        .child(currentFoodID);

                // Lấy trạng thái hiện tại ngay lúc click
                boolean isCurrentlySelected = holder.favBtn.isSelected();

                if (!isCurrentlySelected) {
                    // NẾU CHƯA THÍCH (Đang trắng) -> Tiến hành Thích và Lưu lên Firebase
                    clickFavRef.setValue(true).addOnSuccessListener(aVoid -> {
                        holder.favBtn.setSelected(true); // Đổi trạng thái sang đỏ
                        Toast.makeText(context, "Đã thêm vào yêu thích!", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                } else {
                    // NẾU ĐÃ THÍCH (Đang đỏ) -> Tiến hành Bỏ thích và Xóa khỏi Firebase
                    clickFavRef.removeValue().addOnSuccessListener(aVoid -> {
                        holder.favBtn.setSelected(false); // Đổi trạng thái về trắng
                        Toast.makeText(context, "Đã xóa khỏi yêu thích!", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }*/
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView titleTxt, priceTxt, starTxt, timeTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            starTxt = itemView.findViewById(R.id.starTxt);
            timeTxt = itemView.findViewById(R.id.timeTxt);
            pic = itemView.findViewById(R.id.pic);
        }
    }
}