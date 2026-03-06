import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import data.QuizDao
import data.QuizRepository
import ui.HomeScreen
import ui.QuizView
import vm.HomeViewModel
import vm.QuizViewModel

@Composable
fun App(
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
                    HomeScreen(numberOfQuestions = availableQuestions, onStartClick = { quizViewModel.startQuiz() })
                }
            }
        }
    }
}

fun main(args: Array<String>) = application {
    println("Starting application...")
    val clArgs = CLArgs(args)

    println("Args: ${args.toList()}")
    val homeViewModel = HomeViewModel(launchInPortrait = clArgs.launchInPortrait)
    val dao = QuizDao()
    val repo = QuizRepository(dao)
    val quizViewModel = QuizViewModel(repo)

    App(homeViewModel, quizViewModel, ::exitApplication)
}

class CLArgs(args: Array<String>) {
    var launchInPortrait = false
    var launchInLandscape= false

    init {
        for (arg in args) {
            when (arg) {
                "-p" -> launchInPortrait = true
                "-l" -> launchInLandscape = true
                else -> println("Unknown argument: $arg")
            }
        }
    }
}