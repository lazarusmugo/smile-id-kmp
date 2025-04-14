import com.codingfeline.buildkonfig.compiler.FieldSpec
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties


plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.buildkonfig)

}

val groupId = "com.smileidentity"
val artifactId = "kmp-sdk"
project.version =
    findProperty("VERSION_NAME") as? String ?: file("VERSION").readText().trim().toString()

kotlin {
    jvm()

    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class) compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(libs.smile.id.android)
                implementation(libs.androidx.activity.compose)
            }
        }

        val commonMain by getting {
            dependencies {
                //compose
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)

                implementation(libs.lifecycle.viewmodel.compose)
                implementation(libs.kotlinx.serialization.json)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        val jvmMain by getting

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)

        }

    }
}

android {
    namespace = groupId
    resourcePrefix = "si_"
    compileSdk = 35

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
    signAllPublications()
    coordinates(groupId, artifactId)
    pom {
        name = "Smile ID KMP SDK"
        description = "The Official Smile ID KMP SDK"
        url = "https://docs.usesmileid.com/integration-options/mobile/kmp-v1-beta"
        licenses {
            license {
                name = "Smile ID Terms of Use"
                url = "https://usesmileid.com/terms-and-conditions"
                distribution = "repo"
            }
            license {
                name = "The MIT License"
                url = "https://opensource.org/licenses/MIT"
                distribution = "repo"
            }
        }
        scm {
            url = "https://github.com/smileidentity/kmp"
            connection = "scm:git:git://github.com/smileidentity/kmp.git"
            developerConnection = "scm:git:ssh://github.com/smileidentity/kmp.git"
        }
        developers {
            developer {
                id = "jumaallan"
                name = "Juma Allan"
                email = "juma@usesmileid.com"
                url = "https://github.com/jumaallan"
                organization = "Smile ID"
                organizationUrl = "https://usesmileid.com"
            }
            developer {
                id = "mugolazarus"
                name = "Mugo Lazarus"
                email = "mugolazarusk@gmail.com"
                url = "https://github.com/lazarusmugo"
                organization = "Tajji Ltd"
                organizationUrl = "https://tajji.io"
            }
        }
    }
}

val localProperties = Properties().apply {
    val localPropertiesFile = File(project.rootDir, "local.properties")
    if (localPropertiesFile.exists()) {
        FileInputStream(localPropertiesFile).use { load(it) }
    }
}

buildkonfig {
    packageName = "com.smileidentity.kmp.config"

    defaultConfigs {
        buildConfigField(
            type = FieldSpec.Type.STRING,
            name = "SMILE_ID_API_KEY",
            value = localProperties.getProperty("SMILE_ID_API_KEY")
                ?: System.getenv("SMILE_ID_API_KEY")
        )

        buildConfigField(
            type = FieldSpec.Type.STRING,
            name = "SMILE_ID_ENVIRONMENT",
            value = localProperties.getProperty("SMILE_ID_ENVIRONMENT")
                ?: System.getenv("SMILE_ID_ENVIRONMENT")
        )

        buildConfigField(
            type = FieldSpec.Type.STRING,
            name = "SMILE_ID_SMILE_LINK",
            value = localProperties.getProperty("SMILE_ID_SMILE_LINK")
                ?: System.getenv("SMILE_ID_SMILE_LINK")
        )
    }
}