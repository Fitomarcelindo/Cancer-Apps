package by.marcel.cancer_clasification.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import by.marcel.cancer_clasification.R
import by.marcel.cancer_clasification.databinding.ActivityResultBinding
import by.marcel.cancer_clasification.helper.SQLiteHelper

class ResultActivity : AppCompatActivity() {
    private lateinit var resultBinding: ActivityResultBinding
    private lateinit var currentImageUri: Uri
    private lateinit var sqliteHelper: SQLiteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultBinding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(resultBinding.root)

        sqliteHelper = SQLiteHelper(this)

        intent?.getStringExtra(EXTRA_IMAGE_URI)?.let { uriString ->
            currentImageUri = Uri.parse(uriString)
            resultBinding.resultImage.setImageURI(currentImageUri)
        }

        val resultText = intent.getStringExtra(EXTRA_RESULT)
        resultText?.let {
            resultBinding.resultText.text = it
        }

        resultBinding.resultText.setOnClickListener {
            val searchQuery = Uri.encode(resultBinding.resultText.text.toString())
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=Penyakit $searchQuery"))
            startActivity(intent)
        }
        resultBinding.btnsave.setOnClickListener {
            savePredictionToDatabase()
        }
    }


    private fun savePredictionToDatabase() {
        val predictionResult = resultBinding.resultText.text.toString()
        val confidenceScore = intent.getFloatExtra(EXTRA_CONFIDENCE_SCORE, 0f)

        @Suppress("DEPRECATION") val imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, currentImageUri)

        val insertedRowId = sqliteHelper.insertPrediction(imageBitmap, predictionResult, confidenceScore)

        if (insertedRowId != -1L) {
            showSuccessDialog()
        } else {
            showErrorDialog()
        }
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Success")
            .setMessage("Analysis saved successfully")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showErrorDialog() {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage("Failed to save analysis")
            .setPositiveButton("OK", null)
            .show()
    }


    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
        const val EXTRA_CONFIDENCE_SCORE = "extra_confidence_score"

    }
}