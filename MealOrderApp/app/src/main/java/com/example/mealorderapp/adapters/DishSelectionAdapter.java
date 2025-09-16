package com.example.mealorderapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealorderapp.R;
import com.example.mealorderapp.models.Dish;

import java.util.List;

public class DishSelectionAdapter extends RecyclerView.Adapter<DishSelectionAdapter.DishViewHolder> {
    private final Context context;
    private final List<Dish> dishList;
    private final OnDishSelectionListener listener;
    private List<Dish> selectedDishes;

    public interface OnDishSelectionListener {
        void onDishSelected(Dish dish, boolean isSelected);
    }

    public DishSelectionAdapter(Context context, List<Dish> dishList, OnDishSelectionListener listener) {
        this.context = context;
        this.dishList = dishList;
        this.listener = listener;
    }

    public void setSelectedDishes(List<Dish> selectedDishes) {
        this.selectedDishes = selectedDishes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dish_selection, parent, false);
        return new DishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DishViewHolder holder, int position) {
        Dish dish = dishList.get(position);

        holder.tvDishName.setText(dish.getName());
        holder.tvPrice.setText(String.format("$%.2f", dish.getPrice()));

        if (dish.getImageBitmap() != null) {
            holder.ivDishImage.setImageBitmap(dish.getImageBitmap());
            holder.ivDishImage.setVisibility(View.VISIBLE);
        } else {
            holder.ivDishImage.setVisibility(View.GONE);
        }

        // Check if this dish is selected
        boolean isSelected = selectedDishes != null && selectedDishes.contains(dish);
        holder.cbSelect.setChecked(isSelected);

        holder.cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listener.onDishSelected(dish, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dishList.size();
    }

    static class DishViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbSelect;
        TextView tvDishName, tvPrice;
        ImageView ivDishImage;

        public DishViewHolder(@NonNull View itemView) {
            super(itemView);
            cbSelect = itemView.findViewById(R.id.cbSelect);
            tvDishName = itemView.findViewById(R.id.tvDishName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ivDishImage = itemView.findViewById(R.id.ivDishImage);
        }
    }
}