package com.example.petshop_teste02;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Configuração do Firebase
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
