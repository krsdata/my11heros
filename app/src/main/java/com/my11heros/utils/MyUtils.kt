package com.my11heros.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Handler
import android.provider.Settings
import android.telephony.SmsManager
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.andrognito.flashbar.Flashbar
import com.andrognito.flashbar.anim.FlashAnim
import com.my11heros.R
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


class MyUtils {

    companion object {
        const val INTENT_FILTER_LOCAL_BROADCAST = "com.deliverdas.vendor.notitification"
        const val KEY_DATA_RECEIVED = "com.deliverdas.vendor.notitification"
        fun isEmailValid(email: String): Boolean {
            val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
            val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(email)
            return matcher.matches()
        }

        fun isMobileValid(email: String): Boolean {
            return email.length==10
        }

        fun placeOrderWhatsapp(context: Context,email:String,name:String) {
            try {
                val text =
                    "Hi Rats, I am interested in your demo, Please register me as \n"+name+"\n"+email // Replace with your message.
                var toNumber = "918828002531"
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("http://api.whatsapp.com/send?phone=$toNumber&text=$text")
                context.startActivity(intent)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }


        fun sendSMS(mContext: Context, phoneNumber: String, message: String) {
            val number = phoneNumber
            val message = message

            if (!TextUtils.isEmpty(number) && !TextUtils.isEmpty(message)) {
                val sent = PendingIntent.getBroadcast(mContext, 0, Intent("sent"), 0)
                val deliver =
                    PendingIntent.getBroadcast(mContext, 0, Intent("delivered"), 0)
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(number, null, message, sent, deliver)
                Toast.makeText(mContext, "Sent Message", Toast.LENGTH_SHORT).show()
            } else {

                Toast.makeText(
                    mContext,
                    "Please enter number and message to continue",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }


        fun parseDate(date:String):String?{

            val originalFormat: DateFormat =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            val targetFormat: DateFormat = SimpleDateFormat("HH:mm")
            val date: Date = originalFormat.parse(date)
            val formattedDate: String = targetFormat.format(date) // 20120821

            return formattedDate
        }

        fun logd(s: String, image1Path: String?) {
            Log.d(s+"ZX",image1Path)
        }


        fun isConnectedWithInternet(activity: AppCompatActivity):Boolean{
            val connectivityManager=activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo=connectivityManager.activeNetworkInfo
            return  networkInfo!=null && networkInfo.isConnected
        }

        fun getDeviceID(mContext: Context): String? {

            var androidId =
                MyPreferences.getAndroidId(mContext)
            try {
                if (TextUtils.isEmpty(androidId)) {
                    androidId = Settings.Secure.getString(mContext.contentResolver, Settings.Secure.ANDROID_ID)
                    MyPreferences.setAndroidID(
                        mContext,
                        androidId
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return androidId
        }

        fun showToast(activity:AppCompatActivity, message: String) {
//            val snackbar = Snackbar.make(view, str,
//                Snackbar.LENGTH_LONG).setAction("Ok", null)
//            snackbar.setActionTextColor(Color.WHITE)
//            val snackbarView = snackbar.view
//            snackbarView.setBackgroundColor(Color.RED)
//            val params =view.layoutParams as WindowManager.LayoutParams
//            params.gravity = Gravity.TOP
//            view.setLayoutParams(params)
//            val textView =
//                snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
//            textView.setTextColor(Color.WHITE)
//
//            textView.textSize = 12f
//            snackbar.show()

            if(activity!=null && !activity.isFinishing) {
                var flashbar = Flashbar.Builder(activity)
                    .gravity(Flashbar.Gravity.TOP)
//                    .title(activity!!.getString(R.string.app_name))
                    .message(message)
                    .backgroundDrawable(R.color.secondery_color)
                    /*.showIcon()
                    .icon(R.drawable.ic_photo_camera_black_24dp)
                    .iconAnimation(
                        FlashAnim.with(activity)
                            .animateIcon()
                            .pulse()
                            .alpha()
                            .duration(750)
                            .accelerate()
                    )*/
                    .build()
                flashbar.show()
                Handler().postDelayed(Runnable { flashbar.dismiss() }, 2000L)
            }

        }


        fun getBitmapFromURL(src: String?): Bitmap? {
           // BitmapFactory.decodeResource(context.resources, R.drawable.splash_logo)
            if(TextUtils.isEmpty(src)){
                return null
            }
            return try {
                val url = URL(src)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input: InputStream = connection.inputStream
                BitmapFactory.decodeStream(input)
            } catch (e: IOException) { // Log exception
                null
            }
        }

        fun getDominantColor(bitmap: Bitmap?): Int {
            val newBitmap = Bitmap.createScaledBitmap(bitmap!!, 1, 1, true)
            val color = newBitmap.getPixel(0, 0)
            newBitmap.recycle()
            return color
        }

        fun setDominantViewColor(teamAColorView: View?, urls: String) {

            val thread = Thread(Runnable {
                try {

                    val bitmap =getBitmapFromURL(urls)
                    if(bitmap!=null) {
                        logd("bitmap","Found bitmap for "+urls)
                        val dominantColor = getDominantColor(bitmap)
                        teamAColorView!!.setBackgroundColor(dominantColor)
                    }

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            })

            thread.start()
        }

        fun getAppVersionName(context: Context): String? {
            try {
                val pInfo =
                    context.packageManager.getPackageInfo(context.packageName, 0)
                return pInfo.versionName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return ""
        }

        @Synchronized
        fun showMessage(context:Context,
            toast: String?
        ) {
            if (!TextUtils.isEmpty(toast)) {
                val msg = Toast.makeText(context, toast, Toast.LENGTH_LONG)
                msg.setGravity(Gravity.CENTER, 0, 0)
                msg.show()
            }
        }
    }
}