package com.rallista.car.app.compose.demo.automotive

import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.MapWithContentTemplate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rallista.car.app.compose.ComposableScreen
import kotlinx.serialization.json.JsonObject
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.value.SymbolAnchor
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.RenderOptions
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.ClickResult
import org.maplibre.spatialk.geojson.Feature
import org.maplibre.spatialk.geojson.LineString
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position

class SymbolExampleScreen(carContext: CarContext, private val onNavigateBack: () -> Unit) :
    ComposableScreen(carContext = carContext, surfaceTag = TAG) {

  companion object {
    private const val TAG = "SymbolExampleScreen"
  }

  @Composable
  override fun content() {
    val cam =
        rememberCameraState(
            firstPosition =
                CameraPosition(
                    target = Position(longitude = 103.962, latitude = 1.227), zoom = 10.0))

    MaplibreMap(
        modifier = Modifier.fillMaxSize(),
        baseStyle = BaseStyle.Uri("https://demotiles.maplibre.org/style.json"),
        cameraState = cam,
        options =
            MapOptions(
                renderOptions = RenderOptions(renderMode = RenderOptions.RenderMode.TextureView)),
        onMapClick = { pos, _ ->
          Log.d(TAG, "Tapped at $pos")
          ClickResult.Pass
        },
    ) {
      val redStarSrc =
          rememberGeoJsonSource(
              GeoJsonData.Features(pointFeature(Position(longitude = 103.873, latitude = 1.203))))
      val blueCircleSrc =
          rememberGeoJsonSource(
              GeoJsonData.Features(pointFeature(Position(longitude = 104.019, latitude = 1.253))))
      val blueStarSrc =
          rememberGeoJsonSource(
              GeoJsonData.Features(pointFeature(Position(longitude = 104.019, latitude = 1.253))))
      val defaultLabelSrc =
          rememberGeoJsonSource(
              GeoJsonData.Features(pointFeature(Position(longitude = 103.969, latitude = 1.173))))
      val customLabelSrc =
          rememberGeoJsonSource(
              GeoJsonData.Features(pointFeature(Position(longitude = 103.902, latitude = 1.126))))
      val routeSrc =
          rememberGeoJsonSource(
              GeoJsonData.Features(
                  lineFeature(
                      listOf(
                          Position(longitude = 103.813, latitude = 1.147),
                          Position(longitude = 103.887, latitude = 1.259),
                          Position(longitude = 103.931, latitude = 1.205),
                          Position(longitude = 103.993, latitude = 1.295)))))

      LineLayer(
          id = "route",
          source = routeSrc,
          width = const(12.dp),
          pattern = image(painterResource(R.drawable.arrow)),
          opacity = const(0.5f))

      SymbolLayer(
          id = "red-star",
          source = redStarSrc,
          iconImage = image(painterResource(R.drawable.bitmap)),
          iconSize = const(2f),
          iconRotate = const(-20f),
          iconAllowOverlap = const(true),
          onClick = {
            Log.d(TAG, "Tapped red star")
            ClickResult.Consume
          },
          onLongClick = {
            Log.d(TAG, "Long pressed red star")
            ClickResult.Consume
          })

      CircleLayer(
          id = "blue-circle",
          source = blueCircleSrc,
          radius = const(2.dp),
          color = const(Color.Blue))

      SymbolLayer(
          id = "blue-star",
          source = blueStarSrc,
          iconImage = image(painterResource(R.drawable.vector)),
          iconOffset = const(DpOffset((-20).dp, 20.dp)),
          iconAllowOverlap = const(true),
          onClick = {
            Log.d(TAG, "Tapped blue star")
            ClickResult.Consume
          },
          onLongClick = {
            Log.d(TAG, "Long pressed blue star")
            ClickResult.Consume
          })

      CircleLayer(
          id = "default-circle",
          source = defaultLabelSrc,
          radius = const(10.dp),
          color = const(Color.Black),
          onClick = {
            Log.d(TAG, "Tapped default circle")
            ClickResult.Consume
          },
          onLongClick = {
            Log.d(TAG, "Long pressed default circle")
            ClickResult.Consume
          })
      SymbolLayer(
          id = "default-text",
          source = defaultLabelSrc,
          textField = const("Default").cast(),
          textColor = const(Color.White))

      CircleLayer(
          id = "custom-circle",
          source = customLabelSrc,
          radius = const(10.dp),
          color = const(Color.Black),
          onClick = {
            Log.d(TAG, "Tapped custom circle")
            ClickResult.Consume
          },
          onLongClick = {
            Log.d(TAG, "Long pressed custom circle")
            ClickResult.Consume
          })
      SymbolLayer(
          id = "custom-text",
          source = customLabelSrc,
          textField = const("Custom").cast(),
          textSize = const(20.sp),
          textColor = const(Color.Red),
          textAnchor = const(SymbolAnchor.BottomRight))
    }
  }

  override fun onGetTemplate(): Template {
    return MapWithContentTemplate.Builder()
        .setContentTemplate(MessageTemplate.Builder("Symbol Example").build())
        .setActionStrip(
            ActionStrip.Builder()
                .addAction(
                    Action.Builder()
                        .setTitle("View Camera")
                        .setOnClickListener {
                          Log.d(TAG, "Navigating back to CameraExampleScreen")
                          onNavigateBack()
                        }
                        .build())
                .build())
        .build()
  }
}

private fun pointFeature(pos: Position): Feature<Point, JsonObject?> =
    Feature(geometry = Point(pos), properties = null)

private fun lineFeature(positions: List<Position>): Feature<LineString, JsonObject?> =
    Feature(geometry = LineString(positions), properties = null)
