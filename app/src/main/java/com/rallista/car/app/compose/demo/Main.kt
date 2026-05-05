package com.rallista.car.app.compose.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main() {
  Scaffold(topBar = { AppTopBar() }) { padding ->
    Column(
        modifier =
            Modifier.fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text(
              "ComposableScreen demo",
              style = MaterialTheme.typography.headlineSmall,
          )
          Text(
              "This sample hosts an official maplibre-compose map inside a ComposableScreen surface — the same Compose content the :auto module renders on Android Automotive.",
              style = MaterialTheme.typography.bodyMedium,
          )

          Text(
              "Option A — Android Automotive emulator (no phone needed)",
              style = MaterialTheme.typography.titleMedium,
          )
          Text(
              "Easiest path. Install Android Studio's \"Automotive with Play Store\" system image, boot it, then install and launch the :auto module on it. No DHU, no port forwarding, no Android Auto app required.",
              style = MaterialTheme.typography.bodyMedium,
          )
          SelectionContainer(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                "./gradlew :auto:installDebug",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            )
          }

          Text(
              "Option B — Real phone over USB",
              style = MaterialTheme.typography.titleMedium,
          )
          Text(
              "Use this to validate the actual Android Auto projection flow. Plug a phone in, install Android Auto on it from Play, enable developer mode (tap Version 10x in Auto's settings), then start the Head Unit Server. Connecting the DHU over USB AOA is faster and more reliable than the adb-forward path:",
              style = MaterialTheme.typography.bodyMedium,
          )
          SelectionContainer(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                "\$ANDROID_HOME/extras/google/auto/desktop-head-unit --usb",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            )
          }
          Text(
              "Note: a regular Android emulator can't run Android Auto — Google Play marks it as incompatible. The phone hardware is required for projection testing.",
              style = MaterialTheme.typography.bodySmall,
          )
        }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar() {
  TopAppBar(
      title = { Text(text = "ComposableScreen") },
      colors =
          TopAppBarColors(
              containerColor = MaterialTheme.colorScheme.surfaceVariant,
              scrolledContainerColor = MaterialTheme.colorScheme.surface,
              navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
              titleContentColor = MaterialTheme.colorScheme.onSurface,
              actionIconContentColor = MaterialTheme.colorScheme.onSurface))
}

@Composable
@Preview
fun MainPreview() {
  Main()
}
