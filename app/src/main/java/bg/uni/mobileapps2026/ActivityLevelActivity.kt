package bg.uni.mobileapps2026

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ActivityLevelActivity : AppCompatActivity() {

    private var selectedActivityPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_level)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        val stepsInput = findViewById<EditText>(R.id.stepsInput)
        val workoutsInput = findViewById<EditText>(R.id.workoutsInput)
        val minutesInput = findViewById<EditText>(R.id.minutesInput)

        val calculateButton =
            findViewById<Button>(R.id.calculateActivityButton)

        val resultText =
            findViewById<TextView>(R.id.activityResultText)

        val useActivityButton =
            findViewById<Button>(R.id.useActivityButton)

        calculateButton.setOnClickListener {

            val steps = stepsInput.text.toString().toIntOrNull()
            val workouts = workoutsInput.text.toString().toIntOrNull()
            val minutes = minutesInput.text.toString().toIntOrNull()

            if (steps == null || workouts == null || minutes == null) {

                resultText.text = "Моля, попълни всички полета."

            } else if (
                steps !in 0..50000 ||
                workouts !in 0..14 ||
                minutes !in 0..300
            ) {

                resultText.text = "Моля, провери въведените данни."

            } else {

                val weeklyMinutes = workouts * minutes
                var points = 0

                if (steps >= 10000) {
                    points += 4
                } else if (steps >= 8000) {
                    points += 3
                } else if (steps >= 6000) {
                    points += 2
                } else if (steps >= 3000) {
                    points += 1
                }

                if (weeklyMinutes >= 300) {
                    points += 4
                } else if (weeklyMinutes >= 150) {
                    points += 3
                } else if (weeklyMinutes >= 60) {
                    points += 2
                } else if (weeklyMinutes > 0) {
                    points += 1
                }

                val activityName: String

                if (points <= 1) {
                    selectedActivityPosition = 0
                    activityName = "Заседнал начин на живот"
                } else if (points <= 3) {
                    selectedActivityPosition = 1
                    activityName = "Лека активност"
                } else if (points <= 5) {
                    selectedActivityPosition = 2
                    activityName = "Средна активност"
                } else if (points <= 7) {
                    selectedActivityPosition = 3
                    activityName = "Висока активност"
                } else {
                    selectedActivityPosition = 4
                    activityName = "Много висока активност"
                }

                resultText.text =
                    "Твоето ниво е:\n$activityName\n\n" +
                            "Крачки на ден: $steps\n" +
                            "Тренировки седмично: $workouts\n" +
                            "Минути тренировки седмично: $weeklyMinutes\n\n" +
                            "Нивото е ориентировъчна оценка."
            }
        }

        useActivityButton.setOnClickListener {

            if (selectedActivityPosition == -1) {

                resultText.text =
                    "Първо изчисли нивото си на активност."

            } else {

                val result = android.content.Intent()
                result.putExtra("activityPosition", selectedActivityPosition)
                setResult(RESULT_OK, result)
                finish()
            }
        }
    }
}