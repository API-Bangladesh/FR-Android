package com.shamim.frremoteattendence.sharedpreference
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap

class FR_sharedpreference
{
    companion object {


        fun setLoginToken(context: Context, token: String) {
            val sharedPreferences =
                context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            // Insert (save) the token
            editor.putString("token_key", token)
            // Commit the changes
            editor.apply()
        }

        fun getLoginToken(context: Context): String? {
            val sharedPreferences =
                context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
            return sharedPreferences.getString("token_key", "")
        }

        fun RemoveToken(context: Context) {
            val sharedPreferences =
                context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            // Remove the token by setting it to null
            editor.remove("token_key")
            // Commit the changes
            editor.apply()
        }

        fun setLoginBoolean(value: Boolean, context: Context) {
            val sharedPreferences =
                context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("Login", value)
            editor.apply()
        }

        fun getLoginSP(context: Context): Boolean {
            val sharedPreferences =
                context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean("Login", false)
        }


            fun setallowed_locations(allowedlocation: String?, context: Context) {
                val sharedPreferences =
                    context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("allowedlocation", allowedlocation)
                editor.apply()
            }
        fun removeAllowedLocation(context: Context) {
            val sharedPreferences = context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("allowedlocation")
            editor.apply()
        }



        fun getallowed_locations(context: Context): String? {
            val sharedPreferences =
                context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
            return sharedPreferences.getString("allowedlocation", "")
        }

        public open fun SaveBitmap(context: Context,base64Image:String) {
            val sharedPreferences =
                context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("image", base64Image)
            editor.apply()
        }
        fun getSaveBitmap(context: Context): String? {
            val sharedPreferences = context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
           return sharedPreferences.getString("image", "")
        }
        public open fun Remove_SaveBitmap(context: Context) {
            val sharedPreferences =
                context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("image")
            editor.apply()
        }

        public open  fun saveImageResponse(context: Context,imageCheck:Boolean)
        {
            val sharedPreferences =
                context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("uploadImage", imageCheck)
            editor.apply()

        }

        fun getImageResponse(context: Context): Boolean {
            val sharedPreferences =
                context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean("uploadImage", false)
        }
        public open  fun removeImageResponse(context: Context)
        {
            val sharedPreferences =
                context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("uploadImage")
            editor.apply()
        }


        public open fun Remove_LoginuserName(context: Context) {
            val sharedPreferences =
                context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("name")
            editor.apply()
        }


      open  fun setRememberData(context: Context, name: String?, pass: String?) {
            val sharedPreferences =
                context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("username", name)
            editor.putString("password", pass)
            editor.apply()
        }

        fun getRememberData(context: Context): String? {
            val sharedPreferences =
                context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
            return sharedPreferences.getString("username", "")
        }

        fun Remove_RememberData(context: Context) {
            val sharedPreferences =
                context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("username")
            editor.remove("password")
            editor.putBoolean("rememberMe", false)
            editor.apply()
        }

        fun setCheckRememberData(context: Context, checkData: Boolean) {
            val sharedPreferences =
                context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("rememberMe", checkData)
            editor.apply()
        }

        fun getCheckRememberData(context: Context): Boolean {
            val sharedPreferences =
                context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean("rememberMe", false)
        }


        fun saveLocationData(
            context: Context,
            id: String?,
            latitude: String?,
            longitude: String?,
            locationName: String?,
            user: String?,
            eId: String?
        ) {
            val sharedPreferences =
                context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("ID", id)
            editor.putString("latitude", latitude)
            editor.putString("longitude", longitude)
            editor.putString("location_name", locationName)
            editor.putString("User", user)
            editor.putString("E_ID", eId)
            editor.apply()
        }

        fun getLocationData(context: Context): Map<String, String?>? {
            val sharedPreferences =
                context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
            val locationData: MutableMap<String, String?> = HashMap()
            locationData["ID"] = sharedPreferences.getString("ID", "")
            locationData["latitude"] = sharedPreferences.getString("latitude", "")
            locationData["longitude"] = sharedPreferences.getString("longitude", "")
            locationData["location_name"] = sharedPreferences.getString("location_name", "")
            locationData["User"] = sharedPreferences.getString("User", "")
            locationData["E_ID"] = sharedPreferences.getString("E_ID", "")
            return locationData
        }

    }
}