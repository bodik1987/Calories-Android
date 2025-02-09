package com.bodik.calories.entities.product

import androidx.compose.foundation.focusable
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.focusRequester
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.focus.FocusRequester
import com.bodik.calories.entities.PreferencesHelper
import com.bodik.calories.entities.Product
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewProduct(
    isOpen: MutableState<Boolean>,
    productsState: MutableState<List<Product>>,
    preferencesHelper: PreferencesHelper
) {
    if (isOpen.value) {
        val products = productsState.value
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
                        "Добавить новый продукт", style = TextStyle(fontSize = 22.sp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    var title by remember { mutableStateOf("") }
                    var calories by remember { mutableStateOf("") }
                    var checked by remember { mutableStateOf(false) }

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences
                        ),
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
                        suffix = { Text("ккал/100г") },
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
                                isOpen.value = false
                            },
                        ) {
                            Text("Отменить")
                        }

                        FilledTonalButton(
                            onClick = {
                                if (title.isNotEmpty() && calories.isNotEmpty()) {
                                    val newProduct = Product(
                                        id = UUID.randomUUID().toString(),
                                        title = title,
                                        calories = calories.toInt(),
                                        isFavorites = checked
                                    )
                                    val updatedProducts = products + newProduct
                                    preferencesHelper.saveProducts(updatedProducts)
                                    productsState.value = updatedProducts
                                    title = ""
                                    calories = ""
                                    checked = false
                                }
                                isOpen.value = false
                            },
                        ) {
                            Text("Добавить")
                        }
                    }

                }
            }
        }
    }
}