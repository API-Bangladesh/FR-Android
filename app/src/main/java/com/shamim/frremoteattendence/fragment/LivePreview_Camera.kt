package com.shamim.frremoteattendence.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.view.PreviewView
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkError
import com.android.volley.NoConnectionError
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.ServerError
import com.android.volley.TimeoutError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.shamim.frremoteattendence.LocationService.LocationService
import com.shamim.frremoteattendence.R
import com.shamim.frremoteattendence.alert.CustomDialog
import com.shamim.frremoteattendence.camerax.CameraManager
import com.shamim.frremoteattendence.camerax.GraphicOverlay
import com.shamim.frremoteattendence.face_detection.FaceContourDetectionProcessor
import com.shamim.frremoteattendence.interfaces.FaceImage
import com.shamim.frremoteattendence.interfaces.InternetCheck
import com.shamim.frremoteattendence.interfaces.NetworkQualityCallback
import com.shamim.frremoteattendence.permission.Permission
import com.shamim.frremoteattendence.utils.InternetCheck_Class
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.InetAddress
import java.util.Calendar
import java.util.Locale

class LivePreview_Camera : Fragment(), InternetCheck , NetworkQualityCallback ,FaceContourDetectionProcessor.FaceDetectionListener,FaceImage{
    private var service: Intent? = null
    private lateinit var cameraManager: CameraManager
    private lateinit var faceDetection: FaceContourDetectionProcessor
    private lateinit var btnSwitch: ImageButton;
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
        btnSwitch= view.findViewById<ImageButton>(R.id.btnSwitch)

        faceDetection = FaceContourDetectionProcessor(
            graphicOverlay_finder,
            requireContext(),
            imageView
        )

        // Set the fragment as the listener for face detection events
        faceDetection.setFaceDetectionListener(this)


        checkInternet()
        onClicks()



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
        }
        else {
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

    init {
        object {
            public fun navigateToFragment(fragment: Fragment) {
                val fragmentManager = requireActivity().supportFragmentManager
                val transaction = fragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
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

    override fun onFaceDetected(data:String) {
        Log.d(TAG, "Face Detected Successfully: $data")
        Toast.makeText(requireContext(), "Face Detected Successfully", Toast.LENGTH_SHORT).show()

    }
    fun ted(){

    }

    companion object{

        private val TAG = "LivePreviewFragment"
        val url_img = "https://k7ch2z3we1.execute-api.ca-central-1.amazonaws.com/prod/APILimited"
        private var tts_Object: TextToSpeech? = null


        fun uploadData(context:Context,encodeImage:String)
        {
            Log.d(TAG, "Send Data: ")
             fun textToSpcheechMethod() {
                tts_Object = TextToSpeech(context) { i ->
                    if (i != TextToSpeech.ERROR) {
                        tts_Object?.setLanguage(Locale("en", "US"))
                    } else {
                    }
                }
            }

           val progressDialog = ProgressDialog(context)
            progressDialog?.setMessage("Loading...") // Set your message here
            progressDialog?.setCancelable(false)
            progressDialog?.show()

            val customDialog:CustomDialog=CustomDialog(context as Activity?)

            val handler=Handler()

            val base64_img = "data:image/jpg;base64,$encodeImage"
                val requestQueue = Volley.newRequestQueue(context)
                val postData = JSONObject()
                try {
                    postData.put("base64Img", base64_img)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.POST,
                    url_img,
                    postData,
                    { response ->
                        Log.d(
                           TAG,
                            "Request Get Time:$response"
                        )
                        val calendar = Calendar.getInstance()
                        val hourOfDay = calendar[Calendar.HOUR_OF_DAY]
                        customDialog.dismiss()
                        try {
                            val jsonObject = JSONObject(response.toString())
                            val responseObject = jsonObject.getJSONObject("response")
                            val nameArray = responseObject.getJSONArray("Name")
                            val nameValue = nameArray.getString(0)
                            if (nameValue == "N") {
                                progressDialog.dismiss()
                                tts_Object?.speak(
                                    "Please Try Again ",
                                    TextToSpeech.QUEUE_FLUSH,
                                    null,
                                    null
                                )
                                handler.postDelayed(Runnable {

                                }, 1000)
                            }
                            else if (nameValue == "U") {
                                tts_Object?.speak(
                                    "Welcome To A P I" + "Please Wait",
                                    TextToSpeech.QUEUE_FLUSH,
                                    null,
                                    null)
                                progressDialog.dismiss()

                                Toast.makeText(context, "U", Toast.LENGTH_SHORT).show()
//                                )
                            } else {
                                val greeting: String
                                greeting = if (hourOfDay >= 5 && hourOfDay < 12) {
                                    "Good morning!"
                                } else if (hourOfDay >= 12 && hourOfDay < 17) {
                                    "Good afternoon!"
                                } else if (hourOfDay >= 17 && hourOfDay < 21) {
                                    "Good evening!"
                                } else {
                                    "Good night!"
                                }

                                Toast.makeText(context, nameValue, Toast.LENGTH_SHORT).show()
                                tts_Object?.speak(
                                    "$greeting $nameValue",
                                    TextToSpeech.QUEUE_FLUSH,
                                    null,
                                    null)
                                progressDialog.dismiss()
                                progressDialog.dismiss()


                            }
                        } catch (e: JSONException) {
                            throw RuntimeException(e)
                        }
                    }
                ) { error ->
                    error.printStackTrace()


                    if (error is NetworkError) {
                        Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()

                    } else if (error is ServerError) {
                        Toast.makeText(context, "Server Problem", Toast.LENGTH_SHORT).show()

                    } else if (error is AuthFailureError) {
                        Toast.makeText(context, "Network AuthFailureError", Toast.LENGTH_SHORT).show()

                    } else if (error is ParseError) {
                        Toast.makeText(context, "Network ParseErrorr", Toast.LENGTH_SHORT).show()


                    } else if (error is NoConnectionError) {
                        Toast.makeText(context, "Network NoConnectionError", Toast.LENGTH_SHORT).show()

                    } else if (error is TimeoutError) {
                        Toast.makeText(context, "Oops. Timeout !", Toast.LENGTH_SHORT).show()
                    }
                    customDialog.dismiss()
                }
                requestQueue.add(jsonObjectRequest)

                jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
                    6000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
            }
        }

    override fun callBack(data: String?) {
        TODO("Not yet implemented")
    }


}
