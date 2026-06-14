package com.example.a210122_nazatul_lab1

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(
    username: String,
    billGoalRm: Double,
    totalEstimatedCost: Double,
    goalProgress: Float,
    isOverGoal: Boolean,
    onSetGoal: (Double) -> Unit,
    onLogout: () -> Unit
) {
    var goalInput by remember { mutableStateOf(if (billGoalRm > 0) "%.2f".format(billGoalRm) else "") }
    var goalError by remember { mutableStateOf(false) }
    var goalSaved by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(8.dp))

        // Profile icon + username
        Icon(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = "Profile",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(90.dp)
        )
        Text(
            text = username.ifBlank { "User" },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "SmartTenaga Member",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // --- Monthly Bill Goal Section ---
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Monthly Bill Goal",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Set a monthly bill target in RM to track your estimated electricity cost and reduce energy spending.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )

                OutlinedTextField(
                    value = goalInput,
                    onValueChange = {
                        goalInput = it
                        goalError = false
                        goalSaved = false
                    },
                    label = { Text("Target bill per month (RM)") },
                    placeholder = { Text("e.g. 100.00") },
                    singleLine = true,
                    isError = goalError,
                    supportingText = {
                        if (goalError) Text("Enter a valid amount greater than RM 0.")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        val parsed = goalInput.toDoubleOrNull()
                        if (parsed != null && parsed > 0) {
                            onSetGoal(parsed)
                            goalSaved = true
                            goalError = false
                        } else {
                            goalError = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Set Goal")
                }

                if (goalSaved) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "Goal saved!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        // --- Goal Progress (shown only if goal is set) ---
        if (billGoalRm > 0) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isOverGoal) MaterialTheme.colorScheme.errorContainer
                    else MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Estimated Bill vs Goal",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isOverGoal) MaterialTheme.colorScheme.onErrorContainer
                            else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Icon(
                            imageVector = if (isOverGoal) Icons.Filled.Warning else Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = if (isOverGoal) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    LinearProgressIndicator(
                        progress = { goalProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        color = if (isOverGoal) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "RM %.2f estimated / RM %.2f goal".format(totalEstimatedCost, billGoalRm),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isOverGoal) MaterialTheme.colorScheme.onErrorContainer
                        else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = if (isOverGoal)
                            "⚠ Your estimated bill exceeds your goal. Try removing high-wattage appliances or reducing usage hours."
                        else
                            "✓ Your estimated bill is within your goal. Keep it up!",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = if (isOverGoal) MaterialTheme.colorScheme.onErrorContainer
                        else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        // --- Log Out ---
        ProfileItem(label = "Log Out", onClick = onLogout)
    }
}

@Composable
private fun ProfileItem(label: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = onClick
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = if (label == "Log Out") MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurface
        )
    }
}