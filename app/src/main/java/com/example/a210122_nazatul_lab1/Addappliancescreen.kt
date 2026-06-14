package com.example.a210122_nazatul_lab1

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun AddApplianceScreen(onApplianceAdded: (String, Double, Double) -> Unit) {

    var applianceName by remember { mutableStateOf("") }
    var wattageInput by remember { mutableStateOf("") }
    var hoursInput by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    // Inline validation errors
    var nameError by remember { mutableStateOf(false) }
    var wattageError by remember { mutableStateOf(false) }
    var hoursError by remember { mutableStateOf(false) }

    // Live cost preview (only shows if inputs are valid numbers)
    val previewCost: String? = run {
        val w = wattageInput.toDoubleOrNull()
        val h = hoursInput.toDoubleOrNull()
        if (w != null && h != null && w > 0 && h > 0) {
            val kwh = (w * h * 30) / 1000.0
            val cost = kwh * 0.571
            "Estimated: RM %.2f / month (%.1f kWh)".format(cost, kwh)
        } else null
    }

    fun validate(): Boolean {
        nameError = applianceName.trim().isBlank()
        wattageError = wattageInput.toDoubleOrNull()?.let { it <= 0 } ?: true
        hoursError = hoursInput.toDoubleOrNull()?.let { it <= 0 || it > 24 } ?: true
        return !nameError && !wattageError && !hoursError
    }

    fun submit() {
        if (validate()) {
            onApplianceAdded(
                applianceName.trim(),
                wattageInput.toDouble(),
                hoursInput.toDouble()
            )
            // Reset form
            applianceName = ""
            wattageInput = ""
            hoursInput = ""
            showSuccess = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header info card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Log Your Appliance",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Track how much each appliance costs you monthly based on TNB tariff rates.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        // Success banner
        if (showSuccess) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Success",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = "Appliance added! You can add another below.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }

        // Appliance Name
        OutlinedTextField(
            value = applianceName,
            onValueChange = {
                applianceName = it
                nameError = false
                showSuccess = false
            },
            label = { Text("Appliance Name") },
            placeholder = { Text("e.g. Air Conditioner, Fridge") },
            isError = nameError,
            supportingText = { if (nameError) Text("Please enter an appliance name.") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Wattage
        OutlinedTextField(
            value = wattageInput,
            onValueChange = {
                wattageInput = it
                wattageError = false
                showSuccess = false
            },
            label = { Text("Wattage (W)") },
            placeholder = { Text("e.g. 1500") },
            isError = wattageError,
            supportingText = {
                if (wattageError) Text("Enter a valid wattage greater than 0.")
                else Text("Check the label on your appliance.")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        // Hours per day
        OutlinedTextField(
            value = hoursInput,
            onValueChange = {
                hoursInput = it
                hoursError = false
                showSuccess = false
            },
            label = { Text("Hours Used Per Day") },
            placeholder = { Text("e.g. 8") },
            isError = hoursError,
            supportingText = {
                if (hoursError) Text("Enter hours between 0 and 24.")
                else Text("Average daily usage hours.")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        // Live cost preview
        if (previewCost != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    text = previewCost,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(14.dp)
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        Button(
            onClick = { submit() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Add Appliance", style = MaterialTheme.typography.titleMedium)
        }
    }
}