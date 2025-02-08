package com.bodik.calories.entities.product

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun ProductsList() {
    val openAddProductToDayProductsDialog = remember { mutableStateOf(false) }
    LazyColumn {
        items(25) {
            ListItem(
                modifier = Modifier.clickable {
                    openAddProductToDayProductsDialog.value = true
                },
                headlineContent = { Text("Item $it") },
                colors =
                ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
            )
        }
    }
    AddProductToDayProducts(isOpen = openAddProductToDayProductsDialog)
}