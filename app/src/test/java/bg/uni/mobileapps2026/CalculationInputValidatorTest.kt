package bg.uni.mobileapps2026

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculationInputValidatorTest {
    @Test
    fun reportsMissingFields() {
        assertEquals(
            CalculationInputStatus.MISSING_FIELDS,
            CalculationInputValidator.validate(null, 165.0, 60.0),
        )
        assertEquals(
            CalculationInputStatus.MISSING_FIELDS,
            CalculationInputValidator.validate(25, null, 60.0),
        )
        assertEquals(
            CalculationInputStatus.MISSING_FIELDS,
            CalculationInputValidator.validate(25, 165.0, null),
        )
    }

    @Test
    fun acceptsAllDocumentedBoundaryValues() {
        assertEquals(
            CalculationInputStatus.VALID,
            CalculationInputValidator.validate(18, 100.0, 1.0),
        )
        assertEquals(
            CalculationInputStatus.VALID,
            CalculationInputValidator.validate(100, 250.0, 350.0),
        )
    }

    @Test
    fun rejectsAgeOutsideRange() {
        assertEquals(
            CalculationInputStatus.OUT_OF_RANGE,
            CalculationInputValidator.validate(17, 165.0, 60.0),
        )
        assertEquals(
            CalculationInputStatus.OUT_OF_RANGE,
            CalculationInputValidator.validate(101, 165.0, 60.0),
        )
    }

    @Test
    fun rejectsHeightOutsideRangeOrNotFinite() {
        assertEquals(
            CalculationInputStatus.OUT_OF_RANGE,
            CalculationInputValidator.validate(25, 99.9, 60.0),
        )
        assertEquals(
            CalculationInputStatus.OUT_OF_RANGE,
            CalculationInputValidator.validate(25, 250.1, 60.0),
        )
        assertEquals(
            CalculationInputStatus.OUT_OF_RANGE,
            CalculationInputValidator.validate(25, Double.NaN, 60.0),
        )
    }

    @Test
    fun rejectsWeightOutsideRangeOrNotFinite() {
        assertEquals(
            CalculationInputStatus.OUT_OF_RANGE,
            CalculationInputValidator.validate(25, 165.0, 0.9),
        )
        assertEquals(
            CalculationInputStatus.OUT_OF_RANGE,
            CalculationInputValidator.validate(25, 165.0, 350.1),
        )
        assertEquals(
            CalculationInputStatus.OUT_OF_RANGE,
            CalculationInputValidator.validate(25, 165.0, Double.POSITIVE_INFINITY),
        )
    }
}
