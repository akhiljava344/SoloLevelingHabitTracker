package com.app.sololevelinghabittracker.ui.screens

import android.content.Context
import android.media.MediaPlayer
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PomodoroScreen() {
    val context = LocalContext.current
    val totalTime = 25 * 60 // 25 minutes
    var remainingTime by remember { mutableStateOf(totalTime) }
    var isRunning by remember { mutableStateOf(false) }
    var streakCount by remember { mutableStateOf(0) }
    var lastStreakDate by remember { mutableStateOf(LocalDate.now().toString()) }

    // Reset streak if date changed (at midnight)
    if (lastStreakDate != LocalDate.now().toString()) {
        streakCount = 0
        lastStreakDate = LocalDate.now().toString()
    }

    // Timer effect
    LaunchedEffect(isRunning, remainingTime) {
        if (isRunning && remainingTime > 0) {
            delay(1000)
            remainingTime -= 1
        } else if (isRunning && remainingTime == 0) {
            isRunning = false
            streakCount += 1
            triggerFeedback(context)
            remainingTime = totalTime
        }
    }

    // Timer UI
    val progress = 1f - (remainingTime / totalTime.toFloat())
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "ProgressAnim")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Circular Ring Timer
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(240.dp)) {
                drawArc(
                    color = Color.LightGray,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(18f, cap = StrokeCap.Round)
                )
                drawArc(
                    color = Color(0xFF6C63FF),
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    style = Stroke(18f, cap = StrokeCap.Round)
                )
            }
            Text(
                text = formatTime(remainingTime),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Start / Pause Button
        Button(
            onClick = { isRunning = !isRunning },
            modifier = Modifier
                .clip(CircleShape)
                .size(100.dp)
        ) {
            Text(if (isRunning) "Pause" else "Start", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "🔥 Pomodoro Streak Today: $streakCount")

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Long-press to reset",
            modifier = Modifier
                .combinedClickable(
                    onClick = {},
                    onLongClick = {
                        isRunning = false
                        remainingTime = totalTime
                        streakCount = 0
                    }
                )
                .padding(8.dp),
            color = Color.Gray
        )
    }
}

private fun formatTime(seconds: Int): String {
    val min = seconds / 60
    val sec = seconds % 60
    return String.format("%02d:%02d", min, sec)
}

private fun triggerFeedback(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))

    val mediaPlayer = MediaPlayer.create(context, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
    mediaPlayer.start()
}
