package com.app.sololevelinghabittracker.ui.screens

import android.content.Context
import android.media.MediaPlayer
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.sololevelinghabittracker.datastore.AppPreferencesManager
import com.app.sololevelinghabittracker.viewmodel.ShadowFocusViewModel
import com.app.sololevelinghabittracker.viewmodel.ShadowFocusViewModelFactory
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(appPreferencesManager : AppPreferencesManager) {
    val viewModel: ShadowFocusViewModel = viewModel(
        factory = ShadowFocusViewModelFactory(appPreferencesManager)
    )
    val timeText by viewModel.timeText.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val xpEarned by viewModel.xpEarned.collectAsState()
    val sessionsCompletedToday by viewModel.sessionsCompletedToday.collectAsState()

    var isShieldMode by remember { mutableStateOf(false) }
    var isSoundOn by remember { mutableStateOf(false) }
    var showSessionComplete by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    var mediaPlayer: MediaPlayer? = remember { null }

    // 🎉 Feedback when XP is awarded (session complete)
    LaunchedEffect(xpEarned) {
        if (xpEarned > 0) {
            showSessionComplete = true

            if (isSoundOn) {
                mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_NOTIFICATION_URI)
                mediaPlayer?.start()
            } else {
                vibrator.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Shadow Focus Mode") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            TimerRing(progress = progress, timeText = timeText)

            if (showSessionComplete) {
                Text(
                    text = "🎉 Session Complete! +$xpEarned XP",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    if (isRunning) viewModel.pauseTimer()
                    else viewModel.startTimer(isShieldMode)
                }) {
                    Text(if (isRunning) "Pause" else "Start")
                }

                OutlinedButton(onClick = {
                    viewModel.resetTimer()
                    showSessionComplete = false
                }) {
                    Text("Reset")
                }
            }

            // ⚙️ Settings Toggles
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Shield Mode (XP Boost)", fontWeight = FontWeight.Bold)
                    Switch(checked = isShieldMode, onCheckedChange = { isShieldMode = it })
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ambient Sound", fontWeight = FontWeight.Bold)
                    Switch(checked = isSoundOn, onCheckedChange = { isSoundOn = it })
                }
            }

            // 🌟 XP + Session Info
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("XP on Complete: ${if (isShieldMode) 20 else 10}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Text("🔥 Sessions Today: $sessionsCompletedToday", fontSize = 13.sp, modifier = Modifier.align(Alignment.End))
            }
        }
    }
}

@Composable
fun TimerRing(progress: Float, timeText: String) {
    val strokeWidth = 18f
    val ringColor = MaterialTheme.colorScheme.primary

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(220.dp)
            .padding(16.dp)
    ) {
        Canvas(modifier = Modifier.size(200.dp)) {
            val diameter = min(size.width, size.height)
            val radius = diameter / 2

            drawCircle(
                color = Color.LightGray,
                radius = radius,
                style = Stroke(width = strokeWidth)
            )

            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360 * progress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Text(
            text = timeText,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
