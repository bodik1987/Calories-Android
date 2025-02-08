package com.bodik.calories.entities.dayProduct

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DayProductsList(innerPadding: PaddingValues) {
    val openDayProductDialog = remember { mutableStateOf(false) }

    LazyColumn(
        contentPadding = innerPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val list = (0..75).map { it.toString() }
        items(count = list.size) {
            ListItem(
                modifier = Modifier.clickable {
                    openDayProductDialog.value = true
                },
                headlineContent = { Text("Item $it") },
            )
        }
    }

    DayProduct(isOpen = openDayProductDialog)
}