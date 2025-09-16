package com.example.mealorderapp.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class Dish {
    private String id;
    private String name;
    private String type;
    private String ingredients;
    private double price;
    private byte[] imageBytes;

    public Dish() {
    }

    public Dish(String id, String name, String type, String ingredients, double price, byte[] imageBytes) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.ingredients = ingredients;
        this.price = price;
        this.imageBytes = imageBytes;
    }

    public Bitmap getImageBitmap() {
        if (imageBytes != null) {
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        }
        return null;
    }

    public void setImageBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            imageBytes = stream.toByteArray();
        } else {
            imageBytes = null;
        }
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public byte[] getImageBytes() { return imageBytes; }
    public void setImageBytes(byte[] imageBytes) { this.imageBytes = imageBytes; }
}