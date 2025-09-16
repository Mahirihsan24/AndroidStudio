package com.example.mealorderapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.mealorderapp.R;
import com.example.mealorderapp.fragments.DishListFragment;

public class DishManagementActivity extends AppCompatActivity {
    private Button btnAddDish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_management);

        btnAddDish = findViewById(R.id.btnAddDish);

        // Load dish list fragment
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new DishListFragment());
            transaction.commit();
        }

        btnAddDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DishManagementActivity.this, AddEditDishActivity.class));
            }
        });
    }
}