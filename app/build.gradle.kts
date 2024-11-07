plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.fileconverter"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        namespace = "com.example.fileconverter"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    // Основные зависимости Android и библиотека поддержки
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Для работы с изображениями (например, Bitmap)
    implementation("com.github.juarezpi:imagemagick:1.9.0")

    // Для работы с видео и аудио с помощью FFmpeg
    implementation("com.arthenica:ffmpeg-kit-full:5.0")

    // Для работы с документами DOCX (Apache POI для DOCX)
    implementation("org.apache.poi:poi-ooxml:5.3.0")
}
