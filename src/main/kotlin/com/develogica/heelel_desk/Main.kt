package com.develogica.heelel_desk

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.develogica.heelel_desk.data.QuizDao
import com.develogica.heelel_desk.data.QuizRepository
import com.develogica.heelel_desk.ui.HomeScreen
import com.develogica.heelel_desk.ui.QuizView
import com.develogica.heelel_desk.util.Log
import com.develogica.heelel_desk.vm.HomeViewModel
import com.develogica.heelel_desk.vm.QuizAction
import com.develogica.heelel_desk.vm.QuizViewModel

private const val TAG = "MainKt"

@Composable
fun AppUI(
    homeViewModel: HomeViewModel,
    quizViewModel: QuizViewModel,
    exitApplication: () -> Unit,
) {
    val homeState = homeViewModel.uiState
    val quizUIState = quizViewModel.uiState

    val availableQuestions = quizViewModel.questions.size

    Window(
        onCloseRequest = exitApplication,
        title = "Heelel",
        state = rememberWindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            width = homeState.width,
            height = homeState.height
        ),
        resizable = false
    ) {
        MaterialTheme {
            Scaffold {
                if (quizUIState.isQuizRunning) {
                    QuizView(homeUIState = homeState, quizViewModel = quizViewModel)
                } else {
                    HomeScreen(
                        numberOfQuestions = availableQuestions,
                        onStartClick = { quizViewModel.handleQuizAction(QuizAction.StartQuiz(it)) })
                }
            }
        }
    }
}


fun main(args: Array<String>) = application {
    Log.info(TAG) { "Starting application..." }


    val homeViewModel = HomeViewModel()
    val dao = QuizDao()
    val repo = QuizRepository(dao)
    val quizViewModel = QuizViewModel(repo)

    AppUI(homeViewModel, quizViewModel, ::exitApplication)
}

