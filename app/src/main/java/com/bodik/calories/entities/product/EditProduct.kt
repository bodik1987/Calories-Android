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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.bodik.calories.entities.PreferencesHelper
import com.bodik.calories.entities.Product
import java.util.UUID


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
            calories = selectedProduct?.calories?.toString() ?: ""
            checked = selectedProduct?.isFavorites ?: false
        }

        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(Unit) { focusRequester.requestFocus() }

        val openDeleteSelectedDayProductDialog = remember { mutableStateOf(false) }

        BasicAlertDialog(
            onDismissRequest = {
                isOpen.value = false
            }
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
                        "Изменить продукт", style = TextStyle(fontSize = 24.sp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        shape = RoundedCornerShape(12.dp),
                        label = { Text("Название продукта") },
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = calories,
                        onValueChange = { calories = it },
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        label = { Text("Калорийность") },
                        suffix = { Text("г") },
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
                            "В избранных",
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
                        ) {
                            Text("Удалить")
                        }
                        FilledTonalButton(
                            onClick = {
                                if (title.isNotEmpty() && calories.isNotEmpty()) {
                                    val updatedProduct = Product(
                                        id = UUID.randomUUID().toString(),
                                        title = title,
                                        calories = calories.toInt(),
                                        isFavorites = checked
                                    )
                                    val updatedProducts = productsState.value.map { product ->
                                        if (product.id == selectedProduct!!.id) {
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
                                }
                                isOpen.value = false
                            },
                        ) {
                            Text("Изменить")
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