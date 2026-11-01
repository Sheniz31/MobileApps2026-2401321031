package bg.uni.mobileapps2026

import kotlinx.coroutines.flow.Flow

class CalculationRepository(
    private val calculationDao: CalculationDao
) {

    val allCalculations: Flow<List<Calculation>> =
        calculationDao.getAllCalculations()

    suspend fun addCalculation(calculation: Calculation) {
        calculationDao.addCalculation(calculation)
    }

    suspend fun updateCalculation(calculation: Calculation) {
        calculationDao.updateCalculation(calculation)
    }

    suspend fun deleteCalculation(calculation: Calculation) {
        calculationDao.deleteCalculation(calculation)
    }
}