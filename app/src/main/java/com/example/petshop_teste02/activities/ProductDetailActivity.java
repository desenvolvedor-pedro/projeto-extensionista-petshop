package com.example.petshop_teste02.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.petshop_teste02.R;
import com.example.petshop_teste02.models.Product;

public class ProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // ★★★ Configuração COMPLETA da Toolbar ★★★
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationContentDescription("Voltar");
        toolbar.setNavigationOnClickListener(v -> finish());

        // ▼▼▼ SEU CÓDIGO ORIGINAL (MANTIDO INTACTO) ▼▼▼
        Product product = (Product) getIntent().getSerializableExtra("product");
        if (product == null) {
            Toast.makeText(this, "Produto não encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(product.getName());
        }

        ImageView productImage = findViewById(R.id.product_image);
        TextView productName = findViewById(R.id.product_name);
        TextView productPrice = findViewById(R.id.product_price);
        TextView productDescription = findViewById(R.id.product_description);
        Button btnBuyNow = findViewById(R.id.btn_buy_now);
        Button btnAddToCart = findViewById(R.id.btn_add_to_cart);

        productName.setText(product.getName());
        productPrice.setText(String.format("R$ %.2f", product.getPrice()));
        productDescription.setText(product.getDescription());

        Glide.with(this)
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_placeholder)
                .into(productImage);

        btnBuyNow.setOnClickListener(v -> {
            Toast.makeText(this, "Compra realizada: " + product.getName(), Toast.LENGTH_SHORT).show();
        });

        btnAddToCart.setOnClickListener(v -> {
            Toast.makeText(this, "Adicionado ao carrinho: " + product.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}