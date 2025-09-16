package com.example.mealorderapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealorderapp.R;
import com.example.mealorderapp.adapters.DishSelectionAdapter;
import com.example.mealorderapp.database.DatabaseHelper;
import com.example.mealorderapp.models.Dish;
import com.example.mealorderapp.models.Order;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CreateEditOrderActivity extends AppCompatActivity
        implements DishSelectionAdapter.OnDishSelectionListener {

    private TextInputEditText etOrderId, etTableNumber;
    private TextInputLayout tilTableNumber;
    private RadioGroup rgDiningOption;
    private RecyclerView rvEntries, rvMains, rvDrinks;
    private TextView tvTotalPrice;
    private Button btnSave, btnCancel;

    private DatabaseHelper db;
    private DishSelectionAdapter entryAdapter, mainAdapter, drinkAdapter;
    private final List<Dish> entryDishes = new ArrayList<>();
    private final List<Dish> mainDishes  = new ArrayList<>();
    private final List<Dish> drinkDishes = new ArrayList<>();
    private final List<Dish> selectedDishes = new ArrayList<>();
    private Order currentOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_order);

        db = new DatabaseHelper(this);
        bindViews();
        setupLists();
        loadDishes();

        // If editing
        String editId = getIntent().getStringExtra("ORDER_ID");
        if (editId != null) {
            currentOrder = db.getOrder(editId);
            if (currentOrder != null) populateFields();
        } else {
            etOrderId.setText("O" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        // Visibility logic for table number
        rgDiningOption.setOnCheckedChangeListener((group, checkedId) -> updateTableVisibility());
        updateTableVisibility(); // set initial state

        btnSave.setOnClickListener(v -> saveOrder());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void bindViews() {
        etOrderId      = findViewById(R.id.etOrderId);
        tilTableNumber = findViewById(R.id.tilTableNumber);
        etTableNumber  = findViewById(R.id.etTableNumber);
        rgDiningOption = findViewById(R.id.rgDiningOption);
        rvEntries      = findViewById(R.id.recyclerViewEntries);
        rvMains        = findViewById(R.id.recyclerViewMains);
        rvDrinks       = findViewById(R.id.recyclerViewDrinks);
        tvTotalPrice   = findViewById(R.id.tvTotalPrice);
        btnSave        = findViewById(R.id.btnSaveOrder);
        btnCancel      = findViewById(R.id.btnCancel);
    }

    private void setupLists() {
        entryAdapter = new DishSelectionAdapter(this, entryDishes, this);
        mainAdapter  = new DishSelectionAdapter(this, mainDishes,  this);
        drinkAdapter = new DishSelectionAdapter(this, drinkDishes, this);

        rvEntries.setLayoutManager(new LinearLayoutManager(this));
        rvMains.setLayoutManager(new LinearLayoutManager(this));
        rvDrinks.setLayoutManager(new LinearLayoutManager(this));

        rvEntries.setAdapter(entryAdapter);
        rvMains.setAdapter(mainAdapter);
        rvDrinks.setAdapter(drinkAdapter);

        // ScrollView + RecyclerView combo: disable nested scrolling
        rvEntries.setNestedScrollingEnabled(false);
        rvMains.setNestedScrollingEnabled(false);
        rvDrinks.setNestedScrollingEnabled(false);
    }

    private void loadDishes() {
        List<Dish> all = db.getAllDishes();
        entryDishes.clear(); mainDishes.clear(); drinkDishes.clear();

        for (Dish d : all) {
            String t = d.getType() == null ? "" : d.getType().toLowerCase();
            switch (t) {
                case "entry": entryDishes.add(d); break;
                case "main":  mainDishes.add(d);  break;
                case "drink": drinkDishes.add(d); break;
            }
        }
        entryAdapter.notifyDataSetChanged();
        mainAdapter.notifyDataSetChanged();
        drinkAdapter.notifyDataSetChanged();
    }

    private void populateFields() {
        etOrderId.setText(currentOrder.getId());
        etOrderId.setEnabled(false);

        if ("dine in".equalsIgnoreCase(currentOrder.getDiningOption())) {
            rgDiningOption.check(R.id.rbDineIn);
            etTableNumber.setText(currentOrder.getTableNumber());
        } else {
            rgDiningOption.check(R.id.rbTakeAway);
        }

        selectedDishes.clear();
        for (String dishId : currentOrder.getDishIds()) {
            Dish d = db.getDish(dishId);
            if (d != null) selectedDishes.add(d);
        }
        propagateSelection();
        calculateTotal();
    }

    private void updateTableVisibility() {
        boolean dineIn = rgDiningOption.getCheckedRadioButtonId() == R.id.rbDineIn;
        tilTableNumber.setVisibility(dineIn ? View.VISIBLE : View.GONE);
        if (!dineIn) {
            tilTableNumber.setError(null);
            etTableNumber.setText("");
        }
    }

    private void propagateSelection() {
        entryAdapter.setSelectedDishes(selectedDishes);
        mainAdapter.setSelectedDishes(selectedDishes);
        drinkAdapter.setSelectedDishes(selectedDishes);
        entryAdapter.notifyDataSetChanged();
        mainAdapter.notifyDataSetChanged();
        drinkAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDishSelected(Dish dish, boolean isSelected) {
        if (isSelected && !selectedDishes.contains(dish)) {
            selectedDishes.add(dish);
        } else if (!isSelected) {
            selectedDishes.remove(dish);
        }
        calculateTotal();
    }

    private void calculateTotal() {
        double total = 0;
        for (Dish d : selectedDishes) total += d.getPrice();
        tvTotalPrice.setText(String.format("$%.2f", total));
    }

    private void saveOrder() {
        String orderId = etOrderId.getText() == null ? "" : etOrderId.getText().toString().trim();
        boolean dineIn = rgDiningOption.getCheckedRadioButtonId() == R.id.rbDineIn;
        String diningOption = dineIn ? "Dine In" : "Take Away";
        String tableNumber = etTableNumber.getText() == null ? "" : etTableNumber.getText().toString().trim();

        if (orderId.isEmpty()) {
            Toast.makeText(this, "Order ID is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedDishes.isEmpty()) {
            Toast.makeText(this, "Please select at least one dish", Toast.LENGTH_SHORT).show();
            return;
        }
        if (dineIn && tableNumber.isEmpty()) {
            tilTableNumber.setError("Table number is required for dine in");
            etTableNumber.requestFocus();
            return;
        } else {
            tilTableNumber.setError(null);
        }

        double total = 0;
        List<String> dishIds = new ArrayList<>();
        for (Dish d : selectedDishes) {
            dishIds.add(d.getId());
            total += d.getPrice();
        }

        if (currentOrder == null) currentOrder = new Order();
        currentOrder.setId(orderId);
        currentOrder.setDiningOption(diningOption);
        currentOrder.setTableNumber(tableNumber);
        currentOrder.setDishIds(dishIds);
        currentOrder.setTotalPrice(total);
        currentOrder.setOrderTime(new Date());

        long result;
        if (getIntent().hasExtra("ORDER_ID")) {
            result = db.updateOrder(currentOrder);
        } else {
            if (db.getOrder(orderId) != null) {
                Toast.makeText(this, "Order ID already exists", Toast.LENGTH_SHORT).show();
                return;
            }
            result = db.addOrder(currentOrder);
        }

        if (result != -1) {
            Toast.makeText(this, "Order saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving order", Toast.LENGTH_SHORT).show();
        }
    }
}
