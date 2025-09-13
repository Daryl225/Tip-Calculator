package com.example.tipcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tipcalculator.ui.theme.TipCalculatorTheme
import java.text.NumberFormat
import kotlin.math.ceil

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            TipCalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    TipTimeLayout()
                }
            }
        }
    }
}

/**
 * Composable principal qui gère l'état de l'application et organise l'interface
 * Implémente le principe de "state hoisting" (remontée d'état)
 */
@Composable
fun TipTimeLayout() {
    // États locaux pour gérer les données utilisateur
    // remember() permet de conserver les valeurs lors des recompositions
    var amountInput by remember { mutableStateOf("") }  // Montant de la facture
    var tipInput by remember { mutableStateOf("") }     // Pourcentage du pourboire
    var roundUp by remember { mutableStateOf(false) }   // Option d'arrondir

    // Conversion sécurisée des entrées texte en Double
    // toDoubleOrNull() retourne null si la conversion échoue, remplacé par 0.0
    val amount = amountInput.toDoubleOrNull() ?: 0.0
    val tipPercent = tipInput.toDoubleOrNull() ?: 0.0

    // Calcul du pourboire avec les valeurs actuelles
    val tip = calculateTip(amount, tipPercent, roundUp)

    Column(
        modifier = Modifier
            .statusBarsPadding()        // Évite la barre de statut
            .padding(horizontal = 40.dp) // Marges horizontales
            .verticalScroll(rememberScrollState()) // Permet le défilement
            .safeDrawingPadding(),      // Évite les zones système
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Titre de l'application
        Text(
            text = stringResource(R.string.calculate_tip),
            modifier = Modifier
                .padding(bottom = 16.dp, top = 40.dp)
                .align(alignment = Alignment.Start)
        )

        // Champ de saisie pour le montant de la facture
        // Utilise state hoisting : l'état est géré par le parent
        EditNumberField(
            label = R.string.bill_amount,
            leadingIcon = R.drawable.money,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number, // Clavier numérique
                imeAction = ImeAction.Next          // Bouton "Suivant"
            ),
            value = amountInput,
            onValueChange = { amountInput = it }, // Callback pour mettre à jour l'état
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth()
        )

        // Champ de saisie pour le pourcentage du pourboire
        EditNumberField(
            label = R.string.tip_percentage,
            leadingIcon = R.drawable.percent,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done // Bouton "Terminé"
            ),
            value = tipInput,
            onValueChange = { tipInput = it },
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth()
        )

        // Composant switch pour l'option d'arrondir
        RoundTheTipRow(
            roundUp = roundUp,
            onRoundUpChanged = { roundUp = it },
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Affichage du montant calculé du pourboire
        Text(
            text = stringResource(R.string.tip_amount, tip),
            style = MaterialTheme.typography.displaySmall
        )

        // Espacement final pour améliorer la mise en page
        Spacer(modifier = Modifier.height(150.dp))
    }
}

/**
 * Calculates the tip based on the user input and format the tip amount
 * according to the local currency.
 * Example would be "$10.00".
 */
@VisibleForTesting // Permet de tester cette fonction même si elle est internal
internal fun calculateTip(amount: Double, tipPercent: Double = 15.0, roundUp: Boolean): String {
    // Calcul du montant du pourboire
    var tip = tipPercent / 100 * amount
    // Arrondir vers le haut si demandé
    if (roundUp) {
        tip = ceil(tip) // ceil() arrondit vers le haut
    }
    // Formatage selon la devise locale (ex: "$10.00")
    return NumberFormat.getCurrencyInstance().format(tip)
}

/**
 * Composable réutilisable pour les champs de saisie numériques
 * Composant "stateless" - l'état est géré par le parent
 */
@Composable
fun EditNumberField(
    @StringRes label: Int,           // Ressource string pour le label
    @DrawableRes leadingIcon: Int,   // Ressource drawable pour l'icône
    keyboardOptions: KeyboardOptions,
    value: String,                   // Valeur actuelle du champ
    onValueChange: (String) -> Unit, // Callback appelé lors du changement
    modifier: Modifier = Modifier
) {
    // Ce composable est maintenant sans état (stateless)
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(label)) }, // Label du champ
        leadingIcon = {
                Icon(painter = painterResource(id = leadingIcon),
                contentDescription = null) }, // Icône décorative, pas de description nécessaire
        singleLine = true, // Une seule ligne de texte
        keyboardOptions = keyboardOptions,
        modifier = modifier
    )
}

/**
 * Composable pour la rangée contenant l'option d'arrondir
 * Combine un Text et un Switch dans une Row
 */
@Composable
fun RoundTheTipRow(
    roundUp: Boolean,                    // État actuel du switch
    onRoundUpChanged: (Boolean) -> Unit, // Callback pour les changements
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth() //Prend toute la largeur
            .size(48.dp), // Hauteur fixe pour l'alignement
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.round_up_tip),
            modifier = Modifier.weight(1f) // Cette ligne permet au texte d'occupe l'espace disponible
        )
        Switch(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End), // Alignement à droite
            checked = roundUp,
            onCheckedChange = onRoundUpChanged
        )
    }
}
@Preview(showBackground = true)
@Composable
fun TipTimeLayoutPreview() {
    TipCalculatorTheme {
        TipTimeLayout()
    }
}