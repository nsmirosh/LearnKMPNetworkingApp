1. Build/Configuration Instructions
- JDK: Use the toolchain configured by Gradle. If you build from CLI and encounter memory errors, add or increase heap in gradle.properties: org.gradle.jvmargs=-Xmx4g -Dkotlin.daemon.jvm.options=-Xmx4g
- Android: Build debug APK via ./gradlew :composeApp:assembleDebug. Logs emitted with println(..) appear in Logcat under the "System.out" tag.
- iOS: Use Xcode for the iosApp target. The shared KMP module produces a Darwin-based Ktor client. If you build iOS binaries from Gradle and see bundleId warnings, supply a bundle ID for the K/N binary: kotlin { ios { binaries.all { freeCompilerArgs += listOf("-Xbinary=bundleId=com.example.composeapp") } } } in composeApp/build.gradle.kts.
- Ktor client location: The platform-specific HttpClient builders live in:
  - Android: composeApp/src/androidMain/.../helpers/NetworkHelper.android.kt
  - iOS: composeApp/src/iosMain/.../helpers/NetworkHelper.ios.kt
  The simplified logging interceptor is installed via client.plugin(HttpSend).intercept {...} in createPlatformHttpClient2.

2. Testing Information
- Running existing tests: The project currently contains common tests in composeApp/src/commonTest. Run from CLI:
  - All KMP tests: ./gradlew :composeApp:allTests
  - iOS simulator tests (Intel): ./gradlew :composeApp:iosX64Test; (Apple Silicon): ./gradlew :composeApp:iosSimulatorArm64Test
  Notes: If Kotlin/Native runs out of memory locally, increase Gradle/Kotlin daemon heap as above.
- Adding a new simple test: Place tests under composeApp/src/commonTest/kotlin. Example:
  package com.learnkmp
  import kotlin.test.Test
  import kotlin.test.assertTrue
  class SimpleSanityTest {
      @Test fun itWorks() { assertTrue(1 + 1 == 2) }
  }
  Then run ./gradlew :composeApp:allTests. This avoids networking and executes on supported test targets.
- Demonstrating the interceptor in tests: Networking tests in commonTest are discouraged without wiring a mock engine. If needed, prefer Ktor's MockEngine in commonTest and keep platform-specific engines (OkHttp/Darwin) for runtime.

3. Additional Development Information
- Interceptor usage (simplified): In both Android and iOS implementations of createPlatformHttpClient2 we now log only the essentials to reduce complexity:
  [HTTP] -> METHOD URL
  [HTTP] <- STATUS URL
  For POST requests, if the server returns a Location header, we forward the HTTPS-normalized value to the onNewBlobUrl callback.
- When to use which client:
  - createPlatformHttpClient: Basic client with ContentNegotiation and ResponseObserver for POST Location header handling.
  - createPlatformHttpClient2: Same JSON config + simple request/response logging via HttpSend intercept.
- JSON serialization: kotlinx.serialization Json is configured with prettyPrint, isLenient, and ignoreUnknownKeys in both clients.
- Code style: Keep platform-specific code minimal and mirror changes between Android and iOS to maintain parity. Avoid logging request/response bodies to prevent consuming streams. Prefer println for portability; replace with platform loggers if needed.

Important Notes
- This information targets advanced KMP developers and focuses on project-specific details.
- The provided test example is intentionally trivial to ensure reliable execution across targets. If your environment disallows running tests or iOS simulators, you can still run Gradle configuration and metadata tasks to validate the setup.
- No additional files were added; retain only .junie/guidelines.md if you create any temporary documentation.