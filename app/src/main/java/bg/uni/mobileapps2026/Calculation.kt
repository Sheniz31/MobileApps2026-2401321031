package bg.uni.mobileapps2026

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculations")
data class Calculation(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val gender: String,
    val age: Int,
    val height: Double,
    val weight: Double,
    val activityLevel: String,
    val goal: String,
    val bmr: Int,
    val maintenanceCalories: Int,
    val targetCalories: Int,
    val date: Long = System.currentTimeMillis(),
)
