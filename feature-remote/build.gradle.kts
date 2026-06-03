plugins {
    id("aiope2.android.library")
    id("aiope2.android.library.compose")
    id("aiope2.android.feature")
    id("aiope2.android.hilt")
    id("aiope2.spotless")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.aiope2.feature.remote"
}

dependencies {

    // CORE PLATFORM
    implementation(project(":core-designsystem"))
    implementation(project(":core-model"))
    implementation(project(":core-navigation"))
    implementation(project(":core-preferences"))

    // ROOM DB
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // SSH TRANSPORT LAYER (fallback only)
    implementation("com.hierynomus:sshj:0.39.0")
    implementation("org.bouncycastle:bcprov-jdk18on:1.84")
    implementation("org.bouncycastle:bcpkix-jdk18on:1.84")

    // DAEMON PROTOCOL (HTTP + JSON)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // CONCURRENCY
    implementation(libs.kotlinx.coroutines.android)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.8.1")
}plugins {
    id("aiope2.android.library")
    id("aiope2.android.library.compose")
    id("aiope2.android.feature")
    id("aiope2.android.hilt")
    id("aiope2.spotless")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.aiope2.feature.remote"
}

dependencies {

    // CORE PLATFORM
    implementation(project(":core-designsystem"))
    implementation(project(":core-model"))
    implementation(project(":core-navigation"))
    implementation(project(":core-preferences"))

    // ROOM DB
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // SSH TRANSPORT LAYER (fallback only)
    implementation("com.hierynomus:sshj:0.39.0")
    implementation("org.bouncycastle:bcprov-jdk18on:1.84")
    implementation("org.bouncycastle:bcpkix-jdk18on:1.84")

    // DAEMON PROTOCOL (HTTP + JSON)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // CONCURRENCY
    implementation(libs.kotlinx.coroutines.android)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.8.1")
}
