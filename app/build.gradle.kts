plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.doraemon.x"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.doraemon.x"
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
                "proguard-rules.pro",
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
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.2.0")
//    if (extra.properties["IS_DEV"] == true) {
    implementation(project(mapOf("path" to ":doraemon")))
//    } else {
//        implementation("com.github.Petterpx:DoraemonX:1.0-alpha")
//    }
}
