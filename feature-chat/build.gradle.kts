plugins {
  id("aiope2.android.library")
  id("aiope2.android.library.compose")
  id("aiope2.android.feature")
  id("aiope2.android.hilt")
  id("aiope2.spotless")
  id("com.google.devtools.ksp")
}

android {
  namespace = "com.aiope2.feature.chat"

  defaultConfig {
    buildConfigField(
      "String",
      "GATEWAY_KEY",
      "\"${project.findProperty("GATEWAY_KEY") ?: ""}\""
    )
  }

  buildFeatures {
    buildConfig = true
  }
}

dependencies {

  implementation(project(":core-data"))
  implementation(project(":core-model"))
  implementation(project(":core-terminal"))

  implementation(libs.androidx.lifecycle.runtimeCompose)
  implementation(libs.androidx.lifecycle.viewModelCompose)

  implementation(libs.okhttp)
  implementation(libs.okhttp.sse)

  implementation("org.json:json:20240303")

  implementation("com.squareup.okhttp3:okhttp:4.12.0")

  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.room.ktx)
  ksp(libs.androidx.room.compiler)

  implementation("androidx.datastore:datastore-preferences:1.1.7")

  implementation("io.coil-kt:coil-compose:2.6.0")
}