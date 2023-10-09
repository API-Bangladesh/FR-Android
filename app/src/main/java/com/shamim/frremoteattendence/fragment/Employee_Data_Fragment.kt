package com.shamim.frremoteattendence.fragment

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.datepicker.MaterialDatePicker
import com.shamim.frremoteattendence.R
import com.shamim.frremoteattendence.adapter.door_loc_adapter
import com.shamim.frremoteattendence.alert.CustomDialog
import com.shamim.frremoteattendence.alert.CustomDialog_notification
import com.shamim.frremoteattendence.interfaces.InternetCheck
import com.shamim.frremoteattendence.model_class.door_loc_model_class
import com.shamim.frremoteattendence.sharedpreference.FR_sharedpreference
import com.shamim.frremoteattendence.utils.InternetCheck_Class
import org.json.JSONArray
import org.json.JSONException
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class Employee_Data_Fragment : Fragment() ,InternetCheck {
    private var TAG:String?="Employee_Data_Fragment"
    private var datePickerTextview:TextView?=null
    private var datePickerBtn:Button?=null
    private var recyler:RecyclerView?=null
    private var customDialog: CustomDialog?=null
    private var list_door_all_data = ArrayList<door_loc_model_class>()
    var adapter:door_loc_adapter? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var selectedDateRange:String?=null
    var nameText:TextView?=null
    var name:String?=null
    var id:String?=null
    var totalworkDay:String?=null

    @SuppressLint("MissingInflatedId", "SourceLockedOrientationActivity")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_employee__data_, container, false)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        datePickerTextview=view.findViewById(R.id.datepixkerText)
        datePickerBtn=view.findViewById(R.id.datepixkerBtn)
        recyler=view.findViewById(R.id.employee_dataRV)
        nameText=view.findViewById(R.id.datepixkerNameText)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyler!!.setLayoutManager(linearLayoutManager)
        list_door_all_data = ArrayList<door_loc_model_class>()
        customDialog= CustomDialog(requireActivity())

        datePickerBtn!!.setOnClickListener(View.OnClickListener {

            if (checkInternet())
            {
                datePickerDialog()
            }

        })


        return view
    }

   private fun checkInternet():Boolean{
       if (InternetCheck_Class.isNetworkConnected(context)) {
           return true
       }
       else {
           InternetCheck_Class.openInternetDialog(this, true, context)
           return false
       }
   }


    private fun getEmployeeData(url:String) {

        var totalCumulativeWorkHour = 0

// Create a map for your headers
        val headers = HashMap<String, String>()
        headers["x-api-key"] = "1234567890" // Replace with your actual API key
        try {
            val jsonArrayRequest = @SuppressLint("SetTextI18n")
            object : StringRequest(
                Method.GET,
                url,
                { response ->
                    try {
                        val jsonArray = JSONArray(response)
                        list_door_all_data.clear()

                        totalworkDay= jsonArray.length().toString()
                        Log.d(TAG, "jsonArray Length: "+totalworkDay)

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            list_door_all_data.add(door_loc_model_class(
                                jsonObject.getString("ID"),
                                jsonObject.getString("E_ID"),
                                jsonObject.getString("Name"),
                                jsonObject.getString("Date"),
                                jsonObject.getString("InTime"),
                                jsonObject.getString("OutTime"),
                                jsonObject.getString("Tardiness"),
                                jsonObject.getString("total_worked_hour_in_minutes"),
                                jsonObject.getString("cumulative_work_hour")
                            ))

                            val e_ID = jsonObject.getString("E_ID")
                            name = jsonObject.getString("Name")

                            if (e_ID != "") {
                                adapter = door_loc_adapter(list_door_all_data, context)
                                recyler!!.adapter = adapter
                                //adapter.updateData(list_door_all_data);
                                adapter!!.itemCount
                                customDialog!!.dismiss()
                            } else {
                                Toast.makeText(context, "Data null", Toast.LENGTH_SHORT).show()
                            }

                            val workCountString = jsonObject.getString("total_worked_hour_in_minutes")
                            val workCount: Int = workCountString.toIntOrNull() ?: 0

                            totalCumulativeWorkHour += workCount
                        }

                        nameText!!.visibility = View.VISIBLE
                        nameText!!.text = "$name ID: $id \n Total Work Time $totalCumulativeWorkHour"+"("+totalworkDay+")"
                        Log.d(TAG, "total work: " + totalCumulativeWorkHour)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        if (customDialog != null) {
                            customDialog!!.dismiss()
                        }
                    }
                },
                { error ->
                    error.printStackTrace()
                    if (customDialog != null) {
                        customDialog!!.dismiss()
                    }
                    Log.d(TAG, "error: " + error.message)
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    return headers
                }
            }

            val requestQueue = Volley.newRequestQueue(requireContext())
            requestQueue.add(jsonArrayRequest)
        } catch (e: JSONException) {
            e.printStackTrace()
        }


    }
    private fun datePickerDialog() {
        val builder: MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> =
            MaterialDatePicker.Builder.dateRangePicker()
        builder.setTitleText("Select a date range")

        val datePicker = builder.build()
        datePicker.addOnPositiveButtonClickListener { selection ->
            val startDate = selection.first
            val endDate = selection.second

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val startDateString = sdf.format(Date(startDate))
            val endDateString = sdf.format(Date(endDate))

             selectedDateRange = "$startDateString/$endDateString"

            datePickerTextview!!.text = "$selectedDateRange"  // Update your TextView
            if (datePickerTextview!!.text.isNotEmpty())
            {
                id=FR_sharedpreference.getLoginE_ID(requireContext())
                Log.d(TAG, "URL: "+"https://apil.online/permanent_log/APILimited/"+id+"/"+selectedDateRange)
                val employeedataURL="https://apil.online/permanent_log/APILimited/"+id+"/"+selectedDateRange
                getEmployeeData(employeedataURL)
                if (customDialog !=null)
                {
                    customDialog!!.dismiss()
                }
                customDialog!!.startLoading("Please wait...")
            }
        }

        // Show the date picker dialog using the fragment's childFragmentManager
        datePicker.show(childFragmentManager, "DATE_PICKER")
    }

    override fun onSuccess() {
        TODO("Not yet implemented")
    }

    override fun onCancel() {
        TODO("Not yet implemented")
    }

    override fun onRetry() {
        checkInternet()
    }
}