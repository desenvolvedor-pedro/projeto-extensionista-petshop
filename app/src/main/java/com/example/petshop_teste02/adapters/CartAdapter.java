package com.example.petshop_teste02.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.example.petshop_teste02.R;
import com.example.petshop_teste02.models.Product;

import java.util.HashMap;
import java.util.Map;

public class CartAdapter extends FirebaseRecyclerAdapter<Product, CartAdapter.CartViewHolder> {

    private final Context context;
    private final DatabaseReference cartRef;
    private final Map<String, Boolean> processingItems = new HashMap<>();
    private RecyclerView recyclerView;

    public CartAdapter(@NonNull FirebaseRecyclerOptions<Product> options, Context context) {
        super(options);
        this.context = context;
        this.cartRef = FirebaseDatabase.getInstance().getReference("carts/current_cart");
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Product product) {
        String itemKey = getRef(position).getKey();
        if (itemKey == null) return;

        holder.setEnabled(!processingItems.containsKey(itemKey));
        holder.bind(product, itemKey);
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    public void cleanup() {
        for (int i = 0; i < getItemCount(); i++) {
            CartViewHolder holder = (CartViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            if (holder != null) {
                holder.clearImage();
            }
        }
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImage;
        private final TextView productName, productPrice, productQuantity;
        private final ImageButton btnRemove;
        private final Button btnDecrease, btnIncrease;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            btnRemove = itemView.findViewById(R.id.btn_remove);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
        }

        public void bind(Product product, String itemKey) {
            productName.setText(product.getName());
            productPrice.setText(String.format("R$ %.2f", product.getPrice()));
            productQuantity.setText("Qtd: " + product.getQuantity());

            try {
                if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                    Glide.with(itemView.getContext())
                            .load(product.getImageUrl())
                            .placeholder(R.drawable.ic_placeholder)
                            .error(R.drawable.ic_placeholder)
                            .into(productImage);
                } else {
                    productImage.setImageResource(R.drawable.ic_placeholder);
                }
            } catch (Exception e) {
                productImage.setImageResource(R.drawable.ic_placeholder);
            }

            btnIncrease.setOnClickListener(v -> updateQuantity(itemKey, 1));
            btnDecrease.setOnClickListener(v -> updateQuantity(itemKey, -1));
            btnRemove.setOnClickListener(v -> removeItem(itemKey));
        }

        public void clearImage() {
            Glide.with(itemView.getContext()).clear(productImage);
            productImage.setImageResource(R.drawable.ic_placeholder);
        }

        public void setEnabled(boolean enabled) {
            btnIncrease.setEnabled(enabled);
            btnDecrease.setEnabled(enabled);
            btnRemove.setEnabled(enabled);
        }

        private void updateQuantity(String itemKey, int change) {
            DatabaseReference itemRef = FirebaseDatabase.getInstance()
                    .getReference("carts/current_cart")
                    .child(itemKey);

            itemRef.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                    Product product = currentData.getValue(Product.class);
                    if (product == null) {
                        return Transaction.success(currentData);
                    }

                    int newQuantity = product.getQuantity() + change;
                    if (newQuantity <= 0) {
                        return Transaction.success(null);
                    }

                    product.setQuantity(newQuantity);
                    currentData.setValue(product);
                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                    if (error != null) {
                        Log.e("Cart", "Transaction error: " + error.getMessage());
                    }
                }
            });
        }

        private void removeItem(String itemKey) {
            FirebaseDatabase.getInstance()
                    .getReference("carts/current_cart")
                    .child(itemKey)
                    .removeValue()
                    .addOnFailureListener(e -> {
                        Log.e("Cart", "Remove error: " + e.getMessage());
                    });
        }
    }
}