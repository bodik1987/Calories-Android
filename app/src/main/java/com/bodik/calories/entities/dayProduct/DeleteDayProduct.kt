package com.bodik.calories.entities.dayProduct

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.bodik.calories.entities.DayProduct
import com.bodik.calories.entities.PreferencesHelper
import com.bodik.calories.entities.Product

@Composable
fun DeleteDayProduct(
    isOpen: MutableState<Boolean>, isParentOpen: MutableState<Boolean>, id: String,
    preferencesHelper: PreferencesHelper, dayProductsState: MutableState<List<DayProduct>>
) {
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
                    val updatedDayProducts =
                        dayProductsState.value.filter { dayProduct: DayProduct -> dayProduct.id != id }
                    preferencesHelper.saveDayProducts(updatedDayProducts)
                    dayProductsState.value = updatedDayProducts
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