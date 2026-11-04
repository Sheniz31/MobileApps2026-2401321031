package bg.uni.mobileapps2026

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CalculationViewModel(
    private val repository: CalculationRepository
) : ViewModel() {

    val allCalculations: Flow<List<Calculation>> =
        repository.allCalculations

    fun addCalculation(
        calculation: Calculation,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.addCalculation(calculation)
                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }

    fun updateCalculation(
        calculation: Calculation,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.updateCalculation(calculation)
                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }

    fun deleteCalculation(
        calculation: Calculation,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.deleteCalculation(calculation)
                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }
}

class CalculationViewModelFactory(
    private val repository: CalculationRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(CalculationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalculationViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}