package com.my11heros.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import com.deliverdas.customers.utils.HardwareInfoManager
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.my11heros.R
import com.my11heros.SplashScreenActivity
import com.my11heros.UpdateApplicationActivity
import com.my11heros.network.IApiMethod
import com.my11heros.network.RequestModel
import com.my11heros.network.WebServiceClient
import com.my11heros.ui.home.models.UsersPostDBResponse
import com.my11heros.utils.MyPreferences
import com.my11heros.utils.MyUtils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val NOTIFICATION_ID = 1000
    private val FCM_TAG = "MyFirebaseToken"
    private var notificationUtils: NotificationUtils? = null
    private val KEY_ACTION = "action"
    private val KEY_TITLE = "title"
    private val KEY_MESSAGE = "message"
    private val KEY_UPDATE_APK = "apk_update_url"
    private val KEY_RELEASE_NOTE = "release_note"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(FCM_TAG, token)
        val userId = MyPreferences.getUserID(applicationContext)!!
        val notid = FirebaseInstanceId.getInstance()
            .getToken(getString(R.string.gcm_default_sender_id), "FCM")

        MyPreferences.setDeviceToken(this, token)

        if (!TextUtils.isEmpty(notid) && !TextUtils.isEmpty(userId)) {
            val request = RequestModel()
            request.user_id = userId
            request.device_id = token
            val deviceToken: String? = MyPreferences.getDeviceToken(this)
            request.deviceDetails = HardwareInfoManager(this).collectData(deviceToken!!)
            WebServiceClient(applicationContext).client.create(IApiMethod::class.java)
                .deviceNotification(request)
                .enqueue(object : Callback<UsersPostDBResponse?> {
                    override fun onFailure(call: Call<UsersPostDBResponse?>?, t: Throwable?) {

                    }

                    override fun onResponse(
                        call: Call<UsersPostDBResponse?>?,
                        response: Response<UsersPostDBResponse?>?
                    ) {


                    }
                })
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.let { message ->
            // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
            MyUtils.logd(FCM_TAG, "From: " + remoteMessage.from)
            if (applicationContext != null) {
                if (remoteMessage.data.size > 0) {
                    Log.e(
                        FCM_TAG,
                        "Data Payload: " + remoteMessage.data.toString()
                    )
                    try {
                        val gson = Gson()
                        val resultData = gson.toJson(remoteMessage.data)
                        val json = JSONObject(resultData)
                        // handleDataMessage(json)
                        var action = ""
                        var title = ""
                        var message = ""
                        val url =
                            URL("https://cdn.britannica.com/63/211663-050-A674D74C/Jonny-Bairstow-batting-semifinal-match-England-Australia-2019.jpg")
                        var image =
                            BitmapFactory.decodeStream(url.openConnection().getInputStream())

                        if (json.has(KEY_ACTION)) {
                            action = json.getString(KEY_ACTION)
                            try {
                                if (json.has(KEY_TITLE)) {
                                    title = json.getString(KEY_TITLE)
                                }
                            } catch (e: java.lang.Exception) {
                            }

                            try {
                                if (json.has(KEY_MESSAGE)) {
                                    message = json.getString(KEY_MESSAGE)
                                }
                            } catch (e: java.lang.Exception) {
                            }

                            when (action) {
                                "notify" ->
                                    notifyUsers(applicationContext, title, message)
                                //    sendNotification(message,image)
                                "logout" ->
                                    MyPreferences.clear(applicationContext)
                                "update" ->
                                    updateApplicationRequired(
                                        applicationContext,
                                        title,
                                        message,
                                        json.getString(KEY_UPDATE_APK),
                                        json.getString(KEY_RELEASE_NOTE)
                                    )
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(
                            FCM_TAG, "Exception: " + e.message
                        )
                    }
                }
            }
        }
    }

    private fun sendNotification(
        messageBody: String,
        image: Bitmap
    ) {
        val intent = getIntentNotify()
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder =
            NotificationCompat.Builder(this)
                .setLargeIcon(image)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(messageBody)
                .setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(image)
                ) /*Notification with Image*/
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    private fun notifyUsers(context: Context, title: String, message: String) {
        val powerManager =
            context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!powerManager.isInteractive) {
            return
        }
        val intent: Intent = getIntentNotify()!!
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val stackBuilder =
            TaskStackBuilder.create(context)
        stackBuilder.addNextIntent(intent)
        val resultPendingIntent = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationId = 1
            val channelId = "SportsFightNotify"
            val channelName = "SportsFightNotify"
            val importance = NotificationManager.IMPORTANCE_HIGH
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val mChannel = NotificationChannel(
                    channelId, channelName, importance
                )
                notificationManager.createNotificationChannel(mChannel)
            }
            val mBuilder =
                NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setStyle(NotificationCompat.BigTextStyle())
            mBuilder.setContentIntent(resultPendingIntent)
            mBuilder.setAutoCancel(true)
            notificationManager.notify(notificationId, mBuilder.build())
        } else {
            val builder =
                NotificationCompat.Builder(context) // Set Ticker Message
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setStyle(NotificationCompat.BigTextStyle())
                    .setAutoCancel(true) // Set PendingIntent into Notification
                    .setContentIntent(resultPendingIntent)
            // Sets an ID for the notification
            notificationManager.notify(
                NOTIFICATION_ID,
                builder.build()
            )
        }
    }

    private fun getIntentNotify(): Intent? {
        val intent = Intent(
            applicationContext,
            SplashScreenActivity::class.java
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return intent
    }

    private fun updateApplicationRequired(
        context: Context,
        title: String,
        messagedd: String,
        apkupdateurl: String,
        releasenote: String
    ) {
        val powerManager =
            context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!powerManager.isInteractive) {
            return
        }
        val intent: Intent = getIntentUpdateActvity(apkupdateurl, releasenote)!!
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val stackBuilder =
            TaskStackBuilder.create(context)
        stackBuilder.addNextIntent(intent)
        val resultPendingIntent = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationId = 1
            val channelId = "SportsFightUpdate"
            val channelName = "SportsFightUpdate"
            val importance = NotificationManager.IMPORTANCE_HIGH
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val mChannel = NotificationChannel(
                    channelId, channelName, importance
                )
                notificationManager.createNotificationChannel(mChannel)
            }
            val mBuilder =
                NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(releasenote)
            mBuilder.setContentIntent(resultPendingIntent)
            mBuilder.setAutoCancel(true)
            notificationManager.notify(notificationId, mBuilder.build())
        } else {
            val builder =
                NotificationCompat.Builder(context) // Set Ticker Message
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(releasenote)
                    .setAutoCancel(true) // Set PendingIntent into Notification
                    .setContentIntent(resultPendingIntent)
            // Sets an ID for the notification
            notificationManager.notify(
                NOTIFICATION_ID,
                builder.build()
            )
        }
    }

    private fun getIntentUpdateActvity(apkupdateurl: String, releasenote: String): Intent? {
        val intent = Intent(
            applicationContext,
            UpdateApplicationActivity::class.java
        )
        intent.putExtra(UpdateApplicationActivity.REQUEST_CODE_APK_UPDATE, apkupdateurl)
        intent.putExtra(UpdateApplicationActivity.REQUEST_RELEASE_NOTE, releasenote)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return intent
    }

}