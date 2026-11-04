package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firebase.configureAppCheck
import com.example.ui.components.LifeHubSheet
import com.example.ui.components.LifeOsBottomBar
import com.example.ui.components.LifeOsDrawerContent
import com.example.ui.components.LifeOsTopBar
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.LifeOsScreen
import com.example.viewmodel.LifeOsViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: LifeOsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Step 1: Configure App Check with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)
        enableEdgeToEdge()
        setContent {
            LifeOsTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: LifeOsViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isListening by viewModel.voiceManager.isListening.collectAsState()
    val feedbackMessage by viewModel.userFeedbackMessage.collectAsState()

    var showHubSheet by remember { mutableStateOf(false) }
    var showQuickActionDialog by remember { mutableStateOf(false) }
    // User can either authenticate with Google or continue in local guest mode
    var guestBypassActive by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedbackMessage()
        }
    }

    // Gate: If the user is not authenticated and has not chosen local guest mode, display the Login & Authentication screen
    if (currentUser == null && !guestBypassActive) {
        LoginAuthScreen(
            viewModel = viewModel,
            onContinueOffline = { guestBypassActive = true }
        )
        return
    }

    // Handle system back navigation: close drawer if open, else return to Dashboard
    BackHandler(enabled = drawerState.isOpen || currentScreen != LifeOsScreen.DASHBOARD) {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else {
            viewModel.navigateTo(LifeOsScreen.DASHBOARD)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 768.dp

        if (isWideScreen) {
            // Adaptive Wide-Screen Layout: Permanent side drawer sheet with main canvas
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LifeOsBgPrimary)
            ) {
                LifeOsDrawerContent(
                    currentScreen = currentScreen,
                    currentUser = currentUser,
                    onSelectScreen = { screen: LifeOsScreen ->
                        viewModel.navigateTo(screen)
                    },
                    onQuickCreateClick = {
                        showQuickActionDialog = true
                    },
                    modifier = Modifier.width(280.dp)
                )

                VerticalDivider(color = LifeOsBorder, thickness = 1.dp)

                MainScaffoldContent(
                    viewModel = viewModel,
                    currentScreen = currentScreen,
                    isListening = isListening,
                    snackbarHostState = snackbarHostState,
                    isWideScreen = true,
                    onMenuClick = { /* No-op on wide screen with permanent sidebar */ },
                    onVoiceClick = { viewModel.triggerVoiceInput() },
                    onQuickActionClick = { showQuickActionDialog = true },
                    onSearchClick = { viewModel.navigateTo(LifeOsScreen.TASKS) },
                    onOpenHub = { showHubSheet = true }
                )
            }
        } else {
            // Compact Layout: Modal Navigation Drawer
            ModalNavigationDrawer(
                drawerState = drawerState,
                gesturesEnabled = true,
                drawerContent = {
                    LifeOsDrawerContent(
                        currentScreen = currentScreen,
                        currentUser = currentUser,
                        onSelectScreen = { screen: LifeOsScreen ->
                            viewModel.navigateTo(screen)
                            coroutineScope.launch { drawerState.close() }
                        },
                        onQuickCreateClick = {
                            showQuickActionDialog = true
                            coroutineScope.launch { drawerState.close() }
                        }
                    )
                }
            ) {
                MainScaffoldContent(
                    viewModel = viewModel,
                    currentScreen = currentScreen,
                    isListening = isListening,
                    snackbarHostState = snackbarHostState,
                    isWideScreen = false,
                    onMenuClick = {
                        coroutineScope.launch {
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                        }
                    },
                    onVoiceClick = { viewModel.triggerVoiceInput() },
                    onQuickActionClick = { showQuickActionDialog = true },
                    onSearchClick = { viewModel.navigateTo(LifeOsScreen.TASKS) },
                    onOpenHub = { showHubSheet = true }
                )
            }
        }
    }

    if (showHubSheet) {
        LifeHubSheet(
            onDismiss = { showHubSheet = false },
            onSelectScreen = { screen ->
                viewModel.navigateTo(screen)
            }
        )
    }

    if (showQuickActionDialog) {
        QuickActionMenuDialog(
            onDismiss = { showQuickActionDialog = false },
            onAction = { screen ->
                viewModel.navigateTo(screen)
                showQuickActionDialog = false
            }
        )
    }
}

@Composable
private fun MainScaffoldContent(
    viewModel: LifeOsViewModel,
    currentScreen: LifeOsScreen,
    isListening: Boolean,
    snackbarHostState: SnackbarHostState,
    isWideScreen: Boolean,
    onMenuClick: () -> Unit,
    onVoiceClick: () -> Unit,
    onQuickActionClick: () -> Unit,
    onSearchClick: () -> Unit,
    onOpenHub: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(LifeOsBgPrimary),
        topBar = {
            LifeOsTopBar(
                currentScreen = currentScreen,
                isListening = isListening,
                onMenuClick = onMenuClick,
                onVoiceClick = onVoiceClick,
                onQuickActionClick = onQuickActionClick,
                onSearchClick = onSearchClick
            )
        },
        bottomBar = {
            if (!isWideScreen) {
                LifeOsBottomBar(
                    currentScreen = currentScreen,
                    onNavigate = { viewModel.navigateTo(it) },
                    onOpenHub = onOpenHub
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = LifeOsCardBg,
                    contentColor = LifeOsTextPrimary,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(LifeOsBgPrimary)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    LifeOsScreen.DASHBOARD -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    LifeOsScreen.ASSISTANT -> AssistantScreen(viewModel = viewModel)
                    LifeOsScreen.MY_DAY -> MyDayScreen(
                        viewModel = viewModel,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    LifeOsScreen.TASKS -> TasksScreen(viewModel = viewModel)
                    LifeOsScreen.REMINDERS -> RemindersScreen(viewModel = viewModel)
                    LifeOsScreen.MEMORY -> MemoryScreen(viewModel = viewModel)
                    LifeOsScreen.GOALS_HABITS -> GoalsHabitsScreen(viewModel = viewModel)
                    LifeOsScreen.FINANCE -> FinanceScreen(viewModel = viewModel)
                    LifeOsScreen.KNOWLEDGE -> KnowledgeScreen(viewModel = viewModel)
                    LifeOsScreen.AUTOMATIONS -> AutomationsScreen(viewModel = viewModel)
                    LifeOsScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun QuickActionMenuDialog(
    onDismiss: () -> Unit,
    onAction: (LifeOsScreen) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LifeOsCardBg,
        title = {
            Text("Executive Quick Actions", color = LifeOsTextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickActionItem(
                    title = "New Task",
                    subtitle = "Create & prioritize an initiative",
                    icon = Icons.Default.Checklist,
                    accentColor = LifeOsAccentGold,
                    onClick = { onAction(LifeOsScreen.TASKS) }
                )
                QuickActionItem(
                    title = "Schedule Reminder",
                    subtitle = "Set a time-sensitive checkpoint",
                    icon = Icons.Default.Notifications,
                    accentColor = LifeOsWarning,
                    onClick = { onAction(LifeOsScreen.REMINDERS) }
                )
                QuickActionItem(
                    title = "Log Transaction",
                    subtitle = "Record income or expense",
                    icon = Icons.Default.AccountBalanceWallet,
                    accentColor = LifeOsSuccess,
                    onClick = { onAction(LifeOsScreen.FINANCE) }
                )
                QuickActionItem(
                    title = "Store Personal Memory",
                    subtitle = "Ingest context into AI long-term memory",
                    icon = Icons.Default.Memory,
                    accentColor = LifeOsPurple,
                    onClick = { onAction(LifeOsScreen.MEMORY) }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss", color = LifeOsTextSecondary)
            }
        }
    )
}

@Composable
private fun QuickActionItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        color = LifeOsElevated,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("quick_action_${title.replace(" ", "_")}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = LifeOsTextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = LifeOsTextSecondary
                )
            }
        }
    }
}
