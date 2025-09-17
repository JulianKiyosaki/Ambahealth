package com.dicoding.asclepius.view

import android.animation.ValueAnimator
import android.net.Uri
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.room.PredictionDatabase
import com.dicoding.asclepius.room.PredictionHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private var prediction: Boolean = false
    private var confidenceScore: Float = 0.0f
    private var imageUri: String? = null
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prediction = intent.getBooleanExtra("prediction", false)
        confidenceScore = intent.getFloatExtra("confidenceScore", 0.0f)
        imageUri = intent.getStringExtra("imageUri")

        displayResult()
        binding.saveButton.setOnClickListener {
            toggleFavorite()
        }
    }

    private fun displayResult() {
        imageUri?.let {
            binding.resultImage.setImageURI(Uri.parse(it))
        }

        val confidencePercentage = (confidenceScore * 100).toInt()
        val resultText = if (prediction) {
            "Cancer"
        } else {
            "No Cancer"
        }
        binding.resultText.text = resultText

        val explanationText = if (prediction) {
            "The image indicates a high likelihood of cancer. Please consult a medical professional immediately."
        } else {
            "The image analysis shows no signs of cancer. However, regular check-ups are still recommended."
        }
        binding.resultExplanation.text = explanationText

        animateProgressBar(confidencePercentage)
    }

    private fun animateProgressBar(targetPercentage: Int) {
        binding.confidenceBar.progress = 0
        binding.confidenceText.text = "0%"

        val animator = ValueAnimator.ofInt(0, targetPercentage)
        animator.duration = 1500
        animator.interpolator = AccelerateDecelerateInterpolator()

        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Int
            binding.confidenceBar.progress = progress
            binding.confidenceText.text = "$progress%"

            updateProgressBarColor(progress)
        }

        animator.start()
    }

    private fun updateProgressBarColor(progress: Int) {
        val color = when {
            progress < 30 -> getColor(R.color.white)
            progress < 70 -> getColor(R.color.colorPrimary)
            else -> getColor(R.color.tint_danger)
        }

        binding.confidenceBar.setIndicatorColor(color)
        binding.confidenceText.setTextColor(color)
    }

    private fun toggleFavorite() {
        isFavorite = !isFavorite
        if (isFavorite) {
            binding.saveButton.setImageResource(R.drawable.fav)
            savePredictionHistory()
        } else {
            binding.saveButton.setImageResource(R.drawable.fav_border)
            deletePredictionHistory()
        }
    }

    private fun savePredictionHistory() {
        imageUri?.let { uri ->
            val history = PredictionHistory(
                imageUri = uri,
                prediction = prediction,
                confidenceScore = confidenceScore,
                createdAt = System.currentTimeMillis()
            )

            lifecycleScope.launch(Dispatchers.IO) {
                PredictionDatabase.getDatabase(applicationContext)
                    .predictionHistoryDao()
                    .insertHistory(history)
                runOnUiThread {
                    Toast.makeText(this@ResultActivity, "History saved successfully", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deletePredictionHistory() {
        imageUri?.let { uri ->
            lifecycleScope.launch(Dispatchers.IO) {
                PredictionDatabase.getDatabase(applicationContext)
                    .predictionHistoryDao()
                    .deleteHistoryByImageUri(uri)
                runOnUiThread {
                    Toast.makeText(this@ResultActivity, "History deleted successfully", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}