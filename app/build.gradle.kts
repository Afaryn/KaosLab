plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
}

android {
    namespace = "com.afaryn.kaoslab"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.afaryn.kaoslab"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding= true
    }

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.gridlayout)
    implementation(libs.androidx.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.dotsindicator)
    implementation (libs.androidx.viewpager2)
    implementation (libs.androidx.fragment.ktx)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.firestore)
    implementation (libs.firebase.core)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation (libs.firebaseui.firebase.ui.auth)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.messaging)



    // Circle Image View
    implementation(libs.circleimageview)

    //Livedata
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Dagger Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    // Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Okhttp
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // Glide
    implementation(libs.glide)

    //preference
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.datastore.preferences)

    //calendar
    implementation("com.prolificinteractive:material-calendarview:1.4.3")

    //imageCrop
    implementation ("com.github.yalantis:ucrop:2.2.9-native")

    //Chart
//    implementation ("com.github.AAChartModel:AAChartCore-Kotlin:+")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // ViewPager2
    implementation ("androidx.viewpager2:viewpager2:1.1.0")

    implementation ("de.hdodenhof:circleimageview:3.1.0")
}