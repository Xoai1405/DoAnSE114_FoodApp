package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import Domain.Foods;
import Helper.ChangeNumberItemsListener;
import Helper.Cloud_Service;
import Helper.ManagmentCart;

import com.example.myfoodapp.Activity.CartActivity;
import com.example.myfoodapp.R;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.viewholder> {

    private ArrayList<Foods> list;
    private ManagmentCart managmentCart;

    private ChangeNumberItemsListener changeNumberItemsListener;

    public CartAdapter(ArrayList<Foods> list, Context context, ChangeNumberItemsListener listener)
    {
        this.list = list;
        managmentCart = new ManagmentCart(context);
        this.changeNumberItemsListener = listener;
    }

    @NonNull
    @Override
    public CartAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false);
        return new viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.viewholder holder, int position) {
        holder.FoodTitle.setText(list.get(position).getTitle());
        double price = list.get(position).getPrice();
        holder.FoodDefaultPrice.setText("$" + price);
        int numb = list.get(position).getNumberInCart();
        holder.CurrentFoodNumber.setText("" + numb);
        holder.FoodTotalPrice.setText("$" + Math.round(numb * price*100.0)/100.0);
        Cloud_Service.loadCloudinaryImageWithGlide(list.get(position).getTitle(), holder.FoodAvatar);
        holder.plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                managmentCart.plusNumberItem(list, position, () ->
                        {
                            notifyDataSetChanged();
                            changeNumberItemsListener.change();
                        }
                        );
            }
        });
        holder.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                managmentCart.minusNumberItem(list, position, () ->
                        {
                            notifyDataSetChanged();
                            changeNumberItemsListener.change();
                        }
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder
    {
        TextView FoodTitle;
        TextView FoodDefaultPrice;
        TextView FoodTotalPrice;
        TextView plusBtn, minusBtn;
        TextView CurrentFoodNumber;

        ImageView FoodAvatar;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            FoodTitle = itemView.findViewById(R.id.TitleText);
            FoodDefaultPrice = itemView.findViewById(R.id.DefaultPriceText);
            FoodTotalPrice = itemView.findViewById(R.id.TotalMoneyText);
            plusBtn = itemView.findViewById(R.id.PlusBtn);
            minusBtn = itemView.findViewById(R.id.MinusBtn);
            CurrentFoodNumber = itemView.findViewById(R.id.CurrentNumText);
            FoodAvatar = itemView.findViewById(R.id.FoodAvatar);
        }
    }
}
