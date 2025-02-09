package com.bodik.calories.uiComponents

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import com.bodik.calories.R
import com.bodik.calories.entities.PreferencesHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSwitcher(
    isOpen: MutableState<Boolean>,
    localeOptions: Map<String, String>,
    preferencesHelper: PreferencesHelper,
    savedLanguage: MutableState<String>
) {
    if (isOpen.value) {

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
                        text = stringResource(id = R.string.change_language),
                        style = TextStyle(fontSize = 24.sp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Column(Modifier.selectableGroup()) {
                        localeOptions.entries.forEach { locale ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .selectable(
                                        selected = (locale.value == savedLanguage.value),
                                        onClick = {
                                            savedLanguage.value = locale.value
                                            preferencesHelper.setLanguage(locale.value)
                                            AppCompatDelegate.setApplicationLocales(
                                                LocaleListCompat.forLanguageTags(locale.value)
                                            )
                                        },
                                        role = Role.RadioButton
                                    )
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (locale.value == savedLanguage.value),
                                    onClick = null
                                )
                                Text(
                                    text = locale.key,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}