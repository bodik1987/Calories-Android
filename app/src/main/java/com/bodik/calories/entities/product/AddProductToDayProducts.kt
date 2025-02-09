package com.bodik.calories.entities.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.focusRequester
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import com.bodik.calories.R
import com.bodik.calories.entities.DayProduct
import com.bodik.calories.entities.PreferencesHelper
import com.bodik.calories.entities.Product
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductToDayProducts(
    isOpen: MutableState<Boolean>,
    selectedProduct: Product?,
    productsState: MutableState<List<Product>>,
    preferencesHelper: PreferencesHelper,
    selectedDay: Int,
    dayProductsState: MutableState<List<DayProduct>>
) {
    val openEditProductDialog = remember { mutableStateOf(false) }

    if (isOpen.value && selectedProduct != null) {
        var weight by remember { mutableStateOf("") }
        var calories by remember { mutableStateOf("0") }
        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(Unit) { focusRequester.requestFocus() }

        BasicAlertDialog(
            onDismissRequest = { isOpen.value = false }
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(top = 8.dp)
                ) {
                    Text(
                        selectedProduct.title, style = TextStyle(fontSize = 24.sp)
                    )

                    fun calculateCalories(weight: String): String {
                        val weightFloat = weight.toFloatOrNull()
                        return if (weightFloat != null) {
                            ((selectedProduct.calories.toInt() / 100f) * weightFloat).toInt()
                                .toString()
                        } else {
                            "0"
                        }
                    }

                    Text(
                        "${stringResource(id = R.string.calories)}: $calories ${stringResource(id = R.string.cal)}",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = weight,
                        onValueChange = { newValue ->
                            val filteredValue = newValue.filter { it.isDigit() || it == '.' }
                            if (filteredValue.count { it == '.' } <= 1 && !filteredValue.startsWith(
                                    "."
                                )) {
                                weight = filteredValue
                            }
                            calories = calculateCalories(weight)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        shape = RoundedCornerShape(12.dp),
                        label = { Text(stringResource(id = R.string.product_weight)) },
                        suffix = { Text(stringResource(id = R.string.weight_format)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                openEditProductDialog.value = true
                                isOpen.value = false
                            },
                        ) {
                            Text(stringResource(id = R.string.edit))
                        }

                        FilledTonalButton(
                            enabled = weight.isNotEmpty(),
                            onClick = {
                                val newDayProduct = DayProduct(
                                    id = UUID.randomUUID().toString(),
                                    day = selectedDay,
                                    weight = weight,
                                    product = selectedProduct
                                )
                                val updatedDayProducts = dayProductsState.value + newDayProduct
                                preferencesHelper.saveDayProducts(updatedDayProducts)
                                dayProductsState.value = updatedDayProducts
                                weight = ""
                                isOpen.value = false
                            },
                        ) {
                            Text(stringResource(id = R.string.add))
                        }
                    }

                }
            }
        }
    }
    EditProduct(
        isOpen = openEditProductDialog,
        productsState = productsState,
        selectedProduct = selectedProduct,
        preferencesHelper = preferencesHelper
    )
}