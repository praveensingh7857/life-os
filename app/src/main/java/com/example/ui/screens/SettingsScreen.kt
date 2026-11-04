package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.LifeOsViewModel

@Composable
fun SettingsScreen(viewModel: LifeOsViewModel) {
    val ttsEnabled by viewModel.voiceManager.ttsEnabled.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isAuthLoading by viewModel.isAuthLoading.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LifeOsBgPrimary)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        item {
            Column {
                Text(
                    text = "System Settings",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = LifeOsTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Configuration, Google Auth, cloud synchronization, and voice AI",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LifeOsTextSecondary
                )
            }
        }

        // Google Sign-In & Firebase Cloud Sync Card
        item {
            GlassCard(
                borderColor = LifeOsBorderGold,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Cloud Identity & Firebase Sync",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LifeOsAccentGold
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (currentUser != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0x2A10B981),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = LifeOsSuccess,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = currentUser?.displayName ?: "Google User",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = LifeOsTextPrimary
                                )
                                Text(
                                    text = currentUser?.email ?: "Cloud Sync Active",
                                    fontSize = 12.sp,
                                    color = LifeOsTextSecondary
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { viewModel.signOut() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = LifeOsAlert),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_google_sign_out")
                        ) {
                            Text("Sign Out", fontSize = 11.sp)
                        }
                    }
                } else {
                    Column {
                        Text(
                            text = "Authenticate with your Google Account to synchronize your executive tasks, memory, reminders, and finances across all devices via Cloud Firestore.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LifeOsTextSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val activity = context as? Activity
                                activity?.let { viewModel.signInWithGoogle(it) }
                            },
                            enabled = !isAuthLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LifeOsAccentGold,
                                contentColor = LifeOsBgPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_google_sign_in")
                        ) {
                            if (isAuthLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = LifeOsBgPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sign in with Google", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Profile Section
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Executive Profile", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = LifeOsAccentGold)
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = LifeOsElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, LifeOsAccentGold),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "CMD", fontWeight = FontWeight.Bold, color = LifeOsAccentGold, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = currentUser?.displayName ?: "Commander",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = LifeOsTextPrimary
                        )
                        Text(text = "Operating System Principal", style = MaterialTheme.typography.bodyMedium, color = LifeOsTextSecondary)
                    }
                }
            }
        }

        // Voice & Audio
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Voice Synthesis", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = LifeOsAccentGold)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Read Aloud AI Responses", style = MaterialTheme.typography.bodyLarge, color = LifeOsTextPrimary, fontWeight = FontWeight.Medium)
                        Text(text = "Uses on-device native speech synthesis", style = MaterialTheme.typography.bodyMedium, color = LifeOsTextSecondary)
                    }
                    Switch(
                        checked = ttsEnabled,
                        onCheckedChange = { viewModel.voiceManager.toggleTts(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = LifeOsAccentGold,
                            checkedTrackColor = LifeOsElevated
                        )
                    )
                }
            }
        }

        // Database & Demo Data
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Data Sovereignty & Persistence", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = LifeOsAccentGold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "All data is securely persisted in your on-device Room SQLite database and synced to Cloud Firestore with zero-trust security rules.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LifeOsTextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.resetToDemoData() },
                    colors = ButtonDefaults.buttonColors(containerColor = LifeOsElevated, contentColor = LifeOsAccentGold),
                    modifier = Modifier.fillMaxWidth().testTag("btn_reset_demo_data")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Re-populate Executive Demo Data", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
