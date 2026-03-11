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
import com.develogica.heelel_desk.util.Log
import org.jetbrains.skiko.currentNanoTime
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private const val TAG = "QuizViewModel"

class QuizViewModel(repository: QuizRepository, vararg tags: String) {
    var mode: QuizMode = QuizMode.Manual

    private val random = Random(currentNanoTime())
    val questions = repository.filterQuestions(tags.toSet()).shuffled().toMutableList()
    val tags = questions.flatMap { it.tags }.toSet()

    val questionDuration: Duration = 7.seconds
    val answerDuration: Duration = 3.seconds

    private var questionCounter = 0

    var uiState by mutableStateOf(UIState())
        private set

    var score by mutableStateOf(Score())

    fun handleQuizAction(action: QuizAction) {
        Log.info(TAG) { "Handling action: $action" }
        when (action) {
            is QuizAction.StartQuiz -> startQuiz(action.mode)
            is QuizAction.ShowAnswer -> showAnswer(action.attempt)
            QuizAction.NextQuestion -> setNextQuestion()
            QuizAction.StopQuiz -> stopQuiz()
        }
    }

    private fun startQuiz(mode: QuizMode) {
        this.mode = mode
        score = Score()
        setNextQuestion()
    }

    private fun stopQuiz() {
        Log.info(TAG) { "Stopping quiz" }
        uiState = UIState(event = QuizEvent.Stopped)
    }

    private fun showAnswer(attempt: Attempt) {
        Log.info(TAG) { "Showing answer: $attempt" }
        if (uiState.event != QuizEvent.ShowingQuestion) return

        when (attempt) {
            is Attempt.MCQ -> {
                score = if (attempt.option.isCorrect) {
                    score.copy(correct = score.correct + 1)
                } else {
                    score.copy(incorrect = score.incorrect + 1)
                }
            }
            Attempt.Null -> {
                score = score.copy(unanswered = score.unanswered + 1)
            }
            is Attempt.QnA -> {}
            is Attempt.TrueFalse -> {}
        }

        uiState = uiState.copy(
            event = QuizEvent.ShowingAnswer,
            attempt = attempt
        )
    }

    private fun setNextQuestion() {
        Log.info(TAG) { "Setting next question" }

        if (questions.isEmpty()) {
            stopQuiz()
            return
        }

        val question = questions.removeLast().toLiveQuestion()
        val id = ++questionCounter

        uiState = uiState.copy(
            event = QuizEvent.ShowingQuestion,
            mode = mode,
            attempt = null,
            question = question,
            questionId = id,
            textBackColor = ColorUtil.randomColor(saturation = 1f, lightness = .9f),
            textColor = ColorUtil.randomColor(saturation = 1.0f, lightness = .25f),
            optionBackColors = setOptionColors(saturation = .75f, lightness = .2f),
            backgroundColorA = ColorUtil.randomColor(saturation = 1f, lightness = .25f),
            backgroundColorB = ColorUtil.randomColor(saturation = 1f, lightness = .50f),
        )
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
    Manual, Timed
}

data class Score(val correct: Int = 0, val incorrect: Int = 0, val unanswered: Int = 0) {
    val total: Int get() = correct + incorrect + unanswered
    val points: Float get() = if (total > 0) correct.toFloat() / total.toFloat() * 100 else 0f
}

enum class QuizEvent {
    ShowingQuestion, ShowingAnswer, Paused, Stopped
}

data class UIState(
    val event: QuizEvent = QuizEvent.Stopped,
    val mode: QuizMode = QuizMode.Manual,
    val attempt: Attempt? = null,
    val question: LiveQuestion = sampleQuestions.random(),
    val questionId: Int = 0,
    val optionBackColors: List<Color> = emptyList(),
    val textBackColor: Color = ColorUtil.randomColor(saturation = 1f, lightness = .9f),
    val textColor: Color = ColorUtil.randomColor(saturation = 1.0f, lightness = .25f),
    val correctAnswerBack: Color = ColorUtil.correctAnswerBack,
    val incorrectGuessBack: Color = ColorUtil.incorrectGuessBack,
    val missedCorrectAnswer: Color = ColorUtil.missedCorrectAnswer,
    val backgroundColorA: Color = ColorUtil.randomColor(saturation = 1f, lightness = .25f),
    val backgroundColorB: Color = ColorUtil.randomColor(saturation = 1f, lightness = .50f)
)
