package com.shamim.frremoteattendence.face_detection

import android.annotation.SuppressLint
import android.app.Activity
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
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.shamim.frremoteattendence.LocationService.LocationService
import com.shamim.frremoteattendence.alert.CustomDialog
import com.shamim.frremoteattendence.camerax.BaseImageAnalyzer
import com.shamim.frremoteattendence.camerax.GraphicOverlay
import com.shamim.frremoteattendence.sharedpreference.FR_sharedpreference
import com.shamim.frremoteattendence.utils.Encode_and_DecodeBase64Image
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Random

@SuppressLint("UnsafeOptInUsageError")
class FaceContourDetectionProcessor(
    private val view: GraphicOverlay, private val context: Context, private val imageView: ImageView
) : BaseImageAnalyzer<List<Face>>() {

    private var handler: Handler = Handler(Looper.getMainLooper())
    private var isImageCaptured = false
    private var countDownStarted = false
    private var takePhoto = false
    var prevLeftEyeOpen = true
    var prevRightEyeOpen = true
    var leftEyeClosed = false
    var rightEyeClosed = false
    var blinkCount = 0
    val customDialog = CustomDialog(context as Activity?)
    private var imageCaptureListener: ImageCaptureListener? = null


    interface ImageCaptureListener {
        fun onImageCaptured(encodeImage: String)
    }

    // Add a property to hold the listener

    // Setter method for the listener
    fun setImageCaptureListener(listener: ImageCaptureListener) {
        imageCaptureListener = listener
    }


//    private val realTimeOpts = FaceDetectorOptions.Builder()
//        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
//        .setContourMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
//        .setLandmarkMode( FaceDetectorOptions.LANDMARK_MODE_NONE)
//
//        .enableTracking()
//        .setMinFaceSize(0.35f)
//        .build()


    private var optionsBuilder =
        FaceDetectorOptions.Builder().setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.35f).enableTracking().build()


    private val detector = FaceDetection.getClient(optionsBuilder)
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
    override fun onSuccess(
        faces: List<Face>,
        graphicOverlay: GraphicOverlay,
        rect: Rect,
        image: Image,
        rotationDregree: Int
    ) {

        graphicOverlay.clear()
        if (faces.isNotEmpty()) {
            for (face in faces) {
                if (faces.size == 1)
                {
                    Log.d(TAG,"Single_FaceDetected")
                    val singleFace = faces[0] // Get the first detected face
                    val faceGraphic = FaceContourGraphic(graphicOverlay, singleFace, rect)
                    graphicOverlay.add(faceGraphic)
                    // Add a 2-second delay using a Handler

                    if (!countDownStarted) {
                        if (LocationService.location != null) {
                            val locationCheck = LocationService.checkLocationArea(
                                LocationService.location.latitude, LocationService.location.longitude
                            )
                            if (locationCheck) {
                                Log.d(TAG, "Attendance Location Match:")
                                val leftEyeOpenProbability = face.leftEyeOpenProbability
                                val rightEyeOpenProbability = face.rightEyeOpenProbability
                                // Check if both left and right eyes are open (initial state)
                                if (leftEyeOpenProbability != null && rightEyeOpenProbability != null) {
                                    val isLeftEyeOpen = leftEyeOpenProbability > 0.3f
                                    val isRightEyeOpen = rightEyeOpenProbability > 0.3f
                                    // Detect a blink when both eyes transition from closed to open
                                    if (!isLeftEyeOpen && prevLeftEyeOpen && !isRightEyeOpen && prevRightEyeOpen) {
                                        // Blink detected
                                        leftEyeClosed = true
                                        rightEyeClosed = true
                                    }
                                    // Check if both eyes transition from closed to open again
                                    if (isLeftEyeOpen && !prevLeftEyeOpen && isRightEyeOpen && !prevRightEyeOpen && leftEyeClosed && rightEyeClosed) {
                                        // Blink completed
                                        blinkCount++
                                        leftEyeClosed = false
                                        rightEyeClosed = false
                                    }
                                    // Update previous eye states
                                    prevLeftEyeOpen = isLeftEyeOpen
                                    prevRightEyeOpen = isRightEyeOpen
                                }
                                if (blinkCount == 2) {
                                    customDialog.startLoading("Take Picture")
                                    Log.d(TAG, "Blink detected $blinkCount times")
                                    countDownStarted = true
                                    Log.d(TAG, "onSuccess: inside if condition")
                                    // Add a 2-second delay using a Handler
                                    countDownStarted = false
                                    takePhoto = true
                                    isImageCaptured = false
                                    blinkCount = 0
                                    Log.d(TAG, "onSuccess: 5 seconds passed")
                                    // 2-second delay
                                }
                            }
                            //AttendanceLocation not match
                            else
                            {

                            }
                        }

                    }
                    if (!isImageCaptured && takePhoto) {
                        if (faces.isNotEmpty()) {
                            isImageCaptured = true
                            takePhoto = false
                            Log.d(TAG, "onSuccess:>>Capturing image")
                            Log.d(TAG, "onSuccessImage:$image ")
                            val b = toBitmap(image)
                            val rotatedBitmap = b?.let { rotateBitmap(it, rotationDregree.toFloat()) }
                            val faceRect = face.boundingBox
                            // Capture and save the image
                            val faceImage:Bitmap?=faceBitmap(rotatedBitmap, faceRect)
                            saveCapturedImage(faceImage)
                            // Display the image on the ImageView
                            imageView.setImageBitmap(faceImage)
                            // Set the flag to true to prevent multiple captures
                             val encodeImage=Encode_and_DecodeBase64Image.encodeBitmapImage(faceImage)
                            Log.d(TAG, "EncodeImage=$encodeImage")
                            imageCaptureListener?.onImageCaptured(encodeImage)

                            FR_sharedpreference.SaveBitmap(context,encodeImage)


                        }
                    }
                }
                else
                {
                    if (faces.size >1){
                        Log.d(TAG,"Multi_FaceDetected")
                        Toast.makeText(context, "Please Give Single Face", Toast.LENGTH_SHORT).show()

                    }
                }

            }
        } else {
            countDownStarted = false
            isImageCaptured = false
        }
        graphicOverlay.postInvalidate()
    }

    fun faceBitmap(captureImage: Bitmap?, faceRect: Rect): Bitmap? {
        if (captureImage != null) {
            // Ensure that the faceRect is within the bounds of the captureImage
            if (faceRect.left >= 0 && faceRect.top >= 0 && faceRect.right <= captureImage.width && faceRect.bottom <= captureImage.height) {
                return Bitmap.createBitmap(
                    captureImage, faceRect.left, faceRect.top, faceRect.width(), faceRect.height()
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

        if (capturedBitmap != null) {
            val rand = Random()
            val randNo = rand.nextInt()
            val bytes = ByteArrayOutputStream()
            capturedBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path: String = MediaStore.Images.Media.insertImage(
                context.contentResolver, capturedBitmap, randNo.toString(), null
            )
            isImageCaptured = false
        } else {

        }


    }

    override fun onFailure(e: Exception) {
        Log.w(TAG, "Face Detector failed.$e")
    }

    companion object {
        private const val TAG = "FaceDetectorProcessor"
    }

}