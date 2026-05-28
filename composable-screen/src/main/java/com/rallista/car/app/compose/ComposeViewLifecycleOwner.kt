package com.rallista.car.app.compose

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

class ComposeViewLifecycleOwner : SavedStateRegistryOwner, ViewModelStoreOwner {
  private var lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
  private var savedStateRegistryController: SavedStateRegistryController =
      SavedStateRegistryController.Companion.create(this)

  override val lifecycle: Lifecycle = lifecycleRegistry
  override val savedStateRegistry = savedStateRegistryController.savedStateRegistry
  override val viewModelStore: ViewModelStore = ViewModelStore()

  fun handleLifecycleEvent(event: Lifecycle.Event) {
    lifecycleRegistry.handleLifecycleEvent(event)
  }

  fun performRestore(savedState: Bundle?) {
    savedStateRegistryController.performRestore(savedState)
  }
}
