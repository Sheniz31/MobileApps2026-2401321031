package bg.uni.mobileapps2026

/** Shared input limits used by the calculator and history editor. */
internal object CalculationLimits {
    const val MIN_AGE = 18
    const val MAX_AGE = 100
    const val MIN_HEIGHT_CM = 100.0
    const val MAX_HEIGHT_CM = 250.0
    const val MIN_WEIGHT_KG = 1.0
    const val MAX_WEIGHT_KG = 350.0

    fun isValidWeight(weight: Double): Boolean = weight.isFinite() && weight in MIN_WEIGHT_KG..MAX_WEIGHT_KG
}
