package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = LifeOsCardBg,
    borderColor: Color = LifeOsBorder,
    borderWidth: Dp = 1.dp,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(borderWidth, borderColor, shape)
            .padding(16.dp),
        content = content
    )
}

@Composable
fun PriorityBadge(priority: String) {
    val (bgColor, textColor) = when (priority.lowercase()) {
        "urgent" -> Color(0x2AEF4444) to LifeOsAlert
        "high" -> Color(0x2AF59E0B) to LifeOsWarning
        "medium" -> Color(0x2AC6A15B) to LifeOsAccentGold
        else -> Color(0x1FFFFFFF) to LifeOsTextSecondary
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = priority.uppercase(),
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun DepartmentBadge(
    department: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val (icon, tint) = when (department) {
        "Chief of Staff" -> Icons.Default.Shield to LifeOsAccentGold
        "Productivity Manager" -> Icons.Default.CheckCircle to LifeOsWarning
        "Research Assistant" -> Icons.Default.Psychology to LifeOsInfo
        "Finance Assistant" -> Icons.Default.AccountBalanceWallet to LifeOsSuccess
        "AI Goal Coach" -> Icons.Default.EmojiEvents to Color(0xFFF97316)
        "Memory Manager" -> Icons.Default.Memory to LifeOsPurple
        else -> Icons.Default.Send to LifeOsAccentGoldLight
    }

    val bg = if (isSelected) LifeOsElevated else LifeOsCardBg
    val border = if (isSelected) LifeOsAccentGold else LifeOsBorder

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bg,
        border = androidx.compose.foundation.BorderStroke(1.dp, border),
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag("dept_badge_${department.replace(" ", "_")}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = department,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = department,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) LifeOsTextPrimary else LifeOsTextSecondary,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun ExecutiveStatusPill(
    isOnline: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x1A10B981))
            .border(1.dp, Color(0x3310B981), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.4f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_alpha"
        )

        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(LifeOsSuccess.copy(alpha = alpha))
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = "AI CORE ONLINE",
            color = LifeOsSuccess,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}
