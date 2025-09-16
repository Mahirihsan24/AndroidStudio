MealOrderApp 🍽️

MealOrderApp is a mobile application for managing dishes and customer orders in a restaurant setting.
It provides an admin panel for dish management, and an order management system for employees.

The app is built with Java, Android Studio, and SQLite, and demonstrates modern Android practices with RecyclerView, Fragments, Material Components, and Navigation.

✨ Features
🔐 User Accounts

Admin Login (admin/admin)

Full access: manage dishes & orders.

Employee Login (user/user)

Limited access: manage orders only.

Logout option available at any time.

🍔 Dish Management (Admin only)

Add new dish with:

Unique ID, name, type (entry, main, drink), ingredients, price, and optional image.

View dishes grouped by type in a clean list.

Edit or delete existing dishes.

Multi-delete option for bulk removal.

Images can be added from gallery or camera.

🧾 Order Management (Admin & Employee)

Create new order:

Order ID auto-generated.

Select dining option (Dine In or Take Away).

For Dine In, a table number is required.

Add dishes from all categories.

Total price auto-calculated.

View all orders in a list.

Edit existing orders (update items, dining option, or table number).

Delete or multi-delete orders.

Mark order as Done when completed.

Orders display processing time (waiting time since creation).

🏠 Home Screen

Quick summary of features and navigation instructions.

Bottom navigation bar for Home, Dishes, and Orders.

⚙️ Technical Details

Language: Java

Frameworks: Android SDK, Material Design Components

Database: SQLite (local, persistent)

Architecture: Activity + Fragment + Adapter pattern

UI Components: RecyclerView, CardView, TextInputLayout, Snackbar

📂 Project Structure
MealOrderApp/
 ├── activities/        # Login, Main, DishManagement, OrderManagement
 ├── adapters/          # RecyclerView adapters (dish selection, order display)
 ├── database/          # SQLite DatabaseHelper
 ├── fragments/         # Home, DishList, OrderList
 ├── models/            # Dish, Order classes
 ├── res/               # Layouts, Drawables, Values (colors, strings, styles)
 └── AndroidManifest.xml

🚀 Running the App
Prerequisites

Android Studio Giraffe (2022.3.1) or later

JDK 17+

Android Emulator or physical device with Android 7.0 (API 24) or above

Setup

Clone the repository:

git clone https://github.com/yourusername/MealOrderApp.git


Open the project in Android Studio.

Let Gradle sync and build the project.

Run the app on an emulator or connected device:

Select a device with API 24+.

Click ▶️ Run.

🧪 How to Test

Login

Use admin/admin for full access.

Use user/user for order-only access.

Invalid credentials will block login.

Dish Management (Admin)

Navigate to Dishes.

Add a new dish with details and image.

Edit or delete dishes from the list.

Test multi-delete with long-press selection.

Order Management

Navigate to Orders.

Create a new order, choose dining option and dishes.

Verify total price updates automatically.

Save the order and check it in the order list.

Edit or delete an order, mark it as Done.

Check that processing time is displayed.

Navigation

Use the bottom navigation bar to switch between Home, Dishes, and Orders.

Test Logout to return to login screen.

📦 Database

The app uses SQLite for local persistence.

Database file is stored on the device at:

/data/data/com.example.mealorderapp/databases/


Tables:

Dishes: id, name, type, ingredients, price, image

Orders: id, diningOption, tableNumber, dishIds, totalPrice, orderTime, status

🎨 Screenshots

(Add screenshots here once available: login screen, dish list, create order, order list, etc.)

📌 Future Improvements

Cloud sync with Firebase for multi-device support.

Analytics dashboard for popular dishes/orders.

Customer-facing module for online ordering.

📝 License

This project is licensed under the MIT License. Feel free to use and adapt for your own projects.
