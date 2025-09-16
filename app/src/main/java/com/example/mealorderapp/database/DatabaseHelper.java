package com.example.mealorderapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.mealorderapp.models.Dish;
import com.example.mealorderapp.models.Order;
import com.example.mealorderapp.models.User;
import com.example.mealorderapp.utils.ImageUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MealOrder.db";
    private static final int DATABASE_VERSION = 1;

    // User Table
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role";

    // Dish Table
    public static final String TABLE_DISHES = "dishes";
    public static final String COLUMN_DISH_ID = "dish_id";
    public static final String COLUMN_DISH_NAME = "dish_name";
    public static final String COLUMN_DISH_TYPE = "dish_type";
    public static final String COLUMN_INGREDIENTS = "ingredients";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_IMAGE = "image";

    // Order Table
    public static final String TABLE_ORDERS = "orders";
    public static final String COLUMN_ORDER_ID = "order_id";
    public static final String COLUMN_DINING_OPTION = "dining_option";
    public static final String COLUMN_TABLE_NUMBER = "table_number";
    public static final String COLUMN_DISH_IDS = "dish_ids";
    public static final String COLUMN_TOTAL_PRICE = "total_price";
    public static final String COLUMN_ORDER_TIME = "order_time";
    public static final String COLUMN_IS_DONE = "is_done";

    // Create Users Table
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USERNAME + " TEXT UNIQUE,"
            + COLUMN_PASSWORD + " TEXT,"
            + COLUMN_ROLE + " TEXT"
            + ")";

    // Create Dishes Table
    private static final String CREATE_TABLE_DISHES = "CREATE TABLE " + TABLE_DISHES + "("
            + COLUMN_DISH_ID + " TEXT PRIMARY KEY,"
            + COLUMN_DISH_NAME + " TEXT,"
            + COLUMN_DISH_TYPE + " TEXT,"
            + COLUMN_INGREDIENTS + " TEXT,"
            + COLUMN_PRICE + " REAL,"
            + COLUMN_IMAGE + " BLOB"
            + ")";

    // Create Orders Table
    private static final String CREATE_TABLE_ORDERS = "CREATE TABLE " + TABLE_ORDERS + "("
            + COLUMN_ORDER_ID + " TEXT PRIMARY KEY,"
            + COLUMN_DINING_OPTION + " TEXT,"
            + COLUMN_TABLE_NUMBER + " TEXT,"
            + COLUMN_DISH_IDS + " TEXT,"
            + COLUMN_TOTAL_PRICE + " REAL,"
            + COLUMN_ORDER_TIME + " INTEGER,"
            + COLUMN_IS_DONE + " INTEGER DEFAULT 0"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_DISHES);
        db.execSQL(CREATE_TABLE_ORDERS);
        insertDefaultData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISHES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        onCreate(db);
    }

    private void insertDefaultData(SQLiteDatabase db) {
        // Insert default admin and user accounts
        ContentValues adminValues = new ContentValues();
        adminValues.put(COLUMN_USERNAME, "admin");
        adminValues.put(COLUMN_PASSWORD, "admin123");
        adminValues.put(COLUMN_ROLE, "admin");
        db.insert(TABLE_USERS, null, adminValues);

        ContentValues userValues = new ContentValues();
        userValues.put(COLUMN_USERNAME, "user");
        userValues.put(COLUMN_PASSWORD, "user123");
        userValues.put(COLUMN_ROLE, "user");
        db.insert(TABLE_USERS, null, userValues);

        // Insert sample dishes
        insertSampleDish(db, "DO01", "Spring Rolls (4pcs)", "entry", "Meat, Prawn, Veggie", 10.0, null);
        insertSampleDish(db, "DO02", "BBQ Chicken Wings (4pcs)", "entry", "Chicken, BBQ Sauce", 12.0, null);
        insertSampleDish(db, "DO03", "Break 30/8", "main", "Beef, Vegetables", 36.0, null);
        insertSampleDish(db, "DO04", "Seafood Basket", "main", "Fish, Prawns, Calamari", 45.0, null);
        insertSampleDish(db, "DO05", "Coke", "drink", "Carbonated Water, Sugar", 3.0, null);
        insertSampleDish(db, "DO06", "Water", "drink", "Mineral Water", 3.0, null);
    }

    private void insertSampleDish(SQLiteDatabase db, String id, String name, String type, String ingredients, double price, byte[] image) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_DISH_ID, id);
        values.put(COLUMN_DISH_NAME, name);
        values.put(COLUMN_DISH_TYPE, type);
        values.put(COLUMN_INGREDIENTS, ingredients);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_IMAGE, image);
        db.insert(TABLE_DISHES, null, values);
    }

    // User operations
    public User authenticateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID, COLUMN_USERNAME, COLUMN_ROLE},
                COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[]{username, password}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE))
            );
            cursor.close();
        }
        db.close();
        return user;
    }

    // Dish operations
    public long addDish(Dish dish) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DISH_ID, dish.getId());
        values.put(COLUMN_DISH_NAME, dish.getName());
        values.put(COLUMN_DISH_TYPE, dish.getType());
        values.put(COLUMN_INGREDIENTS, dish.getIngredients());
        values.put(COLUMN_PRICE, dish.getPrice());
        values.put(COLUMN_IMAGE, dish.getImageBytes());

        long result = db.insert(TABLE_DISHES, null, values);
        db.close();
        return result;
    }

    public List<Dish> getAllDishes() {
        List<Dish> dishList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DISHES, null, null, null, null, null, COLUMN_DISH_TYPE + ", " + COLUMN_DISH_NAME);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Dish dish = new Dish(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DISH_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DISH_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DISH_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENTS)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                        cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE))
                );
                dishList.add(dish);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return dishList;
    }

    public Dish getDish(String dishId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Dish dish = null;

        Cursor cursor = db.query(TABLE_DISHES, null, COLUMN_DISH_ID + " = ?",
                new String[]{dishId}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            dish = new Dish(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DISH_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DISH_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DISH_TYPE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENTS)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                    cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE))
            );
            cursor.close();
        }
        db.close();
        return dish;
    }

    public int updateDish(Dish dish) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DISH_NAME, dish.getName());
        values.put(COLUMN_DISH_TYPE, dish.getType());
        values.put(COLUMN_INGREDIENTS, dish.getIngredients());
        values.put(COLUMN_PRICE, dish.getPrice());
        values.put(COLUMN_IMAGE, dish.getImageBytes());

        int result = db.update(TABLE_DISHES, values, COLUMN_DISH_ID + " = ?",
                new String[]{dish.getId()});
        db.close();
        return result;
    }

    public int deleteDish(String dishId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_DISHES, COLUMN_DISH_ID + " = ?", new String[]{dishId});
        db.close();
        return result;
    }

    public int deleteDishes(List<String> dishIds) {
        SQLiteDatabase db = this.getWritableDatabase();
        int count = 0;
        for (String dishId : dishIds) {
            count += db.delete(TABLE_DISHES, COLUMN_DISH_ID + " = ?", new String[]{dishId});
        }
        db.close();
        return count;
    }

    // Order operations
    public long addOrder(Order order) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_ID, order.getId());
        values.put(COLUMN_DINING_OPTION, order.getDiningOption());
        values.put(COLUMN_TABLE_NUMBER, order.getTableNumber());
        values.put(COLUMN_DISH_IDS, order.getDishIdsAsString());
        values.put(COLUMN_TOTAL_PRICE, order.getTotalPrice());
        values.put(COLUMN_ORDER_TIME, order.getOrderTime().getTime());
        values.put(COLUMN_IS_DONE, order.isDone() ? 1 : 0);

        long result = db.insert(TABLE_ORDERS, null, values);
        db.close();
        return result;
    }

    public List<Order> getAllOrders() {
        List<Order> orderList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ORDERS, null, null, null, null, null, COLUMN_ORDER_TIME + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Order order = new Order(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DINING_OPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TABLE_NUMBER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DISH_IDS)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_PRICE)),
                        new java.util.Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ORDER_TIME))),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_DONE)) == 1
                );
                orderList.add(order);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return orderList;
    }

    public Order getOrder(String orderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Order order = null;

        Cursor cursor = db.query(TABLE_ORDERS, null, COLUMN_ORDER_ID + " = ?",
                new String[]{orderId}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            order = new Order(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DINING_OPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TABLE_NUMBER)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DISH_IDS)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_PRICE)),
                    new java.util.Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ORDER_TIME))),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_DONE)) == 1
            );
            cursor.close();
        }
        db.close();
        return order;
    }

    public int updateOrder(Order order) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DINING_OPTION, order.getDiningOption());
        values.put(COLUMN_TABLE_NUMBER, order.getTableNumber());
        values.put(COLUMN_DISH_IDS, order.getDishIdsAsString());
        values.put(COLUMN_TOTAL_PRICE, order.getTotalPrice());
        values.put(COLUMN_ORDER_TIME, order.getOrderTime().getTime());
        values.put(COLUMN_IS_DONE, order.isDone() ? 1 : 0);

        int result = db.update(TABLE_ORDERS, values, COLUMN_ORDER_ID + " = ?",
                new String[]{order.getId()});
        db.close();
        return result;
    }

    public int deleteOrder(String orderId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_ORDERS, COLUMN_ORDER_ID + " = ?", new String[]{orderId});
        db.close();
        return result;
    }

    public int deleteOrders(List<String> orderIds) {
        SQLiteDatabase db = this.getWritableDatabase();
        int count = 0;
        for (String orderId : orderIds) {
            count += db.delete(TABLE_ORDERS, COLUMN_ORDER_ID + " = ?", new String[]{orderId});
        }
        db.close();
        return count;
    }
}