package com.example.mealorderapp.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.mealorderapp.R;
import com.example.mealorderapp.database.DatabaseHelper;
import com.example.mealorderapp.models.Order;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class OrderDetailFragment extends Fragment {
    private TextView tvOrderId, tvDiningOption, tvTableNumber, tvOrderTime, tvStatus, tvProcessingTime, tvTotalPrice;
    private Button btnMarkDone, btnEdit, btnDelete;
    private DatabaseHelper databaseHelper;
    private Order currentOrder;

    public OrderDetailFragment() {
        // Required empty public constructor
    }

    public static OrderDetailFragment newInstance(String orderId) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle args = new Bundle();
        args.putString("ORDER_ID", orderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_order_details, container, false);

        databaseHelper = new DatabaseHelper(getActivity());
        initViews(view);

        // Get order details
        String orderId = getArguments().getString("ORDER_ID");
        if (orderId != null) {
            currentOrder = databaseHelper.getOrder(orderId);
            if (currentOrder != null) {
                populateFields();
            }
        }

        btnMarkDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markOrderAsDone();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editOrder();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteOrder();
            }
        });

        return view;
    }

    private void initViews(View view) {
        tvOrderId = view.findViewById(R.id.tvOrderTitle);
        tvDiningOption = new TextView(getActivity());
        tvTableNumber = new TextView(getActivity());
        tvOrderTime = new TextView(getActivity());
        tvStatus = new TextView(getActivity());
        tvProcessingTime = new TextView(getActivity());
        tvTotalPrice = new TextView(getActivity());

        btnMarkDone = new Button(getActivity());
        btnMarkDone.setText("Mark as Done");
        btnMarkDone.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        btnMarkDone.setTextColor(getResources().getColor(android.R.color.white));

        btnEdit = new Button(getActivity());
        btnEdit.setText("Edit");
        btnEdit.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btnEdit.setTextColor(getResources().getColor(android.R.color.white));

        btnDelete = new Button(getActivity());
        btnDelete.setText("Delete");
        btnDelete.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        btnDelete.setTextColor(getResources().getColor(android.R.color.white));

        // Add views to layout programmatically
        if (view instanceof ViewGroup) {
            ViewGroup layout = (ViewGroup) view;

            // Add text views
            addTextView(layout, tvDiningOption);
            addTextView(layout, tvTableNumber);
            addTextView(layout, tvOrderTime);
            addTextView(layout, tvStatus);
            addTextView(layout, tvProcessingTime);
            addTextView(layout, tvTotalPrice);

            // Add buttons
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                    ViewGroup.MarginLayoutParams.MATCH_PARENT,
                    ViewGroup.MarginLayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 16, 0, 8);

            layout.addView(btnMarkDone, params);
            layout.addView(btnEdit, params);
            layout.addView(btnDelete, params);
        }
    }

    private void addTextView(ViewGroup layout, TextView textView) {
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.MATCH_PARENT,
                ViewGroup.MarginLayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 8, 0, 0);
        layout.addView(textView, params);
    }

    private void populateFields() {
        tvOrderId.setText("Order: " + currentOrder.getId());
        tvDiningOption.setText("Dining Option: " + currentOrder.getDiningOption());

        if ("Dine In".equalsIgnoreCase(currentOrder.getDiningOption())) {
            tvTableNumber.setText("Table Number: " + currentOrder.getTableNumber());
            tvTableNumber.setVisibility(View.VISIBLE);
        } else {
            tvTableNumber.setVisibility(View.GONE);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        tvOrderTime.setText("Order Time: " + sdf.format(currentOrder.getOrderTime()));

        tvStatus.setText("Status: " + (currentOrder.isDone() ? "Done" : "Preparing"));

        if (!currentOrder.isDone()) {
            long processingTime = System.currentTimeMillis() - currentOrder.getOrderTime().getTime();
            long seconds = processingTime / 1000;
            long minutes = seconds / 60;
            seconds = seconds % 60;
            tvProcessingTime.setText("Processing Time: " + minutes + " minutes " + seconds + " seconds");
            tvProcessingTime.setVisibility(View.VISIBLE);
            btnMarkDone.setVisibility(View.VISIBLE);
        } else {
            tvProcessingTime.setVisibility(View.GONE);
            btnMarkDone.setVisibility(View.GONE);
        }

        tvTotalPrice.setText("Total Price: $" + String.format("%.2f", currentOrder.getTotalPrice()));
    }

    private void markOrderAsDone() {
        currentOrder.setDone(true);
        if (databaseHelper.updateOrder(currentOrder) > 0) {
            Toast.makeText(getActivity(), "Order marked as done", Toast.LENGTH_SHORT).show();
            populateFields(); // Refresh UI
        } else {
            Toast.makeText(getActivity(), "Error marking order as done", Toast.LENGTH_SHORT).show();
        }
    }

    private void editOrder() {
        // Navigate to edit order activity
        android.content.Intent intent = new android.content.Intent(getActivity(), com.example.mealorderapp.activities.CreateEditOrderActivity.class);
        intent.putExtra("ORDER_ID", currentOrder.getId());
        startActivity(intent);
    }

    private void deleteOrder() {
        new android.app.AlertDialog.Builder(getActivity())
                .setTitle("Delete Order")
                .setMessage("Are you sure you want to delete this order?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int result = databaseHelper.deleteOrder(currentOrder.getId());
                        if (result > 0) {
                            Toast.makeText(getActivity(), "Order deleted successfully", Toast.LENGTH_SHORT).show();
                            getActivity().onBackPressed();
                        } else {
                            Toast.makeText(getActivity(), "Error deleting order", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}