package com.example.simplesummarizer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class QuizListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_list)

        // Buttons for grammar categories
        val sentenceStructureButton = findViewById<Button>(R.id.btnSentenceStructure)
        val tensesButton = findViewById<Button>(R.id.btnTenses)
        val subjectVerbButton = findViewById<Button>(R.id.btnSubjectVerbAgreement)
        val punctuationButton = findViewById<Button>(R.id.btnPunctuation)
        val vocabButton = findViewById<Button>(R.id.btnVocabulary)

        // Redirect "Sentence Structure" button to the actual QuizActivity
        sentenceStructureButton.setOnClickListener {
            startActivity(Intent(this, QuizActivity::class.java))
        }

        // Placeholder redirects for other categories
        tensesButton.setOnClickListener {
            startActivity(Intent(this, PlaceholderActivity::class.java))
        }

        subjectVerbButton.setOnClickListener {
            startActivity(Intent(this, PlaceholderActivity::class.java))
        }

        punctuationButton.setOnClickListener {
            startActivity(Intent(this, PlaceholderActivity::class.java))
        }

        vocabButton.setOnClickListener {
            startActivity(Intent(this, PlaceholderActivity::class.java))
        }
    }
}

