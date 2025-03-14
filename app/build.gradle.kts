plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.movies"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.movies"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.play.services.cast)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    //Fetching Api
    implementation ("com.squareup.retrofit2:retrofit:2.1.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.1.0")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3")
    //Google Authentication
    implementation ("com.google.android.gms:play-services-auth:19.2.0")
    //Image Slider Library
    implementation ("com.github.denzcoskun:ImageSlideshow:0.1.2")
    //For Video Controls
    implementation ("com.google.android.exoplayer:exoplayer-dash:2.18.7")
    implementation ("com.google.android.exoplayer:exoplayer-hls:2.18.7")
    implementation ("com.google.android.exoplayer:exoplayer-core:2.18.7")
    implementation ("com.google.android.exoplayer:exoplayer-ui:2.18.7")
    implementation ("com.google.android.exoplayer:exoplayer-smoothstreaming:2.18.7")
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.0.0")

    //Pre-loading Effect
    implementation ("com.facebook.shimmer:shimmer:0.5.0")


}