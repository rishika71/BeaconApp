package com.example.beaconapp;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beaconapp.databinding.ProductViewBinding;
import com.example.beaconapp.models.Product;
import com.example.beaconapp.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.UViewHolder>{

    ArrayList<Product> products;
    ProductViewBinding binding;
    ViewGroup parent;
    User user;

    public ProductAdapter(User user, ArrayList<Product> products) {
        this.products = products;
        this.user = user;
    }

    @NonNull
    @Override
    public UViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ProductViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        this.parent = parent;
        return new UViewHolder(binding);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull UViewHolder holder, int position) {

        Product product = products.get(position);
        holder.binding.productName.setText(product.getName());
        holder.binding.productPrice.setText("$" + product.getPrice());
        holder.binding.productPrice.setPaintFlags(holder.binding.productPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.binding.textView4.setText("$" + String.format("%.2f", product.getUpdatedPrice()));
        holder.binding.textView4.setTextColor(0xFF50C878);

        holder.binding.productDiscount.setText("Discount " + product.getDiscount() + "%");
        holder.binding.textView11.setText(product.getRegion());

        Picasso.get()
                .load(product.getPhoto())
                .into(holder.binding.productImage);

    }
    @Override
    public int getItemCount() {
        return this.products.size();
    }

    public static class UViewHolder extends RecyclerView.ViewHolder {

        ProductViewBinding binding;

        public UViewHolder(@NonNull ProductViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}
