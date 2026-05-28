import com.vanniktech.maven.publish.AndroidSingleVariantLibrary

plugins {
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.jetbrainsKotlinAndroid)
  alias(libs.plugins.ktfmt)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.mavenPublish)
  alias(libs.plugins.dokka)
  id("maven-publish")
  id("CommonPomConventionPlugin")
}

dokka { dokkaSourceSets.configureEach { includes.from("dokka/module.md") } }

android {
  namespace = "com.rallista.car.app.compose"
  compileSdk { version = release(36) }

  defaultConfig {
    minSdk = 29

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }
  kotlin { jvmToolchain(21) }
  buildFeatures { compose = true }
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  // Important!
  // This library MUST NOT use libs.androidx.car.app.automotive
  // if we want it to remain compatible with Android Auto.
  // Exposed via ComposableScreen (CarContext, Screen, Template), so consumers
  // need it on their compile classpath when subclassing.
  api(libs.androidx.car.app)

  // Compose runtime/ui types appear in the public API (@Composable content lambdas),
  // so they must be `api` to remain available to downstream subclasses.
  api(platform(libs.androidx.compose.bom))
  api(libs.androidx.ui)
  api(libs.androidx.compose.runtime)

  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
}

mavenPublishing {
  publishToMavenCentral()
  signAllPublications()

  coordinates("io.github.rallista", "car-app-compose", project.version.toString())

  configure(AndroidSingleVariantLibrary(sourcesJar = true, publishJavadocJar = true))
}

mavenPublishing {
  pom {
    name.set("Car App Compose")
    description.set(
        "Compose UI extensions for the AndroidX Car App library (Android Auto and Android Automotive OS)")
  }
}
