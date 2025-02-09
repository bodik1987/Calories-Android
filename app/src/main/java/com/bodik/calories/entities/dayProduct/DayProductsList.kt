package com.bodik.calories.entities.dayProduct

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bodik.calories.R
import com.bodik.calories.entities.DayProduct
import com.bodik.calories.entities.PreferencesHelper
import com.bodik.calories.entities.calculateDayProductCalories
import com.bodik.calories.uiComponents.DynamicNoDataIcon

@Composable
fun DayProductsList(
    innerPadding: PaddingValues,
    dayProductsState: MutableState<List<DayProduct>>,
    preferencesHelper: PreferencesHelper,
    selectedDay: Int
) {
    val openDayProductDialog = remember { mutableStateOf(false) }
    var selectedDayProduct by remember { mutableStateOf<DayProduct?>(null) }
    val filteredProducts = dayProductsState.value.filter { it.day == selectedDay }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (filteredProducts.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DynamicNoDataIcon()
                Text(
                    text = stringResource(id = R.string.empty_list),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        } else {
            LazyColumn(
                contentPadding = innerPadding
            ) {
                items(filteredProducts) {
                    ListItem(
                        modifier = Modifier.clickable {
                            selectedDayProduct = it
                            openDayProductDialog.value = true
                        },
                        headlineContent = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${it.product.title}, ${it.weight} ${stringResource(id = R.string.weight_format)}")
                                Text(
                                    calculateDayProductCalories(it),
                                    modifier = Modifier
                                        .padding(start = 8.dp),
                                )
                            }
                        },
                    )
                }
            }
        }
    }

    EditDayProduct(
        isOpen = openDayProductDialog,
        selectedDayProduct = selectedDayProduct,
        dayProductsState = dayProductsState,
        preferencesHelper = preferencesHelper
    )
}