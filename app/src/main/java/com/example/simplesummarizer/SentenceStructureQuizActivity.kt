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

class SentenceStructureQuizActivity : Activity() {

    private lateinit var questionTextView: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButton1: RadioButton
    private lateinit var radioButton2: RadioButton
    private lateinit var radioButton3: RadioButton
    private lateinit var radioButton4: RadioButton
    private lateinit var buttonSubmit: Button
    private lateinit var scoreTextView: TextView
    private lateinit var buttonRetry: Button

    private val sentenceStructureQuestions = listOf(
        Question(
            "Which sentence is grammatically correct?",
            listOf(
                "He don't know what to do.",
                "She don't like pizza.",
                "They are going to the park.",
                "The book is on the table."
            ),
            3
        ),
        Question(
            "Identify the correct sentence structure.",
            listOf(
                "Running fast, the race was won.",
                "The race was won, running fast.",
                "The runner won the race by running fast.",
                "Won the race by running fast."
            ),
            2
        ),
        Question(
            "Which sentence uses proper subject-verb agreement?",
            listOf(
                "The dogs is barking.",
                "The dog are barking.",
                "The dog barks loudly.",
                "The dogs barks loudly."
            ),
            2
        ),
        Question(
            "Choose the sentence with correct structure.",
            listOf(
                "She quickly the homework finished.",
                "Quickly she finished the homework.",
                "She finished quickly the homework.",
                "Finished she quickly the homework."
            ),
            1
        ),
        Question(
            "Select the grammatically correct complex sentence.",
            listOf(
                "Because she was tired, she went to bed early.",
                "She went to bed early, because she was tired.",
                "Because tired, she went to bed early.",
                "Because was tired, she went early to bed."
            ),
            0
        )
        // Add more questions for sentence structure improvement
    )

    private var selectedQuestions = mutableListOf<Question>()
    private var currentQuestionIndex = 0
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sentence_structure_quiz)

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
        selectedQuestions = sentenceStructureQuestions.shuffled(Random(System.currentTimeMillis())).take(5).toMutableList()
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
            score >= 4 -> "Excellent grasp of sentence structure!"
            score >= 3 -> "Good understanding of sentence structure."
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
