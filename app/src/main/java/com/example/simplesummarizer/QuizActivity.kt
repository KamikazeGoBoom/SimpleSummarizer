package com.example.simplesummarizer

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import kotlin.math.max
import kotlin.random.Random

class QuizActivity : Activity() {

    private lateinit var questionTextView: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButton1: RadioButton
    private lateinit var radioButton2: RadioButton
    private lateinit var radioButton3: RadioButton
    private lateinit var radioButton4: RadioButton
    private lateinit var buttonSubmit: Button
    private lateinit var scoreTextView: TextView
    private lateinit var buttonRetry: Button

    private val allQuestions = listOf(
        Question(
            "Which sentence is grammatically correct?",
            listOf(
                "He don’t know the answer.",
                "She doesn't likes pizza.",
                "They are going to the party.",
                "He are not here."
            ),
            2
        ),
        Question(
            "Which is the correct past tense of 'go'?",
            listOf(
                "Goed", "Gone", "Went", "Going"
            ),
            2
        ),
        Question(
            "Which of these is a complete sentence?",
            listOf(
                "Because she was tired.", "Running fast.", "He finished his homework.", "The boy who was crying."
            ),
            2
        ),
        Question(
            "Choose the correct form of the verb: She ___ to the store yesterday.",
            listOf(
                "go", "gone", "went", "going"
            ),
            2
        ),
        Question(
            "Identify the correctly punctuated sentence.",
            listOf(
                "Lets eat, Grandma!",
                "Let's eat Grandma!",
                "Lets eat Grandma!",
                "Let's eat, Grandma!"
            ),
            3
        ),
        Question(
            "Which sentence uses the correct form of 'their'?",
            listOf(
                "Their going to the park.",
                "There going to the park.",
                "They're going to the park.",
                "There going to the park."
            ),
            2
        ),
        Question(
            "Choose the correct comparative form.",
            listOf(
                "She is more smarter than him.",
                "She is smarter than him.",
                "She is more smart than him.",
                "She is smart than him."
            ),
            1
        ),
        Question(
            "Which sentence is in the passive voice?",
            listOf(
                "The chef cooked a delicious meal.",
                "A delicious meal was cooked by the chef.",
                "The chef is cooking a delicious meal.",
                "The chef will cook a delicious meal."
            ),
            1
        ),
        Question(
            "Select the sentence with the correct subject-verb agreement.",
            listOf(
                "The list of items are on the table.",
                "The list of items is on the table.",
                "The lists of items is on the table.",
                "The lists of items are on the table."
            ),
            1
        ),
        Question(
            "Choose the correct pronoun to complete the sentence: 'Each of the students must bring ___ own lunch.'",
            listOf(
                "their", "his or her", "their own", "his own"
            ),
            1
        ),
        Question(
            "Which sentence correctly uses a semicolon?",
            listOf(
                "I have a big test tomorrow; I can't go out tonight.",
                "I have a big test tomorrow; and I can't go out tonight.",
                "I have a big test tomorrow I can't go out tonight.",
                "I have a big test tomorrow, I can't go out tonight."
            ),
            0
        ),
        Question(
            "Identify the sentence with a dangling modifier.",
            listOf(
                "Running quickly, the finish line was crossed.",
                "Running quickly, he crossed the finish line.",
                "He was running quickly to cross the finish line.",
                "He crossed the finish line quickly."
            ),
            0
        ),
        Question(
            "Choose the correct form of 'to lie' or 'to lay': 'Please ___ the book on the table.'",
            listOf(
                "lie", "lay", "lying", "laid"
            ),
            1
        ),
        Question(
            "Which sentence correctly uses 'affect'?",
            listOf(
                "The weather will effect our plans.",
                "The weather will affect our plans.",
                "The weather will have no affect on our plans.",
                "The weather will affect on our plans."
            ),
            1
        ),
        Question(
            "Select the correct usage of 'its' or 'it's': '___ a beautiful day today.'",
            listOf(
                "Its", "It's", "It is", "It has"
            ),
            1
        ),
        Question(
            "Choose the correct form: 'Neither the teacher nor the students ___ ready for the test.'",
            listOf(
                "is", "are", "were", "be"
            ),
            0
        ),
        Question(
            "Which sentence uses 'fewer' correctly?",
            listOf(
                "There are fewer cars on the road today.",
                "There are less cars on the road today.",
                "There are fewer car on the road today.",
                "There is fewer cars on the road today."
            ),
            0
        ),
        Question(
            "Identify the correctly structured sentence.",
            listOf(
                "Although he was tired, he finished his work.",
                "Although he was tired he finished his work.",
                "He finished his work although he was tired.",
                "Both A and C are correct."
            ),
            3
        ),
        Question(
            "Choose the correct word: 'Their/There/They're going to the concert tonight.'",
            listOf(
                "Their", "There", "They're", "None of the above"
            ),
            2
        ),
        Question(
            "Which sentence correctly uses 'who'?",
            listOf(
                "She is the one who I was talking about.",
                "She is the one whom I was talking about.",
                "She is the one whose I was talking about.",
                "She is the one who I was talking to."
            ),
            0
        ),
        Question(
            "Choose the correct form: 'If I ___ you, I would apologize.'",
            listOf(
                "am", "was", "were", "be"
            ),
            2
        )
        // Add more questions as needed
    )

    private var selectedQuestions = mutableListOf<Question>()
    private var currentQuestionIndex = 0
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

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
        if (allQuestions.size < 10) {
            selectedQuestions = allQuestions.shuffled(Random(System.currentTimeMillis())).toMutableList()
        } else {
            selectedQuestions = allQuestions.shuffled(Random(System.currentTimeMillis())).take(10).toMutableList()
        }
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
        val grammarLevel = when {
            score >= 9 -> "Excellent"
            score >= 7 -> "Good"
            score >= 5 -> "Fair"
            else -> "Needs Improvement"
        }
        scoreTextView.text = "Your score: $score/${selectedQuestions.size}\nGrammar level: $grammarLevel"
        buttonSubmit.isEnabled = false
        buttonRetry.visibility = View.VISIBLE
    }

    private data class Question(
        val text: String,
        val options: List<String>,
        val correctOptionIndex: Int
    )
}