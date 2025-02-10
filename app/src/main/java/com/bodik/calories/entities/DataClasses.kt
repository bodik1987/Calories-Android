package com.bodik.calories.entities

import kotlin.math.roundToInt

data class Product(
    val id: String,
    val title: String,
    val calories: String,
    val isFavorites: Boolean
)

data class UserData(
    val weight: String,
    val age: String,
    val target: String
)

data class DayProduct(
    val id: String,
    val day: Int,
    val product: Product,
    val weight: String
)

fun calculateTarget(weight: String, age: String): String {
    val weightFloat = weight.toFloatOrNull()
    val ageFloat = age.toFloatOrNull()

    return if (weightFloat != null && ageFloat != null) {
        (88 + 13 * weightFloat + 4.2 * 178 - 5.7 * ageFloat).roundToInt().toString()
    } else {
        "0"
    }
}

fun DayProduct.calculateCalories(): Double {
    return (weight.toDouble() / 100) * product.calories.toDouble()
}

fun calculateTotalCalories(dayProducts: List<DayProduct>): String {
    val total = dayProducts.sumOf { it.calculateCalories() }
    return if (dayProducts.isNotEmpty()) {
        total.roundToInt().toString()
    } else {
        "0"
    }
}

fun calculateDayProductCalories(dayProduct: DayProduct): String {
    val total = (dayProduct.weight.toDouble() / 100) * dayProduct.product.calories.toDouble()
    return total.roundToInt().toString()
}