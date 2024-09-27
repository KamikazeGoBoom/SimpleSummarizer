package com.example.simplesummarizer

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import kotlin.math.max

class MainActivity : Activity() {

    private lateinit var editTextInput: EditText
    private lateinit var buttonSummarize: Button
    private lateinit var textViewSummary: TextView
    private lateinit var scrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextInput = findViewById(R.id.editTextInput)
        buttonSummarize = findViewById(R.id.buttonSummarize)
        textViewSummary = findViewById(R.id.textViewSummary)
        scrollView = findViewById(R.id.scrollView)

        buttonSummarize.setOnClickListener {
            val inputText = editTextInput.text.toString()
            val summary = summarizeText(inputText)
            textViewSummary.text = summary
            scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
        }

        editTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                buttonSummarize.isEnabled = !s.isNullOrBlank()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun summarizeText(text: String): String {
        if (text.isBlank()) return ""

        // Define a more robust stopword list
        val stopwords = setOf(
            "the", "is", "in", "and", "to", "of", "a", "that", "with", "as",
            "for", "on", "it", "by", "an", "this", "at", "from", "or", "are",
            "was", "were", "be", "has", "had", "have", "not", "but", "if",
            "would", "should", "could", "will", "can", "do", "does", "did",
            "about", "after", "before", "just", "also", "because", "so", "too",
            "there", "some", "more", "very", "much"
        )

        // Split text into sentences with improved regex
        val sentences = text.split(Regex("(?<=\\.)\\s+|(?<=!)\\s+|(?<=\\?)\\s+")).filter { it.isNotBlank() }

        // Tokenization, handling grammar structures for advanced scoring
        val wordFrequencies = sentences.flatMap { it.toLowerCase().split(Regex("\\s+")) }
            .filter { it !in stopwords }
            .groupingBy { it }
            .eachCount()

        // Enhanced scoring logic with consideration for grammar quality
        val scoredSentences = sentences.mapIndexed { index, sentence ->
            val words = sentence.toLowerCase().split(Regex("\\s+"))
            val uniqueWords = words.distinct()
            val importantWordsCount = words.filter { it !in stopwords }.size
            val sentenceLength = words.size

            // Check for common grammatical constructs like conjunctions, subordination, etc.
            val grammarBonus = calculateGrammarBonus(sentence)

            // Score sentence by combining word frequency, sentence length, and grammar quality
            val score = ((uniqueWords.sumBy { wordFrequencies[it] ?: 0 }) * 2) +
                    importantWordsCount +
                    grammarBonus

            ScoredSentence(index, sentence.trim(), score * sentenceLength) // Multiply by sentence length
        }

        // Sort sentences by score in descending order
        val sortedSentences = scoredSentences.sortedByDescending { it.score }

        // Logic for handling very long texts more efficiently
        val numSentencesInSummary = when {
            sentences.size > 30 -> max(3, sentences.size / 5)
            sentences.size > 10 -> max(2, sentences.size / 3)
            else -> sentences.size
        }

        // Take top scored sentences
        val topSentences = sortedSentences.take(numSentencesInSummary)

        // Sort by original order for a cohesive summary
        val orderedSummary = topSentences.sortedBy { it.index }

        // Build and return the summary, ensuring proper punctuation
        return orderedSummary.joinToString(". ") { it.text.removeSuffix(".") } + "."
    }

    // Helper function to calculate grammar bonus for each sentence
    private fun calculateGrammarBonus(sentence: String): Int {
        var score = 0
        // Check for use of conjunctions (and, or, but) or subordination (because, although)
        if (sentence.contains(Regex("\\b(and|or|but|because|although|since|if|when)\\b", RegexOption.IGNORE_CASE))) {
            score += 5
        }

        // Check for complex sentence structures (clauses, commas)
        val commaCount = sentence.count { it == ',' }
        if (commaCount > 0) score += 2 * commaCount

        // Check for proper sentence length (more words can indicate more detailed grammar)
        val wordCount = sentence.split(Regex("\\s+")).size
        if (wordCount > 15) score += 5 // Reward longer, more complex sentences

        // Penalty for sentences that are too short (likely to be overly simplistic)
        if (wordCount < 5) score -= 5

        return score
    }

    private data class ScoredSentence(val index: Int, val text: String, val score: Int)
}
