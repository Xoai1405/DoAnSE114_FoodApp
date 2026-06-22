package Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfoodapp.Activity.OrderDetailActivity;
import com.example.myfoodapp.R;

import java.util.List;

import Domain.Foods;
import Domain.Order;
import Helper.Cloud_Service;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder>{

    private List<Order> list;

    private List<Foods> listFoods;

    private Context context;
    public OrderAdapter(List<Order> list, List<Foods> listFoods)
    {
        this.list = list;
        this.listFoods = listFoods;
    }



    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_order, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {
        holder.OrderID.setText(list.get(position).getId());
        holder.OrderDate.setText("Time: " + list.get(position).getCreatedAt());
        holder.OrderPrice.setText("Total: $" + list.get(position).getOrderValue());
        String status = list.get(position).getStatus();
        holder.OrderStatus.setText(status);
        switch (status)
        {
            case "Delivered":
                holder.OrderStatus.setTextColor(Color.GREEN);
                break;
            case "Delivering":
                holder.OrderStatus.setTextColor(Color.RED);
                break;
            case "Cancelled":
                holder.OrderStatus.setTextColor(Color.BLACK);
                break;
        }
        for (Foods item: listFoods)
        {
            if (item.getID() == list.get(position).getListItems().get(0).getFoodID())
            {
                Cloud_Service.loadCloudinaryImageWithGlide(item.getTitle(), holder.OrderAvatar);
                break;
            }
        }
        holder.OrderDeatilbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("listFood", (java.util.ArrayList<Foods>) listFoods);
                intent.putExtra("Order", list.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder
    {
        private ImageView OrderAvatar;
        private TextView OrderID, OrderStatus, OrderPrice, OrderDate;

        private Button OrderDeatilbtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            OrderAvatar = itemView.findViewById(R.id.OrderImg);
            OrderID = itemView.findViewById(R.id.OrderIDText);
            OrderStatus = itemView.findViewById(R.id.OrderStatusText);
            OrderPrice = itemView.findViewById(R.id.OrderPriceText);
            OrderDeatilbtn = itemView.findViewById(R.id.OrderDetailBtn);
            OrderDate = itemView.findViewById(R.id.OrderDateText);
        }
    }
}
