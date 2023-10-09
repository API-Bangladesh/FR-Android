package com.shamim.frremoteattendence.face_detection

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.shamim.frremoteattendence.LocationService.LocationService
import com.shamim.frremoteattendence.R
import com.shamim.frremoteattendence.alert.CustomDialog_notification
import com.shamim.frremoteattendence.camerax.BaseImageAnalyzer
import com.shamim.frremoteattendence.camerax.GraphicOverlay
import com.shamim.frremoteattendence.fragment.LivePreview_Camera.Companion.imageCaptureChecked
import com.shamim.frremoteattendence.interfaces.OnFaceDetectedListener
import com.shamim.frremoteattendence.utils.Encode_and_DecodeBase64Image
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Calendar


@SuppressLint("UnsafeOptInUsageError")
class FaceContourDetectionProcessor(
    private val view: GraphicOverlay,
    private val context: Context,
    private val imageView: ImageView,
    private val faceEncodeImage: OnFaceDetectedListener,
    private val singleface:TextView
) : BaseImageAnalyzer<List<Face>>() {

    private val CHANNEL_ID = "my_channel_id"
    private var handler: Handler = Handler(Looper.getMainLooper())
    private var prevLeftEyeOpen = true
    private var prevRightEyeOpen = true
    private var leftEyeClosed = false
    private var rightEyeClosed = false
    private var blinkCount = 0

    private var lastCaptureTime: Long = 0
    val currentTimeMillis = System.currentTimeMillis()
    private val morningStartTimeMillis: Long = getMillisFromHourAndMinute(8, 0)
    private val morningEndTimeMillis: Long = getMillisFromHourAndMinute(10, 0)
    private val eveningStartTimeMillis: Long = getMillisFromHourAndMinute(17, 0)
    private val eveningEndTimeMillis: Long = getMillisFromHourAndMinute(18, 0)


    private var optionsBuilder = FaceDetectorOptions.Builder().setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
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

    @SuppressLint("SuspiciousIndentation", "SetTextI18n")
    override fun onSuccess(results: List<Face>, graphicOverlay: GraphicOverlay, rect: Rect, mediaImage: Image, rotationDegree: Int
    ) {
        graphicOverlay.clear()
        if (results.isNotEmpty()) {
            for (face in results) {
                if (results.size == 1) {
                    singleface.visibility=View.VISIBLE
                    singleface.setText("Blink twice\nদুবার পলক ফেলুন!")

                    val singleFace = results[0] // Get the first detected face
                    val faceGraphic = FaceContourGraphic(graphicOverlay, singleFace, rect)
                    graphicOverlay.add(faceGraphic)
                    if (LocationService.location != null) {
                        val locationCheck = LocationService.checkLocationArea(
                            LocationService.location.latitude, LocationService.location.longitude)
                        if (locationCheck) {
                            if (!imageCaptureChecked) {
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
                                        Log.d(TAG, "blinkCount: $blinkCount")
                                        leftEyeClosed = false
                                        rightEyeClosed = false
                                    }
                                    // Update previous eye states
                                    prevLeftEyeOpen = isLeftEyeOpen
                                    prevRightEyeOpen = isRightEyeOpen
                                }
                                if (blinkCount == 2) {
                                    if (results.isNotEmpty()) {
                                        blinkCount = 0

                                        if (isTimeInRange(currentTimeMillis, morningStartTimeMillis, morningEndTimeMillis) ||
                                            isTimeInRange(currentTimeMillis, eveningStartTimeMillis, eveningEndTimeMillis))

                                        {

                                        }
                                        else
                                        {
//                                            val customDialog = CustomDialog_notification(context)
//
//
//                                            val imageIconNF = BitmapFactory.decodeResource(
//                                                context.resources,
//                                                R.mipmap.ic_launcher
//                                            )
//
//                                            val intent = Intent(context, CustomDialog_notification::class.java)
//                                            val pendingIntent = PendingIntent.getActivity(
//                                                context,
//                                                /*notificationId*/ 1,
//                                                intent,
//                                                PendingIntent.FLAG_UPDATE_CURRENT
//                                            )
//
//                                            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//                                            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
//                                                .setContentTitle("Image Captured")
//                                                .setContentText("Image capture successful!")
//                                                .setStyle(NotificationCompat.BigTextStyle())
//                                                .setSmallIcon(R.drawable.notification_ic)
//                                                .setAutoCancel(true) // Close the notification when tapped
//                                                .setLargeIcon(imageIconNF)
//                                                .setContentIntent(pendingIntent) // Set the PendingIntent here
//                                                .build()
//
//                                            // Show the notification
//                                            notificationManager.notify(/*notificationId*/1, notification)
//                                            customDialog.show()
//
//                                        }
//
//                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                            val channel = NotificationChannel(
//                                                CHANNEL_ID,
//                                                "My Notification Channel",
//                                                NotificationManager.IMPORTANCE_DEFAULT
//                                            )
//                                            val notificationManager =context.getSystemService(NotificationManager::class.java)
//                                            notificationManager.createNotificationChannel(channel)
                                        }














                                        val bitmapImage = toBitmap(mediaImage)
                                            val rotatedBitmap = bitmapImage?.let {
                                                rotateBitmap(it, rotationDegree.toFloat()) }
                                            val faceRect = face.boundingBox
                                            val faceImage: Bitmap? = faceBitmap(rotatedBitmap, faceRect)
//                                        saveCapturedImage(faceImage)
                                        imageView.setImageBitmap(faceImage)
                                            val encodeImage = Encode_and_DecodeBase64Image.encodeBitmapImage(faceImage)

                                        if (encodeImage!=null)
                                        {
                                            imageCaptureChecked=true
                                            faceEncodeImage.onFaceDetected(encodeImage)

                                        }
                                        else
                                        {
                                            imageCaptureChecked=false
                                        }

                                    } else {
                                        Toast.makeText(
                                            context, "Please give Your Face", Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    //if Blink Count is 3, it will be 0
                                    if (blinkCount > 2) {
                                        blinkCount = 0
                                    }
                                }
                            }
                            }
                        else {
                            faceEncodeImage.onFaceDetectedListener = true
                        }
                    }
                }
                else {

                    singleface.visibility = View.VISIBLE
                    singleface.text = "Please give Single Face"

                }
            }
        }
        else
        {
            imageCaptureChecked=false
            singleface.visibility = View.GONE

        }
        graphicOverlay.postInvalidate()
    }

    private fun toBitmap(image: Image): Bitmap? {
        val bitmap:Bitmap?
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
        bitmap= BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        return bitmap
    }
    private fun faceBitmap(captureImage: Bitmap?, faceRect: Rect): Bitmap? {
        if (captureImage != null) {
            if (faceRect.left >= 0 && faceRect.top >= 0 && faceRect.right <= captureImage.width && faceRect.bottom <= captureImage.height) {
                return Bitmap.createBitmap(captureImage, faceRect.left, faceRect.top, faceRect.width(), faceRect.height()
                )
            } else {
               imageCaptureChecked=false
                Toast.makeText(context, "Please Give your Face Properly", Toast.LENGTH_SHORT).show()
            }
        }
        else
        {
            imageCaptureChecked=false
        }
        return null
    }
    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

//    private fun saveCapturedImage(capturedBitmap: Bitmap?) {
//
//        if (capturedBitmap != null) {
//            val rand = Random()
//            val randNo = rand.nextInt()
//            val bytes = ByteArrayOutputStream()
//            capturedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
//             insertImage(context.contentResolver, capturedBitmap, randNo.toString(), null)
//        } else
//        {
//            imageCaptureChecked =false
//        }
//    }
    override fun onFailure(e: Exception) {
        Log.w(TAG, "Face Detector failed.$e")
    }
    companion object {
        private const val TAG = "FaceDetectorProcessor"
    }
    private fun isTimeInRange(currentTimeMillis: Long, startTimeMillis: Long, endTimeMillis: Long): Boolean {
        return currentTimeMillis in startTimeMillis until endTimeMillis
    }

    private fun getMillisFromHourAndMinute(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

}