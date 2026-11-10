package bg.uni.mobileapps2026

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculationRepositoryTest {
    @Test
    fun addUpdateAndDeleteCalculationThroughRepository() =
        runBlocking {
            val dao = FakeCalculationDao()
            val repository = CalculationRepository(dao)
            val original = sampleCalculation()

            assertTrue(repository.allCalculations.first().isEmpty())

            repository.addCalculation(original)
            assertEquals(listOf(original), repository.allCalculations.first())

            val updated = original.copy(weight = 72.0)
            repository.updateCalculation(updated)
            assertEquals(listOf(updated), repository.allCalculations.first())

            repository.deleteCalculation(updated)
            assertTrue(repository.allCalculations.first().isEmpty())
        }

    private fun sampleCalculation() =
        Calculation(
            id = 1,
            gender = "Жена",
            age = 25,
            height = 165.0,
            weight = 60.0,
            activityLevel = "Умерена активност",
            goal = "Поддържане на тегло",
            bmr = 1345,
            maintenanceCalories = 2085,
            targetCalories = 2085,
            date = 123456L,
        )

    private class FakeCalculationDao : CalculationDao {
        private val storedCalculations = MutableStateFlow<List<Calculation>>(emptyList())

        override suspend fun addCalculation(calculation: Calculation) {
            storedCalculations.value = storedCalculations.value + calculation
        }

        override fun getAllCalculations(): Flow<List<Calculation>> = storedCalculations

        override suspend fun updateCalculation(calculation: Calculation) {
            storedCalculations.value =
                storedCalculations.value.map { existing ->
                    if (existing.id == calculation.id) calculation else existing
                }
        }

        override suspend fun deleteCalculation(calculation: Calculation) {
            storedCalculations.value =
                storedCalculations.value.filterNot {
                    it.id == calculation.id
                }
        }
    }
}
