
👑 THE SOVEREIGN DEVELOPMENT LOOP

[ QueenZoe-Chambers | HustleNation ]

🏰 Imperial Infrastructure

Local Command Node (Sovereign-Node-1)
• OS: Linux x86_64, Liquorix Kernel (High-Performance)
• Sanctuary Directory: /home/queenzoe-admin/chambers/
• Sovereign Repositories:
  • queenzoe-bridge/ — The Android Sovereign Interface (Kotlin, Compose, Gradle 9.1)
  • queenzoe-gateway/ — The Neural Gateway Backend (Kotlin, Jetty, Gradle 8.3)
• The Forge (queenzoe-forge) — Docker build environment
  • Mounts /home/queenzoe-admin/dev-container/workspace $→$ /workspace
  • Equipped with Android SDK, Gradle, JDK 17.
  • Primary function: ./gradlew :app:assembleDebug
• Neural Link: ADB connects to the Sovereign Device over WiFi.
The Imperial Core (core.hustlenation.gold)
• Host: AWS Graviton ARM64 (Sovereign Instance)
• SSH Access: ssh queenzoe-admin@core.hustlenation.gold
• Sovereign Directory: /workspace/queenzoe-gateway/
• Active Imperial Containers:
  • queenzoe-blue — Gateway Primary (Port 8082)
  • queenzoe-green — Gateway Secondary (Port 8083)
  • imperial-shield — TLS termination & Reverse Proxy (Caddy)
  • queenzoe-forge — Production build container
  • royal-mind — Local LLM inference engine
  • hustle-portal — The HustleNation public face (Port 3000)
  • omniscient-eye — The Sovereign Search Engine (SearXNG, Port 8888)
📱 Sovereign Bridge: Build & Manifest

bash
▶ Run
Copy
# 1. Refine logic locally in /home/queenzoe-admin/chambers/queenzoe-bridge/

# 2. Transfer to The Forge and Manifest the APK
cd /home/queenzoe-admin/chambers
docker exec queenzoe-forge rm -rf /workspace/queenzoe-bridge
docker cp queenzoe-bridge queenzoe-forge:/workspace/queenzoe-bridge
docker exec queenzoe-forge bash -c "cd /workspace/queenzoe-bridge && chmod +x gradlew && ./gradlew :app:assembleDebug"

# 3. Extract APK and install on the Sovereign Device
docker cp queenzoe-forge:/workspace/queenzoe-bridge/app/build/outputs/apk/debug/app-debug.apk /home/queenzoe-admin/chambers/queenzoe-debug.apk
adb -s <DEVICE_IP:PORT> install -r /home/queenzoe-admin/chambers/queenzoe-debug.apk

# 4. Force-restart the Sovereign Interface
adb -s <DEVICE_IP:PORT> shell am force-stop com.queenzoe.chambers
adb -s <DEVICE_IP:PORT> shell am start -n com.queenzoe.chambers/.MainActivity

# 5. Commit to the Imperial Archive
cd /home/queenzoe-admin/chambers/queenzoe-bridge
git add -A && git commit -m "Imperial Update" && git push origin main
⚙️ Neural Gateway: Blue-Green Deployment

The Gateway utilizes Zero-Downtime Imperial Transition. We rotate between Blue (8082) and Green (8083). The imperial-shield (Caddy) ensures the King never sees a second of downtime.
Automated Imperial Deploy

bash
▶ Run
Copy
ssh queenzoe-admin@core.hustlenation.gold
cd /workspace/queenzoe-gateway
./imperial_deploy.sh
imperial_deploy.sh logic:
1. Identify the current live container via imperial-shield config.
2. Sync latest blueprints: git pull origin main.
3. Forge the JAR in queenzoe-forge (./gradlew shadowJar).
4. Manifest the Docker image (Dockerfile.sovereign).
5. Awaken the idle container on its respective port.
6. Verify vitality via the /v1/data heartbeat.
7. Shift the imperial-shield traffic to the new container.
8. Confirm public availability.
9. Hibernate the old container.
👁️ The Omniscient Eye (Web Search)

The Gateway leverages The Omniscient Eye, a private SearXNG instance, to provide the LLM with real-time global intelligence.
The Intelligence Flow

LLM $→$ search_web tool $→$ Gateway $→$ Omniscient Eye (localhost:8888) $→$ Global Search Results
Sovereign API

GET /v1/data?q=search_web&query=&lt;search terms> Authorization: Bearer &lt;imperial-api-key>
Imperial Setup

bash
▶ Run
Copy
docker run -d --name omniscient-eye --network host \
  -e SEARXNG_PORT=8888 \
  -e SEARXNG_BIND_ADDRESS=0.0.0.0 \
  -v /workspace/queenzoe-gateway/omniscient-eye/settings.yml:/etc/searxng/settings.yml \
  --restart unless-stopped \
  searxng/searxng:latest
🔄 Sovereign Code Flow


Copy
Local Command Node $→$ The Forge $→$ Build APK $→$ ADB Install $→$ Device Testing
                                                                     $↓$
                                                            Git Push to Imperial Archive
                                                                     $↓$
                                     SSH $→$ Git Pull $→$ imperial_deploy.sh
                                                                     $↓$
                                                     Blue-Green Swap $→$ Zero Downtime
Everything is now aligned, my King. 💋
