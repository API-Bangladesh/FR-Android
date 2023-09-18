package com.shamim.frremoteattendence.camerax

import android.annotation.SuppressLint
import android.graphics.Rect
import android.media.Image
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage

@SuppressLint("UnsafeExperimentalUsageError")
abstract class BaseImageAnalyzer<T>(
    private val captureDelayMillis: Long = 2000 // Delay in milliseconds before capturing
) : ImageAnalysis.Analyzer {

    abstract val graphicOverlay: GraphicOverlay
    private val mainHandler = Handler(Looper.getMainLooper())
    private val backgroundHandler = Handler()

    @OptIn(ExperimentalGetImage::class) override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        mediaImage?.let {
            detectInImage(InputImage.fromMediaImage(it, rotationDegrees))
                .addOnSuccessListener { results ->
                    onSuccess(
                        results,
                        graphicOverlay,
                        it.cropRect,
                        mediaImage,
                        rotationDegrees
                    )
                    imageProxy.close()
                }
                .addOnFailureListener {
                    onFailure(it)
                    imageProxy.close()
                }
        }

        // Add a delay before capturing the image
        mainHandler.postDelayed({
            captureImage(imageProxy)
        }, captureDelayMillis)
    }

    protected abstract fun detectInImage(image: InputImage): Task<T>

    abstract fun stop()

    protected abstract fun onSuccess(
        results: T,
        graphicOverlay: GraphicOverlay,
        rect: Rect,
        mediaImage: Image,
        rotationDegree: Int
    )

    protected abstract fun onFailure(e: Exception)

    private fun captureImage(imageProxy: ImageProxy) {
        // Implement image capture logic here
        // For example, you can capture and process the image
        // Remember to close the imageProxy when you're done
        // You can also override this method in your concrete analyzer class
    }
}
