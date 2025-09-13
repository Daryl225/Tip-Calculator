package com.example.tiptime


import com.example.tipcalculator.calculateTip
import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.NumberFormat

class TipCalculatorTests {

    @Test
    fun calculateTip_20PercentNoRoundup() {
        // Variables de test
        val amount = 10.00
        val tipPercent = 20.00

        // Résultat attendu formaté en devise
        val expectedTip = NumberFormat.getCurrencyInstance().format(2)

        // Appel de la fonction à tester
        val actualTip = calculateTip(amount = amount, tipPercent = tipPercent, roundUp = false)

        // Assertion pour vérifier que le résultat est correct
        assertEquals(expectedTip, actualTip)
    }
}