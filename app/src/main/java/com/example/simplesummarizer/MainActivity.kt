package com.example.simplesummarizer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import kotlin.math.log2

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
                if (text.isNotEmpty()) {
                    editTextInput.setText(text)
                } else {
                    Toast.makeText(this, "Failed to read the file or unsupported format.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val saveFileLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri ->
        uri?.let {
            saveSummaryToFile(it)
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
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
                "text/plain",
                "application/pdf",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            ))
        }
        openFileLauncher.launch(intent)
    }

    private fun readFileContent(uri: Uri): String {
        val mimeType = contentResolver.getType(uri)
        Log.d("MainActivity", "Selected URI: $uri, MIME Type: $mimeType")
        val inputStream = contentResolver.openInputStream(uri)
        if (inputStream == null) {
            Log.e("MainActivity", "Unable to open input stream for URI: $uri")
            Toast.makeText(this, "Unable to open file.", Toast.LENGTH_SHORT).show()
            return ""
        }

        return when (mimeType) {
            "text/plain" -> {
                inputStream.bufferedReader().use { it.readText() }
            }
            "application/pdf" -> {
                try {
                    val document = PDDocument.load(inputStream)
                    val stripper = PDFTextStripper()
                    val text = stripper.getText(document)
                    document.close()
                    text
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error reading PDF file: ${e.message}")
                    Toast.makeText(this, "Error reading PDF file.", Toast.LENGTH_SHORT).show()
                    ""
                }
            }
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> {
                try {
                    val document = XWPFDocument(inputStream)
                    val extractor = XWPFWordExtractor(document)
                    val text = extractor.text
                    extractor.close()
                    document.close()
                    text
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error reading DOCX file: ${e.message}")
                    Toast.makeText(this, "Error reading Word document.", Toast.LENGTH_SHORT).show()
                    ""
                }
            }
            else -> {
                Toast.makeText(this, "Unsupported file type.", Toast.LENGTH_SHORT).show()
                ""
            }
        }
    }

    private fun saveSummary() {
        val summary = textViewSummary.text.toString()
        if (summary.isBlank()) {
            Toast.makeText(this, "No summary to save.", Toast.LENGTH_SHORT).show()
            return
        }

        saveFileLauncher.launch("summary.txt")
    }

    private fun saveSummaryToFile(uri: Uri) {
        val summary = textViewSummary.text.toString()
        try {
            contentResolver.openFileDescriptor(uri, "w")?.use { parcelFileDescriptor ->
                FileOutputStream(parcelFileDescriptor.fileDescriptor).use { fileOutputStream ->
                    fileOutputStream.write(summary.toByteArray())
                }
            }
            Toast.makeText(this, "Summary saved successfully.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error saving summary: ${e.message}")
            Toast.makeText(this, "Failed to save summary.", Toast.LENGTH_SHORT).show()
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

        val sentences = text.split(Regex("(?<=[.!?])\\s+")).filter { it.isNotBlank() }
        val wordFrequencies = calculateWordFrequencies(sentences, stopwords)
        val scoredSentences = scoreSentences(sentences, wordFrequencies, stopwords)

        val numSentencesInSummary = when {
            sentences.size > 30 -> max(3, sentences.size / 5)
            sentences.size > 10 -> max(2, sentences.size / 3)
            else -> sentences.size
        }

        val topSentences = scoredSentences.sortedByDescending { it.score }.take(numSentencesInSummary)
        val orderedSummary = topSentences.sortedBy { it.index }

        return orderedSummary.joinToString(". ") { it.text.removeSuffix(".") } + "."
    }

    private fun calculateWordFrequencies(sentences: List<String>, stopwords: Set<String>): Map<String, Double> {
        val wordCounts = sentences.flatMap { it.toLowerCase().split(Regex("\\s+")) }
            .filter { it !in stopwords }
            .groupingBy { it }
            .eachCount()

        val totalWords = wordCounts.values.sum().toDouble()
        return wordCounts.mapValues { (_, count) -> count.toDouble() / totalWords }
    }

    private fun scoreSentences(sentences: List<String>, wordFrequencies: Map<String, Double>, stopwords: Set<String>): List<ScoredSentence> {
        val totalSentences = sentences.size.toDouble()

        return sentences.mapIndexed { index, sentence ->
            val words = sentence.toLowerCase().split(Regex("\\s+"))
            val uniqueWords = words.filter { it !in stopwords }.distinct()

            val tfIdfScore = calculateTfIdfScore(uniqueWords, wordFrequencies, totalSentences)
            val positionScore = calculatePositionScore(index, sentences.size)
            val lengthScore = calculateLengthScore(words.size)
            val keyPhraseScore = calculateKeyPhraseScore(sentence)

            val score = (tfIdfScore * 0.4) + (positionScore * 0.3) + (lengthScore * 0.2) + (keyPhraseScore * 0.1)

            ScoredSentence(index, sentence.trim(), score)
        }
    }

    private fun calculateTfIdfScore(words: List<String>, wordFrequencies: Map<String, Double>, totalSentences: Double): Double {
        return words.sumOf { word ->
            val tf = wordFrequencies[word] ?: 0.0
            val idf = log2(totalSentences / (wordFrequencies[word]?.let { 1.0 } ?: 0.0))
            tf * idf
        }
    }

    private fun calculatePositionScore(index: Int, totalSentences: Int): Double {
        return when {
            index == 0 || index == totalSentences - 1 -> 1.0
            index < totalSentences / 3 -> 0.8
            index > (2 * totalSentences) / 3 -> 0.6
            else -> 0.4
        }
    }

    private fun calculateLengthScore(wordCount: Int): Double {
        return when {
            wordCount < 5 -> 0.1
            wordCount < 10 -> 0.3
            wordCount < 20 -> 0.7
            wordCount < 30 -> 1.0
            else -> 0.8
        }
    }

    private fun calculateKeyPhraseScore(sentence: String): Double {
        val keyPhrases = listOf(
            "in conclusion", "to summarize", "in summary", "in essence", "to sum up",
            "therefore", "thus", "consequently", "as a result", "hence",
            "importantly", "significantly", "notably", "in particular", "especially",
            "for example", "for instance", "such as", "specifically", "namely"
        )

        return keyPhrases.count { sentence.toLowerCase().contains(it) }.toDouble() * 0.5
    }

    private data class ScoredSentence(val index: Int, val text: String, val score: Double)
}