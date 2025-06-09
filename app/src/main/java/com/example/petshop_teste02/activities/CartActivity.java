package com.example.petshop_teste02.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshop_teste02.adapters.CartAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.petshop_teste02.R;
import com.example.petshop_teste02.models.Product;

public class CartActivity extends AppCompatActivity {

    private CartAdapter adapter;
    private DatabaseReference cartRef;
    private TextView tvTotal;
    private boolean isProcessingCheckout = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        setupToolbar();
        setupFirebase();
        setupRecyclerView();
        setupTotalListener();
        setupCheckoutButton();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Carrinho");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupFirebase() {
        cartRef = FirebaseDatabase.getInstance()
                .getReference("carts/current_cart");
        cartRef.keepSynced(true);
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }

            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (IndexOutOfBoundsException e) {
                    Log.e("CartActivity", "Layout error: " + e.getMessage());
                }
            }
        };

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(null);

        FirebaseRecyclerOptions<Product> options = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(cartRef.orderByChild("id"), Product.class)
                .build();

        adapter = new CartAdapter(options, this);
        recyclerView.setAdapter(adapter);
    }

    private void setupTotalListener() {
        tvTotal = findViewById(R.id.tv_total);
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double total = 0;
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Product product = itemSnapshot.getValue(Product.class);
                    if (product != null) {
                        total += product.getPrice() * product.getQuantity();
                    }
                }
                tvTotal.setText(String.format("Total: R$ %.2f", total));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CartActivity.this, "Erro ao calcular total", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCheckoutButton() {
        Button btnCheckout = findViewById(R.id.btn_checkout);
        btnCheckout.setOnClickListener(v -> {
            if (!isProcessingCheckout) {
                checkout();
            }
        });
    }

    private void checkout() {
        isProcessingCheckout = true;
        adapter.stopListening();

        if (adapter != null) {
            adapter.cleanup();
        }

        cartRef.removeValue()
                .addOnCompleteListener(task -> {
                    isProcessingCheckout = false;

                    runOnUiThread(() -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Compra finalizada!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            adapter.startListening();
                            Toast.makeText(this, "Erro: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    });
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null && !isProcessingCheckout) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.cleanup();
        }
    }
}