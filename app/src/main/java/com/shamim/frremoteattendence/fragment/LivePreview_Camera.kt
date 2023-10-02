package com.shamim.frremoteattendence.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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
import com.shamim.frremoteattendence.interfaces.InternetCheck
import com.shamim.frremoteattendence.interfaces.NetworkQualityCallback
import com.shamim.frremoteattendence.interfaces.OnFaceDetectedListener
import com.shamim.frremoteattendence.permission.Permission
import com.shamim.frremoteattendence.sharedpreference.FR_sharedpreference
import com.shamim.frremoteattendence.utils.InternetCheck_Class
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.InetAddress
import java.util.Calendar
import java.util.Locale

class LivePreview_Camera : Fragment(), InternetCheck , NetworkQualityCallback,OnFaceDetectedListener{
    private var service: Intent? = null
    private var TAG:String?="LivePreview_Camera"
    private lateinit var cameraManager: CameraManager
    private lateinit var btnSwitch: ImageButton;
    private  lateinit var graphicOverlay_finder: GraphicOverlay
    private lateinit var previewView_finder: PreviewView
    private lateinit var imageView:ImageView
    private lateinit var single_faceTextview:TextView
    private val url_img = "https://k7ch2z3we1.execute-api.ca-central-1.amazonaws.com/prod/APILimited"
    private lateinit var customDialog:CustomDialog
    private var handler: Handler? = null
    private var tts_Object: TextToSpeech? = null
    private var encodeImageString: String? = null
    private var view: View?=null
    companion object{
        var imageCaptureChecked=false;
    }

    private var toast: Toast? = null
    private val inflater: LayoutInflater by lazy {
        requireActivity().layoutInflater
    }


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_camera_fragent, container, false)

        previewView_finder= view.findViewById<PreviewView>(R.id.previewView_finder)
        graphicOverlay_finder= view.findViewById<GraphicOverlay>(R.id.graphicOverlay_finder)
        single_faceTextview=view.findViewById(R.id.single_faceTextview);
         imageView= view.findViewById<ImageView>(R.id.imageView)
        btnSwitch= view.findViewById<ImageButton>(R.id.btnSwitch)
        customDialog=CustomDialog(requireActivity())
        handler = Handler()


        // Set the fragment as the listener for face detection events

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
            textToSpcheechMethod()
            onClicks()
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
        ,this,
            single_faceTextview
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
                Toast.makeText(this.callback, "Internet Connection Poor", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onPause() {
        super.onPause()

        requireContext().stopService(service)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (tts_Object != null) {
            tts_Object!!.stop()
            tts_Object!!.shutdown()
        }
        requireContext().stopService(service)
    }

    override fun onFaceDetected(encodeImage: String?)
    {
        customDialog.startLoading("Recognition...")

        encodeImageString=encodeImage
        NetworkQualityTask(this)
            .execute()

    }
    override fun onNetworkQualityCheck(isGoodConnection: Boolean) {
        if (isGoodConnection) {
            // Good connection
            if (encodeImageString != null) {
                uploadimagedb(encodeImageString!!)
                encodeImageString = null
            }
        } else {
            customDialog.dismiss()
            // Poor connection
            tts_Object?.speak("Please Check Internet ", TextToSpeech.QUEUE_FLUSH, null, null)
            imageView.setImageBitmap(null)
            showToastOnUiThread("Connection is Poor")
            imageCaptureChecked=false        }
    }
    private fun uploadimagedb(encodeImageString: String)
    {
        Log.d(TAG, "uploadimagedb: $imageCaptureChecked")
        NetworkQualityTask(this).execute()
        val base64_img = "data:image/jpg;base64,$encodeImageString"
        val requestQueue = Volley.newRequestQueue(context)
        val postData = JSONObject()
        try {
            postData.put("base64Img", base64_img)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url_img, postData,
            { response ->
                Log.d(TAG, "Request Get Time:$response")
                val calendar = Calendar.getInstance()
                val hourOfDay = calendar[Calendar.HOUR_OF_DAY]
                customDialog.dismiss()
                try {
                    val jsonObject = JSONObject(response.toString())
                    val responseObject = jsonObject.getJSONObject("response")
                    val nameArray = responseObject.getJSONArray("Name")
                    val nameValue = nameArray.getString(0)
                    if (nameValue == "N") {
                        customDialog.dismiss()
                        tts_Object!!.speak(
                            "Please Try Again ",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            null
                        )
                        handler!!.postDelayed({
                            imageView.setImageBitmap(null)
                            imageCaptureChecked=false
                            Log.d(TAG, "uploadimagedb N: $imageCaptureChecked")

                        }, 1000)
                    } else if (nameValue == "U") {
                        customDialog.dismiss()

                        tts_Object!!.speak(
                            "Welcome To A P I" + "Please Wait",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            null
                        )
                        imageView.setImageBitmap(null)
                        imageCaptureChecked=false
                        Log.d(TAG, "uploadimagedb U: $imageCaptureChecked")
                    } else {
                        customDialog.dismiss()
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
                        tts_Object!!.speak(
                            "$greeting $nameValue",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            null
                        )
                        customToast(nameValue)
                        imageView.setImageBitmap(null)
                        imageCaptureChecked=false
                    }
                } catch (e: JSONException) {
                    throw RuntimeException(e)
                }
            }
        ) { error ->
            error.printStackTrace()
            customDialog.dismiss()
            imageView.setImageBitmap(null)
            imageCaptureChecked=false
            if (error is NetworkError) {
                showToastOnUiThread("Network Error")
            } else if (error is ServerError) {
                showToastOnUiThread("Server Problem")
            } else if (error is AuthFailureError) {
                showToastOnUiThread("Network AuthFailureError")
            } else if (error is ParseError) {
                showToastOnUiThread("Network ParseError")
            } else if (error is NoConnectionError) {
                showToastOnUiThread("Network NoConnectionError")
            } else if (error is TimeoutError) {
                showToastOnUiThread("Oops. Timeout !")
            }

        }
        requestQueue.add(jsonObjectRequest)
        Log.d(TAG,"Request Send Time:$requestQueue"
        )
        jsonObjectRequest.retryPolicy =
            DefaultRetryPolicy(
                4000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
    }

    override var onFaceDetectedListener: Boolean
        get() = TODO("Not yet implemented")
        set(value) {
            if (value)
            {
                val fragmentManager = requireActivity().supportFragmentManager
                val transaction = fragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, Location_Not_Match())
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
    private fun showToastOnUiThread(message: String) {
        requireActivity().runOnUiThread {
            if (toast != null) {
                toast!!.cancel()
            }
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            toast!!.setGravity(Gravity.CENTER, 0, 0)
            toast!!.show()
        }
    }
    @SuppressLint("UseRequireInsteadOfGet")
    private fun customToast(name: String) {
        requireActivity().runOnUiThread {
            if (toast == null) {
                toast = Toast(requireContext())
                view = inflater.inflate(R.layout.custom_toast, requireActivity().findViewById<ViewGroup>(R.id.custom_toast_container))
                toast!!.duration = Toast.LENGTH_SHORT
                toast!!.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                toast!!.view = view
            }

            val e_ToastTextName = view!!.findViewById<TextView>(R.id.e_ToastText_Name)
            e_ToastTextName?.text = name // Ensure you have the correct ID for the TextView
            toast!!.show()
        }
    }

    private fun textToSpcheechMethod() {
        tts_Object = TextToSpeech(activity) { i ->
            if (i != TextToSpeech.ERROR) {
                tts_Object!!.language = Locale("en", "US")
            } else {
                showToastOnUiThread("TextToSpeech initialization error")
            }
        }
    }


}
