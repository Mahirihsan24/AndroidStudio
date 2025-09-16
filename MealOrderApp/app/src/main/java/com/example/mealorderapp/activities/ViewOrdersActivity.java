package com.example.mealorderapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.mealorderapp.R;
import com.example.mealorderapp.fragments.OrderListFragment;

public class ViewOrdersActivity extends AppCompatActivity {
    private Button btnMarkDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders);

        // Load order list fragment
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new OrderListFragment());
            transaction.commit();
        }
    }
}