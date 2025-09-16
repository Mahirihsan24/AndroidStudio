package com.example.mealorderapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealorderapp.R;
import com.example.mealorderapp.models.Dish;

import java.util.ArrayList;
import java.util.List;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.DishViewHolder> {
    private final Context context;
    private final List<Dish> dishList;
    private final OnDishClickListener listener;
    private boolean isMultiSelectMode = false;
    private final List<Integer> selectedItems = new ArrayList<>();

    public interface OnDishClickListener {
        void onDishClick(int position);
        void onDishLongClick(int position);
    }

    public DishAdapter(Context context, List<Dish> dishList, OnDishClickListener listener) {
        this.context = context;
        this.dishList = dishList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dish, parent, false);
        return new DishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DishViewHolder holder, int position) {
        Dish dish = dishList.get(position);

        holder.tvDishName.setText(dish.getName());
        holder.tvDishType.setText(dish.getType());
        holder.tvPrice.setText(String.format("$%.2f", dish.getPrice()));

        if (dish.getImageBitmap() != null) {
            holder.ivDishImage.setImageBitmap(dish.getImageBitmap());
            holder.ivDishImage.setVisibility(View.VISIBLE);
        } else {
            holder.ivDishImage.setVisibility(View.GONE);
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
                    listener.onDishClick(position);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onDishLongClick(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return dishList.size();
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

    static class DishViewHolder extends RecyclerView.ViewHolder {
        TextView tvDishName, tvDishType, tvPrice;
        ImageView ivDishImage;

        public DishViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDishName = itemView.findViewById(R.id.tvDishName);
            tvDishType = itemView.findViewById(R.id.tvDishType);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ivDishImage = itemView.findViewById(R.id.ivDishImage);
        }
    }
}