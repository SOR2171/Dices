import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(project(":shared"))

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "io.github.sor2171.dices.MainKt"

        nativeDistributions {
            targetFormats(
                TargetFormat.Msi,    // Windows
                TargetFormat.Exe,    // Windows
                TargetFormat.Dmg,    // macOS
                TargetFormat.Pkg,    // macOS
                TargetFormat.Deb,    // Linux
                TargetFormat.Rpm     // Linux
            )
            packageName = AppConfig.packageName
            packageVersion = AppConfig.appVersion
            description = AppConfig.appName
        }
    }
}