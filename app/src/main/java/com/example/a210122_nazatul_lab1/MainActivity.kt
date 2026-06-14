package com.example.a210122_nazatul_lab1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.a210122_nazatul_lab1.ui.theme.A210122_NAZATUL_Lab1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Lab 5: Build the dependency chain once here in the Activity.
        // Database → DAOs → Repository → ViewModelFactory
        val database   = SmartTenagaDatabase.getInstance(applicationContext)
        val repository = ApplianceRepository(
            applianceDao = database.applianceDao(),
            billGoalDao  = database.billGoalDao()
        )
        val factory = SmartTenagaViewModelFactory(repository)

        setContent {
            A210122_NAZATUL_Lab1Theme {
                // Pass factory so viewModel() uses our custom constructor
                SmartTenagaApp(factory = factory)
            }
        }
    }
}

private const val ROUTE_LOGIN = "login"

sealed class AppScreen(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object MyHome         : AppScreen("myhome",           "MyHome",  Icons.Filled.Home)
    data object Bills          : AppScreen("bills",            "Bills",   Icons.Filled.ShoppingCart)
    data object WhatsNew       : AppScreen("whats_new",        "What's New", Icons.AutoMirrored.Filled.List)
    data object AddAppliance   : AppScreen("add_appliance",    "Add",     Icons.Filled.Add)
    data object ApplianceHistory: AppScreen("appliance_history","History", Icons.Filled.Star)
    data object Profile        : AppScreen("profile",          "Profile", Icons.Filled.Person)
}

data class QuickAccessItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

private data class DiscoverItem(
    val title: String,
    val description: String,
    val imageRes: Int
)

@Composable
fun SmartTenagaApp(
    factory: SmartTenagaViewModelFactory,                    // Lab 5: injected factory
    viewModel: SmartTenagaViewModel = viewModel(factory = factory)
) {
    val navController = rememberNavController()

    val navigationItems = listOf(
        AppScreen.MyHome,
        AppScreen.Bills,
        AppScreen.AddAppliance,
        AppScreen.ApplianceHistory,
        AppScreen.Profile
    )

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: ROUTE_LOGIN
    val showChrome = currentRoute != ROUTE_LOGIN

    Scaffold(
        topBar = {
            if (showChrome) {
                AppTopBar(
                    currentRoute = currentRoute,
                    canGoBack    = navController.previousBackStackEntry != null &&
                            navController.previousBackStackEntry?.destination?.route != ROUTE_LOGIN,
                    onBackClick  = { navController.popBackStack() }
                )
            }
        },
        bottomBar = {
            if (showChrome) {
                AppBottomBar(
                    navController            = navController,
                    items                    = navigationItems,
                    currentDestinationRoute  = currentRoute
                )
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            innerPadding  = innerPadding,
            viewModel     = viewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar(
    currentRoute: String,
    canGoBack: Boolean,
    onBackClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            if (currentRoute == AppScreen.Profile.route) {
                Icon(
                    imageVector     = Icons.Filled.AccountCircle,
                    contentDescription = "Profile Symbol",
                    modifier        = Modifier.size(34.dp)
                )
            } else {
                Text(
                    text = when (currentRoute) {
                        AppScreen.Bills.route            -> "Bills"
                        AppScreen.WhatsNew.route         -> "What's New"
                        AppScreen.AddAppliance.route     -> "Add Appliance"
                        AppScreen.ApplianceHistory.route -> "My Appliances"
                        else                             -> "MyHome"
                    }
                )
            }
        },
        navigationIcon = {
            if (canGoBack) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        }
    )
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues,
    viewModel: SmartTenagaViewModel
) {
    // Lab 5: collect StateFlows from ViewModel here, pass plain values down.
    // All screen composables remain UNCHANGED from Lab 4.
    val applianceList  by viewModel.applianceList.collectAsState()
    val billGoalRm     by viewModel.billGoalRm.collectAsState()

    NavHost(
        navController    = navController,
        startDestination = ROUTE_LOGIN,
        modifier         = Modifier.padding(innerPadding)
    ) {
        composable(ROUTE_LOGIN) {
            LoginScreen(
                onLoginSuccess = { username ->
                    viewModel.login(username)
                    navController.navigate(AppScreen.MyHome.route) {
                        popUpTo(ROUTE_LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(AppScreen.MyHome.route) {
            MyHomeScreen(
                loggedInUsername   = viewModel.loggedInUsername,
                totalMonthlyKwh    = viewModel.totalMonthlyKwh,
                totalEstimatedCost = viewModel.totalEstimatedCost,
                billGoalRm         = billGoalRm,
                isOverGoal         = viewModel.isOverGoal,
                onNavigate         = { route -> navController.navigateToScreen(route) }
            )
        }

        composable(AppScreen.Bills.route) {
            BillsScreen(
                applianceList    = applianceList,
                totalCost        = viewModel.totalEstimatedCost,
                totalKwh         = viewModel.totalMonthlyKwh,
                billGoalRm       = billGoalRm,
                proportionalCost = { appliance -> viewModel.applianceProportionalCost(appliance) }
            )
        }

        composable(AppScreen.AddAppliance.route) {
            AddApplianceScreen(
                onApplianceAdded = { name, wattage, hours ->
                    viewModel.addAppliance(name, wattage, hours)
                }
            )
        }

        composable(AppScreen.ApplianceHistory.route) {
            ApplianceHistoryScreen(
                applianceList = applianceList,
                totalCost     = viewModel.totalEstimatedCost,
                totalKwh      = viewModel.totalMonthlyKwh,
                onDelete      = { id -> viewModel.deleteAppliance(id) }
            )
        }

        composable(AppScreen.Profile.route) {
            ProfileScreen(
                username           = viewModel.loggedInUsername,
                billGoalRm         = billGoalRm,
                totalEstimatedCost = viewModel.totalEstimatedCost,
                goalProgress       = viewModel.goalProgress,
                isOverGoal         = viewModel.isOverGoal,
                onSetGoal          = { rm -> viewModel.setBillGoal(rm) },
                onLogout           = {
                    viewModel.logout()
                    navController.navigate(ROUTE_LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(AppScreen.WhatsNew.route) {
            WhatsNewScreen(notifications = viewModel.notifications)
        }
    }
}

// ------------------------------------------------------------------
// MyHomeScreen — unchanged layout from Lab 4
// ------------------------------------------------------------------
@Composable
fun MyHomeScreen(
    loggedInUsername: String,
    totalMonthlyKwh: Double,
    totalEstimatedCost: Double,
    billGoalRm: Double,
    isOverGoal: Boolean,
    onNavigate: (String) -> Unit
) {
    val quickAccessItems = listOf(
        QuickAccessItem("Bills",    AppScreen.Bills.route,           Icons.Filled.ShoppingCart),
        QuickAccessItem("Add",      AppScreen.AddAppliance.route,    Icons.Filled.Add),
        QuickAccessItem("History",  AppScreen.ApplianceHistory.route, Icons.Filled.Star),
        QuickAccessItem("What's New", AppScreen.WhatsNew.route,      Icons.AutoMirrored.Filled.List)
    )

    val discoverItems = listOf(
        DiscoverItem(
            "Energy Saving Tips",
            "Small habits like switching off appliances on standby can cut your bill by up to 10%.",
            R.drawable.savenergy
        ),
        DiscoverItem(
            "Peak Hour Awareness",
            "Understanding peak hours helps you manage your energy consumption more efficiently.",
            R.drawable.peakhour
        ),

    )

    val expandedCardIndex = remember { mutableStateOf<Int?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color    = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Greeting
            Text(
                text  = "Hello, ${loggedInUsername.ifBlank { "User" }} 👋",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Usage summary card
            Card(
                colors    = CardDefaults.cardColors(
                    containerColor = if (isOverGoal)
                        MaterialTheme.colorScheme.errorContainer
                    else MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier  = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text  = "Monthly Usage Summary",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isOverGoal) MaterialTheme.colorScheme.onErrorContainer
                        else   MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text  = "Logged Usage",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isOverGoal) MaterialTheme.colorScheme.onErrorContainer
                                else   MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text  = "%.1f kWh".format(totalMonthlyKwh),
                                style = MaterialTheme.typography.headlineSmall,
                                color = if (isOverGoal) MaterialTheme.colorScheme.onErrorContainer
                                else   MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text  = "Est. Cost",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isOverGoal) MaterialTheme.colorScheme.onErrorContainer
                                else   MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text  = "RM %.2f".format(totalEstimatedCost),
                                style = MaterialTheme.typography.headlineSmall,
                                color = if (isOverGoal) MaterialTheme.colorScheme.onErrorContainer
                                else   MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (billGoalRm > 0) {
                            if (isOverGoal)
                                "⚠ Over your RM %.2f/month goal — reduce appliance usage.".format(billGoalRm)
                            else
                                "✓ Within your RM %.2f/month goal. Keep it up!".format(billGoalRm)
                        } else {
                            "Set a monthly bill goal in Profile to track your progress."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isOverGoal) MaterialTheme.colorScheme.onErrorContainer
                        else   MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                    )
                }
            }

            // Quick Access
            Card(
                colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text  = "Quick Access",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        quickAccessItems.forEach { item ->
                            Qa(
                                icon     = item.icon,
                                label    = item.title,
                                color    = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onNavigate(item.route) }
                            )
                        }
                    }
                }
            }

            Text(
                text  = "Discover More",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            discoverItems.forEachIndexed { index, item ->
                DiscoverCard(
                    item     = item,
                    expanded = expandedCardIndex.value == index,
                    onClick  = {
                        expandedCardIndex.value =
                            if (expandedCardIndex.value == index) null else index
                    }
                )
            }
        }
    }
}

@Composable
private fun Qa(
    icon: ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier.padding(horizontal = 4.dp),
        colors   = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        ),
        elevation = CardDefaults.outlinedCardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = label,
                tint               = color,
                modifier           = Modifier.size(30.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text  = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun DiscoverCard(
    item: DiscoverItem,
    expanded: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable(onClick = onClick),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Image(
                painter            = painterResource(item.imageRes),
                contentDescription = item.title,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text  = item.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text  = if (expanded) item.description else "Tap to expand",
                style = MaterialTheme.typography.bodyMedium,
                color = if (expanded) MaterialTheme.colorScheme.onSurfaceVariant
                else   MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun AppBottomBar(
    navController: NavHostController,
    items: List<AppScreen>,
    currentDestinationRoute: String
) {
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentDestinationRoute == item.route,
                onClick  = { navController.navigateToScreen(item.route) },
                icon     = {
                    Icon(imageVector = item.icon, contentDescription = item.label)
                },
                label    = { Text(item.label) }
            )
        }
    }
}

fun NavHostController.navigateToScreen(route: String) {
    navigate(route) { launchSingleTop = true }
}

@Preview(showBackground = true)
@Composable
fun SmartTenagaAppPreview() {
    A210122_NAZATUL_Lab1Theme {
        // Preview cannot use Room — show LoginScreen as a safe fallback
        LoginScreen(onLoginSuccess = {})
    }
}