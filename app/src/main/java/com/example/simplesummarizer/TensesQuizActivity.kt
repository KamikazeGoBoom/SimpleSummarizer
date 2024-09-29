package com.example.simplesummarizer

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import kotlin.random.Random

class TensesQuizActivity : Activity() {

    private lateinit var questionTextView: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButton1: RadioButton
    private lateinit var radioButton2: RadioButton
    private lateinit var radioButton3: RadioButton
    private lateinit var radioButton4: RadioButton
    private lateinit var buttonSubmit: Button
    private lateinit var scoreTextView: TextView
    private lateinit var buttonRetry: Button

    private val tenseQuestions = listOf(
        Question(
            "Which sentence uses the past tense correctly?",
            listOf(
                "I was run yesterday.",
                "She were going to the store.",
                "They went to the concert last night.",
                "We is watching the game."
            ),
            2
        ),
        Question(
            "Identify the sentence with correct future tense usage.",
            listOf(
                "She will went to the market tomorrow.",
                "I will go to the party next week.",
                "They will eats dinner later.",
                "We are will finish the task soon."
            ),
            1
        ),
        Question(
            "Choose the sentence that is in the present perfect tense.",
            listOf(
                "I have finished my homework.",
                "She finishes her homework.",
                "They are finishing their homework.",
                "He is finish his homework."
            ),
            0
        ),
        Question(
            "Which sentence correctly uses the past continuous tense?",
            listOf(
                "She is running yesterday.",
                "They were playing soccer when it started to rain.",
                "He was eats when she arrived.",
                "We are watched the movie last night."
            ),
            1
        ),
        Question(
            "Select the sentence that is in the correct present continuous tense.",
            listOf(
                "He is runs to the store.",
                "They are eat breakfast now.",
                "She is studying for the test right now.",
                "We were watching TV."
            ),
            2
        )
        // Add more tense-related questions here
    )

    private var selectedQuestions = mutableListOf<Question>()
    private var currentQuestionIndex = 0
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tenses_quiz)

        questionTextView = findViewById(R.id.questionTextView)
        radioGroup = findViewById(R.id.radioGroup)
        radioButton1 = findViewById(R.id.radioButton1)
        radioButton2 = findViewById(R.id.radioButton2)
        radioButton3 = findViewById(R.id.radioButton3)
        radioButton4 = findViewById(R.id.radioButton4)
        buttonSubmit = findViewById(R.id.buttonSubmit)
        scoreTextView = findViewById(R.id.scoreTextView)
        buttonRetry = findViewById(R.id.buttonRetry)

        initializeQuiz()

        buttonSubmit.setOnClickListener {
            val selectedOptionId = radioGroup.checkedRadioButtonId
            if (selectedOptionId != -1) {
                val selectedAnswerIndex = when (selectedOptionId) {
                    R.id.radioButton1 -> 0
                    R.id.radioButton2 -> 1
                    R.id.radioButton3 -> 2
                    R.id.radioButton4 -> 3
                    else -> -1
                }
                checkAnswer(selectedAnswerIndex)
                currentQuestionIndex++

                if (currentQuestionIndex < selectedQuestions.size) {
                    loadQuestion()
                } else {
                    showResults()
                }
            } else {
                Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show()
            }
        }

        buttonRetry.setOnClickListener {
            initializeQuiz()
            buttonRetry.visibility = View.GONE
            scoreTextView.text = ""
            buttonSubmit.isEnabled = true
        }
    }

    private fun initializeQuiz() {
        selectedQuestions = tenseQuestions.shuffled(Random(System.currentTimeMillis())).take(5).toMutableList()
        currentQuestionIndex = 0
        score = 0
        buttonSubmit.isEnabled = true
        loadQuestion()
    }

    private fun loadQuestion() {
        val currentQuestion = selectedQuestions[currentQuestionIndex]
        questionTextView.text = "${currentQuestionIndex + 1}. ${currentQuestion.text}"
        radioButton1.text = currentQuestion.options[0]
        radioButton2.text = currentQuestion.options[1]
        radioButton3.text = currentQuestion.options[2]
        radioButton4.text = currentQuestion.options[3]
        radioGroup.clearCheck()
    }

    private fun checkAnswer(selectedAnswerIndex: Int) {
        val correctAnswerIndex = selectedQuestions[currentQuestionIndex].correctOptionIndex
        if (selectedAnswerIndex == correctAnswerIndex) {
            score++
        }
    }

    private fun showResults() {
        val resultText = when {
            score >= 4 -> "Excellent grasp of tenses!"
            score >= 3 -> "Good understanding of tenses."
            score >= 2 -> "Fair, but you can improve."
            else -> "Needs improvement."
        }
        scoreTextView.text = "Your score: $score/${selectedQuestions.size}\n$resultText"
        buttonSubmit.isEnabled = false
        buttonRetry.visibility = View.VISIBLE
    }

    private data class Question(
        val text: String,
        val options: List<String>,
        val correctOptionIndex: Int
    )
}
