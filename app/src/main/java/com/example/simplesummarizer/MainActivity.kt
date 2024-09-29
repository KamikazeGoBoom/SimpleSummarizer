package com.example.simplesummarizer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.FileOutputStream
import kotlin.math.max
import org.apache.poi.xwpf.extractor.XWPFWordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper

class MainActivity : AppCompatActivity() {

    private lateinit var editTextInput: EditText
    private lateinit var buttonSummarize: Button
    private lateinit var textViewSummary: TextView
    private lateinit var scrollView: ScrollView
    private lateinit var buttonOpenFile: Button
    private lateinit var buttonSaveSummary: Button

    private val openFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val text = readFileContent(uri)
                editTextInput.setText(text)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextInput = findViewById(R.id.editTextInput)
        buttonSummarize = findViewById(R.id.buttonSummarize)
        textViewSummary = findViewById(R.id.textViewSummary)
        scrollView = findViewById(R.id.scrollView)
        buttonOpenFile = findViewById(R.id.buttonOpenFile)
        buttonSaveSummary = findViewById(R.id.buttonSaveSummary)

        buttonSummarize.setOnClickListener {
            val inputText = editTextInput.text.toString()
            val summary = summarizeText(inputText)
            textViewSummary.text = summary
            scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
        }

        buttonOpenFile.setOnClickListener {
            openFile()
        }

        buttonSaveSummary.setOnClickListener {
            saveSummary()
        }

        editTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                buttonSummarize.isEnabled = !s.isNullOrBlank()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun openFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("text/plain", "application/pdf", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
        }
        openFileLauncher.launch(intent)
    }

    private fun readFileContent(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri)
        return when {
            uri.toString().endsWith(".txt", ignoreCase = true) -> {
                inputStream?.bufferedReader().use { it?.readText() } ?: ""
            }
            uri.toString().endsWith(".pdf", ignoreCase = true) -> {
                val document = PDDocument.load(inputStream)
                val stripper = PDFTextStripper()
                val text = stripper.getText(document)
                document.close()
                text
            }
            uri.toString().endsWith(".docx", ignoreCase = true) -> {
                val document = XWPFDocument(inputStream)
                val extractor = XWPFWordExtractor(document)
                val text = extractor.text
                extractor.close()
                document.close()
                text
            }
            else -> ""
        }
    }

    private fun saveSummary() {
        val summary = textViewSummary.text.toString()
        if (summary.isBlank()) {
            Toast.makeText(this, "No summary to save", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, "summary.txt")
        }
        startActivityForResult(intent, CREATE_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_FILE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val summary = textViewSummary.text.toString()
                contentResolver.openFileDescriptor(uri, "w")?.use { parcelFileDescriptor ->
                    FileOutputStream(parcelFileDescriptor.fileDescriptor).use { fileOutputStream ->
                        fileOutputStream.write(summary.toByteArray())
                    }
                }
                Toast.makeText(this, "Summary saved successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun summarizeText(text: String): String {
        if (text.isBlank()) return ""

        val stopwords = setOf(
            "the", "is", "in", "and", "to", "of", "a", "that", "with", "as",
            "for", "on", "it", "by", "an", "this", "at", "from", "or", "are",
            "was", "were", "be", "has", "had", "have", "not", "but", "if",
            "would", "should", "could", "will", "can", "do", "does", "did",
            "about", "after", "before", "just", "also", "because", "so", "too",
            "there", "some", "more", "very", "much"
        )

        val sentences = text.split(Regex("(?<=\\.)\\s+|(?<=!)\\s+|(?<=\\?)\\s+")).filter { it.isNotBlank() }

        val wordFrequencies = sentences.flatMap { it.toLowerCase().split(Regex("\\s+")) }
            .filter { it !in stopwords }
            .groupingBy { it }
            .eachCount()

        val scoredSentences = sentences.mapIndexed { index, sentence ->
            val words = sentence.toLowerCase().split(Regex("\\s+"))
            val uniqueWords = words.distinct()
            val importantWordsCount = words.filter { it !in stopwords }.size
            val sentenceLength = words.size

            val grammarBonus = calculateGrammarBonus(sentence)

            val score = ((uniqueWords.sumBy { wordFrequencies[it] ?: 0 }) * 2) +
                    importantWordsCount +
                    grammarBonus

            ScoredSentence(index, sentence.trim(), score * sentenceLength)
        }

        val sortedSentences = scoredSentences.sortedByDescending { it.score }

        val numSentencesInSummary = when {
            sentences.size > 30 -> max(3, sentences.size / 5)
            sentences.size > 10 -> max(2, sentences.size / 3)
            else -> sentences.size
        }

        val topSentences = sortedSentences.take(numSentencesInSummary)

        val orderedSummary = topSentences.sortedBy { it.index }

        return orderedSummary.joinToString(". ") { it.text.removeSuffix(".") } + "."
    }

    private fun calculateGrammarBonus(sentence: String): Int {
        var score = 0
        if (sentence.contains(Regex("\\b(and|or|but|because|although|since|if|when)\\b", RegexOption.IGNORE_CASE))) {
            score += 5
        }

        val commaCount = sentence.count { it == ',' }
        if (commaCount > 0) score += 2 * commaCount

        val wordCount = sentence.split(Regex("\\s+")).size
        if (wordCount > 15) score += 5
        if (wordCount < 5) score -= 5

        return score
    }

    private data class ScoredSentence(val index: Int, val text: String, val score: Int)

    companion object {
        private const val CREATE_FILE = 1
    }
}