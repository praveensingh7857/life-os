package com.example.ui.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ExecutiveStatusPill
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.LifeOsViewModel

@Composable
fun LoginAuthScreen(
    viewModel: LifeOsViewModel,
    onContinueOffline: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isAuthLoading by viewModel.isAuthLoading.collectAsState()
    val authError by viewModel.authManager.errorMessage.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        LifeOsBgPrimary,
                        LifeOsBgSecondary,
                        LifeOsBgPrimary
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Brand Insignia & Badge
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(LifeOsCardBg)
                    .border(1.5.dp, LifeOsAccentGold, RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AllInclusive,
                    contentDescription = "LIFEOS AI",
                    tint = LifeOsAccentGold,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "LIFEOS",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = LifeOsTextPrimary,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "AI",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = LifeOsAccentGold,
                    letterSpacing = 1.5.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Your Life. Organized by Intelligence.",
                style = MaterialTheme.typography.bodyMedium,
                color = LifeOsAccentGoldLight,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            ExecutiveStatusPill()

            Spacer(modifier = Modifier.height(32.dp))

            // Executive Architecture Capabilities Card
            GlassCard(
                borderColor = LifeOsBorderGold,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Executive Operating System",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LifeOsTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Sign in to synchronize your executive assistant, memories, tasks, and financial intelligence with Cloud Firestore.",
                    style = MaterialTheme.typography.bodySmall,
                    color = LifeOsTextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                AuthFeatureBullet(
                    icon = Icons.Default.AutoAwesome,
                    title = "Multi-Model AI Executive Team",
                    subtitle = "Gemini Flash & Argon reasoning orchestrator"
                )
                AuthFeatureBullet(
                    icon = Icons.Default.CloudSync,
                    title = "Durable Cloud Synchronization",
                    subtitle = "Zero-trust encrypted database storage"
                )
                AuthFeatureBullet(
                    icon = Icons.Default.Security,
                    title = "Credential Manager Security",
                    subtitle = "One-tap Google Identity with Passkeys support"
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Error Display if any
            AnimatedVisibility(visible = authError != null) {
                authError?.let { err ->
                    Surface(
                        color = Color(0x33EF4444),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, LifeOsAlert),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = LifeOsAlert,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = err,
                                color = LifeOsTextPrimary,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.authManager.clearError() },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = LifeOsTextSecondary)
                            }
                        }
                    }
                }
            }

            // Primary Google Sign-In Action Button
            Button(
                onClick = {
                    val activity = context as? Activity
                    if (activity != null) {
                        viewModel.signInWithGoogle(activity)
                    }
                },
                enabled = !isAuthLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LifeOsAccentGold,
                    contentColor = LifeOsBgPrimary,
                    disabledContainerColor = LifeOsCardBg,
                    disabledContentColor = LifeOsTextSecondary
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("auth_screen_google_signin_btn")
            ) {
                if (isAuthLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = LifeOsBgPrimary,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Authenticating...",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Continue with Google",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Secondary Action: Continue in Local Guest / Offline Mode
            OutlinedButton(
                onClick = onContinueOffline,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = LifeOsTextSecondary
                ),
                border = BorderStroke(1.dp, LifeOsBorder),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("auth_screen_continue_offline_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.OfflineBolt,
                    contentDescription = null,
                    tint = LifeOsAccentGold,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Launch in Local OS Core Mode",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "LIFEOS AI v1.0 • Enterprise Cloud Architecture",
                fontSize = 11.sp,
                color = LifeOsTextSecondary.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun AuthFeatureBullet(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(LifeOsElevated)
                .border(1.dp, LifeOsBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LifeOsAccentGold,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
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
