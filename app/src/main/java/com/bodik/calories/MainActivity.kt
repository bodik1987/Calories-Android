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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
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
import com.bodik.calories.entities.ThemeMode
import com.bodik.calories.entities.calculateTarget
import com.bodik.calories.entities.calculateTotalCalories
import com.bodik.calories.entities.dayProduct.DayProductsList
import com.bodik.calories.uiComponents.CleanDay
import com.bodik.calories.entities.product.Products
import com.bodik.calories.entities.product.getInitialProducts
import com.bodik.calories.ui.theme.CaloriesTheme
import com.bodik.calories.uiComponents.SetStatusBarColorBasedOnTheme
import com.bodik.calories.uiComponents.ThemeSwitcher
import com.bodik.calories.uiComponents.UserMeasurements
import java.util.Calendar
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferencesHelper = PreferencesHelper(this)
        enableEdgeToEdge()
        setContent {
            CaloriesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var themeMode by remember { mutableStateOf(preferencesHelper.getThemeMode()) }
                    App(
                        modifier = Modifier.padding(innerPadding),
                        themeMode = themeMode, onThemeChange = {
                            themeMode = it
                            preferencesHelper.setThemeMode(it)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(modifier: Modifier = Modifier, themeMode: ThemeMode, onThemeChange: (ThemeMode) -> Unit) {
    val isDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    SetStatusBarColorBasedOnTheme(themeMode == ThemeMode.DARK)

    MaterialTheme(colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()) {
        val context = LocalContext.current
        val isTouchExplorationEnabled = remember {
            val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
            am.isEnabled && am.isTouchExplorationEnabled
        }
        val scrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()
        val scrollBehaviorTopBar = TopAppBarDefaults.pinnedScrollBehavior()

        val openCleanDayDialog = remember { mutableStateOf(false) }
        val openUserMeasurements = remember { mutableStateOf(false) }
        val openThemeSettings = remember { mutableStateOf(false) }

        val openBottomSheet = remember { mutableStateOf(false) }
        val bottomSheetState =
            rememberModalBottomSheetState(skipPartiallyExpanded = true)

        val today = remember { Calendar.getInstance().get(Calendar.DAY_OF_MONTH) }
        val isTodayOdd = today % 2 != 0

        val preferencesHelper = PreferencesHelper(context)
        var selectedDay by remember { mutableIntStateOf(preferencesHelper.loadSelectedDay()) }

        var target by remember { mutableStateOf("") }
        val userData = preferencesHelper.loadUserData()

        val productsState = remember { mutableStateOf(preferencesHelper.loadProducts()) }
        val dayProductsState = remember { mutableStateOf(preferencesHelper.loadDayProducts()) }
        val restDayProducts = dayProductsState.value.filter { it.day != selectedDay }
        val todayDayProducts = dayProductsState.value.filter { it.day == selectedDay }

        LaunchedEffect(Unit) {
            selectedDay = preferencesHelper.loadSelectedDay()

            if (productsState.value.isEmpty()) {
                val initialProducts = getInitialProducts()
                preferencesHelper.saveProducts(initialProducts)
                productsState.value = initialProducts
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

//                        HorizontalDivider()

                            DropdownMenuItem(
                                text = { Text("Тема приложения") },
                                onClick = {
                                    openThemeSettings.value = true
                                    expanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Outlined.Build,
                                        contentDescription = null
                                    )
                                },
                            )
                        }
                    },
                    actions = {
                        val totalCalories =
                            calculateTotalCalories(dayProductsState.value.filter { it.day == selectedDay })
                        val targetCalories =
                            target.toDoubleOrNull() ?: 0.0
                        val totalCaloriesValue =
                            totalCalories.toDoubleOrNull() ?: 0.0
                        val remainingCalories = targetCalories - totalCaloriesValue
                        val isTargetExceeded = remainingCalories >= 0
                        Text(
                            "${if (isTargetExceeded) "Осталось" else "Превышено на"} ${if (isTargetExceeded) remainingCalories.roundToInt() else -1 * remainingCalories.roundToInt()}",
                            modifier = Modifier.padding(end = 12.dp)
                        )
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
                                FilledTonalButton(onClick = {
                                    preferencesHelper.updateSelectedDay(day)
                                    selectedDay = day
                                }) {
                                    Text(text = buttonText)
                                }
                            }
                        }
                        if (todayDayProducts.isNotEmpty()) TextButton(onClick = {
                            openCleanDayDialog.value = true
                        }) {
                            Text("Очистить")

                            CleanDay(
                                isOpen = openCleanDayDialog,
                                restDayProducts = restDayProducts,
                                preferencesHelper = preferencesHelper,
                                dayProductsState = dayProductsState
                            )
                        }
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
                DayProductsList(
                    innerPadding = innerPadding,
                    dayProductsState = dayProductsState,
                    preferencesHelper = preferencesHelper,
                    selectedDay = selectedDay
                )
            }
        )

        UserMeasurements(isOpen = openUserMeasurements, preferencesHelper = preferencesHelper)

        ThemeSwitcher(
            isOpen = openThemeSettings,
            themeMode = themeMode,
            onThemeChange = onThemeChange
        )

        if (openBottomSheet.value) {
            ModalBottomSheet(
                onDismissRequest = { openBottomSheet.value = false },
                sheetState = bottomSheetState,
                modifier = Modifier.fillMaxHeight(0.92f)
            ) {
                Products(
                    productsState = productsState,
                    preferencesHelper = preferencesHelper,
                    selectedDay = selectedDay,
                    dayProductsState = dayProductsState
                )
            }
        }
    }
}
