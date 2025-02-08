package com.bodik.calories.entities.product

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bodik.calories.entities.Product

@Composable
fun ProductsList(products: List<Product>, searchQuery: String) {
    val openAddProductToDayProductsDialog = remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    LazyColumn {
        items(products.filter { it.title.contains(searchQuery, ignoreCase = true) }
            .sortedWith(compareByDescending<Product> { it.isFavorites }
                .thenBy { it.title })) {
            ListItem(
                modifier = Modifier.clickable {
                    selectedProduct = it
                    openAddProductToDayProductsDialog.value = true
                },
                headlineContent = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(it.title)
                        Text(it.calories.toString())
                    }
                },
                colors =
                ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
            )
        }
    }
    AddProductToDayProducts(
        isOpen = openAddProductToDayProductsDialog,
        selectedProduct = selectedProduct
    )
}