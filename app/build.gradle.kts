/**
 * I'd create a `buildSrc` directory, and define all the dependencies alongside their
 * versions in there.
 * Not only dependencies, but also all the sdk version, build versions, namespace, version name
 * and version code, basically I'd remove any hardcode from build files except a few of them.
 * */
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
}

android {
    namespace = "com.example.linenassignment"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.linenassignment"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            // In a real world project, I'd create a `remote` or `common` module, and add
            // these endpoints configurations there. Instead of hardcoding, I'd also add the endpoint
            // to the `buildSrc` module.
            buildConfigField("String", "BASE_URL", "\"https://rpc.ankr.com/eth/\"")
        }
        release {
            buildConfigField("String", "BASE_URL", "\"https://rpc.ankr.com/eth/\"")
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
        viewBinding = true
    }
    packagingOptions {
        resources.merges.add("build-data.properties")
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
kapt {
    correctErrorTypes = true
}

dependencies {
    val coroutines = "1.6.4"

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.fragment:fragment-ktx:1.5.6")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutines}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${coroutines}")
    /**
     * In MavenCentral a list of vulnerabilities have been reported for these libraries'
     * dependencies, but I couldn't find any better replacement.
     * https://mvnrepository.com/artifact/org.web3j/core/5.0.0
     * */
    implementation("org.web3j:core:5.0.0")
    implementation("org.web3j:contracts:5.0.0")

    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.truth:truth:1.1.3")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("io.mockk:mockk:1.13.4")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}