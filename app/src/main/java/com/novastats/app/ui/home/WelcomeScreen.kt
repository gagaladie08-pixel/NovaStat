package com.novastats.app.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novastats.app.core.theme.LocalNovaTheme

@Composable
fun WelcomeScreen(
    onFinished: () -> Unit,
    onThemeChange: (String) -> Unit
) {
    val theme = LocalNovaTheme.current
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "✨ NovaStats",
                color = theme.primary,
                fontSize = 32.sp
            )
            Text(
                text = "Bienvenue dans ta renaissance",
                color = theme.text,
                fontSize = 16.sp
            )
            Button(onClick = onFinished) {
                Text("ENTRE DANS TON ÈRE")
            }
        }
    }
}