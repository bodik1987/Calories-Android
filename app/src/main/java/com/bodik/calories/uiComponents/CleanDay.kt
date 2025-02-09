package com.bodik.calories.uiComponents

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import com.bodik.calories.R
import com.bodik.calories.entities.DayProduct
import com.bodik.calories.entities.PreferencesHelper

@Composable
fun CleanDay(
    isOpen: MutableState<Boolean>,
    restDayProducts: List<DayProduct>,
    preferencesHelper: PreferencesHelper,
    dayProductsState: MutableState<List<DayProduct>>
) {
    if (isOpen.value) {
        AlertDialog(
            onDismissRequest = {
                isOpen.value = false
            },
            icon = { Icon(Icons.Filled.Warning, contentDescription = null) },
            title = { Text(text = stringResource(id = R.string.clear_day)) },
            text = {
                Text(
                    stringResource(id = R.string.clear_products_confirmation)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    preferencesHelper.saveDayProducts(restDayProducts)
                    dayProductsState.value = preferencesHelper.loadDayProducts()
                    isOpen.value = false
                }) { Text(stringResource(id = R.string.clear)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    isOpen.value = false
                }) { Text(stringResource(id = R.string.cancel)) }
            }
        )
    }
}