package com.bodik.calories.uiComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.bodik.calories.R
import com.bodik.calories.entities.PreferencesHelper
import com.bodik.calories.entities.calculateTarget
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMeasurements(isOpen: MutableState<Boolean>, preferencesHelper: PreferencesHelper) {
    if (isOpen.value) {

        var userWeight by remember { mutableStateOf("") }
        var age by remember { mutableStateOf("") }
        var target by remember { mutableStateOf("") }

        LaunchedEffect(isOpen.value) {
            if (isOpen.value) {
                val userData = preferencesHelper.loadUserData()
                userWeight = userData.weight
                age = userData.age
                target = calculateTarget(userWeight, age)
            }
        }

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
                        stringResource(id = R.string.user_measurements),
                        style = TextStyle(fontSize = 24.sp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = age,
                            onValueChange = { newValue ->
                                // Only digit & .
                                val filteredValue = newValue.filter { it.isDigit() || it == '.' }
                                // . not first & repeat
                                if (filteredValue.count { it == '.' } <= 1 && !filteredValue.startsWith(
                                        "."
                                    )) {
                                    age = filteredValue
                                }
                                target = calculateTarget(userWeight, age)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            label = { Text(stringResource(id = R.string.age)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = userWeight,
                            onValueChange = { newValue ->
                                val filteredValue = newValue.filter { it.isDigit() || it == '.' }
                                if (filteredValue.count { it == '.' } <= 1 && !filteredValue.startsWith(
                                        "."
                                    )) {
                                    userWeight = filteredValue
                                }
                                target = calculateTarget(userWeight, age)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            label = { Text(stringResource(id = R.string.weight)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    Text(
                        "${stringResource(id = R.string.daily_goal)} $target ${stringResource(id = R.string.cal)}",
                        modifier = Modifier
                            .padding(top = 16.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { isOpen.value = false },
                        ) {
                            Text(stringResource(id = R.string.cancel))
                        }

                        FilledTonalButton(
                            enabled = userWeight.isNotEmpty() && age.isNotEmpty(),
                            onClick = {
                                val weightDouble = userWeight.toDoubleOrNull() ?: 0.0
                                val ageDouble = age.toDoubleOrNull() ?: 0.0
                                val newTarget =
                                    (88 + 13 * weightDouble + 4.2 * 178 - 5.7 * ageDouble).roundToInt()
                                        .toString()

                                preferencesHelper.saveUserData(
                                    weight = userWeight,
                                    age = age
                                )

                                target = newTarget

                                isOpen.value = false
                            },
                        ) {
                            Text(stringResource(id = R.string.edit))
                        }
                    }
                }
            }
        }
    }
}