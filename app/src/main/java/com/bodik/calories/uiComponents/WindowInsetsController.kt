package com.bodik.calories.uiComponents

import android.app.Activity
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun SetStatusBarColorBasedOnTheme(isDarkTheme: Boolean) {
    val view = LocalView.current

    DisposableEffect(isDarkTheme) {
        val window = (view.context as? Activity)?.window
        if (window != null) {
            val insetsController = WindowInsetsControllerCompat(window, view)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                insetsController.isAppearanceLightStatusBars = !isDarkTheme
            } else {

                android.graphics.Color.WHITE
            }
        }
        onDispose { }
    }
}