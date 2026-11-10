package bg.uni.mobileapps2026

/** Result of validating the three required fields on the main calculator screen. */
enum class CalculationInputStatus {
    MISSING_FIELDS,
    OUT_OF_RANGE,
    VALID,
}

object CalculationInputValidator {
    fun validate(
        age: Int?,
        height: Double?,
        weight: Double?,
    ): CalculationInputStatus {
        if (age == null || height == null || weight == null) {
            return CalculationInputStatus.MISSING_FIELDS
        }

        return validatePresentValues(age, height, weight)
    }

    private fun validatePresentValues(
        age: Int,
        height: Double,
        weight: Double,
    ): CalculationInputStatus =
        if (!isValidAge(age) || !isValidHeight(height) || !CalculationLimits.isValidWeight(weight)) {
            CalculationInputStatus.OUT_OF_RANGE
        } else {
            CalculationInputStatus.VALID
        }

    private fun isValidAge(age: Int): Boolean = age in CalculationLimits.MIN_AGE..CalculationLimits.MAX_AGE

    private fun isValidHeight(height: Double): Boolean =
        height.isFinite() && height in CalculationLimits.MIN_HEIGHT_CM..CalculationLimits.MAX_HEIGHT_CM
}
