package by.marcel.cancer_clasification.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import by.marcel.cancer_clasification.R
import by.marcel.cancer_clasification.databinding.ActivityMainBinding
import by.marcel.cancer_clasification.helper.ImageClassifierHelper
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding : ActivityMainBinding

    private lateinit var imageClassifier: ImageClassifierHelper

    private var currentImageUri: Uri? = null
    private var croppedImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Permission request granted")
            } else {
                showToast("Permission request denied")
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        imageClassifier = ImageClassifierHelper(this)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        mainBinding.galleryButton.setOnClickListener { startGallery() }
        mainBinding.analyzeButton.setOnClickListener {
            currentImageUri?.let {
                analyzeImage(it)
            } ?: run {
                showToast(getString(R.string.notfound))
            }
        }
        mainBinding.historyBtn.setOnClickListener {
            val intent = Intent(this, ReadDataActivity::class.java)
            startActivity(intent)
        }

    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            ImageCrop(uri)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun ImageCrop(uri: Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1920, 1080)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(this)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // TODO: Crop Image Kemudian Menampilkan gambar sesuai Gallery yang dipilih.
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                croppedImageUri = result.uri
                showImage(croppedImageUri!!)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Log.e("Crop Error", "Error cropping image: $error")
            }
        }
    }

    private fun showImage(uri: Uri) {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            mainBinding.previewImageView.setImageURI(uri)
        }
    }

    private fun analyzeImage(uri: Uri) {
        croppedImageUri?.let { croppedUri ->
            val result = imageClassifier.classifyStaticImage(croppedUri)
            val confidenceScore = result.second
            val intent = Intent(this, ResultActivity::class.java).apply {
                putExtra(ResultActivity.EXTRA_IMAGE_URI, croppedUri.toString())
                putExtra(ResultActivity.EXTRA_RESULT, result.first)
                putExtra(ResultActivity.EXTRA_CONFIDENCE_SCORE, confidenceScore)
            }
            startActivity(intent)
        } ?: run {
            showToast("Image not found or crop not successful.")
        }
    }




    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}