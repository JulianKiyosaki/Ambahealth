package com.dicoding.asclepius.helper

import android.content.Context
import android.graphics.Bitmap
import com.dicoding.asclepius.ml.CancerClassification
import org.tensorflow.lite.support.image.TensorImage


class ImageClassifierHelper(private val context: Context) {

    private fun setupImageClassifier(): CancerClassification {
        return CancerClassification.newInstance(context)
    // TODO: Menyiapkan Image Classifier untuk memproses gambar.
    }

    fun classifyStaticImage(bitmap: Bitmap): Pair<Boolean, Float> {
        val model = setupImageClassifier()
        val image = TensorImage.fromBitmap(bitmap)
        val outputs = model.process(image)
        val probability = outputs.probabilityAsCategoryList

        val highestProb = probability.maxByOrNull { it.score }

        model.close()

        return if (highestProb != null) {
            Pair(highestProb.label == "Cancer", highestProb.score)
        } else {
            Pair(false, 0.0f)
        }
    }

}