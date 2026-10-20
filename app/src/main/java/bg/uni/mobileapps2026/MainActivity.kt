package bg.uni.mobileapps2026

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        val genderSpinner = findViewById<Spinner>(R.id.genderSpinner)
        val ageInput = findViewById<EditText>(R.id.ageInput)
        val heightInput = findViewById<EditText>(R.id.heightInput)
        val weightInput = findViewById<EditText>(R.id.weightInput)
        val activitySpinner = findViewById<Spinner>(R.id.activitySpinner)
        val goalSpinner = findViewById<Spinner>(R.id.goalSpinner)
        val calculateButton = findViewById<Button>(R.id.calculateButton)
        val resultTextView = findViewById<TextView>(R.id.resultTextView)

        calculateButton.setOnClickListener {

            val age = ageInput.text.toString().toIntOrNull()

            val height = heightInput.text.toString()
                .replace(",", ".").toDoubleOrNull()

            val weight = weightInput.text.toString()
                .replace(",", ".").toDoubleOrNull()

            if (age == null || height == null || weight == null) {

                resultTextView.text =
                    "Моля, попълни всички полета с валидни числа."

            } else if (
                age !in 18..100 ||
                height !in 100.0..250.0 ||
                weight !in 1.0..350.0
            ) {

                resultTextView.text =
                    "Моля, провери въведените данни."

            } else {

                val bmr: Double

                if (genderSpinner.selectedItemPosition == 0) {
                    bmr = 10 * weight + 6.25 * height - 5 * age - 161
                } else {
                    bmr = 10 * weight + 6.25 * height - 5 * age + 5
                }

                val activityLevel = activitySpinner.selectedItemPosition

                val activityMultiplier = when (activityLevel) {
                    0 -> 1.2
                    1 -> 1.375
                    2 -> 1.55
                    3 -> 1.725
                    else -> 1.9
                }

                val maintenanceCalories = bmr * activityMultiplier

                val targetCalories: Double

                if (goalSpinner.selectedItemPosition == 0) {
                    targetCalories = maintenanceCalories * 0.85
                } else if (goalSpinner.selectedItemPosition == 2) {
                    targetCalories = maintenanceCalories * 1.10
                } else {
                    targetCalories = maintenanceCalories
                }

                resultTextView.text =
                    "Разход в покой: ${bmr.roundToInt()} kcal\n\n" +
                            "Калории за поддържане: " +
                            "${maintenanceCalories.roundToInt()} kcal\n\n" +
                            "Твоята цел (${goalSpinner.selectedItem}): " +
                            "${targetCalories.roundToInt()} kcal на ден\n\n" +
                            "Резултатите са ориентировъчни."
            }
        }
    }
}