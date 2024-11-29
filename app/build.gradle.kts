plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
//    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.kotlin.serialization)

}

android {
    namespace = "com.example.kola_language_learning"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.kola_language_learning"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

//        // Safely retrieve the OPENAI_API_KEY
//        val openAiApiKey: String? = project.extra["OPENAI_API_KEY"] as String?
//        if (openAiApiKey.isNullOrEmpty()) {
//            throw GradleException("OPENAI_API_KEY is not defined in local.properties")
//        }
        buildConfigField("String", "OPENAI_API_KEY", "\"${System.getenv("OPENAI_API_KEY")}\"")

//        buildConfigField("String", "OPENAI_API_KEY", "\"$openAiApiKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

//            debug {
//                println("OPENAI_API_KEY from env: ${System.getenv("OPENAI_API_KEY")}")
//                buildConfigField("String", "OPENAI_API_KEY", "\"${System.getenv("OPENAI_API_KEY")}\"")
//            }
        }
        buildTypes {
            debug {
                buildConfigField("String", "OPENAI_API_KEY", "\"${System.getenv("OPENAI_API_KEY")}\"")
            }
            release {
                buildConfigField("String", "OPENAI_API_KEY", "\"${System.getenv("OPENAI_API_KEY")}\"")
            }
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

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.multidex)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.material.icons.extended)

    // Navigation
    implementation(libs.androidx.navigation)
    implementation(libs.androidx.navigation.compose)
    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    androidTestImplementation(libs.hilt.android.testing)
    testImplementation(libs.hilt.android.testing)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.kotlinx.serialization)
    implementation(libs.serialization.converter)
    implementation(libs.coil)

    // Runtime
    implementation("androidx.compose.runtime:runtime-livedata:1.5.3")
    implementation ("com.google.accompanist:accompanist-permissions:0.31.0-alpha")



}
