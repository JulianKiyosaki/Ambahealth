package com.dicoding.asclepius.view

import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showSelectedImage(uri)
        } else {
            showToast("Gagal mengambil gambar")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAnimations()
        setupButtonListeners()
    }

    override fun onResume() {
        super.onResume()
        showAnalyzeButton(false)
    }

    override fun onBackPressed() {
        if (binding.analyzeButton.visibility == View.VISIBLE) {
            binding.defaultImageView.setImageResource(R.drawable.ironi)
            binding.defaultImageView.alpha = 1f
            binding.defaultImageView.visibility = View.VISIBLE
            currentImageUri = null

            showAnalyzeButton(false)
        } else {
            super.onBackPressed()
        }
    }

    private fun showSelectedImage(uri: Uri) {
        try {
            binding.defaultImageView.setImageURI(uri)

            binding.defaultImageView.alpha = 0f
            binding.defaultImageView.visibility = View.VISIBLE
            binding.defaultImageView.animate()
                .alpha(1f)
                .setDuration(300)
                .start()

            showAnalyzeButton(true)
        } catch (e: Exception) {
            showToast("Error menampilkan gambar: ${e.message}")
        }
    }


    private fun setupAnimations() {
        binding.quoteTextView.alpha = 0f
        ObjectAnimator.ofFloat(binding.quoteTextView, "alpha", 1f).apply {
            duration = 1000
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        binding.buttonGroup.translationY = 200f
        ObjectAnimator.ofFloat(binding.buttonGroup, "translationY", 0f).apply {
            duration = 1000
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun setupButtonListeners() {
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener { analyzeImage() }
        binding.viewHistoryButton.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }

    private fun showAnalyzeButton(show: Boolean) {
        if (show) {
            binding.galleryButton.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction {
                    binding.galleryButton.visibility = View.GONE
                }
                .start()

            binding.viewHistoryButton.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction {
                    binding.viewHistoryButton.visibility = View.GONE
                    // Show analyze button after others are hidden
                    binding.analyzeButton.visibility = View.VISIBLE
                    binding.analyzeButton.alpha = 0f
                    binding.analyzeButton.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .start()
                }
                .start()
        } else {
            binding.analyzeButton.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction {
                    binding.analyzeButton.visibility = View.GONE
                    binding.galleryButton.visibility = View.VISIBLE
                    binding.viewHistoryButton.visibility = View.VISIBLE
                    binding.galleryButton.alpha = 0f
                    binding.viewHistoryButton.alpha = 0f

                    binding.galleryButton.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .start()
                    binding.viewHistoryButton.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .start()
                }
                .start()
        }
    }

    private fun startGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun analyzeImage() {
        currentImageUri?.let { uri ->
            try {
                binding.progressIndicator.visibility = View.VISIBLE
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                val result = ImageClassifierHelper(this).classifyStaticImage(bitmap)
                binding.progressIndicator.visibility = View.GONE
                moveToResult(result.first, result.second)
            } catch (e: Exception) {
                binding.progressIndicator.visibility = View.GONE
                showToast("Error memproses gambar: ${e.message}")
            }
        } ?: showToast("Silakan pilih gambar terlebih dahulu")
    }

    private fun moveToResult(isCancer: Boolean, confidenceScore: Float) {
        Intent(this, ResultActivity::class.java).apply {
            putExtra("prediction", isCancer)
            putExtra("confidenceScore", confidenceScore)
            putExtra("imageUri", currentImageUri.toString())
            startActivity(this)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
