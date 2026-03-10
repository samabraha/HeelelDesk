package com.develogica.heelel_desk.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.develogica.heelel_desk.data.QuizRepository
import com.develogica.heelel_desk.model.LiveQuestion
import com.develogica.heelel_desk.model.Option
import com.develogica.heelel_desk.model.sampleQuestions
import com.develogica.heelel_desk.util.ColorUtil
import org.jetbrains.skiko.currentNanoTime
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class QuizViewModel(repository: QuizRepository, vararg tags: String) {
    var mode: QuizMode = QuizMode.Regular
    private val random = Random(currentNanoTime())
    val questions = repository.filterQuestions(tags.toSet()).shuffled().toMutableList()
    val tags = questions.flatMap { it.tags }.toSet()

    val questionDuration: Duration = 7.seconds
    val answerDuration: Duration = 3.seconds

    private var questionCounter = 0

    var uiState by mutableStateOf(createNewUiState())
        private set

    fun handleQuizAction(action: QuizAction) {
        when (action) {
            is QuizAction.StartQuiz -> startQuiz(action.mode)
            is QuizAction.ShowAnswer -> showAnswer(action.attempt)
            QuizAction.NextQuestion -> setNextQuestion()
            QuizAction.StopQuiz -> stopQuiz()
        }
    }

    private fun startQuiz(mode: QuizMode) {
        this.mode = mode
        uiState = createNewUiState(isQuizRunning = true)
    }

    private fun stopQuiz() {
        uiState = createNewUiState(isQuizRunning = false)
    }

    private fun createNewUiState(question: LiveQuestion? = null, isQuizRunning: Boolean = false): UIState {
        val id = ++questionCounter
        val q = question ?: sampleQuestions.random(random)

        return UIState(
            isQuizRunning = isQuizRunning,
            mode = mode,
            question = q,
            questionId = id,
            textBackColor = ColorUtil.randomColor(saturation = 1f, lightness = .9f),
            textColor = ColorUtil.randomColor(saturation = 1.0f, lightness = .25f),
            optionBackColors = setOptionColors(saturation = .75f, lightness = .2f),
            backgroundColorA = ColorUtil.randomColor(saturation = 1f, lightness = .25f),
            backgroundColorB = ColorUtil.randomColor(saturation = 1f, lightness = .50f),
        )
    }

    private fun showAnswer(attempt: Attempt) {
        uiState = when (attempt) {
            is Attempt.MCQ -> uiState.copy(showAnswer = true, attempt = attempt)
            Attempt.Null -> uiState.copy(showAnswer = true, attempt = null)
            is Attempt.QnA -> uiState.copy(showAnswer = true, attempt = attempt)
            is Attempt.TrueFalse -> uiState.copy(showAnswer = true, attempt = attempt)
        }
    }

    private fun setNextQuestion() {
        if (questions.isEmpty()) {
            uiState = uiState.copy(isQuizRunning = false)
            return
        }

        val question = questions.removeLast().toLiveQuestion()

        uiState = createNewUiState(question, isQuizRunning = true)
    }

    private fun setOptionColors(saturation: Float, lightness: Float): List<Color> {
        val colors = mutableSetOf<Color>()
        val start = random.nextInt(360)

        for (i in 0..3) {
            colors += ColorUtil.randomColor(
                hue = (start + i * 80) % 360f, saturation = saturation, lightness = lightness
            )
        }
        return colors.toList()
    }
}

sealed class QuizAction {
    data class StartQuiz(val mode: QuizMode) : QuizAction()
    object NextQuestion : QuizAction()
    data class ShowAnswer(val attempt: Attempt) : QuizAction()
    object StopQuiz : QuizAction()
}

sealed class Attempt {
    data class MCQ(val option: Option) : Attempt()
    data class TrueFalse(val answer: Boolean) : Attempt()
    data class QnA(val answer: String) : Attempt()
    object Null : Attempt()
}

enum class QuizMode {
    Regular, Timed
}

data class UIState(
    val isQuizRunning: Boolean = false,
    val mode: QuizMode = QuizMode.Regular,
    val attempt: Attempt? = null,
    val question: LiveQuestion,
    val questionId: Int,
    val optionBackColors: List<Color> = emptyList(),
    val textBackColor: Color,
    val textColor: Color,
    val showAnswer: Boolean = false,
    val backgroundColorA: Color,
    val backgroundColorB: Color,
)
