package com.example.mealorderapp.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mealorderapp.R;
import com.example.mealorderapp.database.DatabaseHelper;
import com.example.mealorderapp.models.Dish;
import com.example.mealorderapp.utils.ImageUtils;

import java.io.IOException;

public class DishDetailFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    private EditText etDishId, etDishName, etIngredients, etPrice;
    private Spinner spinnerDishType;
    private ImageView ivDishImage;
    private Button btnSelectImage, btnSave, btnCancel, btnDelete;
    private DatabaseHelper databaseHelper;
    private Dish currentDish;
    private Bitmap selectedImage;

    public DishDetailFragment() {
        // Required empty public constructor
    }

    public static DishDetailFragment newInstance(String dishId) {
        DishDetailFragment fragment = new DishDetailFragment();
        Bundle args = new Bundle();
        args.putString("DISH_ID", dishId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_edit_dish, container, false);

        databaseHelper = new DatabaseHelper(getActivity());
        initViews(view);
        setupSpinner();

        // Check if we're editing an existing dish
        String dishId = getArguments().getString("DISH_ID");
        if (dishId != null) {
            currentDish = databaseHelper.getDish(dishId);
            if (currentDish != null) {
                populateFields();
                btnDelete.setVisibility(View.VISIBLE);
            }
        } else {
            currentDish = new Dish();
            btnDelete.setVisibility(View.GONE);
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
                getActivity().onBackPressed();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDish();
            }
        });

        return view;
    }

    private void initViews(View view) {
        etDishId = view.findViewById(R.id.etDishId);
        etDishName = view.findViewById(R.id.etDishName);
        etIngredients = view.findViewById(R.id.etIngredients);
        etPrice = view.findViewById(R.id.etPrice);
        spinnerDishType = view.findViewById(R.id.spinnerDishType);
        ivDishImage = view.findViewById(R.id.ivDishImage);
        btnSelectImage = view.findViewById(R.id.btnSelectImage);
        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);

        // Add delete button programmatically since it's not in the layout
        btnDelete = new Button(getActivity());
        btnDelete.setText("Delete");
        btnDelete.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        btnDelete.setTextColor(getResources().getColor(android.R.color.white));

        // Add to layout if there's a container
        if (view instanceof ViewGroup) {
            ViewGroup layout = (ViewGroup) view;
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                    ViewGroup.MarginLayoutParams.MATCH_PARENT,
                    ViewGroup.MarginLayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 16, 0, 0);
            layout.addView(btnDelete, params);
        }
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
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
        android.content.Intent intent = new android.content.Intent();
        intent.setType("image/*");
        intent.setAction(android.content.Intent.ACTION_GET_CONTENT);
        startActivityForResult(android.content.Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        getActivity();
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                Uri imageUri = data.getData();
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                    ivDishImage.setImageBitmap(selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Failed to load image", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Please enter a valid price", Toast.LENGTH_SHORT).show();
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
        if (getArguments().containsKey("DISH_ID")) {
            // Updating existing dish
            result = databaseHelper.updateDish(currentDish);
        } else {
            // Adding new dish
            // Check if dish ID already exists
            if (databaseHelper.getDish(dishId) != null) {
                Toast.makeText(getActivity(), "Dish ID already exists", Toast.LENGTH_SHORT).show();
                return;
            }
            result = databaseHelper.addDish(currentDish);
        }

        if (result != -1) {
            Toast.makeText(getActivity(), "Dish saved successfully", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        } else {
            Toast.makeText(getActivity(), "Error saving dish", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteDish() {
        new android.app.AlertDialog.Builder(getActivity())
                .setTitle("Delete Dish")
                .setMessage("Are you sure you want to delete this dish?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int result = databaseHelper.deleteDish(currentDish.getId());
                        if (result > 0) {
                            Toast.makeText(getActivity(), "Dish deleted successfully", Toast.LENGTH_SHORT).show();
                            getActivity().onBackPressed();
                        } else {
                            Toast.makeText(getActivity(), "Error deleting dish", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}