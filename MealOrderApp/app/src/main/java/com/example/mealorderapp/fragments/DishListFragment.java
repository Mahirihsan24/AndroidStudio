package com.example.mealorderapp.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;


import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealorderapp.R;
import com.example.mealorderapp.activities.AddEditDishActivity;
import com.example.mealorderapp.adapters.DishAdapter;
import com.example.mealorderapp.database.DatabaseHelper;
import com.example.mealorderapp.models.Dish;

import java.util.ArrayList;
import java.util.List;

public class DishListFragment extends Fragment implements DishAdapter.OnDishClickListener {
    private RecyclerView recyclerView;
    private DishAdapter adapter;
    private List<Dish> dishList;
    private DatabaseHelper databaseHelper;
    private Button btnDeleteSelected;
    private boolean isMultiSelectMode = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dish_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        btnDeleteSelected = view.findViewById(R.id.btnDeleteSelected);

        databaseHelper = new DatabaseHelper(getActivity());
        dishList = new ArrayList<>();

        setupRecyclerView();
        loadDishes();

        btnDeleteSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedDishes();
            }
        });

        return view;
    }

    private void setupRecyclerView() {
        adapter = new DishAdapter(getActivity(), dishList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private void loadDishes() {
        dishList.clear();
        dishList.addAll(databaseHelper.getAllDishes());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDishClick(int position) {
        Dish dish = dishList.get(position);
        if (isMultiSelectMode) {
            adapter.toggleSelection(position);
            updateDeleteButtonVisibility();
        } else {
            Intent intent = new Intent(getActivity(), AddEditDishActivity.class);
            intent.putExtra("DISH_ID", dish.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onDishLongClick(int position) {
        if (!isMultiSelectMode) {
            isMultiSelectMode = true;
            adapter.setMultiSelectMode(true);
            btnDeleteSelected.setVisibility(View.VISIBLE);
        }
        adapter.toggleSelection(position);
        updateDeleteButtonVisibility();
    }

    private void updateDeleteButtonVisibility() {
        int selectedCount = adapter.getSelectedItems().size();
        if (selectedCount > 0) {
            btnDeleteSelected.setText("Delete (" + selectedCount + ")");
            btnDeleteSelected.setVisibility(View.VISIBLE);
        } else {
            btnDeleteSelected.setVisibility(View.GONE);
            isMultiSelectMode = false;
            adapter.setMultiSelectMode(false);
        }
    }

    private void deleteSelectedDishes() {
        final List<Integer> selectedPositions = adapter.getSelectedItems();
        if (selectedPositions.isEmpty()) {
            return;
        }

        new AlertDialog.Builder(getActivity())
                .setTitle("Delete Dishes")
                .setMessage("Are you sure you want to delete " + selectedPositions.size() + " dishes?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<String> dishIdsToDelete = new ArrayList<>();
                        for (int position : selectedPositions) {
                            dishIdsToDelete.add(dishList.get(position).getId());
                        }

                        int deletedCount = databaseHelper.deleteDishes(dishIdsToDelete);
                        if (deletedCount > 0) {
                            Toast.makeText(getActivity(), deletedCount + " dishes deleted", Toast.LENGTH_SHORT).show();
                            loadDishes();
                            isMultiSelectMode = false;
                            adapter.setMultiSelectMode(false);
                            btnDeleteSelected.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(getActivity(), "Error deleting dishes", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}