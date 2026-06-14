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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BillsScreen(
    applianceList: List<ApplianceItem>,
    totalCost: Double,
    totalKwh: Double,
    billGoalRm: Double,
    proportionalCost: (ApplianceItem) -> Double
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Estimated Bill Summary
            item {
                EstimatedBillCard(totalCost = totalCost, totalKwh = totalKwh, billGoalRm = billGoalRm)
            }

            // 2. Per-appliance breakdown header
            item {
                Text(
                    text = "Appliance Breakdown (${applianceList.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // 4. Empty state
            if (applianceList.isEmpty()) {
                item {
                    EmptyState()
                }
            } else {
                // 5. Appliance rows
                items(applianceList, key = { it.id }) { appliance ->
                    ApplianceBillRow(
                        appliance = appliance,
                        cost = proportionalCost(appliance),
                        totalCost = totalCost
                    )
                }

                // 6. Total footer
                item {
                    TotalFooterCard(totalCost = totalCost, totalKwh = totalKwh)
                }
            }

            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

//
// Estimated Bill Summary Card
//
@Composable
private fun EstimatedBillCard(totalCost: Double, totalKwh: Double, billGoalRm: Double) {
    val isOverGoal = billGoalRm > 0 && totalCost > billGoalRm
    val containerColor = when {
        totalCost == 0.0 -> MaterialTheme.colorScheme.surfaceVariant
        isOverGoal -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.primaryContainer
    }
    val contentColor = when {
        totalCost == 0.0 -> MaterialTheme.colorScheme.onSurfaceVariant
        isOverGoal -> MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Estimated Monthly Bill",
                style = MaterialTheme.typography.titleMedium,
                color = contentColor
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = if (totalCost == 0.0) "RM 0.00" else "RM %.2f".format(totalCost),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "%.1f kWh / month  •  Bill estimated using TNB tariff rates".format(totalKwh),
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.75f)
            )
            if (billGoalRm > 0) {
                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = contentColor.copy(alpha = 0.2f))
                Spacer(Modifier.height(10.dp))
                Text(
                    text = if (isOverGoal)
                        "⚠ Estimated bill exceeds your RM %.2f/month goal by RM %.2f".format(
                            billGoalRm, totalCost - billGoalRm
                        )
                    else
                        "✓ Within your RM %.2f/month goal. RM %.2f remaining.".format(
                            billGoalRm, billGoalRm - totalCost
                        ),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = contentColor
                )
            }
        }
    }
}

//
// Per-appliance row
//
@Composable
private fun ApplianceBillRow(appliance: ApplianceItem, cost: Double, totalCost: Double) {
    val pct = if (totalCost > 0) (cost / totalCost * 100) else 0.0

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = appliance.name,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = appliance.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${appliance.wattage.toInt()} W  •  ${appliance.hoursPerDay}h/day  •  %.1f kWh/mo".format(appliance.monthlyKwh),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "RM %.2f".format(cost),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "%.0f%%".format(pct),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

//
// Total footer
// ------------------------------------------------------------------
@Composable
private fun TotalFooterCard(totalCost: Double, totalKwh: Double) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "RM %.2f / month".format(totalCost),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = "%.1f kWh / month".format(totalKwh),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

// ------------------------------------------------------------------
// Empty state
// ------------------------------------------------------------------
@Composable
private fun EmptyState() {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "No appliances added yet.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Go to Add to log your appliances and see your estimated bill here.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}