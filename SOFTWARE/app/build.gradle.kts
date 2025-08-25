import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.androidApplication)
    //id("com.android.application")
    alias(libs.plugins.org.jetbrains.kotlin.android)
    //id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    //id("com.google.dagger.hilt.android")
    alias(libs.plugins.daggerHilt)
    alias(libs.plugins.compose.compiler)
}


android {
    namespace = "com.nano_tablet.nanotabletrfid"
    compileSdk = 35

    /*splits {
        abi {
            isEnable = true
            isUniversalApk = false
        }
    }*/

    defaultConfig {
        applicationId = "com.nano_tablet.nanotabletrfid"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    //https://www.youtube.com/watch?v=o0M4f5djJTQ
    flavorDimensions += "instruments"
    productFlavors {
        create("RFIDtest") {
            dimension = "instruments"
            applicationIdSuffix = ".test"
            buildConfigField("String", "INSTRUMENT_LIST", "\"test\"")

        }
        create("CLEAN100") {
            dimension = "instruments"
            applicationIdSuffix = ".clean100"
            buildConfigField("String", "INSTRUMENT_LIST", "\"CLEAN100\"")
        }

        create("STAN") {
            dimension = "instruments"
            applicationIdSuffix = ".stan"
            buildConfigField("String", "INSTRUMENT_LIST", "\"STAN\"")
        }

        create("EW") {
            dimension = "instruments"
            applicationIdSuffix = ".ew"
            buildConfigField("String", "INSTRUMENT_LIST", "\"EW\"")
        }
        create("CERAM") {
            dimension = "instruments"
            applicationIdSuffix = ".ceram"
            buildConfigField("String", "INSTRUMENT_LIST", "\"CERAM\"")
        }
    }



    applicationVariants.all {
        outputs.all { output ->
            if (output is BaseVariantOutputImpl) {
                val date = SimpleDateFormat("dd-MM-yyyy").format(Date())
                val filename = "${flavorName}_nanoTablet_${date}.apk"
                output.outputFileName = filename
            }
            true
        }
    }



/*
    applicationVariants.all {
        outputs.all { output ->
            if (output is BaseVariantOutputImpl) {
                val date = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val filename = "${flavorName}_${versionName}-${versionCode}_${date}_${name}.apk"
                output.outputFileName = filename
            }
            true
        }
    }*/

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
         sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        //sourceCompatibility = JavaVersion.VERSION_1_8
        //targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
           jvmTarget = "17"
        //jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    stabilityConfigurationFile = rootProject.layout.projectDirectory.file("stability_config.conf")
}

dependencies {
    implementation(libs.converter.scalars)
    debugImplementation(libs.mockwebserver)

    testImplementation(libs.mockwebserver)
    implementation(libs.androidx.datastore.preferences)
    implementation (libs.material)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    //implementation(libs.androidx.compose.material)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.animation.core)

    //okhttp - API calls
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.androidx.material3.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    androidTestImplementation(libs.androidx.ui.test.junit4)


    debugImplementation(libs.androidx.ui.test.manifest)

//https://www.youtube.com/watch?v=TosPS55y_IY
    // Arrow
    implementation(libs.arrow.core)
    implementation(libs.arrow.fx.coroutines)
    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    //Coil
    implementation(libs.coil.compose)
    //dagger hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.androidx.navigation.compose)

    //splash screen
    //noinspection UseTomlInstead
    //implementation("androidx.core:core-splashscreen:1.0.1")
    implementation(libs.splashscreen)
    implementation(libs.retrosheet)


}
// Allow references to generated code
kapt {
    correctErrorTypes = true
}