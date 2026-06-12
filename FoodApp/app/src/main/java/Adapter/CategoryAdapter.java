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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfoodapp.Activity.ListFoodsActivity;
import com.example.myfoodapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import Domain.Category;
import Domain.Foods;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    ArrayList<Category> items;
    Context context;

    public CategoryAdapter(ArrayList<Category> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View inflate= LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_category,parent,false);

        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        holder.titleTxt.setText(items.get(position).getName());
        switch (position)
        {
            case 0:
                holder.pic.setBackgroundResource(R.drawable.cat_1_background);
                break;
            case 1:
                holder.pic.setBackgroundResource(R.drawable.cat_0_background);
                break;
                case 2:
                holder.pic.setBackgroundResource(R.drawable.cat_2_background);
                break;
            case 3:
                holder.pic.setBackgroundResource(R.drawable.cat_3_background);
                break;
            case 4:
                holder.pic.setBackgroundResource(R.drawable.cat_4_background);
                break;
            case 5:
                holder.pic.setBackgroundResource(R.drawable.cat_5_background);
                break;
            case 6:
                holder.pic.setBackgroundResource(R.drawable.cat_6_background);
                break;
            case 7:
                holder.pic.setBackgroundResource(R.drawable.cat_7_background);
                break;
        }
        int drawableResourceId = context.getResources().getIdentifier(
                items.get(position).getImagePath(),
                "drawable",
                context.getPackageName()
        );

        Drawable res = ContextCompat.getDrawable(context, drawableResourceId);
        holder.pic.setImageDrawable(res);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ListFoodsActivity.class);
                intent.putExtra("CategoryId",items.get(position).getId());
                intent.putExtra("CategoryName",items.get(position).getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {

        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView pic;
        TextView titleTxt;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            titleTxt=itemView.findViewById(R.id.catNameTxt);
            pic=itemView.findViewById(R.id.imgCat);
        }
    }
}
