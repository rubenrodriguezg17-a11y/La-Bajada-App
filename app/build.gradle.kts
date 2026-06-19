plugins {
    alias(libs.plugins.android.application)
    id("com.google.devtools.ksp")
    alias(libs.plugins.compose.compiler)
    // El plugin de Firebase y Hilt los comentamos por ahora hasta que enlaces tu Firebase Console
    // alias(libs.plugins.google-services)
    // alias(libs.plugins.hilt-android)
}

android {
    namespace = "com.labajada.app"
    compileSdk = 36 // Cambiado a 34 estable para asegurar compatibilidad total con librerías de mapas y Compose de este año

    defaultConfig {
        applicationId = "com.labajada.app"
        minSdk = 26 // Bajado a 26 (Android 8) para que corra en la gran mayoría de celulares en Trujillo, no solo en los últimos de gama alta
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_17 // Actualizado a Java 17, obligatorio para las versiones modernas de Compose y Room
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    // OBLIGATORIO: Esto activa el compilador de Jetpack Compose en tu proyecto
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Android Core tradicional (Por si acaso)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.activity.ktx)

    // OBLIGATORIO: Soporte e Implementación de Jetpack Compose (UI Moderna)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)

    // Room (BD Local Offline para favoritos e historial con KSP)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Red y Consumo de API (Retrofit con Moshi según tu informe)
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.converter.moshi)
    implementation(libs.moshi.kotlin)

    // Corrutinas y Ciclo de vida para Compose
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Mapas de Google y Localización GPS
    implementation(libs.google.maps.compose)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    // Preferencias Locales y Carga de Fotos
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.coil.compose)

    // NOTA: Firebase e Hilt se quedan comentados hasta que configures el proyecto en la consola web de Google
    // implementation(platform(libs.firebase.bom))
    // implementation(libs.firebase.firestore)
    // implementation(libs.firebase.auth)
    // implementation(libs.hilt.android)
    // ksp(libs.hilt.compiler)
    // implementation(libs.androidx.hilt.navigation.compose)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}