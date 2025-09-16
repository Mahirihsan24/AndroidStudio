package com.example.mealorderapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealorderapp.R;
import com.example.mealorderapp.models.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private final Context context;
    private final List<Order> orderList;
    private final OnOrderClickListener listener;
    private boolean isMultiSelectMode = false;
    private final List<Integer> selectedItems = new ArrayList<>();

    public interface OnOrderClickListener {
        void onOrderClick(int position);
        void onOrderLongClick(int position);
    }

    public OrderAdapter(Context context, List<Order> orderList, OnOrderClickListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.tvOrderId.setText(order.getId());
        holder.tvDiningOption.setText(order.getDiningOption());

        if ("Dine In".equalsIgnoreCase(order.getDiningOption())) {
            holder.tvTableNumber.setText("Table: " + order.getTableNumber());
            holder.tvTableNumber.setVisibility(View.VISIBLE);
        } else {
            holder.tvTableNumber.setVisibility(View.GONE);
        }

        holder.tvOrderTime.setText(android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", order.getOrderTime()));
        holder.tvTotalPrice.setText(String.format("$%.2f", order.getTotalPrice()));
        holder.tvStatus.setText(order.isDone() ? "Done" : "Preparing");

        if (!order.isDone()) {
            long processingTime = System.currentTimeMillis() - order.getOrderTime().getTime();
            long seconds = processingTime / 1000;
            long minutes = seconds / 60;
            seconds = seconds % 60;
            holder.tvProcessingTime.setText(minutes + "m " + seconds + "s");
            holder.tvProcessingTime.setVisibility(View.VISIBLE);
        } else {
            holder.tvProcessingTime.setVisibility(View.GONE);
        }

        // Highlight selected items in multi-select mode
        if (isMultiSelectMode && selectedItems.contains(position)) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light));
        } else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMultiSelectMode) {
                    toggleSelection(position);
                } else {
                    listener.onOrderClick(position);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onOrderLongClick(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void toggleSelection(int position) {
        if (selectedItems.contains(position)) {
            selectedItems.remove((Integer) position);
        } else {
            selectedItems.add(position);
        }
        notifyItemChanged(position);
    }

    public List<Integer> getSelectedItems() {
        return selectedItems;
    }

    public void setMultiSelectMode(boolean multiSelectMode) {
        this.isMultiSelectMode = multiSelectMode;
        if (!multiSelectMode) {
            selectedItems.clear();
        }
        notifyDataSetChanged();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvDiningOption, tvTableNumber, tvOrderTime, tvTotalPrice, tvStatus, tvProcessingTime;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvDiningOption = itemView.findViewById(R.id.tvDiningOption);
            tvTableNumber = itemView.findViewById(R.id.tvTableNumber);
            tvOrderTime = itemView.findViewById(R.id.tvOrderTime);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvProcessingTime = itemView.findViewById(R.id.tvProcessingTime);
        }
    }
}