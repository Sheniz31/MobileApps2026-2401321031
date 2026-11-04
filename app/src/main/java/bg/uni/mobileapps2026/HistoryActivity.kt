package bg.uni.mobileapps2026

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.text.InputType
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val recyclerView =
            findViewById<RecyclerView>(R.id.calculationsRecyclerView)

        val emptyText =
            findViewById<TextView>(R.id.emptyText)

        val database = AppDatabase.getDatabase(this)
        val repository = CalculationRepository(database.calculationDao())

        val adapter = CalculationAdapter(

            onEditClick = { calculation ->
                editCalculation(calculation, repository)
            },

            onDeleteClick = { calculation ->

                AlertDialog.Builder(this)
                    .setTitle("Изтриване")
                    .setMessage("Сигурен ли си, че искаш да изтриеш това изчисление?")
                    .setNegativeButton("Отказ", null)
                    .setPositiveButton("Изтрий") { _, _ ->

                        lifecycleScope.launch {
                            repository.deleteCalculation(calculation)

                            Toast.makeText(
                                this@HistoryActivity,
                                "Изчислението е изтрито.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .show()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        lifecycleScope.launch {
            repository.allCalculations.collect { calculations ->

                adapter.setData(calculations)

                if (calculations.isEmpty()) {
                    emptyText.visibility = View.VISIBLE
                } else {
                    emptyText.visibility = View.GONE
                }
            }
        }
    }

    private fun editCalculation(
        calculation: Calculation,
        repository: CalculationRepository
    ) {

        val weightInput = EditText(this)

        weightInput.inputType =
            InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL

        weightInput.setText(calculation.weight.toString())
        weightInput.hint = "Ново тегло в кг"

        AlertDialog.Builder(this)
            .setTitle("Промени теглото")
            .setMessage("Въведи новото си тегло.")
            .setView(weightInput)
            .setNegativeButton("Отказ", null)
            .setPositiveButton("Запази") { _, _ ->

                val newWeight = weightInput.text.toString()
                    .replace(",", ".")
                    .toDoubleOrNull()

                if (newWeight == null || newWeight !in 1.0..350.0) {

                    Toast.makeText(
                        this,
                        "Моля, въведи валидно тегло.",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    val bmr: Double

                    if (calculation.gender == "Жена") {
                        bmr = 10 * newWeight +
                                6.25 * calculation.height -
                                5 * calculation.age - 161
                    } else {
                        bmr = 10 * newWeight +
                                6.25 * calculation.height -
                                5 * calculation.age + 5
                    }

                    val activityMultiplier =
                        when (calculation.activityLevel) {
                            "Заседнал начин на живот" -> 1.2
                            "Лека активност" -> 1.375
                            "Умерена активност" -> 1.55
                            "Висока активност" -> 1.725
                            else -> 1.9
                        }

                    val maintenance = bmr * activityMultiplier

                    val target: Double

                    if (calculation.goal == "Сваляне на тегло") {
                        target = maintenance * 0.85
                    } else if (calculation.goal == "Качване на тегло") {
                        target = maintenance * 1.10
                    } else {
                        target = maintenance
                    }

                    val updatedCalculation = calculation.copy(
                        weight = newWeight,
                        bmr = bmr.roundToInt(),
                        maintenanceCalories = maintenance.roundToInt(),
                        targetCalories = target.roundToInt(),
                        date = System.currentTimeMillis()
                    )

                    lifecycleScope.launch {
                        repository.updateCalculation(updatedCalculation)

                        Toast.makeText(
                            this@HistoryActivity,
                            "Изчислението е променено.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            .show()
    }
}