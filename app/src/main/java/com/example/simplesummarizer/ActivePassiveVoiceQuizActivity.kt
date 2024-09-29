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

class ActivePassiveVoiceQuizActivity : Activity() {

    private lateinit var questionTextView: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButton1: RadioButton
    private lateinit var radioButton2: RadioButton
    private lateinit var radioButton3: RadioButton
    private lateinit var radioButton4: RadioButton
    private lateinit var buttonSubmit: Button
    private lateinit var scoreTextView: TextView
    private lateinit var buttonRetry: Button

    private val activePassiveVoiceQuestions = listOf(
        Question(
            "The book was read by the student.",
            listOf(
                "Active",
                "Passive"
            ),
            1
        ),
        Question(
            "The chef cooks dinner every night.",
            listOf(
                "Active",
                "Passive"
            ),
            0
        ),
        Question(
            "The song was sung by the choir.",
            listOf(
                "Active",
                "Passive"
            ),
            1
        ),
        Question(
            "She wrote a letter to her friend.",
            listOf(
                "Active",
                "Passive"
            ),
            0
        ),
        Question(
            "The homework was completed by the students.",
            listOf(
                "Active",
                "Passive"
            ),
            1
        )
        // Add more active/passive voice questions here
    )

    private var selectedQuestions = mutableListOf<Question>()
    private var currentQuestionIndex = 0
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_passive_voice_quiz)

        questionTextView = findViewById(R.id.questionTextView)
        radioGroup = findViewById(R.id.radioGroup)
        radioButton1 = findViewById(R.id.radioButton1)
        radioButton2 = findViewById(R.id.radioButton2)
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
        selectedQuestions = activePassiveVoiceQuestions.shuffled(Random(System.currentTimeMillis())).take(5).toMutableList()
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
            score >= 4 -> "Excellent understanding of active and passive voice!"
            score >= 3 -> "Good grasp of active and passive voice."
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
