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
    private lateinit var genderSpinner: Spinner
    private lateinit var ageInput: EditText
    private lateinit var heightInput: EditText
    private lateinit var weightInput: EditText
    private lateinit var activitySpinner: Spinner
    private lateinit var goalSpinner: Spinner
    private lateinit var resultTextView: TextView

    private val activityLevelLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val position = result.data?.getIntExtra(ACTIVITY_POSITION_EXTRA, INVALID_POSITION)
            if (result.resultCode == RESULT_OK &&
                position != null &&
                position in validActivityPositions
            ) {
                activitySpinner.setSelection(position)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        bindViews()
        applyWindowInsets()
        bindNavigationActions()
        bindCalculationAction(createCalculationViewModel())
    }

    private fun bindViews() {
        genderSpinner = findViewById(R.id.genderSpinner)
        ageInput = findViewById(R.id.ageInput)
        heightInput = findViewById(R.id.heightInput)
        weightInput = findViewById(R.id.weightInput)
        activitySpinner = findViewById(R.id.activitySpinner)
        goalSpinner = findViewById(R.id.goalSpinner)
        resultTextView = findViewById(R.id.resultTextView)
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom,
            )
            insets
        }
    }

    private fun bindNavigationActions() {
        findViewById<Button>(R.id.activityLevelButton).setOnClickListener {
            activityLevelLauncher.launch(Intent(this, ActivityLevelActivity::class.java))
        }
        findViewById<Button>(R.id.historyButton).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        findViewById<Button>(R.id.foodSearchButton).setOnClickListener {
            startActivity(Intent(this, FoodSearchActivity::class.java))
        }
    }

    private fun bindCalculationAction(viewModel: CalculationViewModel) {
        findViewById<Button>(R.id.calculateButton).setOnClickListener {
            calculateAndSave(viewModel)
        }
    }

    private fun createCalculationViewModel(): CalculationViewModel {
        val database = AppDatabase.getDatabase(this)
        val repository = CalculationRepository(database.calculationDao())
        val factory = CalculationViewModelFactory(repository)
        return ViewModelProvider(this, factory)[CalculationViewModel::class.java]
    }

    private fun calculateAndSave(viewModel: CalculationViewModel) {
        val input = readCalculationInput() ?: return
        val calculation = createCalculation(input)
        showCalculationResult(calculation)
        viewModel.addCalculation(calculation, ::showSaveResult)
    }

    private fun readCalculationInput(): CalculatorInput? {
        val age = ageInput.text.toString().toIntOrNull()
        val height = heightInput.text.toString().toDoubleOrNullAfterCommaReplacement()
        val weight = weightInput.text.toString().toDoubleOrNullAfterCommaReplacement()

        return when (CalculationInputValidator.validate(age, height, weight)) {
            CalculationInputStatus.MISSING_FIELDS -> {
                resultTextView.text = "Моля, попълни всички полета с валидни числа."
                null
            }

            CalculationInputStatus.OUT_OF_RANGE -> {
                resultTextView.text = "Моля, провери въведените данни."
                null
            }

            CalculationInputStatus.VALID -> {
                CalculatorInput(
                    age = requireNotNull(age),
                    height = requireNotNull(height),
                    weight = requireNotNull(weight),
                )
            }
        }
    }

    private fun createCalculation(input: CalculatorInput): Calculation {
        val isFemale = genderSpinner.selectedItemPosition == FEMALE_POSITION
        val bmr = CalorieCalculator.calculateBmr(input.weight, input.height, input.age, isFemale)
        val maintenance =
            CalorieCalculator.calculateMaintenanceCalories(
                bmr,
                activitySpinner.selectedItemPosition,
            )
        val target =
            CalorieCalculator.calculateTargetCalories(
                maintenance,
                goalSpinner.selectedItemPosition,
            )

        return Calculation(
            gender = genderSpinner.selectedItem.toString(),
            age = input.age,
            height = input.height,
            weight = input.weight,
            activityLevel = activitySpinner.selectedItem.toString(),
            goal = goalSpinner.selectedItem.toString(),
            bmr = bmr.roundToInt(),
            maintenanceCalories = maintenance.roundToInt(),
            targetCalories = target.roundToInt(),
        )
    }

    private fun showCalculationResult(calculation: Calculation) {
        resultTextView.text =
            "Разход в покой: ${calculation.bmr} kcal\n\n" +
            "Калории за поддържане: ${calculation.maintenanceCalories} kcal\n\n" +
            "Твоята цел (${calculation.goal}): ${calculation.targetCalories} kcal на ден\n\n" +
            "Резултатите са ориентировъчни."
    }

    private fun showSaveResult(success: Boolean) {
        val message =
            if (success) {
                "Изчислението е запазено!"
            } else {
                "Възникна грешка при запазването."
            }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private companion object {
        const val ACTIVITY_POSITION_EXTRA = "activityPosition"
        const val INVALID_POSITION = -1
        const val FEMALE_POSITION = 0
        const val LAST_ACTIVITY_POSITION = 4
        val validActivityPositions = 0..LAST_ACTIVITY_POSITION
    }
}

private data class CalculatorInput(
    val age: Int,
    val height: Double,
    val weight: Double,
)

private fun String.toDoubleOrNullAfterCommaReplacement(): Double? = replace(",", ".").toDoubleOrNull()
