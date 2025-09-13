package com.example.tiptime

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.example.tipcalculator.TipTimeLayout
import com.example.tipcalculator.ui.theme.TipCalculatorTheme
import org.junit.Rule
import org.junit.Test
import java.text.NumberFormat


class TipUITests {

    // Règle qui permet d'utiliser l'environnement de test de Jetpack Compose
    @get:Rule
    val composeTestRule = createComposeRule()

    // Test qui vérifie qu'un pourboire de 20% est correctement calculé
    @Test
    fun calculate_20_percent_tip() {
        // Définit le contenu à tester (l'UI)
        composeTestRule.setContent {
            TipCalculatorTheme {
                Surface(modifier = Modifier.fillMaxSize())
                {
                    TipTimeLayout() // Composable principal de l'application
                }
            }
        }

        // Saisie du montant de la facture = 10
        composeTestRule.onNodeWithText("Bill Amount")
            .performTextInput("10")

        // Saisie du pourcentage du pourboire = 20
        composeTestRule.onNodeWithText("Tip Percentage")
            .performTextInput("20")

        // Valeur attendue du pourboire = 2 (10 * 20%)
        // formattée selon la monnaie locale ($, €, etc.)
        val expectedTip = NumberFormat.getCurrencyInstance().format(2)

        // Vérifie que le texte affiché correspond bien au résultat attendu
        composeTestRule.onNodeWithText("Tip Amount: $expectedTip")
            .assertExists("No node with this text was found.")
    }
}