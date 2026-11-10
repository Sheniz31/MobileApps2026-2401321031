package bg.uni.mobileapps2026

import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CalculationViewModel(
    private val repository: CalculationRepository,
) : ViewModel() {
    val allCalculations: Flow<List<Calculation>> = repository.allCalculations

    fun addCalculation(
        calculation: Calculation,
        onComplete: (Boolean) -> Unit,
    ) = performDatabaseOperation("add", onComplete) {
        repository.addCalculation(calculation)
    }

    fun updateCalculation(
        calculation: Calculation,
        onComplete: (Boolean) -> Unit,
    ) = performDatabaseOperation("update", onComplete) {
        repository.updateCalculation(calculation)
    }

    fun deleteCalculation(
        calculation: Calculation,
        onComplete: (Boolean) -> Unit,
    ) = performDatabaseOperation("delete", onComplete) {
        repository.deleteCalculation(calculation)
    }

    private fun performDatabaseOperation(
        operation: String,
        onComplete: (Boolean) -> Unit,
        action: suspend () -> Unit,
    ) {
        viewModelScope.launch {
            val succeeded =
                try {
                    action()
                    true
                } catch (exception: SQLiteException) {
                    Log.e(TAG, "Could not $operation calculation", exception)
                    false
                }
            onComplete(succeeded)
        }
    }

    private companion object {
        const val TAG = "CalculationViewModel"
    }
}

class CalculationViewModelFactory(
    private val repository: CalculationRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalculationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalculationViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
