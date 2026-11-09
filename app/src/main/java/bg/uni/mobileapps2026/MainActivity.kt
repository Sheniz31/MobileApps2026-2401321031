
package bg.uni.mobileapps2026

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private val activityLevelLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == RESULT_OK) {
                val position = result.data?.getIntExtra(
                    "activityPosition", -1
                ) ?: -1

                if (position in 0..4) {
                    findViewById<Spinner>(
                        R.id.activitySpinner
                    ).setSelection(position)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->

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

        // Създаваме ViewModel
        val database = AppDatabase.getDatabase(this)
        val repository = CalculationRepository(database.calculationDao())

        val factory = CalculationViewModelFactory(repository)

        val viewModel = ViewModelProvider(
            this, factory
        )[CalculationViewModel::class.java]

        val genderSpinner = findViewById<Spinner>(R.id.genderSpinner)
        val ageInput = findViewById<EditText>(R.id.ageInput)
        val heightInput = findViewById<EditText>(R.id.heightInput)
        val weightInput = findViewById<EditText>(R.id.weightInput)
        val activitySpinner = findViewById<Spinner>(R.id.activitySpinner)
        val goalSpinner = findViewById<Spinner>(R.id.goalSpinner)

        val calculateButton = findViewById<Button>(R.id.calculateButton)
        val resultTextView = findViewById<TextView>(R.id.resultTextView)

        val activityLevelButton =
            findViewById<Button>(R.id.activityLevelButton)

        val historyButton =
            findViewById<Button>(R.id.historyButton)

        val foodSearchButton =
            findViewById<Button>(R.id.foodSearchButton)

        activityLevelButton.setOnClickListener {
            val intent = Intent(this, ActivityLevelActivity::class.java)
            activityLevelLauncher.launch(intent)
        }

        historyButton.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        foodSearchButton.setOnClickListener {
            startActivity(Intent(this, FoodSearchActivity::class.java))
        }

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

                // Изчисляваме калориите чрез CalorieCalculator
                val bmr = CalorieCalculator.calculateBmr(
                    weight,
                    height,
                    age,
                    genderSpinner.selectedItemPosition == 0
                )

                val maintenanceCalories =
                    CalorieCalculator.calculateMaintenanceCalories(
                        bmr,
                        activitySpinner.selectedItemPosition
                    )

                val targetCalories =
                    CalorieCalculator.calculateTargetCalories(
                        maintenanceCalories,
                        goalSpinner.selectedItemPosition
                    )

                resultTextView.text =
                    "Разход в покой: ${bmr.roundToInt()} kcal\n\n" +
                            "Калории за поддържане: " +
                            "${maintenanceCalories.roundToInt()} kcal\n\n" +
                            "Твоята цел (${goalSpinner.selectedItem}): " +
                            "${targetCalories.roundToInt()} kcal на ден\n\n" +
                            "Резултатите са ориентировъчни."

                val calculation = Calculation(
                    gender = genderSpinner.selectedItem.toString(),
                    age = age,
                    height = height,
                    weight = weight,
                    activityLevel = activitySpinner.selectedItem.toString(),
                    goal = goalSpinner.selectedItem.toString(),
                    bmr = bmr.roundToInt(),
                    maintenanceCalories = maintenanceCalories.roundToInt(),
                    targetCalories = targetCalories.roundToInt()
                )

                // Запазваме чрез ViewModel
                viewModel.addCalculation(calculation) { success ->

                    if (success) {
                        Toast.makeText(
                            this,
                            "Изчислението е запазено!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Възникна грешка при запазването.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}
