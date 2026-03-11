package com.develogica.heelel_desk.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.develogica.heelel_desk.vm.QuizMode

@Composable
fun HomeScreen(
    numberOfQuestions: Int,
    onStartClick: (QuizMode) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Available Questions: $numberOfQuestions")
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = { onStartClick(QuizMode.Manual)}) {
            Text("Start Regular Quiz")
        }
        Button(onClick = { onStartClick(QuizMode.Timed)}) {
            Text("Start Timed Quiz")
        }
    }
}
