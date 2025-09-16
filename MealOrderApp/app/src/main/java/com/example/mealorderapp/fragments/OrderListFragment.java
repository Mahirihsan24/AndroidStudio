package com.example.mealorderapp.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealorderapp.R;
import com.example.mealorderapp.activities.CreateEditOrderActivity;
import com.example.mealorderapp.adapters.OrderAdapter;
import com.example.mealorderapp.database.DatabaseHelper;
import com.example.mealorderapp.models.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderListFragment extends Fragment implements OrderAdapter.OnOrderClickListener {
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<Order> orderList;
    private DatabaseHelper databaseHelper;
    private Button btnDeleteSelected, btnMarkDone;
    private boolean isMultiSelectMode = false;
    private final Handler handler = new Handler();
    private Runnable updateTimeRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        btnDeleteSelected = view.findViewById(R.id.btnDeleteSelected);
        btnMarkDone = view.findViewById(R.id.btnMarkDone);

        databaseHelper = new DatabaseHelper(getActivity());
        orderList = new ArrayList<>();

        setupRecyclerView();
        loadOrders();

        btnDeleteSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedOrders();
            }
        });

        btnMarkDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markOrdersAsDone();
            }
        });

        // Set up periodic updates for order times
        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged(); // Refresh to update times
                handler.postDelayed(this, 1000); // Update every second
            }
        };
        handler.postDelayed(updateTimeRunnable, 1000);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateTimeRunnable);
    }

    private void setupRecyclerView() {
        adapter = new OrderAdapter(getActivity(), orderList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private void loadOrders() {
        orderList.clear();
        orderList.addAll(databaseHelper.getAllOrders());
        adapter.notifyDataSetChanged();
        updateButtonVisibility();
    }

    @Override
    public void onOrderClick(int position) {
        Order order = orderList.get(position);
        if (isMultiSelectMode) {
            adapter.toggleSelection(position);
            updateButtonVisibility();
        } else {
            // Show order details
            showOrderDetails(order);
        }
    }

    @Override
    public void onOrderLongClick(int position) {
        if (!isMultiSelectMode) {
            isMultiSelectMode = true;
            adapter.setMultiSelectMode(true);
            updateButtonVisibility();
        }
        adapter.toggleSelection(position);
        updateButtonVisibility();
    }

    private void updateButtonVisibility() {
        int selectedCount = adapter.getSelectedItems().size();
        if (selectedCount > 0) {
            btnDeleteSelected.setText("Delete (" + selectedCount + ")");
            btnDeleteSelected.setVisibility(View.VISIBLE);
            btnMarkDone.setVisibility(View.VISIBLE);
        } else {
            btnDeleteSelected.setVisibility(View.GONE);
            btnMarkDone.setVisibility(View.GONE);
            isMultiSelectMode = false;
            adapter.setMultiSelectMode(false);
        }
    }

    private void showOrderDetails(Order order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Order Details: " + order.getId());

        StringBuilder details = new StringBuilder();
        details.append("Dining Option: ").append(order.getDiningOption()).append("\n");
        if ("Dine In".equalsIgnoreCase(order.getDiningOption())) {
            details.append("Table Number: ").append(order.getTableNumber()).append("\n");
        }
        details.append("Order Time: ").append(android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", order.getOrderTime())).append("\n");
        details.append("Total Price: $").append(String.format("%.2f", order.getTotalPrice())).append("\n");
        details.append("Status: ").append(order.isDone() ? "Done" : "Preparing").append("\n");

        if (!order.isDone()) {
            long processingTime = System.currentTimeMillis() - order.getOrderTime().getTime();
            long seconds = processingTime / 1000;
            long minutes = seconds / 60;
            seconds = seconds % 60;
            details.append("Processing Time: ").append(minutes).append(" minutes ").append(seconds).append(" seconds").append("\n");
        }

        builder.setMessage(details.toString());
        builder.setPositiveButton("OK", null);

        if (!order.isDone()) {
            builder.setNeutralButton("Mark as Done", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    markOrderAsDone(order);
                }
            });
        }

        builder.show();
    }

    private void deleteSelectedOrders() {
        final List<Integer> selectedPositions = adapter.getSelectedItems();
        if (selectedPositions.isEmpty()) {
            return;
        }

        new AlertDialog.Builder(getActivity())
                .setTitle("Delete Orders")
                .setMessage("Are you sure you want to delete " + selectedPositions.size() + " orders?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<String> orderIdsToDelete = new ArrayList<>();
                        for (int position : selectedPositions) {
                            orderIdsToDelete.add(orderList.get(position).getId());
                        }

                        int deletedCount = databaseHelper.deleteOrders(orderIdsToDelete);
                        if (deletedCount > 0) {
                            Toast.makeText(getActivity(), deletedCount + " orders deleted", Toast.LENGTH_SHORT).show();
                            loadOrders();
                            isMultiSelectMode = false;
                            adapter.setMultiSelectMode(false);
                            updateButtonVisibility();
                        } else {
                            Toast.makeText(getActivity(), "Error deleting orders", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void markOrdersAsDone() {
        final List<Integer> selectedPositions = adapter.getSelectedItems();
        if (selectedPositions.isEmpty()) {
            return;
        }

        new AlertDialog.Builder(getActivity())
                .setTitle("Mark Orders as Done")
                .setMessage("Are you sure you want to mark " + selectedPositions.size() + " orders as done?")
                .setPositiveButton("Mark Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int markedCount = 0;
                        for (int position : selectedPositions) {
                            Order order = orderList.get(position);
                            if (!order.isDone()) {
                                order.setDone(true);
                                if (databaseHelper.updateOrder(order) > 0) {
                                    markedCount++;
                                }
                            }
                        }

                        if (markedCount > 0) {
                            Toast.makeText(getActivity(), markedCount + " orders marked as done", Toast.LENGTH_SHORT).show();
                            loadOrders();
                            isMultiSelectMode = false;
                            adapter.setMultiSelectMode(false);
                            updateButtonVisibility();
                        } else {
                            Toast.makeText(getActivity(), "Error marking orders as done", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void markOrderAsDone(Order order) {
        order.setDone(true);
        if (databaseHelper.updateOrder(order) > 0) {
            Toast.makeText(getActivity(), "Order marked as done", Toast.LENGTH_SHORT).show();
            loadOrders();
        } else {
            Toast.makeText(getActivity(), "Error marking order as done", Toast.LENGTH_SHORT).show();
        }
    }
}