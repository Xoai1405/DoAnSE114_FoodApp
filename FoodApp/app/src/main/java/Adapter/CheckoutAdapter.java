package Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfoodapp.R;

import java.util.List;

import Domain.CartItem;
import Helper.Cloud_Service;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.ViewHolder> {

    private List<CartItem> list;

    public CheckoutAdapter(List<CartItem> list)
    {
        this.list = list;
    }

    @NonNull
    @Override
    public CheckoutAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_checkout_item, parent, false);


        return new ViewHolder(inflate);

    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutAdapter.ViewHolder holder, int position) {
        holder.FoodTitle.setText(list.get(position).getFoodDetails().getTitle());
        holder.FoodPrice.setText("$" + list.get(position).getFoodDetails().getPrice());
        holder.FoodQuantity.setText("x" + list.get(position).getQuantity());
        Cloud_Service.loadCloudinaryImageWithGlide(list.get(position).getFoodDetails().getTitle(), holder.FoodAvatar);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView FoodAvatar;
        private TextView FoodTitle, FoodQuantity, FoodPrice;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            FoodAvatar = itemView.findViewById(R.id.FoodImg);
            FoodTitle = itemView.findViewById(R.id.FoodTitleText);
            FoodQuantity = itemView.findViewById(R.id.FoodQuanText);
            FoodPrice = itemView.findViewById(R.id.FoodPriceText);
        }
    }
}
