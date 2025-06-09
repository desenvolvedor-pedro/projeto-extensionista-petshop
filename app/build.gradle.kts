plugins {
    alias(libs.plugins.android.application)

    /*código abaixo ja esta sendo utilizado em algum lugar, mas estará aqui caso precise.
    id("com.android.application")
    */

    //código firebase
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.petshop_teste02"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.petshop_teste02"
        minSdk = 27
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
}


dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)




    //código firebase
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))

    implementation("com.google.firebase:firebase-database-ktx") // Versão será gerenciada pelo BOM
    implementation("com.firebaseui:firebase-ui-database:8.0.2") // Versão atualizada

    //Glide
    implementation("com.github.bumptech.glide:glide:4.16.0") // Versão atualizada
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // RecyclerView (adicionado explicitamente)
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Lifecycle components (opcional, mas recomendado)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2") {
        because("Versão específica para evitar crashes com FirebaseUI")
    }

    //implementação do authentication
    implementation("com.google.firebase:firebase-auth")

    //implementação para o banco de dados
    implementation("com.google.firebase:firebase-firestore")


}
