package bg.uni.mobileapps2026

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Calculation::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun calculationDao(): CalculationDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) {

                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            "calorie_database"
                        ).build()
                    }
                }
            }

            return INSTANCE!!
        }
    }
}