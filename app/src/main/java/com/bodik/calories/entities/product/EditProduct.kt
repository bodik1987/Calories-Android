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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.Switch
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.bodik.calories.R
import com.bodik.calories.entities.PreferencesHelper
import com.bodik.calories.entities.Product
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProduct(
    isOpen: MutableState<Boolean>,
    productsState: MutableState<List<Product>>,
    selectedProduct: Product?,
    preferencesHelper: PreferencesHelper
) {
    if (isOpen.value) {

        var title by remember { mutableStateOf("") }
        var calories by remember { mutableStateOf("") }
        var checked by remember { mutableStateOf(false) }

        LaunchedEffect(isOpen.value) {
            title = selectedProduct?.title ?: ""
            calories = selectedProduct?.calories ?: ""
            checked = selectedProduct?.isFavorites ?: false
        }

        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(Unit) { focusRequester.requestFocus() }

        val openDeleteSelectedDayProductDialog = remember { mutableStateOf(false) }

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
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        label = { Text(stringResource(id = R.string.product_name)) },
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = calories,
                        onValueChange = { newValue ->
                            val filteredValue = newValue.filter { it.isDigit() || it == '.' }
                            if (filteredValue.count { it == '.' } <= 1 && !filteredValue.startsWith(
                                    "."
                                )) {
                                calories = filteredValue
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        label = { Text(stringResource(id = R.string.calories)) },
                        suffix = { Text(stringResource(id = R.string.weight_format)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            modifier = Modifier.semantics { contentDescription = "Demo with icon" },
                            checked = checked,
                            onCheckedChange = { checked = it },
                        )
                        Text(
                            stringResource(id = R.string.in_favorites),
                            modifier = Modifier
                                .padding(start = 16.dp),
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                openDeleteSelectedDayProductDialog.value = true
                            },
                        ) { Text(stringResource(id = R.string.delete)) }
                        FilledTonalButton(
                            enabled = title.isNotEmpty() && calories.isNotEmpty(),
                            onClick = {
                                val updatedProduct = Product(
                                    id = selectedProduct!!.id,
                                    title = title,
                                    calories = calories.toFloat().roundToInt().toString(),
                                    isFavorites = checked
                                )
                                val updatedProducts = productsState.value.map { product ->
                                    if (product.id == selectedProduct.id) {
                                        updatedProduct
                                    } else {
                                        product
                                    }
                                }
                                preferencesHelper.saveProducts(updatedProducts)
                                productsState.value = updatedProducts
                                title = ""
                                calories = ""
                                checked = false
                                isOpen.value = false
                            },
                        ) {
                            Text(stringResource(id = R.string.edit))
                        }
                    }
                }
            }
            if (selectedProduct != null) {
                DeleteProduct(
                    isOpen = openDeleteSelectedDayProductDialog,
                    isParentOpen = isOpen,
                    productsState = productsState,
                    id = selectedProduct.id,
                    preferencesHelper = preferencesHelper
                )
            }
        }
    }
}