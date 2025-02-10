package com.bodik.calories

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.view.accessibility.AccessibilityManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Edit
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bodik.calories.entities.PreferencesHelper
import com.bodik.calories.entities.calculateTarget
import com.bodik.calories.entities.calculateTotalCalories
import com.bodik.calories.entities.dayProduct.DayProductsList
import com.bodik.calories.uiComponents.CleanDay
import com.bodik.calories.entities.product.Products
import com.bodik.calories.entities.product.getInitialProducts
import com.bodik.calories.ui.theme.CaloriesTheme
import com.bodik.calories.uiComponents.LanguageSwitcher
import com.bodik.calories.uiComponents.UserMeasurements
import java.util.Calendar
import kotlin.math.roundToInt
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.input.pointer.pointerInput
import com.bodik.calories.uiComponents.Backup

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CaloriesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    App(
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(modifier: Modifier) {
    val context = LocalContext.current
    val isTouchExplorationEnabled = remember {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        am.isEnabled && am.isTouchExplorationEnabled
    }
    val scrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()
    val scrollBehaviorTopBar = TopAppBarDefaults.pinnedScrollBehavior()
    val openCleanDayDialog = remember { mutableStateOf(false) }
    val openBackupDialog = remember { mutableStateOf(false) }
    val openUserMeasurements = remember { mutableStateOf(false) }
    val openLanguageSwitcher = remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val openBottomSheet = remember { mutableStateOf(false) }
    val bottomSheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val today = remember { Calendar.getInstance().get(Calendar.DAY_OF_MONTH) }
    val isTodayOdd = today % 2 != 0
    val preferencesHelper = PreferencesHelper(context)
    var selectedDay by remember { mutableIntStateOf(preferencesHelper.loadSelectedDay()) }
    val savedLanguage = remember { mutableStateOf(preferencesHelper.getLanguage()) }
    var target by remember { mutableStateOf("") }
    val userData = preferencesHelper.loadUserData()
    val productsState = remember { mutableStateOf(preferencesHelper.loadProducts()) }
    val dayProductsState = remember { mutableStateOf(preferencesHelper.loadDayProducts()) }
    val restDayProducts = dayProductsState.value.filter { it.day != selectedDay }
    val todayDayProducts = dayProductsState.value.filter { it.day == selectedDay }

    LaunchedEffect(Unit) {
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
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -30) {
                        openBottomSheet.value = true
                    }
                }
            }
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .nestedScroll(scrollBehaviorTopBar.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.app_name),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description"
                        )
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.user_measurements)) },
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
                            text = { Text(stringResource(id = R.string.change_language)) },
                            onClick = {
                                openLanguageSwitcher.value = true
                                expanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Edit,
                                    contentDescription = null
                                )
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.backup)) },
                            onClick = {
                                openBackupDialog.value = true
                                expanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Refresh,
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
                        "${
                            if (isTargetExceeded) stringResource(id = R.string.calories_remaining) else stringResource(
                                id = R.string.calories_exceeded
                            )
                        } ${if (isTargetExceeded) remainingCalories.roundToInt() else -1 * remainingCalories.roundToInt()}",
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
                        val buttonText =
                            if (isToday) stringResource(id = R.string.tomorrow) else stringResource(
                                id = R.string.today
                            )

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
                        Text(stringResource(id = R.string.clear))

                        CleanDay(
                            isOpen = openCleanDayDialog,
                            restDayProducts = restDayProducts,
                            preferencesHelper = preferencesHelper,
                            dayProductsState = dayProductsState
                        )
                    }
                },
                modifier = Modifier,
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
    Backup(
        isOpen = openBackupDialog,
        preferencesHelper = preferencesHelper,
        productsState = productsState,
        context = context
    )

    LanguageSwitcher(
        isOpen = openLanguageSwitcher,
        localeOptions = mapOf(
            R.string.en to "en",
            R.string.ru to "ru-rRu",
            R.string.uk to "uk",
            R.string.pl to "pl",
        ).mapKeys { stringResource(it.key) },
        preferencesHelper = preferencesHelper,
        savedLanguage = savedLanguage
    )
    if (openBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { openBottomSheet.value = false },
            sheetState = bottomSheetState,
            modifier = Modifier.padding(top = 90.dp),
            dragHandle = null
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
