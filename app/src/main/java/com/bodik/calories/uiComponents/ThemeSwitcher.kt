package com.bodik.calories.uiComponents

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.bodik.calories.entities.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSwitcher(
    isOpen: MutableState<Boolean>,
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit
) {
    if (isOpen.value) {
        val (selectedTheme, onThemeSelected) = remember { mutableStateOf(themeMode) }

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
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = "Выбранная тема: ${themeMode.toDisplayName()}")

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(Modifier.selectableGroup()) {
                        ThemeMode.entries.forEach { theme ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .selectable(
                                        selected = (theme == selectedTheme),
                                        onClick = {
                                            onThemeSelected(theme)
                                            onThemeChange(theme)
                                        },
                                        role = Role.RadioButton
                                    )
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (theme == selectedTheme),
                                    onClick = null
                                )
                                Text(
                                    text = theme.toDisplayName(),
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

fun ThemeMode.toDisplayName(): String {
    return when (this) {
        ThemeMode.LIGHT -> "Светлая тема"
        ThemeMode.DARK -> "Тёмная тема"
        ThemeMode.SYSTEM -> "Как в системе"
    }
}