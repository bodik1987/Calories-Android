package com.bodik.calories.entities.dayProduct

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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.bodik.calories.R
import com.bodik.calories.entities.DayProduct
import com.bodik.calories.entities.PreferencesHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDayProduct(
    isOpen: MutableState<Boolean>,
    selectedDayProduct: DayProduct?,
    dayProductsState: MutableState<List<DayProduct>>,
    preferencesHelper: PreferencesHelper
) {
    if (isOpen.value) {
        val openDeleteDayProductDialog = remember { mutableStateOf(false) }
        var weight by remember { mutableStateOf("") }

        LaunchedEffect(isOpen.value) { weight = selectedDayProduct?.weight ?: "" }

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
                        stringResource(id = R.string.edit_product),
                        style = TextStyle(fontSize = 24.sp)
                    )

                    fun calculateCalories(weight: String): String {
                        val weightDouble = weight.toDoubleOrNull() ?: 0.0
                        return if (selectedDayProduct != null) {
                            ((selectedDayProduct.product.calories.toInt() / 100f) * weightDouble).toInt()
                                .toString()
                        } else {
                            "0"
                        }
                    }

                    Text(
                        "${stringResource(id = R.string.calories)}: ${calculateCalories(weight)} ${
                            stringResource(
                                id = R.string.cal
                            )
                        }",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                        label = { Text(stringResource(id = R.string.product_weight)) },
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
                            onClick = { openDeleteDayProductDialog.value = true },
                        ) {
                            Text(stringResource(id = R.string.delete))
                        }
                        FilledTonalButton(
                            enabled = weight.isNotEmpty(),
                            onClick = {
                                val updatedDayProduct =
                                    selectedDayProduct!!.copy(weight = weight)
                                val updatedDayProducts =
                                    dayProductsState.value.map { dayProduct ->
                                        if (dayProduct.id == selectedDayProduct.id) {
                                            updatedDayProduct
                                        } else {
                                            dayProduct
                                        }
                                    }
                                preferencesHelper.saveDayProducts(updatedDayProducts)
                                dayProductsState.value = updatedDayProducts
                                weight = ""
                                isOpen.value = false
                            },
                        ) {
                            Text(stringResource(id = R.string.update))
                        }
                    }
                }
            }
            if (selectedDayProduct != null) {
                DeleteDayProduct(
                    isOpen = openDeleteDayProductDialog,
                    isParentOpen = isOpen,
                    id = selectedDayProduct.id,
                    preferencesHelper = preferencesHelper,
                    dayProductsState = dayProductsState
                )
            }
        }
    }
}