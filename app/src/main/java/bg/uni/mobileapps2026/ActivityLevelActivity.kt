package bg.uni.mobileapps2026

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
                systemBars.bottom,
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
            } else if (!ActivityLevelCalculator.areInputsValid(steps, workouts, minutes)) {
                resultText.text = "Моля, провери въведените данни."
            } else {
                val activityResult =
                    ActivityLevelCalculator.calculate(
                        steps,
                        workouts,
                        minutes,
                    )

                selectedActivityPosition = activityResult.position

                resultText.text =
                    "Твоето ниво е:\n${activityResult.name}\n\n" +
                    "Крачки на ден: $steps\n" +
                    "Тренировки седмично: $workouts\n" +
                    "Минути тренировки седмично: ${activityResult.weeklyWorkoutMinutes}\n\n" +
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
