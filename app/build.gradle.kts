// // plugins {
// //     id("com.android.application")
// //     id("org.jetbrains.kotlin.android")
// //     jacoco
// // }

// // android {
// //     namespace = "com.quickchat.app"
// //     compileSdk = 34
// //     buildToolsVersion = "34.0.0"

// //     defaultConfig {
// //         applicationId = "com.quickchat.app"
// //         minSdk = 26
// //         targetSdk = 34
// //         versionCode = 1
// //         versionName = "1.0"

// //         testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
// //     }

// //     buildTypes {
// //         release {
// //             isMinifyEnabled = false
// //             proguardFiles(
// //                 getDefaultProguardFile("proguard-android-optimize.txt"),
// //                 "proguard-rules.pro"
// //             )
// //         }
// //     }

// //     compileOptions {
// //         sourceCompatibility = JavaVersion.VERSION_1_8
// //         targetCompatibility = JavaVersion.VERSION_1_8
// //     }

// //     kotlinOptions {
// //         jvmTarget = "1.8"
// //     }

// //     buildFeatures {
// //         viewBinding = true
// //     }

// //     testOptions {
// //         unitTests.all {

// //             unitTests {
// //             all {
// //                 it.useJUnitPlatform()   // ← syntaxe correcte pour .kts
// //             }
// //         }
// //             it.extensions.configure(org.gradle.testing.jacoco.plugins.JacocoTaskExtension::class.java) {
// //                 isIncludeNoLocationClasses = true
// //                 excludes = listOf("jdk.internal.*")
// //             }
// //         }
// //     }
// // }

// // dependencies {
// //     implementation("androidx.core:core-ktx:1.12.0")
// //     implementation("androidx.appcompat:appcompat:1.6.1")
// //     implementation("com.google.android.material:material:1.11.0")
// //     implementation("androidx.constraintlayout:constraintlayout:2.1.4")
// //     implementation("androidx.recyclerview:recyclerview:1.3.2")
// //     implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
// //     implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
// //     implementation("androidx.activity:activity-ktx:1.8.2")


// //     // Android instrumented tests
// //     androidTestImplementation("androidx.test.ext:junit:1.1.5")
// //     androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

// //     testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
// // testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.3")
// // testImplementation("io.mockk:mockk:1.13.8")
// // testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
// // testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")
// // }

// // tasks.register<JacocoReport>("jacocoTestReport") {
// //     dependsOn("testDebugUnitTest")

// //     reports {
// //         xml.required.set(true)
// //         html.required.set(true)
// //     }

// //     val fileFilter = listOf(
// //         "**/R.class",
// //         "**/R$*.class",
// //         "**/BuildConfig.*",
// //         "**/Manifest*.*",
// //         "**/*Test*.*",
// //         "**/android/**/*.*"
// //     )

// //     val debugTree = fileTree("${buildDir}/tmp/kotlin-classes/debug") {
// //         exclude(fileFilter)
// //     }

// //     classDirectories.setFrom(files(debugTree))
// //     sourceDirectories.setFrom(files("$projectDir/src/main/java", "$projectDir/src/main/kotlin"))
// //     executionData.setFrom(fileTree(buildDir) {
// //         include(
// //             "jacoco/testDebugUnitTest.exec",
// //             "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"
// //         )
// //     })
// // }
// plugins {
//     id("com.android.application")
//     id("org.jetbrains.kotlin.android")
//     jacoco
// }

// android {
//     namespace = "com.quickchat.app"
//     compileSdk = 34
//     buildToolsVersion = "34.0.0"

//     defaultConfig {
//         applicationId = "com.quickchat.app"
//         minSdk = 26
//         targetSdk = 34
//         versionCode = 1
//         versionName = "1.0"
//         testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//     }

//     buildTypes {
//         release {
//             isMinifyEnabled = false
//             proguardFiles(
//                 getDefaultProguardFile("proguard-android-optimize.txt"),
//                 "proguard-rules.pro"
//             )
//         }
//     }

//     compileOptions {
//         sourceCompatibility = JavaVersion.VERSION_1_8
//         targetCompatibility = JavaVersion.VERSION_1_8
//     }

//     kotlinOptions {
//         jvmTarget = "1.8"
//     }

//     buildFeatures {
//         viewBinding = true
//     }

//     testOptions {
//         unitTests.all {
//             it.useJUnitPlatform()
//             it.extensions.configure(org.gradle.testing.jacoco.plugins.JacocoTaskExtension::class.java) {
//                 isIncludeNoLocationClasses = true
//                 excludes = listOf("jdk.internal.*")
//             }
//         }
//     }
// }

// dependencies {
//     implementation("androidx.core:core-ktx:1.12.0")
//     implementation("androidx.appcompat:appcompat:1.6.1")
//     implementation("com.google.android.material:material:1.11.0")
//     implementation("androidx.constraintlayout:constraintlayout:2.1.4")
//     implementation("androidx.recyclerview:recyclerview:1.3.2")
//     implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
//     implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
//     implementation("androidx.activity:activity-ktx:1.8.2")

//     androidTestImplementation("androidx.test.ext:junit:1.1.5")
//     androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

//     testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
//     testImplementation("io.mockk:mockk:1.13.8")
//     testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
//     testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")
// }

// tasks.register<JacocoReport>("jacocoTestReport") {
//     dependsOn("testDebugUnitTest")

//     reports {
//         xml.required.set(true)
//         html.required.set(true)
//     }

//     val fileFilter = listOf(
//         "**/R.class",
//         "**/R$*.class",
//         "**/BuildConfig.*",
//         "**/Manifest*.*",
//         "**/*Test*.*",
//         "**/android/**/*.*"
//     )

//     val debugTree = fileTree("${layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
//         exclude(fileFilter)
//     }

//     classDirectories.setFrom(files(debugTree))
//     sourceDirectories.setFrom(files("$projectDir/src/main/java", "$projectDir/src/main/kotlin"))
//     executionData.setFrom(fileTree(layout.buildDirectory.get()) {
//         include(
//             "jacoco/testDebugUnitTest.exec",
//             "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"
//         )
//     })
// }
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("jacoco")
}

android {
    namespace = "com.quickchat.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.quickchat.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // ViewModel et LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.activity:activity-ktx:1.8.2")


    // Testing - JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    
    // MockK
    testImplementation("io.mockk:mockk:1.13.8")
    
    // Coroutines Test
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    
    // Android Test
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    testImplementation("com.google.truth:truth:1.1.5")
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport")
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")
    
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    
    val fileFilter = listOf(
        "**/R.class", "**/R$*.class", "**/BuildConfig.*",
        "**/Manifest*.*", "**/*Test*.*", "android/**/*.*"
    )
    
    val debugTree = fileTree("${buildDir}/intermediates/javac/debug") {
        exclude(fileFilter)
    }
    val kotlinDebugTree = fileTree("${buildDir}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }
    
    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))
    classDirectories.setFrom(files(debugTree, kotlinDebugTree))
    executionData.setFrom(fileTree(buildDir) {
        include("**/*.exec", "**/*.ec")
    })
}