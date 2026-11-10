package bg.uni.mobileapps2026

import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/** Handles the Open Food Facts request and converts its response to product records. */
object FoodSearchService {
    private const val SEARCH_ENDPOINT =
        "https://world.openfoodfacts.org/" +
            "cgi/search.pl"
    private const val PRODUCT_PAGE_SIZE = 50
    private const val REQUEST_TIMEOUT_MILLIS = 15_000
    private const val USER_AGENT = "CalorieCalculator/1.0"

    /** Searches the food service and returns the best matching product and kcal per 100 g. */
    @Throws(IOException::class, JSONException::class)
    fun searchFoodProduct(searchName: String): Pair<String, Double>? {
        val translatedName = FoodProductLogic.translateSearchTerm(searchName)
        val response = downloadSearchResponse(createSearchUrl(translatedName))
        val products = parseProductRecords(response)
        return FoodProductLogic.findMatchingProduct(translatedName, products)
    }

    private fun createSearchUrl(searchName: String): URL {
        val encodedName = URLEncoder.encode(searchName, StandardCharsets.UTF_8.name())
        val address =
            "$SEARCH_ENDPOINT?search_terms=$encodedName" +
                "&search_simple=1&action=process&json=1" +
                "&page_size=$PRODUCT_PAGE_SIZE&fields=product_name,nutriments"
        return URL(address)
    }

    private fun downloadSearchResponse(url: URL): String {
        val connection = url.openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = REQUEST_TIMEOUT_MILLIS
            connection.readTimeout = REQUEST_TIMEOUT_MILLIS
            connection.setRequestProperty("User-Agent", USER_AGENT)

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw IOException("Food service returned HTTP ${connection.responseCode}")
            }

            return connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    private fun parseProductRecords(response: String): List<FoodProductRecord> {
        val products = JSONObject(response).optJSONArray("products") ?: return emptyList()
        return (0 until products.length()).mapNotNull { index ->
            val product = products.optJSONObject(index)
            val nutriments = product?.optJSONObject("nutriments")
            if (product == null || nutriments == null) {
                null
            } else {
                FoodProductRecord(
                    name = product.optString("product_name").trim(),
                    caloriesKcalPer100g =
                        nutriments.optDouble("energy-kcal_100g", Double.NaN),
                    energyKjPer100g =
                        nutriments.optDouble("energy-kj_100g", Double.NaN),
                )
            }
        }
    }
}
