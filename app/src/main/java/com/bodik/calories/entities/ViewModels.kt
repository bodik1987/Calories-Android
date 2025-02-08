package com.bodik.calories.entities

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlin.math.roundToInt

class PreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_data", Context.MODE_PRIVATE)


    fun updateSelectedDay(day: Int) {
        with(sharedPreferences.edit()) {
            putString("selectedDay", day.toString())
            apply()
        }
    }

    fun loadSelectedDay(): Int {
        val selectedDay = sharedPreferences.getString("selectedDay", "1") ?: "1"
        return selectedDay.toInt()
    }

    // Сохранить вес, возраст и целевую величину
    fun saveUserData(weight: String, age: String) {
        val target = if (weight.isNotEmpty() && age.isNotEmpty()) {
            (weight.toFloat() * age.toFloat()).roundToInt().toString()
        } else {
            "0"
        }

        with(sharedPreferences.edit()) {
            putString("weight", weight)
            putString("age", age)
            putString("target", target) // Сохраняем target
            apply()
        }
    }

    // Загрузить данные пользователя
    fun loadUserData(): UserData {
        val weight = sharedPreferences.getString("weight", "75") ?: "75"
        val age = sharedPreferences.getString("age", "37") ?: "37"
        val target = sharedPreferences.getString("target", "0") ?: "0"

        return UserData(weight, age, target)
    }

    // Сохранить список продуктов
    fun saveProducts(products: List<Product>) {
        val editor = sharedPreferences.edit()
        editor.putInt("product_count", products.size)

        products.forEachIndexed { index, product ->
            editor.putString("product_${index + 1}_id", product.id)
            editor.putString("product_${index + 1}_title", product.title)
            editor.putInt("product_${index + 1}_calories", product.calories)
            editor.putBoolean("product_${index + 1}_isFavorites", product.isFavorites)
        }
        editor.apply()
    }

    // Загрузить список продуктов
    fun loadProducts(): List<Product> {
        val productCount = sharedPreferences.getInt("product_count", 0)
        val products = mutableListOf<Product>()

        for (i in 1..productCount) {
            val id = sharedPreferences.getString("product_${i}_id", "") ?: ""
            val title = sharedPreferences.getString("product_${i}_title", "") ?: ""
            val calories = sharedPreferences.getInt("product_${i}_calories", 0)
            val isFavorites = sharedPreferences.getBoolean("product_${i}_isFavorites", false)

            if (id.isNotEmpty() && title.isNotEmpty() && calories != 0) {
                products.add(Product(id, title, calories, isFavorites))
            }
        }
        return products
    }

    // Загрузить список еды за день
    fun loadProductsToEat(): List<ProductToEat> {
        val productsToEatCount = sharedPreferences.getInt("productsToEat", 0)
        val productsToEat = mutableListOf<ProductToEat>()

        for (i in 1..productsToEatCount) {
            val id = sharedPreferences.getString("productToEat_${i}_id", "") ?: ""
            val day = sharedPreferences.getInt("productToEat_${i}_day", 0)
            val productId = sharedPreferences.getString("productToEat_${i}_product_id", "") ?: ""
            val title = sharedPreferences.getString("productToEat_${i}_product_title", "") ?: ""
            val calories =
                sharedPreferences.getInt("productToEat_${i}_product_calories", 0)
            val isFavorites =
                sharedPreferences.getBoolean("productToEat_${i}_product_isFavorites", false)
            val weight = sharedPreferences.getInt("productToEat_${i}_weight", 0)

            if (id.isNotEmpty() && day != 0 && title.isNotEmpty() && weight != 0) {
                productsToEat.add(
                    ProductToEat(
                        id,
                        day,
                        Product(productId, title, calories, isFavorites),
                        weight
                    )
                )
            }
        }
        return productsToEat
    }

    // Сохранить список еды за день
    fun saveProductsToEat(products: List<ProductToEat>) {
        val editor = sharedPreferences.edit()
        editor.putInt("productsToEat", products.size)

        products.forEachIndexed { index, item ->
            editor.putString("productToEat_${index + 1}_id", item.id)
            editor.putInt("productToEat_${index + 1}_day", item.day)
            editor.putString("productToEat_${index + 1}_product_id", item.product.id)
            editor.putString("productToEat_${index + 1}_product_title", item.product.title)
            editor.putInt("productToEat_${index + 1}_product_calories", item.product.calories)
            editor.putBoolean(
                "productToEat_${index + 1}_product_isFavorites",
                item.product.isFavorites
            )
            editor.putInt("productToEat_${index + 1}_weight", item.weight)
        }
        editor.apply()
    }
}


data class ProductToEat(
    val id: String,
    val day: Int,
    val product: Product,
    val weight: Int
)