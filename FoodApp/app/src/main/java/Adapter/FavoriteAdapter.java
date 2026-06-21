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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

import Domain.Foods;
import Helper.Cloud_Service;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {
    private ArrayList<Foods> items;
    private Context context;

    // Interface xử lý reload động chống crash
    public interface OnFavoriteChangeListener {
        void onFavoriteChanged();
    }

    private OnFavoriteChangeListener listener;

    public FavoriteAdapter(ArrayList<Foods> items, OnFavoriteChangeListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_list_food, parent, false);

        ViewGroup.LayoutParams layoutParams = inflate.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        inflate.setLayoutParams(layoutParams);

        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.ViewHolder holder, int position) {
        Foods foodItem = items.get(position);

        holder.titleTxt.setText(foodItem.getTitle());
        holder.priceTxt.setText("$" + foodItem.getPrice());
        holder.rateTxt.setText("" + foodItem.getStar());
        holder.timeTxt.setText(foodItem.getTimeValue() + " min");

        Cloud_Service.loadCloudinaryImageWithGlide(foodItem.getTitle(), holder.img);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", foodItem);
            context.startActivity(intent);
        });

        // Màn hình yêu thích -> Mặc định tô đỏ trái tim
        if (holder.favBtn != null) {
            holder.favBtn.setSelected(true);

            holder.favBtn.setOnClickListener(v -> {
                int clickedPosition = holder.getAdapterPosition();
                if (clickedPosition == RecyclerView.NO_POSITION) return;

                Foods currentFood = items.get(clickedPosition);
                String currentFoodID = String.valueOf(currentFood.getID()).trim();

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String currentUserID = currentUser.getUid();

                    DatabaseReference clickFavRef = FirebaseDatabase.getInstance().getReference("Favorites")
                            .child(currentUserID)
                            .child(currentFoodID);

                    clickFavRef.removeValue().addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Đã xóa khỏi yêu thích!", Toast.LENGTH_SHORT).show();
                        // Kích hoạt làm mới nếu listener không null
                        if (listener != null) {
                            listener.onFavoriteChanged();
                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(context, "Lỗi kết nối firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img, favBtn;
        TextView titleTxt, priceTxt, rateTxt, timeTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            rateTxt = itemView.findViewById(R.id.rateTxt);
            timeTxt = itemView.findViewById(R.id.timeTxt);
            img = itemView.findViewById(R.id.img);
            favBtn = itemView.findViewById(R.id.favBtn);
        }
    }
}