package com.example.mealorderapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.mealorderapp.R;
import com.example.mealorderapp.fragments.OrderListFragment;

public class OrderManagementActivity extends AppCompatActivity {
    private Button btnCreateOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);

        btnCreateOrder = findViewById(R.id.btnCreateOrder);

        // Load order list fragment
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new OrderListFragment());
            transaction.commit();
        }

        btnCreateOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderManagementActivity.this, CreateEditOrderActivity.class));
            }
        });
    }
}