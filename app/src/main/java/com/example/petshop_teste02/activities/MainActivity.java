package com.example.petshop_teste02.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petshop_teste02.Cadastro;
import com.example.petshop_teste02.FormLogin;
import com.example.petshop_teste02.TelaDeslogar;
import com.google.firebase.database.FirebaseDatabase;
import com.example.petshop_teste02.R;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configuração inicial do Firebase
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Button btnFood = findViewById(R.id.btn_food);
        Button btnToys = findViewById(R.id.btn_toys);
        Button btnHygiene = findViewById(R.id.btn_hygiene);
        Button btnAddProduct = findViewById(R.id.btn_add_product);

        btnFood.setOnClickListener(v -> openCategory("food"));
        btnToys.setOnClickListener(v -> openCategory("toys"));
        btnHygiene.setOnClickListener(v -> openCategory("hygiene"));
        btnAddProduct.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddProductActivity.class)));
    }

    private void openCategory(String category) {
        Intent intent = new Intent(this, CategoryActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    public void trocar_Tela_Deslogar(View v){
        Intent intent = new Intent(this, TelaDeslogar.class);
        startActivity(intent);
    }
}