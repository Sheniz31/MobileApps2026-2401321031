package bg.uni.mobileapps2026

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val recyclerView =
            findViewById<RecyclerView>(R.id.calculationsRecyclerView)

        val emptyText =
            findViewById<TextView>(R.id.emptyText)

        // Свързваме ViewModel с базата данни
        val database = AppDatabase.getDatabase(this)
        val repository = CalculationRepository(database.calculationDao())
        val factory = CalculationViewModelFactory(repository)

        val viewModel =
            ViewModelProvider(
                this,
                factory,
            )[CalculationViewModel::class.java]

        // Създаваме списъка с изчисления
        val adapter =
            CalculationAdapter(
                onEditClick = { calculation ->
                    editCalculation(calculation, viewModel)
                },
                onDeleteClick = { calculation ->

                    AlertDialog
                        .Builder(this)
                        .setTitle("Изтриване")
                        .setMessage("Искаш ли да изтриеш това изчисление?")
                        .setNegativeButton("Отказ", null)
                        .setPositiveButton("Изтрий") { _, _ ->

                            viewModel.deleteCalculation(calculation) { success ->

                                if (success) {
                                    Toast
                                        .makeText(
                                            this,
                                            "Изчислението е изтрито.",
                                            Toast.LENGTH_SHORT,
                                        ).show()
                                } else {
                                    Toast
                                        .makeText(
                                            this,
                                            "Възникна грешка.",
                                            Toast.LENGTH_SHORT,
                                        ).show()
                                }
                            }
                        }.show()
                },
            )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Зареждаме запазените изчисления
        lifecycleScope.launch {
            viewModel.allCalculations.collect { calculations ->

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
        viewModel: CalculationViewModel,
    ) {
        val weightInput = EditText(this)

        weightInput.inputType =
            InputType.TYPE_CLASS_NUMBER or
            InputType.TYPE_NUMBER_FLAG_DECIMAL

        weightInput.setText(calculation.weight.toString())

        AlertDialog
            .Builder(this)
            .setTitle("Промени теглото")
            .setMessage("Въведи новото си тегло в кг.")
            .setView(weightInput)
            .setNegativeButton("Отказ", null)
            .setPositiveButton("Запази") { _, _ ->

                val newWeight =
                    weightInput.text
                        .toString()
                        .replace(",", ".")
                        .toDoubleOrNull()

                if (newWeight == null || !CalculationLimits.isValidWeight(newWeight)) {
                    Toast
                        .makeText(
                            this,
                            "Моля, въведи валидно тегло.",
                            Toast.LENGTH_SHORT,
                        ).show()
                } else {
                    val updatedCalculation =
                        CalculationUpdater.withUpdatedWeight(
                            calculation,
                            newWeight,
                        )

                    if (updatedCalculation == null) {
                        Toast
                            .makeText(
                                this,
                                "Моля, въведи валидно тегло.",
                                Toast.LENGTH_SHORT,
                            ).show()
                    } else {
                        viewModel.updateCalculation(updatedCalculation) { success ->

                            val message =
                                if (success) {
                                    "Изчислението е променено."
                                } else {
                                    "Възникна грешка."
                                }

                            Toast
                                .makeText(
                                    this,
                                    message,
                                    Toast.LENGTH_SHORT,
                                ).show()
                        }
                    }
                }
            }.show()
    }
}
