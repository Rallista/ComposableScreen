# ComposableScreen

Render [Jetpack Compose](https://developer.android.com/jetpack/compose) content onto the
custom map surface of an [AndroidX Car App](https://developer.android.com/training/cars/apps)
`Screen` — for both Android Auto and Android Automotive OS.

The Car App library hands your app a raw `Surface` for the map area but no view system on top of
it. `ComposableScreen` hosts that surface on a virtual display and presents a `ComposeView` into it,
so you can write your map/overlay UI as ordinary `@Composable` functions while still returning a
normal Car App `Template` for the rest of the screen.

## Installation

```kotlin
dependencies {
  implementation("io.github.rallista:car-app-compose:<version>")
}
```

## Compatibility

| Requirement        | Version                                            |
| ------------------ | -------------------------------------------------- |
| `minSdk`           | 29                                                 |
| AndroidX Car App   | 1.7.0 (`androidx.car.app:app`)                     |
| Jetpack Compose    | BOM `2026.02.01` (Compose runtime + UI)            |
| Kotlin             | 2.3.10                                             |
| JDK / Java         | 21                                                 |

> **Note:** This library depends on `androidx.car.app:app`, *not*
> `androidx.car.app:app-automotive`, so it stays compatible with Android Auto. The Compose and Car
> App dependencies are exposed as `api`, so they are available on your compile classpath when you
> subclass `ComposableScreen`.

## Required permissions

To draw on the map surface, your app must request the Car App surface and map template permissions
in its `AndroidManifest.xml`:

```xml
<uses-permission android:name="androidx.car.app.ACCESS_SURFACE" />
<uses-permission android:name="androidx.car.app.MAP_TEMPLATES" />
```

Anything your Compose content itself needs (for example `ACCESS_FINE_LOCATION` /
`ACCESS_COARSE_LOCATION` for a location puck) must be declared and requested as usual.

## Usage

Subclass `ComposableScreen`, render your UI in `content()`, and return a Car App `Template` from
`onGetTemplate()`:

```kotlin
import androidx.car.app.CarContext
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.MapWithContentTemplate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rallista.car.app.compose.ComposableScreen

class MapScreen(carContext: CarContext) :
    ComposableScreen(carContext, surfaceTag = "MapScreen") {

  // Rendered onto the Android Auto / Automotive map surface.
  @Composable
  override fun content() {
    Text("Hello from Compose", modifier = Modifier.fillMaxSize())
  }

  // The Car App template (action strips, lists, etc.) drawn around the surface.
  override fun onGetTemplate(): Template =
      MapWithContentTemplate.Builder().build()
}
```

Return the screen from your `CarAppService` session and register the service in the manifest:

```kotlin
class ExampleCarAppService : CarAppService() {
  override fun createHostValidator(): HostValidator =
      HostValidator.ALLOW_ALL_HOSTS_VALIDATOR // use a real validator in production

  override fun onCreateSession(sessionInfo: SessionInfo): Session =
      object : Session() {
        override fun onCreateScreen(intent: Intent): Screen = MapScreen(carContext)
      }
}
```

```xml
<service
    android:name=".ExampleCarAppService"
    android:exported="true">
    <intent-filter>
        <action android:name="androidx.car.app.CarAppService" />
        <category android:name="androidx.car.app.category.POI" />
    </intent-filter>
</service>
```

### Surface gestures

To react to pan/zoom/fling gestures and visible-area changes on the surface, set a
`SurfaceGestureCallback`:

```kotlin
surfaceGestureCallback = object : SurfaceGestureCallback {
  override fun onScroll(distanceX: Float, distanceY: Float) { /* ... */ }
  override fun onScale(focusX: Float, focusY: Float, scaleFactor: Float) { /* ... */ }
}
```

## License

See [LICENSE](LICENSE).
