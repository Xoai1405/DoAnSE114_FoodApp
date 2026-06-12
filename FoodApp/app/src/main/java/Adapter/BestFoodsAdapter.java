package Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfoodapp.Activity.DetailActivity;
import com.example.myfoodapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import Domain.Foods;

public class BestFoodsAdapter extends RecyclerView.Adapter<BestFoodsAdapter.ViewHolder> {
    ArrayList<Foods> items;
    Context context;

    public BestFoodsAdapter(ArrayList<Foods> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public BestFoodsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View inflate= LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_best_deal,parent,false);

        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull BestFoodsAdapter.ViewHolder holder, int position) {
        holder.titleTxt.setText(items.get(position).getTitle());
        holder.priceTxt.setText("$"+items.get(position).getPrice());
        holder.starTxt.setText(""+items.get(position).getStar());
        holder.timeTxt.setText(items.get(position).getTimeValue()+" min");

        try {
            InputStream inputStream = context.getAssets()
                    .open("food/" + items.get(position).getImagePath());

            Drawable drawable = Drawable.createFromStream(inputStream, null);
            holder.pic.setImageDrawable(drawable);

            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            holder.pic.setImageResource(R.drawable.logo); // ảnh mặc định nếu lỗi
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent= new Intent(context, DetailActivity.class);
            intent.putExtra("object", items.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {

        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView pic;
        TextView titleTxt,priceTxt,starTxt,timeTxt;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            titleTxt=itemView.findViewById(R.id.titleTxt);
            priceTxt=itemView.findViewById(R.id.priceTxt);
            starTxt=itemView.findViewById(R.id.starTxt);
            timeTxt=itemView.findViewById(R.id.timeTxt);
            pic=itemView.findViewById(R.id.pic);
        }
    }
}
