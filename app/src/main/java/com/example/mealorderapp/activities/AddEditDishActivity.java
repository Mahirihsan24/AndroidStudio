package com.example.mealorderapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mealorderapp.R;
import com.example.mealorderapp.database.DatabaseHelper;
import com.example.mealorderapp.models.Dish;
import com.example.mealorderapp.utils.ImageUtils;

import java.io.IOException;

public class AddEditDishActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    private EditText etDishId, etDishName, etIngredients, etPrice;
    private Spinner spinnerDishType;
    private ImageView ivDishImage;
    private Button btnSelectImage, btnSave, btnCancel;
    private DatabaseHelper databaseHelper;
    private Dish currentDish;
    private Bitmap selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_dish);

        databaseHelper = new DatabaseHelper(this);
        initViews();
        setupSpinner();

        // Check if we're editing an existing dish
        String dishId = getIntent().getStringExtra("DISH_ID");
        if (dishId != null) {
            currentDish = databaseHelper.getDish(dishId);
            if (currentDish != null) {
                populateFields();
            }
        } else {
            currentDish = new Dish();
        }

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initViews() {
        etDishId = findViewById(R.id.etDishId);
        etDishName = findViewById(R.id.etDishName);
        etIngredients = findViewById(R.id.etIngredients);
        etPrice = findViewById(R.id.etPrice);
        spinnerDishType = findViewById(R.id.spinnerDishType);
        ivDishImage = findViewById(R.id.ivDishImage);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.dish_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDishType.setAdapter(adapter);
    }

    private void populateFields() {
        etDishId.setText(currentDish.getId());
        etDishId.setEnabled(false); // Don't allow editing ID
        etDishName.setText(currentDish.getName());
        etIngredients.setText(currentDish.getIngredients());
        etPrice.setText(String.valueOf(currentDish.getPrice()));

        // Set spinner selection
        String[] dishTypes = getResources().getStringArray(R.array.dish_types);
        for (int i = 0; i < dishTypes.length; i++) {
            if (dishTypes[i].equalsIgnoreCase(currentDish.getType())) {
                spinnerDishType.setSelection(i);
                break;
            }
        }

        // Load image if exists
        if (currentDish.getImageBitmap() != null) {
            ivDishImage.setImageBitmap(currentDish.getImageBitmap());
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                Uri imageUri = data.getData();
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    ivDishImage.setImageBitmap(selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == CAMERA_REQUEST && data != null) {
                selectedImage = (Bitmap) data.getExtras().get("data");
                ivDishImage.setImageBitmap(selectedImage);
            }
        }
    }

    private void saveDish() {
        String dishId = etDishId.getText().toString().trim();
        String dishName = etDishName.getText().toString().trim();
        String ingredients = etIngredients.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String dishType = spinnerDishType.getSelectedItem().toString();

        if (dishId.isEmpty() || dishName.isEmpty() || ingredients.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update or create dish
        if (currentDish == null) {
            currentDish = new Dish();
        }

        currentDish.setId(dishId);
        currentDish.setName(dishName);
        currentDish.setType(dishType);
        currentDish.setIngredients(ingredients);
        currentDish.setPrice(price);

        if (selectedImage != null) {
            currentDish.setImageBitmap(selectedImage);
        }

        long result;
        if (getIntent().hasExtra("DISH_ID")) {
            // Updating existing dish
            result = databaseHelper.updateDish(currentDish);
        } else {
            // Adding new dish
            // Check if dish ID already exists
            if (databaseHelper.getDish(dishId) != null) {
                Toast.makeText(this, "Dish ID already exists", Toast.LENGTH_SHORT).show();
                return;
            }
            result = databaseHelper.addDish(currentDish);
        }

        if (result != -1) {
            Toast.makeText(this, "Dish saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving dish", Toast.LENGTH_SHORT).show();
        }
    }
}