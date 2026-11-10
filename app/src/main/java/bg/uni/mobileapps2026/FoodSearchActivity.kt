package bg.uni.mobileapps2026

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import java.io.IOException
import java.util.Locale

class FoodSearchActivity : AppCompatActivity() {
    private lateinit var productInput: EditText
    private lateinit var gramsInput: EditText
    private lateinit var searchButton: Button
    private lateinit var calculateButton: Button
    private lateinit var messageText: TextView
    private lateinit var productNameText: TextView
    private lateinit var caloriesPer100Text: TextView
    private lateinit var totalCaloriesText: TextView

    private var caloriesPer100Grams: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_search)
        bindViews()
        bindActions()
    }

    private fun bindViews() {
        productInput = findViewById(R.id.productInput)
        gramsInput = findViewById(R.id.gramsInput)
        searchButton = findViewById(R.id.searchButton)
        calculateButton = findViewById(R.id.calculateButton)
        messageText = findViewById(R.id.messageText)
        productNameText = findViewById(R.id.productNameText)
        caloriesPer100Text = findViewById(R.id.caloriesPer100Text)
        totalCaloriesText = findViewById(R.id.totalCaloriesText)
    }

    private fun bindActions() {
        searchButton.setOnClickListener { searchProduct() }
        calculateButton.setOnClickListener { calculateCalories() }
    }

    private fun searchProduct() {
        val searchName = productInput.text.toString().trim()
        if (searchName.isEmpty()) {
            messageText.text = "Моля, въведи име на продукт."
            return
        }

        clearPreviousResult()
        searchButton.isEnabled = false

        lifecycleScope.launch {
            try {
                val result =
                    withContext(Dispatchers.IO) {
                        FoodSearchService.searchFoodProduct(searchName)
                    }
                showSearchResult(result)
            } catch (exception: IOException) {
                Log.e(TAG, "Food search request failed", exception)
                messageText.text =
                    "Грешка при връзката. Провери интернет връзката."
            } catch (exception: JSONException) {
                Log.e(TAG, "Food search response could not be read", exception)
                messageText.text =
                    "Получените данни не могат да бъдат обработени."
            } finally {
                searchButton.isEnabled = true
            }
        }
    }

    private fun clearPreviousResult() {
        caloriesPer100Grams = null
        productNameText.text = ""
        caloriesPer100Text.text = ""
        totalCaloriesText.text = ""
        messageText.text = "Търсене..."
    }

    private fun showSearchResult(result: Pair<String, Double>?) {
        if (result == null) {
            messageText.text = "Не е намерен продукт с налични калорийни данни."
            return
        }

        caloriesPer100Grams = result.second
        productNameText.text = result.first
        caloriesPer100Text.text = formatCaloriesPer100(result.second)
        messageText.text = "Продуктът е намерен!"
    }

    private fun formatCaloriesPer100(calories: Double): String =
        String.format(
            Locale.getDefault(),
            "Калории за 100 грама: %.1f kcal",
            calories,
        )

    private fun calculateCalories() {
        val calories = caloriesPer100Grams
        if (calories == null) {
            messageText.text = "Първо потърси хранителен продукт."
        } else {
            val grams =
                gramsInput.text
                    .toString()
                    .trim()
                    .replace(',', '.')
                    .toDoubleOrNull()
            val totalCalories = grams?.let { FoodProductLogic.calculateTotalCalories(calories, it) }
            totalCaloriesText.text =
                totalCalories?.let { formatTotalCalories(it) }
                    ?: "Въведи валиден грамаж."
        }
    }

    private fun formatTotalCalories(calories: Double): String =
        String.format(
            Locale.getDefault(),
            "Общо: %.1f kcal",
            calories,
        )

    private companion object {
        const val TAG = "FoodSearchActivity"
    }
}
