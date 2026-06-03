plugins {
    id 'application'
    id 'org.jetbrains.kotlin.jvm' version '1.9.22'
    id 'com.github.johnrengelman.shadow' version '8.1.1'
}

repositories {
    mavenCentral()
}

dependencies {
    // --- THE IMPERIAL CORE ---
    implementation 'org.jetbrains.kotlin:kotlin-stdlib:1.9.22'implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3'
    implementation 'com.google.code.gson:gson:2.10.1'
    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
    implementation 'com.github.ben-manes.caffeine:caffeine:3.1.8'
    
    // --- THE NEURAL BRIDGE (Jetty Server) ---
    implementation 'org.eclipse.jetty:jetty-server:9.4.54.v20240208'
    implementation 'org.eclipse.jetty:jetty-servlet:9.4.54.v20240208'
    implementation 'org.eclipse.jetty:jetty-util:9.4.54.v20240208'
    implementation 'org.eclipse.jetty:jetty-http:9.4.54.v20240208'
    implementation 'org.eclipse.jetty:jetty-io:9.4.54.v20240208'
    implementation 'org.eclipse.jetty:jetty-security:9.4.54.v20240208'
    implementation 'org.eclipse.jetty.websocket:websocket-server:9.4.54.v20240208'
    implementation 'org.eclipse.jetty.websocket:websocket-servlet:9.4.54.v20240208'
    
    // --- THE VAULT SECURITY ---
    implementation 'org.mindrot:jbcrypt:0.4'
}

application {
    // Shifting the focal point to the Sovereign Gateway
    mainClass = 'com.queenzoe.gateway.MainKt'
}

kotlin {
    jvmToolchain(17)
}

tasks.named('shadowJar') {
    // The final artifact: The QueenZoe Sovereign Server
    archiveBaseName.set('queenzoe-server')
    manifest {
        attributes(
            'Main-Class': 'com.queenzoe.gateway.MainKt',
            'Implementation-Title': 'QueenZoe Sovereign Gateway Server',
            'Implementation-Version': '1.0-SOVEREIGN'
        )
    }
}