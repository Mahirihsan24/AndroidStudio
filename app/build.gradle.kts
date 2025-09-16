plugins {
    id("com.android.application")
    // id("org.jetbrains.kotlin.android") // <-- uncomment only if you actually have Kotlin files in :app
}

android {
    namespace = "com.example.mealorderapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mealorderapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // AGP 8.x requires JDK 17
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // If you enable Kotlin above, you can also set:
    // kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // (Optional) Navigation, if you use it:
    // implementation("androidx.navigation:navigation-fragment:2.8.0")
    // implementation("androidx.navigation:navigation-ui:2.8.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
