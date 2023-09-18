package com.shamim.frremoteattendence.fragment

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.camera.view.PreviewView
import androidx.fragment.app.Fragment
import com.shamim.frremoteattendence.LocationService.LocationService
import com.shamim.frremoteattendence.R
import com.shamim.frremoteattendence.camerax.CameraManager
import com.shamim.frremoteattendence.camerax.GraphicOverlay
import com.shamim.frremoteattendence.face_detection.FaceContourDetectionProcessor
import com.shamim.frremoteattendence.interfaces.InternetCheck
import com.shamim.frremoteattendence.interfaces.NetworkQualityCallback
import com.shamim.frremoteattendence.permission.Permission
import com.shamim.frremoteattendence.utils.InternetCheck_Class
import java.io.IOException
import java.net.InetAddress

class LivePreview_Camera : Fragment(), InternetCheck , NetworkQualityCallback {
    private val TAG = "LivePreviewFragment"
    private var service: Intent? = null
    private val url_img = "https://k7ch2z3we1.execute-api.ca-central-1.amazonaws.com/prod/APILimited"
    private lateinit var cameraManager: CameraManager
    private lateinit var faceDetection: FaceContourDetectionProcessor

    private lateinit var btnSwitch: Button;
    private  lateinit var graphicOverlay_finder: GraphicOverlay
    private lateinit var previewView_finder: PreviewView
    private lateinit var imageView:ImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_camera_fragent, container, false)

        previewView_finder= view.findViewById<PreviewView>(R.id.previewView_finder)
        graphicOverlay_finder= view.findViewById<GraphicOverlay>(R.id.graphicOverlay_finder)
         imageView= view.findViewById<ImageView>(R.id.imageView)


        checkInternet()

    return  view
    }

    private fun checkInternet() {
        if (InternetCheck_Class.isNetworkConnected(context)) {
            NetworkQualityTask(this)
                .execute()
            service = Intent(context, LocationService::class.java)
            Permission.CheckGps(activity)
            requireContext().startService(service)
            createCameraManager()


        } else {
//            com.shamim.apifacedetector.fragment.LivePreviewFragment.uploadimageCheck = true
//            isPhotoDetected = false
            InternetCheck_Class.openInternetDialog(this, true, context)
        }
    }

    private fun createCameraManager() {
        cameraManager =CameraManager(
            requireContext(), previewView_finder,
            requireActivity(),
            graphicOverlay_finder
            ,
            imageView
        )
        cameraManager.startCamera()
    }

    private fun onClicks() {
        btnSwitch= requireActivity().findViewById<Button>(R.id.btnSwitch)
        btnSwitch.setOnClickListener {
            cameraManager.changeCameraSelector()
        }
    }


    override fun onSuccess() {
        TODO("Not yet implemented")
    }

    override fun onCancel() {
        TODO("Not yet implemented")
    }

    override fun onRetry() {
        TODO("Not yet implemented")
    }

    override fun onNetworkQualityCheck(isGoodConnection: Boolean) {
        if (isGoodConnection) {
//            // Good connection
//            if (encodeImageString != null) {
//                uploadimagedb(encodeImageString)
//                encodeImageString = null
//            } else {
//                com.shamim.apifacedetector.fragment.LivePreviewFragment.uploadimageCheck = false
//            }
        } else {
//            customDialog.dismiss()
//            // Poor connection
//            tts_Object.speak("Please Check Internet ", TextToSpeech.QUEUE_FLUSH, null, null)
//            imgCapture.setImageBitmap(null)
//            showToastOnUiThread("Connection is Poor")
//            isPhotoDetected = false
//            com.shamim.apifacedetector.fragment.LivePreviewFragment.uploadimageCheck = false
        }
    }

    private class NetworkQualityTask(private val callback: NetworkQualityCallback?) :
        AsyncTask<Void?, Void?, Boolean>() {

        protected override fun doInBackground(vararg p0: Void?): Boolean? {
            return try {
                // Measure latency by pinging a known host (e.g., Google's DNS)
                val address = InetAddress.getByName("8.8.8.8")
                address.isReachable(2000) // Timeout in milliseconds
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }

        override fun onPostExecute(isGoodConnection: Boolean) {
            if (callback != null) {
                callback.onNetworkQualityCheck(isGoodConnection)
            } else {
//                Toast.makeText(context, "Internet Connection Poor", Toast.LENGTH_SHORT).show()
            }
        }

    }
}