package bg.uni.mobileapps2026

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CalculationAdapter(
    private val onEditClick: (Calculation) -> Unit,
    private val onDeleteClick: (Calculation) -> Unit,
) : RecyclerView.Adapter<CalculationAdapter.CalculationViewHolder>() {
    private var calculations = listOf<Calculation>()

    fun setData(newCalculations: List<Calculation>) {
        calculations = newCalculations
        notifyDataSetChanged()
    }

    class CalculationViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        val dateText: TextView = itemView.findViewById(R.id.dateText)
        val detailsText: TextView = itemView.findViewById(R.id.detailsText)
        val caloriesText: TextView = itemView.findViewById(R.id.caloriesText)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CalculationViewHolder {
        val view =
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_calculation, parent, false)

        return CalculationViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CalculationViewHolder,
        position: Int,
    ) {
        val calculation = calculations[position]

        val dateFormat =
            SimpleDateFormat(
                "dd.MM.yyyy HH:mm",
                Locale.getDefault(),
            )

        holder.dateText.text =
            dateFormat.format(Date(calculation.date))

        holder.detailsText.text =
            "${calculation.gender}, ${calculation.age} г.\n" +
            "${calculation.height} см, ${calculation.weight} кг\n" +
            "Активност: ${calculation.activityLevel}\n" +
            "Цел: ${calculation.goal}"

        holder.caloriesText.text =
            "${calculation.targetCalories} kcal на ден"

        holder.editButton.setOnClickListener {
            onEditClick(calculation)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick(calculation)
        }
    }

    override fun getItemCount(): Int = calculations.size
}
