package com.bodik.calories.entities.product

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
fun DeleteProduct(isOpen: MutableState<Boolean>, isParentOpen: MutableState<Boolean>) {
    if (isOpen.value) {
        AlertDialog(
            onDismissRequest = {
                isOpen.value = false
            },
            icon = { Icon(Icons.Filled.Warning, contentDescription = null) },
            title = { Text(text = "Удалить продукт") },
            text = {
                Text(
                    "Вы уверены, что хотите удалить продукт?"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    isOpen.value = false
                    isParentOpen.value = false
                }) { Text("Удалить") }
            },
            dismissButton = {
                TextButton(onClick = {
                    isOpen.value = false
                }) { Text("Отменить") }
            }
        )
    }
}