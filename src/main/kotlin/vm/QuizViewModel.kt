package vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import data.QuizRepository
import kotlinx.coroutines.delay
import model.LiveQuestion
import model.sampleQuestions
import org.jetbrains.skiko.currentNanoTime
import util.ColorUtil
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class QuizViewModel(repository: QuizRepository, vararg tags: String) {
    private val random = Random(currentNanoTime())
    val questions = repository.filterQuestions(tags.toSet()).shuffled().toMutableList()
    val tags = questions.flatMap { it.tags }.toSet()

    val questionDuration: Duration = 7.seconds
    val answerDuration: Duration = 3.seconds

    private var questionCounter = 0

    var uiState by mutableStateOf(createNewUiState())
        private set

    fun startQuiz() {
        uiState = createNewUiState(isQuizRunning = true)
    }

    private fun createNewUiState(question: LiveQuestion? = null, isQuizRunning: Boolean = false): UIState {
        val id = ++questionCounter
        val q = question ?: sampleQuestions.random(random)

        return UIState(
            isQuizRunning = isQuizRunning,
            question = q,
            questionId = id,
            textBackColor = ColorUtil.randomColor(saturation = 1f, lightness = .9f),
            textColor = ColorUtil.randomColor(saturation = 1.0f, lightness = .25f),
            optionBackColors = setOptionColors(saturation = .75f, lightness = .2f),
            backgroundColorA = ColorUtil.randomColor(saturation = 1f, lightness = .25f),
            backgroundColorB = ColorUtil.randomColor(saturation = 1f, lightness = .50f),
        )
    }

    suspend fun onQuestionTimeExpired() {
        showAnswer()
        updateQuestion()
    }

    private fun updateQuestion() {
        if (questions.isEmpty()) {
            uiState = uiState.copy(isQuizRunning = false)
            return
        }

        val question = questions.removeLast().toLiveQuestion()

        uiState = createNewUiState(question, isQuizRunning = true)
    }

    private suspend fun showAnswer() {
        uiState = uiState.copy(showAnswer = true)
        delay(answerDuration)
    }


    fun setOptionColors(saturation: Float, lightness: Float): List<Color> {
        val colors = mutableSetOf<Color>()
        val start = random.nextInt(360)

        for (i in 0..3) {
            colors += ColorUtil.randomColor(
                hue = (start + i * 80) % 360f,
                saturation = saturation,
                lightness = lightness
            )
        }
        return colors.toList()
    }
}


data class UIState(
    val isQuizRunning: Boolean = false,
    val question: LiveQuestion,
    val questionId: Int,
    val optionBackColors: List<Color> = emptyList(),
    val textBackColor: Color,
    val textColor: Color,
    val showAnswer: Boolean = false,
    val backgroundColorA: Color,
    val backgroundColorB: Color,
)
