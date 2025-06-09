package com.example.petshop_teste02.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.example.petshop_teste02.R;
import com.example.petshop_teste02.models.Product;

import java.util.HashMap;
import java.util.Map;

public class ProductAdapter extends FirebaseRecyclerAdapter<Product, ProductAdapter.ProductViewHolder> {

    private final Context context;
    private final ProductClickListener listener;
    private final DatabaseReference cartRef;
    private final Map<String, Boolean> processingItems = new HashMap<>();
    private Toast currentToast;

    public ProductAdapter(@NonNull FirebaseRecyclerOptions<Product> options,
                          Context context,
                          ProductClickListener listener) {
        super(options);
        this.context = context;
        this.listener = listener;
        this.cartRef = FirebaseDatabase.getInstance().getReference("carts/current_cart");
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return getRef(position).getKey().hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product product) {
        String productKey = getRef(position).getKey();
        if (product.getId() == null || product.getId().isEmpty()) {
            product.setId(productKey);
        }

        holder.bind(product);

        holder.btnAddToCart.setOnClickListener(v -> {
            if (productKey != null && !processingItems.containsKey(productKey)) {
                processingItems.put(productKey, true);
                holder.btnAddToCart.setEnabled(false);
                addToCart(product);
            }
        });
    }

    private void addToCart(Product product) {
        String productId = product.getId();
        if (productId == null) {
            processingItems.remove(productId);
            showSingleToast("Erro: Produto sem ID válido");
            return;
        }

        DatabaseReference cartItemRef = cartRef.child(productId);

        cartItemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    updateItemQuantity(cartItemRef, productId);
                } else {
                    addNewItem(cartItemRef, product, productId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                processingItems.remove(productId);
                showSingleToast("Erro: " + error.getMessage());
            }
        });
    }

    private void updateItemQuantity(DatabaseReference itemRef, String productId) {
        itemRef.child("quantity").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Integer currentQuantity = currentData.getValue(Integer.class);
                if (currentQuantity == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue(currentQuantity + 1);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                processingItems.remove(productId);
                if (error != null) {
                    showSingleToast("Erro ao atualizar quantidade");
                } else if (committed) {
                    showSingleToast("Quantidade atualizada");
                }
            }
        });
    }

    private void addNewItem(DatabaseReference itemRef, Product product, String productId) {
        Map<String, Object> itemValues = new HashMap<>();
        itemValues.put("id", productId);
        itemValues.put("name", product.getName());
        itemValues.put("price", product.getPrice());
        itemValues.put("imageUrl", product.getImageUrl());
        itemValues.put("category", product.getCategory());
        itemValues.put("quantity", 1);

        itemRef.setValue(itemValues)
                .addOnSuccessListener(aVoid -> {
                    showSingleToast(product.getName() + " adicionado ao carrinho");
                    if (listener != null) {
                        listener.onAddToCartClick(product);
                    }
                })
                .addOnFailureListener(e -> showSingleToast("Erro ao adicionar ao carrinho"))
                .addOnCompleteListener(task -> processingItems.remove(productId));
    }

    private void showSingleToast(String message) {
        if (currentToast != null) {
            currentToast.cancel();
        }
        currentToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        currentToast.show();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView nameTextView;
        private final TextView priceTextView;
        private final MaterialButton btnAddToCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.product_image);
            nameTextView = itemView.findViewById(R.id.product_name);
            priceTextView = itemView.findViewById(R.id.product_price);
            btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);
        }

        public void bind(Product product) {
            try {
                nameTextView.setText(product.getName());
                priceTextView.setText(String.format("R$ %.2f", product.getPrice()));

                if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                    Glide.with(itemView.getContext())
                            .load(product.getImageUrl())
                            .placeholder(R.drawable.ic_placeholder)
                            .error(R.drawable.ic_placeholder)
                            .into(imageView);
                } else {
                    imageView.setImageResource(R.drawable.ic_placeholder);
                }
            } catch (Exception e) {
                Log.e("ProductAdapter", "Error binding product", e);
                imageView.setImageResource(R.drawable.ic_placeholder);
            }

            btnAddToCart.setEnabled(true);
        }
    }

    public interface ProductClickListener {
        void onProductClick(Product product);
        void onAddToCartClick(Product product);
    }
}