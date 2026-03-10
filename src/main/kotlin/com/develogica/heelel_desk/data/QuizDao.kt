package com.develogica.heelel_desk.data

import com.develogica.heelel_desk.ConfigInfo
import kotlinx.serialization.json.Json
import com.develogica.heelel_desk.model.QuestionDTO
import com.develogica.heelel_desk.model.QuestionType
import com.develogica.heelel_desk.util.Log
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

private const val tableName = "Questions"

private const val TAG = "QuizDao"

class QuizDao {
    private val connection: Connection

    init {
        Log.info(TAG) { "Initializing QuizDao" }
        connection = connectToDBFile("tig_qz.db")
    }

    private fun connectToDBFile(location: String): Connection {
        val path = ConfigInfo.develogicaPath.resolve(location)
        Log.info(TAG) { "DB path: $path" }

        if (path != null) {
//            val filePath = Path(url.path.substring(1))
            val filePath = path.toFile()
            Log.info(TAG) { "DB file exists: ${filePath.exists()}" }

            val connection = DriverManager.getConnection("jdbc:sqlite:$filePath")
            if (connection != null) {
                Log.info(TAG) { "Connection is not null" }

                return connection
            }
        } else {
            Log.error(TAG) { "Database URL is null." }
        }
        throw Exception("Could not connect to database.")
    }

    /** Loads n questions from database.
     * Loads all questions if n is -1. */
    fun getQuestions(howMany: Int = -1): List<QuestionDTO> {
        val baseSql = "SELECT * FROM $tableName"
        val sql = if (howMany != -1) "$baseSql LIMIT $howMany" else baseSql
        val result = connection.prepareStatement(sql).executeQuery()

        return buildList {
            while (result.next()) {
                add(getQuestion(result))
            }
        }
    }

    private fun getQuestion(resultRow: ResultSet): QuestionDTO {
        val questionType = resultRow.getString(QuizTable.QuestionType.header)
        val text = resultRow.getString(QuizTable.Text.header)
        val image = resultRow.getString(QuizTable.Image.header)
        val answer = resultRow.getString(QuizTable.Answer.header)
        val options = resultRow.getString(QuizTable.Options.header)
        val moreInfo = resultRow.getString(QuizTable.MoreInfo.header)
        val tags = resultRow.getString(QuizTable.Tags.header)
        return QuestionDTO(
            questionType = QuestionType.valueOf(questionType),
            text = text,
            image = image,
            answer = answer,
            options = Json.decodeFromString(options),
            moreInfo = moreInfo,
            tags = Json.decodeFromString(tags)
        )
    }

    fun closeDatabase() {
        connection.close()
    }
}

enum class QuizTable(val header: String) {
    QuestionType("question_type"),
    Text("question_text"),
    Image("image"),
    Answer("answer"),
    Options("choices"),
    MoreInfo("more_info"),
    Tags("tags");
}