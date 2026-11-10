package bg.uni.mobileapps2026

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ActivityLevelCalculatorTest {
    @Test
    fun acceptsMinimumAndMaximumInputValues() {
        assertTrue(ActivityLevelCalculator.areInputsValid(0, 0, 0))
        assertTrue(ActivityLevelCalculator.areInputsValid(50000, 14, 300))
    }

    @Test
    fun rejectsValuesOutsideSupportedRanges() {
        assertFalse(ActivityLevelCalculator.areInputsValid(-1, 0, 0))
        assertFalse(ActivityLevelCalculator.areInputsValid(50001, 0, 0))
        assertFalse(ActivityLevelCalculator.areInputsValid(0, -1, 0))
        assertFalse(ActivityLevelCalculator.areInputsValid(0, 15, 0))
        assertFalse(ActivityLevelCalculator.areInputsValid(0, 0, -1))
        assertFalse(ActivityLevelCalculator.areInputsValid(0, 0, 301))
    }

    @Test
    fun calculatesAllFiveActivityLevels() {
        val sedentary = ActivityLevelCalculator.calculate(0, 0, 0)
        assertEquals(0, sedentary.position)
        assertEquals("Заседнал начин на живот", sedentary.name)
        assertEquals(0, sedentary.points)

        val light = ActivityLevelCalculator.calculate(3000, 1, 60)
        assertEquals(1, light.position)
        assertEquals("Лека активност", light.name)
        assertEquals(60, light.weeklyWorkoutMinutes)

        val moderate = ActivityLevelCalculator.calculate(6000, 1, 60)
        assertEquals(2, moderate.position)
        assertEquals("Умерена активност", moderate.name)

        val high = ActivityLevelCalculator.calculate(8000, 1, 150)
        assertEquals(3, high.position)
        assertEquals("Висока активност", high.name)

        val veryHigh = ActivityLevelCalculator.calculate(10000, 1, 300)
        assertEquals(4, veryHigh.position)
        assertEquals("Много висока активност", veryHigh.name)
        assertEquals(300, veryHigh.weeklyWorkoutMinutes)
    }

    @Test
    fun countsWorkoutMinutesAcrossTheWeek() {
        val result = ActivityLevelCalculator.calculate(0, 3, 30)

        assertEquals(90, result.weeklyWorkoutMinutes)
        assertEquals(2, result.points)
        assertEquals(1, result.position)
    }

    @Test(expected = IllegalArgumentException::class)
    fun refusesToCalculateWithInvalidInput() {
        ActivityLevelCalculator.calculate(50001, 0, 0)
    }

    @Test
    fun handlesTheLowAndMiddlePointBoundaries() {
        assertEquals(0, ActivityLevelCalculator.calculate(3000, 0, 0).position)
        assertEquals(0, ActivityLevelCalculator.calculate(0, 1, 10).position)
        assertEquals(1, ActivityLevelCalculator.calculate(3000, 1, 10).position)
        assertEquals(2, ActivityLevelCalculator.calculate(8000, 1, 60).position)
        assertEquals(3, ActivityLevelCalculator.calculate(10000, 1, 150).position)
    }
}
