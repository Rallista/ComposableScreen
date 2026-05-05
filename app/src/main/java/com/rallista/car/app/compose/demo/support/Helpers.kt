package com.rallista.car.app.compose.demo.support

import org.maplibre.compose.camera.CameraPosition
import org.maplibre.spatialk.geojson.BoundingBox
import org.maplibre.spatialk.geojson.Position

internal sealed class CameraMode {
  data class Centered(val position: CameraPosition) : CameraMode()

  data class TrackingUserLocation(val zoom: Double, val tilt: Double) : CameraMode()

  data class TrackingUserLocationWithBearing(val zoom: Double, val tilt: Double) : CameraMode()

  data class BoundingBoxFit(val box: BoundingBox) : CameraMode()
}

internal val DefaultCenteredPosition: CameraPosition =
    CameraPosition(target = Position(longitude = -2.9779, latitude = 53.4106), zoom = 10.0)

internal fun getNextCamera(current: CameraMode): CameraMode =
    when (current) {
      is CameraMode.Centered -> CameraMode.TrackingUserLocation(zoom = 18.0, tilt = 45.0)
      is CameraMode.TrackingUserLocation ->
          CameraMode.TrackingUserLocationWithBearing(zoom = 18.0, tilt = 45.0)
      is CameraMode.TrackingUserLocationWithBearing ->
          CameraMode.BoundingBoxFit(
              BoundingBox(
                  west = 6.02260949059,
                  south = 45.7769477403,
                  east = 10.4427014502,
                  north = 47.8308275417))
      is CameraMode.BoundingBoxFit -> CameraMode.Centered(DefaultCenteredPosition)
    }
