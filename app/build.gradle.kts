
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.parental_control_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.parental_control_app"
        minSdk = 28
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
    buildTypes.configureEach {
        buildConfigField("String", "TOMTOM_API_KEY", "\"ADmBZ6RaLW61babmRsAMAmfHALfbVw5u\"")
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }

        jniLibs.pickFirsts.add("lib/**/libc++_shared.so")
    }
}

dependencies {

    val workVersion = "2.8.1"

    //noinspection GradleDependency
    implementation("androidx.compose.ui:ui-text-google-fonts:1.5.0")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("io.coil-kt:coil-compose:2.4.0")

    // (Java only)
    implementation("androidx.work:work-runtime:$workVersion")

    // Kotlin + coroutines
    implementation("androidx.work:work-runtime-ktx:$workVersion")

    // optional - RxJava2 support
    implementation("androidx.work:work-rxjava2:$workVersion")

    // optional - GCMNetworkManager support
    implementation("androidx.work:work-gcm:$workVersion")

    // optional - Test helpers
    androidTestImplementation("androidx.work:work-testing:$workVersion")

    // optional - Multiprocess support
    implementation("androidx.work:work-multiprocess:$workVersion")

    implementation("com.google.code.gson:gson:2.9.0")
    implementation("org.mindrot:jbcrypt:0.4")
    //noinspection GradleDependency
    implementation("com.google.android.gms:play-services-auth:20.6.0")
    //noinspection GradleDependency
    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0")
    //noinspection GradleDependency
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0")
    val navVersion = "2.6.0"

    //noinspection GradleDependency
    implementation("androidx.navigation:navigation-compose:$navVersion")

    //noinspection GradleDependency
    implementation("com.google.firebase:firebase-firestore-ktx:24.7.0")
    implementation(platform("com.google.firebase:firebase-bom:32.2.2"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")

    //noinspection GradleDependency
    implementation("androidx.compose.material3:material3:1.2.0-alpha04")
    implementation("androidx.palette:palette-ktx:1.0.0")
    //noinspection GradleDependency
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")

    //noinspection GradleDependency
    implementation("androidx.core:core-ktx:1.10.1")
    //noinspection GradleDependency
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("com.tomtom.sdk.routing:route-planner-online:0.33.1") {
        exclude("com.google.protobuf")
    }

    implementation("com.tomtom.sdk.location:provider-android:0.33.1")
    implementation("com.tomtom.sdk.maps:map-display:0.33.1")
//    implementation("com.tomtom.sdk.maps:map-display:0.30.1") {
//        exclude("com.google.protobuf")
//    }

//    implementation("com.tomtom.sdk.routing:route-planner-online:0.32.6") {
//        exclude("com.google.protobuf")
//    }


    implementation("com.google.android.gms:play-services-location:21.0.1")

    implementation("com.google.accompanist:accompanist-permissions:0.23.1")

    implementation("com.google.firebase:firebase-ml-modeldownloader-ktx")
    implementation("org.tensorflow:tensorflow-lite:2.3.0")



}