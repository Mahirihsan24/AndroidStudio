package com.example.mealorderapp.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    private String id;
    private String diningOption;
    private String tableNumber;
    private List<String> dishIds;
    private double totalPrice;
    private Date orderTime;
    private boolean isDone;

    public Order() {
        this.dishIds = new ArrayList<>();
        this.orderTime = new Date();
        this.isDone = false;
    }

    public Order(String id, String diningOption, String tableNumber, String dishIdsStr,
                 double totalPrice, Date orderTime, boolean isDone) {
        this.id = id;
        this.diningOption = diningOption;
        this.tableNumber = tableNumber;
        this.dishIds = parseDishIds(dishIdsStr);
        this.totalPrice = totalPrice;
        this.orderTime = orderTime;
        this.isDone = isDone;
    }

    public String getDishIdsAsString() {
        if (dishIds == null || dishIds.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (String dishId : dishIds) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(dishId);
        }
        return sb.toString();
    }

    private List<String> parseDishIds(String dishIdsStr) {
        List<String> ids = new ArrayList<>();
        if (dishIdsStr != null && !dishIdsStr.isEmpty()) {
            String[] parts = dishIdsStr.split(",");
            for (String part : parts) {
                ids.add(part.trim());
            }
        }
        return ids;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDiningOption() { return diningOption; }
    public void setDiningOption(String diningOption) { this.diningOption = diningOption; }

    public String getTableNumber() { return tableNumber; }
    public void setTableNumber(String tableNumber) { this.tableNumber = tableNumber; }

    public List<String> getDishIds() { return dishIds; }
    public void setDishIds(List<String> dishIds) { this.dishIds = dishIds; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public Date getOrderTime() { return orderTime; }
    public void setOrderTime(Date orderTime) { this.orderTime = orderTime; }

    public boolean isDone() { return isDone; }
    public void setDone(boolean done) { isDone = done; }
}