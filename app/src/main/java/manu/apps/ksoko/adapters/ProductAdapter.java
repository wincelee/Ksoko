package manu.apps.ksoko.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import manu.apps.ksoko.R;
import manu.apps.ksoko.classes.Config;
import manu.apps.ksoko.classes.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder>{

    Context context;
    List<Product> productList;
    OnClick onClick;

    public interface OnClick {
        void onEvent(Product product, int pos);
    }

    public ProductAdapter(Context context, List<Product> productList, OnClick onClick) {
        this.context = context;
        this.productList = productList;

        this.onClick = onClick;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_product, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final Product currentProduct = productList.get(position);

        holder.tvTitle.setText(currentProduct.getName());

        holder.tvPrice.setText(String.format("%s%s%s", context.getString(R.string.usd),
                context.getString(R.string.space), Config.numberFormatter(currentProduct.getPrice())));


        Glide.with(context)
                .load(currentProduct.getPhoto())
                .fitCenter()
                .into(holder.imvProduct);

        holder.mcvProduct.setOnClickListener(v -> onClick.onEvent(currentProduct, position));

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvPrice;
        MaterialCardView mcvProduct;

        ImageView imvProduct;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            tvPrice = itemView.findViewById(R.id.tv_price);
            imvProduct = itemView.findViewById(R.id.imv_product);
            mcvProduct = itemView.findViewById(R.id.mcv_product);

        }
    }
}
