package com.example.parental_control_app.activities.parent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.parental_control_app.managers.SharedPreferencesManager
import com.example.parental_control_app.repositories.users.UserMaturityLevel
import com.example.parental_control_app.screens.parent.ParentChildAppsScreen
import com.example.parental_control_app.viewmodels.parent.ParentChildAppsViewModel
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class ParentChildAppsActivity : AppCompatActivity() {

    private lateinit var parentChildAppsViewModel : ParentChildAppsViewModel
    private var interpreter : Interpreter? = null

    companion object {
        const val modelFile = "model.tflite"
        val contentRatingMap = mapOf(
            UserMaturityLevel.BELOW_AVERAGE.toString() to 0.0F,
            UserMaturityLevel.AVERAGE.toString() to 1.0F,
            UserMaturityLevel.ABOVE_AVERAGE.toString() to 2.0F,
        )
    }

    private fun loadModelFile(modelFilename: String): MappedByteBuffer {
        val assetFileDescriptor = assets.openFd(modelFilename)
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences(SharedPreferencesManager.PREFS_KEY, MODE_PRIVATE)
        val profile = SharedPreferencesManager.getProfile(sharedPreferences)
        val kidProfileId = intent.getStringExtra("kidProfileId")
        val maturityLevel = intent.getStringExtra("maturityLevel")
        val age = intent.getStringExtra("age")

        interpreter = Interpreter(loadModelFile(modelFile))

        val inputTensorIndex = 0 // Replace with the correct index
        val outputTensorIndex = 0 // Replace with the correct index
        val inputShape = interpreter?.getInputTensor(inputTensorIndex)?.shape()!!
        val outputShape = interpreter?.getOutputTensor(outputTensorIndex)?.shape()!!

        // Allocate input and output arrays
        val inputArray = Array(inputShape[0]) { FloatArray(2) }
        val outputArray = Array(outputShape[0]) { FloatArray(outputShape[1]) }

        inputArray[0][0] = age?.toFloat()!!
        inputArray[0][1] = contentRatingMap[maturityLevel]!!

        interpreter?.run(inputArray, outputArray)
        val contentRating = outputArray[0].indexOfFirst { it == outputArray[0].max() }

        if (profile != null) {
            parentChildAppsViewModel = ParentChildAppsViewModel(profile, kidProfileId.toString(), contentRating)
        }

        setContent {
            ParentChildAppsScreen(parentChildAppsViewModel) {
                finish()
            }
        }
    }
}