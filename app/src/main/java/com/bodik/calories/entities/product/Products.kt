package com.bodik.calories.entities.product

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import com.bodik.calories.entities.PreferencesHelper
import com.bodik.calories.entities.Product

@Composable
fun Products(productsState: MutableState<List<Product>>, preferencesHelper: PreferencesHelper) {

//    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
//        Button(
//            onClick = {
//                scope
//                    .launch { sheetState.hide() }
//                    .invokeOnCompletion {
//                        if (!sheetState.isVisible) {
//                            isOpen.value = false
//                        }
//                    }
//            }
//        ) {
//            Text("Hide Bottom Sheet")
//        }
//    }
    val openNewProduct = remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp),
            shape = RoundedCornerShape(12.dp),
            label = { Text("Поиск по названию") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.length > 1) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Clear text")
                    }
                }
            }
        )
        FloatingActionButton(
            modifier = Modifier.offset(y = 4.dp),
            onClick = { openNewProduct.value = true },
            containerColor = FloatingActionButtonDefaults.containerColor,
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
//            shape = CircleShape
        ) {
            Icon(Icons.Filled.Add, "Localized description")
        }
    }
    NewProduct(
        isOpen = openNewProduct,
        productsState = productsState,
        preferencesHelper = preferencesHelper
    )
    ProductsList(
        productsState = productsState,
        searchQuery = searchQuery,
        preferencesHelper = preferencesHelper
    )
}