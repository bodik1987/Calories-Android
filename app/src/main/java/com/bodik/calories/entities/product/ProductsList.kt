package com.bodik.calories.entities.product

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bodik.calories.entities.DayProduct
import com.bodik.calories.entities.PreferencesHelper
import com.bodik.calories.entities.Product

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductsList(
    productsState: MutableState<List<Product>>,
    searchQuery: String,
    preferencesHelper: PreferencesHelper,
    selectedDay: Int,
    dayProductsState: MutableState<List<DayProduct>>,
    showFavorites: Boolean
) {
    val openAddProductToDayProductsDialog = remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    val groupedProducts = productsState.value
        .filter { it.title.contains(searchQuery, ignoreCase = true) }
        .sortedWith(compareBy { it.title })
        .groupBy { it.title.first().uppercaseChar() }
        .toSortedMap()

    LazyColumn {
        groupedProducts.forEach { (initial, products) ->
            stickyHeader {
                if (!showFavorites) Text(
                    text = initial.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            items(products) { product ->
                ListItem(
                    modifier = Modifier.clickable {
                        selectedProduct = product
                        openAddProductToDayProductsDialog.value = true
                    },
                    headlineContent = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(product.title)
                            Text(product.calories)
                        }
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                )
            }
        }
    }

    AddProductToDayProducts(
        isOpen = openAddProductToDayProductsDialog,
        selectedProduct = selectedProduct,
        productsState = productsState,
        preferencesHelper = preferencesHelper,
        selectedDay = selectedDay,
        dayProductsState = dayProductsState
    )
}