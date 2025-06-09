package com.example.petshop_teste02.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshop_teste02.adapters.ProductAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.petshop_teste02.R;
import com.example.petshop_teste02.models.Product;



import java.util.HashMap;
import java.util.Map;

public class CategoryActivity extends AppCompatActivity implements ProductAdapter.ProductClickListener {

    private ProductAdapter adapter;
    private DatabaseReference productsRef;
    private RecyclerView recyclerView;

    // Mapeamento simples das categorias para português
    private static final Map<String, String> CATEGORY_NAMES = new HashMap<String, String>() {{
        put("food", "Ração");
        put("toys", "Brinquedos");
        put("hygiene", "Higiene");
        put("accessories", "Acessórios");
        // Adicione outras categorias conforme necessário
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Configuração da Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String categoryKey = getIntent().getStringExtra("category");
        String categoryName = getCategoryNameInPortuguese(categoryKey);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(categoryName);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Configuração do Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        productsRef = firebaseDatabase.getReference("products").child(categoryKey);

        // Configuração do RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
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
                    Log.e("CategoryActivity", "Layout error: " + e.getMessage());
                }
            }
        };
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(null);

        FirebaseRecyclerOptions<Product> options = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(productsRef, Product.class)
                .build();

        adapter = new ProductAdapter(options, this, this);
        recyclerView.setAdapter(adapter);

        // Configuração do FAB
        FloatingActionButton fabCart = findViewById(R.id.fab_cart);
        fabCart.setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));
    }

    private String getCategoryNameInPortuguese(String categoryKey) {
        // Retorna o nome traduzido ou a própria chave se não encontrar tradução
        return CATEGORY_NAMES.getOrDefault(categoryKey, categoryKey);
    }

    @Override
    public void onProductClick(Product product) {
        startActivity(new Intent(this, ProductDetailActivity.class)
                .putExtra("product", product));
    }

    @Override
    public void onAddToCartClick(Product product) {
        // Pode ser deixado vazio ou adicionar lógica adicional se necessário
        Toast.makeText(this, product.getName() + " adicionado ao carrinho", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }
}