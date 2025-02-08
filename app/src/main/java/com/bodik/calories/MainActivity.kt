package com.bodik.calories

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.content.Context
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bodik.calories.entities.PreferencesHelper
import com.bodik.calories.entities.calculateTarget
import com.bodik.calories.entities.dayProduct.DayProductsList
import com.bodik.calories.uiComponents.CleanDay
import com.bodik.calories.entities.product.Products
import com.bodik.calories.entities.product.getInitialProducts
import com.bodik.calories.ui.theme.CaloriesTheme
import com.bodik.calories.uiComponents.UserMeasurements
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CaloriesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    App(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val isTouchExplorationEnabled = remember {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        am.isEnabled && am.isTouchExplorationEnabled
    }
    val scrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()
    val scrollBehaviorTopBar = TopAppBarDefaults.pinnedScrollBehavior()

    val openCleanDayDialog = remember { mutableStateOf(false) }
    val openUserMeasurements = remember { mutableStateOf(false) }

    val openBottomSheet = remember { mutableStateOf(false) }
    val bottomSheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val today = remember { Calendar.getInstance().get(Calendar.DAY_OF_MONTH) }
    val isTodayOdd = today % 2 != 0

    val preferencesHelper = PreferencesHelper(context)
    var selectedDay by remember { mutableIntStateOf(preferencesHelper.loadSelectedDay()) }

    var target by remember { mutableStateOf("") }
    val userData = preferencesHelper.loadUserData()

    var products by remember { mutableStateOf(preferencesHelper.loadProducts()) }

    LaunchedEffect(Unit) {
        selectedDay = preferencesHelper.loadSelectedDay()

        if (products.isEmpty()) {
            val initialProducts = getInitialProducts()
            preferencesHelper.saveProducts(initialProducts)
            products = initialProducts
        }
    }

    LaunchedEffect(openUserMeasurements.value) {
        target = calculateTarget(userData.weight, userData.age)
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .nestedScroll(scrollBehaviorTopBar.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Калории", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    var expanded by remember { mutableStateOf(false) }

                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description"
                        )
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Данные пользователя") },
                            onClick = {
                                openUserMeasurements.value = true
                                expanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.AccountCircle,
                                    contentDescription = null
                                )
                            }
                        )

                        HorizontalDivider()

                        DropdownMenuItem(
                            text = { Text("$isTodayOdd") },
                            onClick = { /* Handle send feedback! */ },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Email,
                                    contentDescription = null
                                )
                            },
                            trailingIcon = { Text("$selectedDay", textAlign = TextAlign.Center) }
                        )
                    }
                },
                actions = {
                    TextButton(onClick = {
                        openCleanDayDialog.value = true
                    }) { Text("Очистить день") }

                    CleanDay(isOpen = openCleanDayDialog)
                },
                scrollBehavior = if (!isTouchExplorationEnabled) scrollBehaviorTopBar else null,
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    listOf(1, 2).forEach { day ->
                        val isSelected = selectedDay == day
                        val isToday = (day == 1) == isTodayOdd
                        val buttonText = if (isToday) "Завтра" else "Сегодня"

                        if (isSelected) {
                            Button(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                onClick = {
                                    preferencesHelper.updateSelectedDay(day)
                                    selectedDay = day
                                }) { Text(text = buttonText) }
                        } else {
                            TextButton(onClick = {
                                preferencesHelper.updateSelectedDay(day)
                                selectedDay = day
                            }) {
                                Text(text = buttonText)
                            }
                        }
                    }

                    Text("${target}")
                },
                scrollBehavior = if (!isTouchExplorationEnabled) scrollBehavior else null,
            )
        },


        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.offset(y = 4.dp),
                onClick = { openBottomSheet.value = true },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(Icons.Filled.KeyboardArrowUp, "Localized description")
            }
        },
        floatingActionButtonPosition = FabPosition.EndOverlay,
        content = { innerPadding ->
            DayProductsList(innerPadding = innerPadding)
        }
    )

    UserMeasurements(isOpen = openUserMeasurements, preferencesHelper = preferencesHelper)

    if (openBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { openBottomSheet.value = false },
            sheetState = bottomSheetState,
            modifier = Modifier.fillMaxHeight(0.92f)
        ) { Products(products = products) }
    }
}
