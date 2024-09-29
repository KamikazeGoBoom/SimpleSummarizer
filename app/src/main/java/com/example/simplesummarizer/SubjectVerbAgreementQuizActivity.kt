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

class SubjectVerbAgreementQuizActivity : Activity() {

    private lateinit var questionTextView: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButton1: RadioButton
    private lateinit var radioButton2: RadioButton
    private lateinit var radioButton3: RadioButton
    private lateinit var radioButton4: RadioButton
    private lateinit var buttonSubmit: Button
    private lateinit var scoreTextView: TextView
    private lateinit var buttonRetry: Button

    private val subjectVerbAgreementQuestions = listOf(
        Question(
            "The dog ____ barking loudly.",
            listOf(
                "is",
                "are",
                "were",
                "be"
            ),
            0
        ),
        Question(
            "The team ____ winning the game.",
            listOf(
                "is",
                "are",
                "was",
                "were"
            ),
            0
        ),
        Question(
            "She ____ to the store every Saturday.",
            listOf(
                "go",
                "goes",
                "going",
                "gone"
            ),
            1
        ),
        Question(
            "Neither of the answers ____ correct.",
            listOf(
                "is",
                "are",
                "were",
                "be"
            ),
            0
        ),
        Question(
            "Either the cat or the dogs ____ making noise.",
            listOf(
                "is",
                "are",
                "was",
                "be"
            ),
            1
        )
        // Add more subject-verb agreement questions here
    )

    private var selectedQuestions = mutableListOf<Question>()
    private var currentQuestionIndex = 0
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_verb_agreement_quiz)

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
        selectedQuestions = subjectVerbAgreementQuestions.shuffled(Random(System.currentTimeMillis())).take(5).toMutableList()
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
            score >= 4 -> "Excellent understanding of subject-verb agreement!"
            score >= 3 -> "Good grasp of subject-verb agreement."
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
