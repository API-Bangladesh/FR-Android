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





        fun setLoginE_ID(context: Context, E_ID: String) {
            val sharedPreferences =
                context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            // Insert (save) the token
            editor.putString("e_ID", E_ID)
            // Commit the changes
            editor.apply()
        }
        fun getLoginE_ID(context: Context): String? {
            val sharedPreferences =
                context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
            return sharedPreferences.getString("e_ID", "")
        }

        fun RemoveE_ID(context: Context) {
            val sharedPreferences =
                context.getSharedPreferences("LoginAndLogoutSP", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            // Remove the token by setting it to null
            editor.remove("e_ID")
            // Commit the changes
            editor.apply()
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