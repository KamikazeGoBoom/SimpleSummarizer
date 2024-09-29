package com.example.simplesummarizer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.simplesummarizer.ActivePassiveVoiceQuizActivity
import com.example.simplesummarizer.SentenceStructureQuizActivity
import com.example.simplesummarizer.SubjectVerbAgreementQuizActivity
import com.example.simplesummarizer.TensesQuizActivity
import com.example.simplesummarizer.PartsOfSpeechQuizActivity

class QuizListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_list)

        // Buttons for grammar categories
        val sentenceStructureButton = findViewById<Button>(R.id.btnSentenceStructure)
        val tensesButton = findViewById<Button>(R.id.btnTenses)
        val subjectVerbButton = findViewById<Button>(R.id.btnSubjectVerbAgreement)
        val activePassiveVoiceButton = findViewById<Button>(R.id.btnActivePassiveVoice)
        val partsOfSpeechButton = findViewById<Button>(R.id.btnPartsOfSpeech)

        // Redirect "Sentence Structure" button to the actual SentenceStructureQuizActivity
        sentenceStructureButton.setOnClickListener {
            startActivity(Intent(this, SentenceStructureQuizActivity::class.java))
        }

        // Redirect "Tenses" button to TensesQuizActivity
        tensesButton.setOnClickListener {
            startActivity(Intent(this, TensesQuizActivity::class.java))
        }

        // Redirect "Subject-Verb Agreement" button to SubjectVerbAgreementQuizActivity
        subjectVerbButton.setOnClickListener {
            startActivity(Intent(this, SubjectVerbAgreementQuizActivity::class.java))
        }

        // Redirect "Active-Passive Voice" button to ActivePassiveVoiceQuizActivity
        activePassiveVoiceButton.setOnClickListener {
            startActivity(Intent(this, ActivePassiveVoiceQuizActivity::class.java))
        }

        // Redirect "Parts of Speech" button to PartsOfSpeechQuizActivity
        partsOfSpeechButton.setOnClickListener {
            startActivity(Intent(this, PartsOfSpeechQuizActivity::class.java))
        }
    }
}
