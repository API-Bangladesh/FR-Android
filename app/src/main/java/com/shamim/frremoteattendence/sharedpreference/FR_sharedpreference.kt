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

        public open fun Remove_LoginuserName(context: Context) {
            val sharedPreferences =
                context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("name")
            editor.apply()
        }


      open  fun setRememberData(context: Context, id: String?) {
            val sharedPreferences =
                context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("userID", id)

            editor.apply()
        }

        fun getRememberData(context: Context): String? {
            val sharedPreferences =
                context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
            return sharedPreferences.getString("userID", "")
        }

        open fun Remove_RememberData(context: Context) {
            val sharedPreferences =
                context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("userID")
            editor.apply()
        }





    }
}