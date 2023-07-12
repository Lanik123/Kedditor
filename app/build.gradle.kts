import org.jlleitschuh.gradle.ktlint.KtlintExtension

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)

    // Is third party plugins
    alias(libs.plugins.kapt)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ktlintPlugin)
    alias(libs.plugins.daggerPlugin)
}

android {
    namespace = "ru.lanik.kedditor"
    compileSdk = 33

    defaultConfig {
        applicationId = "ru.lanik.kedditor"
        minSdk = 23
        targetSdk = 33
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
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    // Is third party dependencies
    implementation(libs.json.serialization)
    implementation(libs.rxjava3.rxjava)
    implementation(libs.rxjava3.rxandroid)
    implementation(libs.rxjava3.rxkotlin)
    implementation(libs.constraintlayout)
    implementation(libs.appcompat)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.fragment.ktx)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
    implementation(libs.datastore)
    implementation(libs.datastore.rx)
    implementation(libs.paging.runtime)
    implementation(libs.paging.compose)
    implementation(libs.dagger.hilt.android)
    kapt(libs.dagger.hilt.compiler)
    testImplementation(libs.dagger.hilt.android.testing)
    kaptTest(libs.dagger.hilt.compiler)
    androidTestImplementation(libs.dagger.hilt.android.testing)
    kaptAndroidTest(libs.dagger.hilt.compiler)
    debugImplementation(libs.leakcanary)

    // Project lib module
    implementation(project(mapOf("path" to ":network")))
}

kapt {
    correctErrorTypes = true
}

configure<KtlintExtension> {
    version.set("0.49.1")
}
