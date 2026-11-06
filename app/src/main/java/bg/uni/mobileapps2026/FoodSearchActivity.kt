
package bg.uni.mobileapps2026

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
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

        productInput = findViewById(R.id.productInput)
        gramsInput = findViewById(R.id.gramsInput)
        searchButton = findViewById(R.id.searchButton)
        calculateButton = findViewById(R.id.calculateButton)
        messageText = findViewById(R.id.messageText)
        productNameText = findViewById(R.id.productNameText)
        caloriesPer100Text = findViewById(R.id.caloriesPer100Text)
        totalCaloriesText = findViewById(R.id.totalCaloriesText)

        searchButton.setOnClickListener {
            searchProduct()
        }

        calculateButton.setOnClickListener {
            calculateCalories()
        }
    }

    private fun searchProduct() {
        val searchName = productInput.text.toString().trim()

        if (searchName.isEmpty()) {
            messageText.text = "Моля, въведи име на продукт."
            return
        }

        caloriesPer100Grams = null
        productNameText.text = ""
        caloriesPer100Text.text = ""
        totalCaloriesText.text = ""
        messageText.text = "Търсене..."

        searchButton.isEnabled = false

        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    searchFoodProduct(searchName)
                }

                if (result != null) {
                    caloriesPer100Grams = result.second

                    productNameText.text = result.first

                    caloriesPer100Text.text = String.format(
                        Locale.getDefault(),
                        "Калории за 100 грама: %.1f kcal",
                        result.second
                    )

                    messageText.text = "Продуктът е намерен!"
                } else {
                    messageText.text =
                        "Не е намерен продукт с налични калорийни данни."
                }

            } catch (e: Exception) {
                messageText.text =
                    "Грешка при връзката. Провери интернет връзката."
            } finally {
                searchButton.isEnabled = true
            }
        }
    }


    private fun searchFoodProduct(searchName: String): Pair<String, Double>? {

        val translatedName = when (searchName.trim().lowercase(Locale.ROOT)) {
            "банан", "банани" -> "banana"
            "ябълка", "ябълки" -> "apple"
            "мляко" -> "milk"
            "хляб" -> "bread"
            "яйце", "яйца" -> "egg"
            "ориз" -> "rice"
            "картоф", "картофи" -> "potato"
            "домат", "домати" -> "tomato"
            "сирене" -> "cheese"
            else -> searchName.trim()
        }

        val encodedName = URLEncoder.encode(translatedName, "UTF-8")

        val url = URL(
            "https://world.openfoodfacts.org/cgi/search.pl" +
                    "?search_terms=$encodedName" +
                    "&search_simple=1" +
                    "&action=process" +
                    "&json=1" +
                    "&page_size=50" +
                    "&fields=product_name,nutriments"
        )

        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 15000
            connection.readTimeout = 15000

            connection.setRequestProperty(
                "User-Agent",
                "CalorieCalculator/1.0"
            )

            val responseCode = connection.responseCode

            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw Exception("Грешка от сървъра: $responseCode")
            }

            val response = connection.inputStream
                .bufferedReader()
                .use { it.readText() }

            val jsonObject = JSONObject(response)
            val products = jsonObject.optJSONArray("products")
                ?: return null

            val searchWords = translatedName.lowercase(Locale.ROOT)
                .split(Regex("[^\\p{L}\\p{N}]+"))
                .filter { it.isNotBlank() }

            for (i in 0 until products.length()) {

                val product = products.optJSONObject(i) ?: continue

                val name = product.optString("product_name").trim()

                if (name.isEmpty()) continue

                val productWords = name.lowercase(Locale.ROOT)
                    .split(Regex("[^\\p{L}\\p{N}]+"))
                    .filter { it.isNotBlank() }

                if (!searchWords.all { it in productWords }) {
                    continue
                }

                val nutriments = product.optJSONObject("nutriments")
                    ?: continue

                val kcal = nutriments.optDouble(
                    "energy-kcal_100g",
                    Double.NaN
                )

                val calories = if (kcal.isFinite()) {
                    kcal
                } else {
                    val kj = nutriments.optDouble(
                        "energy-kj_100g",
                        Double.NaN
                    )

                    if (kj.isFinite()) {
                        kj / 4.184
                    } else {
                        continue
                    }
                }

                if (calories >= 0) {
                    return Pair(name, calories)
                }
            }

            return null

        } finally {
            connection.disconnect()
        }
    }


    private fun calculateCalories() {

        val calories = caloriesPer100Grams

        if (calories == null) {
            messageText.text = "Първо потърси хранителен продукт."
            return
        }

        val gramsText = gramsInput.text.toString()
            .trim()
            .replace(',', '.')

        val grams = gramsText.toDoubleOrNull()

        if (grams == null || grams <= 0) {
            totalCaloriesText.text = "Въведи валиден грамаж."
            return
        }

        val totalCalories = calories * grams / 100

        totalCaloriesText.text = String.format(
            Locale.getDefault(),
            "Общо: %.1f kcal",
            totalCalories
        )
    }
}
