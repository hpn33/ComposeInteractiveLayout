plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")

//    id("app.cash.sqldelight")
}

kotlin {
    androidTarget()

    jvm("desktop")

//    listOf(
//        iosX64(),
//        iosArm64(),
//        iosSimulatorArm64()
//    ).forEach { iosTarget ->
//        iosTarget.binaries.framework {
//            baseName = "shared"
//            isStatic = true
//        }
//    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)

//                api(compose.materialIconsExtended)
//                api(compose.material3)


//                implementation("io.github.xxfast:kstore:0.6.0")
//                implementation("com.aminography:primecalendar:1.7.0")
//                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")

//                implementation("ca.gosyer:compose-material-dialogs-datetime:0.9.3")
//                implementation("io.github.vanpra.compose-material-dialogs:core:0.9.0")
//                implementation("io.github.vanpra.compose-material-dialogs:datetime:0.9.0")
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.activity:activity-compose:1.7.2")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.10.1")

//                implementation("app.cash.sqldelight:android-driver:2.0.0")
            }
        }
//        val iosX64Main by getting
//        val iosArm64Main by getting
//        val iosSimulatorArm64Main by getting
//        val iosMain by creating {
//            dependsOn(commonMain)
//            iosX64Main.dependsOn(this)
//            iosArm64Main.dependsOn(this)
//            iosSimulatorArm64Main.dependsOn(this)
//        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.common)

//                implementation("app.cash.sqldelight:sqlite-driver:2.0.0")
            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.myapplication.common"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        jvmToolchain(11)
    }
}

//sqldelight {
//    databases {
//
//        create("AppDatabase") {
//            packageName.set("hpn332.pms.db")
//
//        }
//    }
//
//}