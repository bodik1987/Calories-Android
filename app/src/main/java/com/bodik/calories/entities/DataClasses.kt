package com.bodik.calories.entities

import kotlin.math.roundToInt

data class Product(
    val id: String,
    val title: String,
    val calories: Int,
    val isFavorites: Boolean
)

data class UserData(
    val weight: String,
    val age: String,
    val target: String
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