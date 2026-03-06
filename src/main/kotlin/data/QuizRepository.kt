package data

import model.QuestionDTO
import model.QuestionType
import java.io.File
import kotlin.collections.filter

class QuizRepository(val quizDao: QuizDao) {
    private val questions: List<QuestionDTO>
    private val tags: Set<String>

    init {
        println("Initializing QuizRepository")
        questions = quizDao.getQuestions().ensureImageExists(true)
//            .removeWordyQuestions(maxText = 100, maxOption = 75)
        println("Loaded ${questions.size} questions.")
        quizDao.closeDatabase()
        tags = questions.flatMap { it.tags }.toSet()
        println("Discovered ${tags.size} tags.")
    }

    /**
     * Filters questions to remove questions with missing images.
     */
    private fun List<QuestionDTO>.ensureImageExists(logMissing: Boolean = false): List<QuestionDTO> {
        val beforeSize = size
        return filter { q ->
            if (q.questionType == QuestionType.IS_IMAGE) {
                imageExists(q).also {
                    if (!it && logMissing) {
                        println("Missing image: ${q.image}")
                    }
                }
            } else true
        }.also { x ->
            println("Removed ${beforeSize - x.size} questions without images.")
        }
    }

    private fun imageExists(question: QuestionDTO): Boolean {
        return File(question.image).exists()
    }

    fun filterQuestions(hasTags: Set<String>, ensureAllTags: Boolean = false): List<QuestionDTO> {
        if (hasTags.isEmpty()) {
            println("No tags specified, returning all questions.")
            return questions
        }

        if (hasTags.none { tags.contains(it) }) {
            println("None of the ${questions.size} questions contain any of the specified tags: $hasTags")
            return emptyList()
        }

        return if (ensureAllTags) {
            questions.filter { q ->
                hasTags.all { t -> q.tags.contains(t) }
            }
        } else {
            questions.filter { q ->
                hasTags.any { t -> q.tags.contains(t) }
            }
        }.also {
            val modifier = if (ensureAllTags) "all" else "any of"
            println("Filtered to ${it.size} questions containing $modifier ${hasTags.size} tags: $hasTags")
        }
    }

    fun List<QuestionDTO>.removeWordyQuestions(maxText: Int, maxOption: Int): List<QuestionDTO> {
        return filter {
            it.questionType == QuestionType.IS_TEXT
        }.filter { q ->
            q.text?.length ?: 0 < maxText && q.options.all { it.text.length < maxOption }
        }.also {
            println("Removed ${this.size - it.size} wordy questions.")
        }
    }
}
