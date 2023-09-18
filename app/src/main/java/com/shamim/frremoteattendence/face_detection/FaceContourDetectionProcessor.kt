package com.shamim.frremoteattendence.face_detection
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.shamim.frremoteattendence.camerax.BaseImageAnalyzer
import com.shamim.frremoteattendence.camerax.GraphicOverlay
import com.shamim.frremoteattendence.interfaces.OnFaceDetectedListener
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Random

@Suppress("UNREACHABLE_CODE")
@SuppressLint("UnsafeOptInUsageError")
class FaceContourDetectionProcessor(
    private val view: GraphicOverlay,
    private val context: Context,
    private  val imageView: ImageView
) :
    BaseImageAnalyzer<List<Face>>() {
   private val uploadimageCheck = false
    private val isPhotoDetected = false
    private  var  handler: Handler=Handler(Looper.getMainLooper())
    private var isImageCaptured = false
    private val faceDetectedListener: OnFaceDetectedListener? = null


    private val realTimeOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .build()

    private val detector = FaceDetection.getClient(realTimeOpts)
    override val graphicOverlay: GraphicOverlay = view

    override fun detectInImage(image: InputImage): Task<List<Face>> {
        return detector.process(image)
    }

    override fun stop() {
        try {
            detector.close()
        } catch (e: IOException) {
            Log.e(TAG, "Exception thrown while trying to close Face Detector: $e")
        }
    }


    @SuppressLint("SuspiciousIndentation")
    override fun onSuccess(results: List<Face>, graphicOverlay: GraphicOverlay, rect: Rect, image: Image, rotationDregree:Int
    ) {
        graphicOverlay.clear()
        for (face in results) {
            val faceGraphic = FaceContourGraphic(graphicOverlay, face, rect)
            graphicOverlay.add(faceGraphic)

            // Add a 2-second delay using a Handler
            handler.postDelayed({
                if (!isImageCaptured) {
                    isImageCaptured = true
                    Log.d(TAG, "onSuccessImage:$image ")
                    val b = toBitmap(image)
                    val rotatedBitmap = b?.let { rotateBitmap(it,rotationDregree.toFloat()) }
                    val faceRect = face.boundingBox

                    // Capture and save the image
                    saveCapturedImage(faceBitmap(rotatedBitmap, faceRect))

                    // Display the image on the ImageView
                    imageView.setImageBitmap(faceBitmap(rotatedBitmap, faceRect))

                    // Set the flag to true to prevent multiple captures

                }

            }, 2000) // 2-second delay
        }
        graphicOverlay.postInvalidate()
    }




    fun faceBitmap(captureImage: Bitmap?, faceRect: Rect): Bitmap? {
        if (captureImage != null) {
            // Ensure that the faceRect is within the bounds of the captureImage
            if (faceRect.left >= 0 && faceRect.top >= 0 && faceRect.right <= captureImage.width && faceRect.bottom <= captureImage.height) {
                return Bitmap.createBitmap(
                    captureImage,
                    faceRect.left,
                    faceRect.top,
                    faceRect.width(),
                    faceRect.height()
                )
            }
        }
        return null
    }

    fun getImageFormat(image: Image): String {
        val format = image.format
        return when (format) {
            ImageFormat.YUV_420_888 -> "YUV_420_888"
            ImageFormat.JPEG -> "JPEG"
            ImageFormat.NV21 -> "NV21"
            // Add more cases for other formats as needed
            else -> "Unknown Format"
        }
    }
    private fun toBitmap(image: Image): Bitmap? {
        Log.d(TAG, "toBitmap: $image")
        val planes = image.planes
        val yBuffer = planes[0].buffer
        val uBuffer = planes[1].buffer
        val vBuffer = planes[2].buffer
        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()
        val nv21 = ByteArray(ySize + uSize + vSize)
        //U and V are swapped
        yBuffer[nv21, 0, ySize]
        vBuffer[nv21, ySize, vSize]
        uBuffer[nv21, ySize + vSize, uSize]
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun saveCapturedImage(capturedBitmap: Bitmap?) {

        if (capturedBitmap !=null){
            val rand = Random()
            val randNo = rand.nextInt()
            val bytes = ByteArrayOutputStream()
            capturedBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path: String = MediaStore.Images.Media.insertImage(
                context.contentResolver,
                capturedBitmap,
                randNo.toString(),
                null
            )
            isImageCaptured =false
        }
        else
        {

        }


    }

    override fun onFailure(e: Exception) {
        Log.w(TAG, "Face Detector failed.$e")
    }

    companion object {
        private const val TAG = "FaceDetectorProcessor"
    }

}