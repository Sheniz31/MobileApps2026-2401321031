package bg.uni.mobileapps2026

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculationDao {

    @Insert
    suspend fun addCalculation(calculation: Calculation)

    @Query("SELECT * FROM calculations ORDER BY date DESC")
    fun getAllCalculations(): Flow<List<Calculation>>

    @Update
    suspend fun updateCalculation(calculation: Calculation)

    @Delete
    suspend fun deleteCalculation(calculation: Calculation)
}