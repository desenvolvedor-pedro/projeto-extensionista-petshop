package com.example.petshop_teste02.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.petshop_teste02.R;
import com.example.petshop_teste02.models.Product;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {

    private TextInputEditText etName, etDescription, etPrice, etImageUrl, etCategory;
    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Configuração da Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Adicionar Produto");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Inicialização dos campos
        initViews();

        productsRef = FirebaseDatabase.getInstance().getReference("products");

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        etImageUrl = findViewById(R.id.etImageUrl);
        etCategory = findViewById(R.id.etCategory);
    }

    private void saveProduct() {
        // Validação dos campos
        if (!validateFields()) return;

        // Criação do produto
        Product product = createProductFromInputs();

        // Salva no Firebase
        saveProductToFirebase(product);
    }

    private boolean validateFields() {
        String name = etName.getText().toString().trim();
        String category = etCategory.getText().toString().trim().toLowerCase();
        String priceStr = etPrice.getText().toString().trim();

        if (name.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Nome e categoria são obrigatórios", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!priceStr.isEmpty()) {
            try {
                Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Preço inválido", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private Product createProductFromInputs() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        double price = etPrice.getText().toString().trim().isEmpty() ?
                0 : Double.parseDouble(etPrice.getText().toString().trim());
        String imageUrl = etImageUrl.getText().toString().trim();
        String category = etCategory.getText().toString().trim().toLowerCase();

        if (imageUrl.isEmpty()) {
            //LINK OMITIDO
        }

        // Gera um ID único para o produto
        String productId = UUID.randomUUID().toString();

        Product product = new Product(name, description, price, imageUrl, category);
        product.setId(productId);
        product.setQuantity(1); // Quantidade padrão para novos produtos

        return product;
    }

    private void saveProductToFirebase(Product product) {
        // Usa o ID do produto como chave para evitar duplicatas
        productsRef.child(product.getCategory())
                .child(product.getId())
                .setValue(product)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Produto salvo com sucesso!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Erro ao salvar: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}