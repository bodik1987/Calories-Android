package com.bodik.calories.entities.product

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.bodik.calories.entities.PreferencesHelper
import com.bodik.calories.entities.Product

@Composable
fun DeleteProduct(
    isOpen: MutableState<Boolean>,
    isParentOpen: MutableState<Boolean>,
    productsState: MutableState<List<Product>>,
    id: String,
    preferencesHelper: PreferencesHelper
) {
    if (isOpen.value) {
        val products = productsState.value
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
                    val updatedProducts = products.filter { product: Product -> product.id != id }
                    preferencesHelper.saveProducts(updatedProducts)
                    productsState.value = updatedProducts
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