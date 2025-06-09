package com.example.petshop_teste02.models;

import java.io.Serializable;
import java.util.Objects;

public class Product implements Serializable {
    private String id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private String category;
    private int quantity;

    // Construtor vazio necessário para o Firebase
    public Product() {
        this.quantity = 1; // Garante que quantity nunca seja null
    }

    public Product(String name, String description, double price, String imageUrl, String category) {
        this(); // Chama o construtor vazio para inicializar quantity
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl != null ? imageUrl : ""; // Garante nunca retornar null
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category != null ? category : ""; // Garante nunca retornar null
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = Math.max(quantity, 0); // Garante que quantity nunca seja negativo
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id != null && id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Importante para funcionar com HashMap
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
