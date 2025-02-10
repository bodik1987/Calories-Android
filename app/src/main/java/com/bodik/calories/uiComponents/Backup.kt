package com.bodik.calories.uiComponents

import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bodik.calories.R
import com.bodik.calories.entities.PreferencesHelper
import com.bodik.calories.entities.Product
import com.bodik.calories.entities.saveBackupToUri

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Backup(
    isOpen: MutableState<Boolean>,
    preferencesHelper: PreferencesHelper,
    productsState: MutableState<List<Product>>,
    context: Context
) {
    if (isOpen.value) {
        val restoredProducts = preferencesHelper.loadProducts()
        val createBackupLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument("application/json")
        ) { uri ->
            uri?.let { saveBackupToUri(context, it, productsState.value) }
        }

        val restoreBackupLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri ->
            uri?.let { preferencesHelper.restoreBackupFromUri(context, it) }
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
                        stringResource(id = R.string.backup),
                        style = TextStyle(fontSize = 24.sp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        onClick = {
                            preferencesHelper.clearAllProducts(context)
                            isOpen.value = false
                        }) { Text(text = stringResource(id = R.string.clear)) }

                    Button(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        onClick = {
                            createBackupLauncher.launch("calories_backup.json")
                            isOpen.value = false
                        }) { Text(text = stringResource(id = R.string.add)) }

//                    FilledTonalButton(onClick = {
//                        restoreBackupLauncher.launch(arrayOf("application/json"))
//                        productsState.value = restoredProducts
//                        isOpen.value = false
//                    }) {
//                        Text(text = stringResource(id = R.string.edit))
//                    }
                }
            }
        }
    }
}
