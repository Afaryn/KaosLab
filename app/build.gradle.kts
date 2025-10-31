plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin")
    kotlin("kapt")
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

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/license.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/notice.txt"
            excludes += "META-INF/ASL2.0"
            excludes += "META-INF/*.kotlin_module"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField(
                "String",
                "MIDTRANS_BASE_URL",
                "\"https://ngtlqybmpszxnjnpcvgy.supabase.co/functions/v1/\""
            )
            buildConfigField("String", "CLIENT_KEY", "\"SB-Mid-client-8f2UJwxCvEBJuwBg\"")
            buildConfigField(
                "String",
                "SUPABASE_API_KEY",
                "\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5ndGxxeWJtcHN6eG5qbnBjdmd5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTkyOTA5NjMsImV4cCI6MjA3NDg2Njk2M30.iB8E18Nb-6_zlyQgd9B6OIAuiTVIZXcV0JmU4q4j7Dc\""
            )
        }
        debug {
            buildConfigField(
                "String",
                "MIDTRANS_BASE_URL",
                "\"https://ngtlqybmpszxnjnpcvgy.supabase.co/functions/v1/\""
            )
            buildConfigField("String", "CLIENT_KEY", "\"SB-Mid-client-8f2UJwxCvEBJuwBg\"")
            buildConfigField(
                "String",
                "SUPABASE_API_KEY",
                "\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5ndGxxeWJtcHN6eG5qbnBjdmd5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTkyOTA5NjMsImV4cCI6MjA3NDg2Njk2M30.iB8E18Nb-6_zlyQgd9B6OIAuiTVIZXcV0JmU4q4j7Dc\""
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
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += setOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt"
            )
        }
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
    implementation(libs.androidx.viewpager2)
    implementation(libs.androidx.fragment.ktx)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.core)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebaseui.firebase.ui.auth)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.messaging)


    // Circle Image View
    implementation(libs.circleimageview)

    //Livedata
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Dagger Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    kapt(libs.androidx.hilt.compiler)

    // Room
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)

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
    implementation("com.github.yalantis:ucrop:2.2.9-native")

    //Chart
//    implementation ("com.github.AAChartModel:AAChartCore-Kotlin:+")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    //flexbox
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation("androidx.gridlayout:gridlayout:1.0.0")

    // ViewPager2
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    implementation("de.hdodenhof:circleimageview:3.1.0")

    // MidTrans
    implementation("com.midtrans:uikit:2.4.0-SANDBOX")

    // Google OAuth
    implementation(libs.google.api.client)
    implementation(libs.google.auth.library.oauth2.http)
}
kapt {
    correctErrorTypes = true
}