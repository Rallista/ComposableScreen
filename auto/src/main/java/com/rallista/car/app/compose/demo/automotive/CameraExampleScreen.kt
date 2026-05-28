package com.rallista.car.app.compose.demo.automotive

import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarIcon
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.MapController
import androidx.car.app.navigation.model.MapWithContentTemplate
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.IconCompat
import com.rallista.car.app.compose.ComposableScreen
import kotlin.time.TimeSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.location.BearingUpdate
import org.maplibre.compose.location.Location
import org.maplibre.compose.location.LocationProvider
import org.maplibre.compose.location.LocationPuck
import org.maplibre.compose.location.LocationTrackingEffect
import org.maplibre.compose.location.rememberUserLocationState
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.RenderOptions
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position

class CameraExampleScreen(carContext: CarContext) :
    ComposableScreen(carContext = carContext, surfaceTag = TAG) {

  companion object {
    private const val TAG = "CameraExampleScreen"
  }

  private val cameraMode = mutableStateOf<CameraMode>(CameraMode.Centered(DefaultCenteredPosition))

  @Composable
  override fun content() {
    val mode = cameraMode.value
    val cam = rememberCameraState(firstPosition = DefaultCenteredPosition)

    val staticProvider = remember {
      StaticLocationProvider(
          Location(
              position = Position(longitude = -18.529602, latitude = 66.137331),
              accuracy = 5.0,
              bearing = 0.0,
              bearingAccuracy = null,
              speed = null,
              speedAccuracy = null,
              timestamp = TimeSource.Monotonic.markNow()))
    }
    val userLocation = rememberUserLocationState(staticProvider)

    LaunchedEffect(mode) {
      when (mode) {
        is CameraMode.Centered -> cam.animateTo(mode.position)
        is CameraMode.BoundingBoxFit ->
            cam.animateTo(boundingBox = mode.box, padding = PaddingValues(40.dp))
        else -> Unit
      }
    }

    if (mode is CameraMode.TrackingUserLocation ||
        mode is CameraMode.TrackingUserLocationWithBearing) {
      LocationTrackingEffect(userLocation) {
        cam.updateFromLocation(
            updateBearing =
                if (mode is CameraMode.TrackingUserLocationWithBearing) BearingUpdate.TRACK_LOCATION
                else BearingUpdate.IGNORE)
      }
    }

    MaplibreMap(
        modifier = Modifier.fillMaxSize(),
        baseStyle = BaseStyle.Uri("https://demotiles.maplibre.org/style.json"),
        cameraState = cam,
        options =
            MapOptions(
                renderOptions = RenderOptions(renderMode = RenderOptions.RenderMode.TextureView)),
    ) {
      LocationPuck(idPrefix = "puck", locationState = userLocation, cameraState = cam)
    }
  }

  override fun onGetTemplate(): Template {
    return MapWithContentTemplate.Builder()
        .setContentTemplate(
            MessageTemplate.Builder("Camera is currently ${cameraMode.value::class.simpleName}")
                .addAction(
                    Action.Builder()
                        .setTitle("Toggle Camera")
                        .setOnClickListener {
                          cameraMode.value = getNextCamera(cameraMode.value)
                          Log.d(TAG, "Camera value ${cameraMode.value}")
                          invalidate()
                        }
                        .build())
                .build())
        .setActionStrip(
            ActionStrip.Builder()
                .addAction(
                    Action.Builder()
                        .setTitle("View Symbols")
                        .setOnClickListener {
                          Log.d(TAG, "Navigating to SymbolExampleScreen")
                          screenManager.push(
                              SymbolExampleScreen(carContext) { screenManager.pop() })
                        }
                        .build())
                .build())
        .setMapController(
            MapController.Builder()
                .setMapActionStrip(
                    ActionStrip.Builder()
                        .addAction(
                            Action.Builder()
                                .setIcon(
                                    CarIcon.Builder(
                                            IconCompat.createWithResource(
                                                carContext, R.drawable.navigation_24px))
                                        .build())
                                .setOnClickListener {
                                  cameraMode.value = getNextCamera(cameraMode.value)
                                  invalidate()
                                }
                                .build())
                        .addAction(
                            Action.Builder()
                                .setIcon(
                                    CarIcon.Builder(
                                            IconCompat.createWithResource(
                                                carContext, R.drawable.add_24px))
                                        .build())
                                .setOnClickListener { incrementZoom(1.0) }
                                .build())
                        .addAction(
                            Action.Builder()
                                .setIcon(
                                    CarIcon.Builder(
                                            IconCompat.createWithResource(
                                                carContext, R.drawable.remove_24px))
                                        .build())
                                .setOnClickListener { incrementZoom(-1.0) }
                                .build())
                        .addAction(Action.PAN)
                        .build())
                .build())
        .build()
  }

  private fun incrementZoom(delta: Double) {
    val current = cameraMode.value
    if (current is CameraMode.Centered) {
      val newZoom = (current.position.zoom + delta).coerceIn(0.0, 20.0)
      cameraMode.value = CameraMode.Centered(current.position.copy(zoom = newZoom))
      invalidate()
    }
  }
}

private class StaticLocationProvider(initial: Location) : LocationProvider {
  private val state: MutableStateFlow<Location?> = MutableStateFlow(initial)
  override val location: StateFlow<Location?> = state.asStateFlow()
}
