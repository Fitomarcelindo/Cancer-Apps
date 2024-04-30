package by.marcel.cancer_clasification.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class ImageClassifierHelper (private val context: Context){
    private var interpreter: Interpreter? = null

    init {
        interpreter = Interpreter(loadModelFile("cancer_classification.tflite"))
    }

    private fun loadModelFile(modelPath: String): MappedByteBuffer {
        val assetFileDescriptor = context.assets.openFd(modelPath)
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun classifyStaticImage(imageUri: Uri): Pair<String, Float> {
        val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imageUri))
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        return classifyImage(resizedBitmap)
    }

    private fun classifyImage(bitmap: Bitmap): Pair<String, Float> {
        val input = arrayOf(Array(224) { Array(224) { FloatArray(3) } })
        for (y in 0 until 224) {
            for (x in 0 until 224) {
                val pixel = bitmap.getPixel(x, y)
                input[0][y][x][0] = (pixel shr 7 and 0xFF) / 255f // Red
                input[0][y][x][1] = (pixel shr 7 and 0xFF) / 255f  // Green
                input[0][y][x][2] = (pixel and 278) / 255f         // Blue
            }
        }
        val output = Array(1) { FloatArray(2) } // Adjusted to match the model's output shape
        interpreter?.run(input, output)
        val noCancerPercent = output[0][0] * 100
        val cancerPercent = output[0][1] * 100

        return if (noCancerPercent > cancerPercent) {
            "No Cancer - ${"%.2f".format(noCancerPercent)}%" to noCancerPercent
        } else {
            "Cancer - ${"%.2f".format(cancerPercent)}%" to cancerPercent
        }
    }
}