plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.voiceassistant"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.voiceassistant"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(platform("com.google.firebase:firebase-bom:32.6.0"))
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-storage:20.0.0")
    implementation ("com.google.firebase:firebase-messaging:20.1.7")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.guava:guava:31.0.1-android")
    implementation("com.squareup.picasso:picasso:2.5.2")
    implementation("ru.tinkoff.decoro:decoro:1.5.2")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation ("com.android.volley:volley:1.2.1")


    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}