package com.example.mealorderapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mealorderapp.R;

public class MainActivity extends AppCompatActivity {

    private Button btnManageDishes;
    private Button btnCreateOrder;
    private Button btnViewOrders;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // drawer-free layout

        btnManageDishes = findViewById(R.id.btnManageDishes);
        btnCreateOrder  = findViewById(R.id.btnCreateOrder);
        btnViewOrders   = findViewById(R.id.btnViewOrders);
        btnLogout       = findViewById(R.id.btnLogout);

        if (btnManageDishes != null) {
            btnManageDishes.setOnClickListener(v ->
                    startActivity(new Intent(this, DishManagementActivity.class)));
        }

        if (btnCreateOrder != null) {
            btnCreateOrder.setOnClickListener(v ->
                    startActivity(new Intent(this, CreateEditOrderActivity.class)));
        }

        if (btnViewOrders != null) {
            btnViewOrders.setOnClickListener(v ->
                    startActivity(new Intent(this, ViewOrdersActivity.class)));
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            });
        }
    }
}
