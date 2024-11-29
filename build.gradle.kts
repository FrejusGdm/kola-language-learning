import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
//    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.compose) apply false


    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.hilt.gradle) apply false
    alias(libs.plugins.kotlin.serialization) apply false

}


//val localProperties = Properties()
//val localPropertiesFile = rootDir.resolve("local.properties")
//if (localPropertiesFile.exists()) {
//    localPropertiesFile.inputStream().use { localProperties.load(it) }
//}
//
//// Ensure the OPENAI_API_KEY is loaded into the extra properties
//extra["OPENAI_API_KEY"] = localProperties["OPENAI_API_KEY"] ?: throw GradleException("OPENAI_API_KEY not found in local.properties")





