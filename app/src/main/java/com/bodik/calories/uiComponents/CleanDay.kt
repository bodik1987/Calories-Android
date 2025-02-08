package com.bodik.calories.uiComponents

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
fun CleanDay(isOpen: MutableState<Boolean>) {
    if (isOpen.value) {
        AlertDialog(
            onDismissRequest = {
                isOpen.value = false
            },
            icon = { Icon(Icons.Filled.Warning, contentDescription = null) },
            title = { Text(text = "Очистить день") },
            text = {
                Text(
                    "Очисть все продукты за сегодняшний день?"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    isOpen.value = false
                }) { Text("Очистить") }
            },
            dismissButton = {
                TextButton(onClick = {
                    isOpen.value = false
                }) { Text("Отменить") }
            }
        )
    }
}