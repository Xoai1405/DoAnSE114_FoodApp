package Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myfoodapp.R;
import java.util.ArrayList;
import Domain.Voucher;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.ViewHolder> {
    private ArrayList<Voucher> list;
    private Context context;

    public VoucherAdapter(ArrayList<Voucher> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_voucher, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Voucher voucher = list.get(position);
        holder.descriptionTxt.setText(voucher.getDescription());
        holder.expiryTxt.setText("HSD: " + voucher.getExpiryDate() + " | Đơn tối thiểu: $" + voucher.getMinOrderValue());

        if (voucher.getDiscountPercentage() > 0) {
            holder.discountValueTxt.setText(voucher.getDiscountPercentage() + "%");
        } else {
            holder.discountValueTxt.setText("$" + (int) voucher.getMaxDiscount());
        }

        holder.useVoucherBtn.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("selectedVoucher", voucher);
            ((Activity) context).setResult(Activity.RESULT_OK, intent);
            ((Activity) context).finish();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView discountValueTxt, descriptionTxt, expiryTxt;
        Button useVoucherBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            discountValueTxt = itemView.findViewById(R.id.discountValueTxt);
            descriptionTxt = itemView.findViewById(R.id.descriptionTxt);
            expiryTxt = itemView.findViewById(R.id.expiryTxt);
            useVoucherBtn = itemView.findViewById(R.id.useVoucherBtn);
        }
    }
}