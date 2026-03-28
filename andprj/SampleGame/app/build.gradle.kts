plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "kr.ac.tukorea.ge.scgyong.samplegame"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    defaultConfig {
        applicationId = "kr.ac.tukorea.ge.scgyong.samplegame"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("boolean", "DRAWS_DEBUG_GRID", "true")
            buildConfigField("boolean", "DRAWS_DEBUG_INFO", "true")
            buildConfigField("boolean", "DRAWS_FPS_GRAPH", "true")
        }
        release {
            isMinifyEnabled = false
            buildConfigField("boolean", "DRAWS_DEBUG_GRID", "false")
            buildConfigField("boolean", "DRAWS_DEBUG_INFO", "false")
            buildConfigField("boolean", "DRAWS_FPS_GRAPH", "false")
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
}

dependencies {
    implementation(project(":a2dg"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
