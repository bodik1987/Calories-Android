package com.bodik.calories.entities.product

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bodik.calories.R
import com.bodik.calories.entities.DayProduct
import com.bodik.calories.entities.PreferencesHelper
import com.bodik.calories.entities.Product

@SuppressLint("UnrememberedMutableState")
@Composable
fun Products(
    productsState: MutableState<List<Product>>,
    preferencesHelper: PreferencesHelper,
    selectedDay: Int,
    dayProductsState: MutableState<List<DayProduct>>
) {
    val openNewProduct = remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showFavorites by remember { mutableStateOf(false) }
    val filteredProducts = if (showFavorites) {
        productsState.value.filter { it.isFavorites }
    } else {
        productsState.value
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            shape = RoundedCornerShape(16.dp),
            label = { Text(stringResource(id = R.string.search)) },
            textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
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
            modifier = Modifier
                .offset(y = 4.dp)
                .padding(end = 8.dp),
            onClick = { showFavorites = !showFavorites },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            elevation = FloatingActionButtonDefaults.loweredElevation(),
        ) {
            Icon(
                if (showFavorites) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Toggle favorites"
            )
        }
        FloatingActionButton(
            modifier = Modifier.offset(y = 4.dp),
            onClick = { openNewProduct.value = true },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
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
        productsState = mutableStateOf(filteredProducts),
        searchQuery = searchQuery,
        preferencesHelper = preferencesHelper,
        selectedDay = selectedDay,
        dayProductsState = dayProductsState,
        showFavorites = showFavorites
    )
}