package com.develogica.heelel_desk.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class HomeViewModel(
) {
    var uiState by mutableStateOf(HomeUIState())
        private set
}

data class HomeUIState(
    val width: Dp = 1860.dp, val height: Dp = 1000.dp, val gradientFocus: Float = width.value + 20
)
