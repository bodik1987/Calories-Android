package com.bodik.calories.entities

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import kotlin.math.roundToInt
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_data", Context.MODE_PRIVATE)

    fun getLanguage(): String {
        return sharedPreferences.getString("app_language", "en") ?: "en"
    }

    fun setLanguage(languageCode: String) {
        sharedPreferences.edit().putString("app_language", languageCode).apply()
    }

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

    // User data
    fun saveUserData(weight: String, age: String) {
        val target = if (weight.isNotEmpty() && age.isNotEmpty()) {
            (weight.toFloat() * age.toFloat()).roundToInt().toString()
        } else {
            "0"
        }

        with(sharedPreferences.edit()) {
            putString("weight", weight)
            putString("age", age)
            putString("target", target)
            apply()
        }
    }

    fun loadUserData(): UserData {
        val weight = sharedPreferences.getString("weight", "75") ?: "75"
        val age = sharedPreferences.getString("age", "37") ?: "37"
        val target = sharedPreferences.getString("target", "0") ?: "0"

        return UserData(weight, age, target)
    }

    // Products
    fun saveProducts(products: List<Product>) {
        val editor = sharedPreferences.edit()
        editor.putInt("product_count", products.size)

        products.forEachIndexed { index, product ->
            editor.putString("product_${index + 1}_id", product.id)
            editor.putString("product_${index + 1}_title", product.title)
            editor.putString("product_${index + 1}_calories", product.calories)
            editor.putBoolean("product_${index + 1}_isFavorites", product.isFavorites)
        }
        editor.apply()
    }

    fun loadProducts(): List<Product> {
        val productCount = sharedPreferences.getInt("product_count", 0)
        val products = mutableListOf<Product>()

        for (i in 1..productCount) {
            val id = sharedPreferences.getString("product_${i}_id", "") ?: ""
            val title = sharedPreferences.getString("product_${i}_title", "") ?: ""
            val calories = sharedPreferences.getString("product_${i}_calories", "0") ?: "0"
            val isFavorites = sharedPreferences.getBoolean("product_${i}_isFavorites", false)

            if (id.isNotEmpty() && title.isNotEmpty() && calories.isNotEmpty()) {
                products.add(Product(id, title, calories, isFavorites))
            }
        }
        return products
    }

    fun clearAllProducts(context: Context) {
        sharedPreferences.edit().clear().apply()
    }

    // DayProduct
    fun loadDayProducts(): List<DayProduct> {
        val dayProductsCount = sharedPreferences.getInt("dayProducts", 0)
        val dayProducts = mutableListOf<DayProduct>()

        for (i in 1..dayProductsCount) {
            val id = sharedPreferences.getString("dayProduct_${i}_id", "") ?: ""
            val day = sharedPreferences.getInt("dayProduct_${i}_day", 0)
            val productId = sharedPreferences.getString("dayProduct_${i}_product_id", "") ?: ""
            val title = sharedPreferences.getString("dayProduct_${i}_product_title", "") ?: ""
            val calories =
                sharedPreferences.getString("dayProduct_${i}_product_calories", "0") ?: "0"
            val isFavorites =
                sharedPreferences.getBoolean("dayProduct_${i}_product_isFavorites", false)
            val weight = sharedPreferences.getString("dayProduct_${i}_weight", "0") ?: "0"

            if (id.isNotEmpty() && day != 0 && title.isNotEmpty() && weight.isNotEmpty()) {
                dayProducts.add(
                    DayProduct(
                        id,
                        day,
                        Product(productId, title, calories, isFavorites),
                        weight
                    )
                )
            }
        }
        return dayProducts
    }

    fun saveDayProducts(dayProducts: List<DayProduct>) {
        val editor = sharedPreferences.edit()
        editor.putInt("dayProducts", dayProducts.size)

        dayProducts.forEachIndexed { index, item ->
            editor.putString("dayProduct_${index + 1}_id", item.id)
            editor.putInt("dayProduct_${index + 1}_day", item.day)
            editor.putString("dayProduct_${index + 1}_product_id", item.product.id)
            editor.putString("dayProduct_${index + 1}_product_title", item.product.title)
            editor.putString("dayProduct_${index + 1}_product_calories", item.product.calories)
            editor.putBoolean(
                "dayProduct_${index + 1}_product_isFavorites",
                item.product.isFavorites
            )
            editor.putString("dayProduct_${index + 1}_weight", item.weight)
        }
        editor.apply()
    }

    fun restoreBackupFromUri(context: Context, uri: Uri) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val json = inputStream?.bufferedReader()?.use { it.readText() }

            println("Loaded JSON: $json")

            val type = object : TypeToken<List<Product>>() {}.type
            val products: List<Product> = Gson().fromJson(json, type) ?: emptyList()

            println("Parsed Products: $products")

            saveProducts(products)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun saveBackupToUri(context: Context, uri: Uri, products: List<Product>) {
    try {
        val json = Gson().toJson(products)
        context.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray()) }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}




