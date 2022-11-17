package com.sample.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONException
import org.json.JSONObject
import java.util.*

const val FCM_API = "https://fcm.googleapis.com/fcm/send"
const val serverKey =
    "key=" + "AAAAnPHwtRs:APA91bG-bNErodH86h6VLFO7pp-bJHSNAeHORGLqMJw2_5nxdX7MtcUU3aa9jXgFKvclhN62l6i3Oh7AdVBs5deEtQMRif6jjz3_WBSIPZJsmhhOy8LKcHuxBS-1hJqNPDr8_mRQibZh"
const val contentType = "application/json"
const val ADMIN_CHANNEL_ID = "admin_channel"
const val SUBSCRIBE_TO = "userABC"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = MyFirebaseMessagingService::class.simpleName + "_"

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        /*
          This method is invoked whenever the token refreshes
          OPTIONAL: If you want to send messages to this application instance
          or manage this apps subscriptions on the server side,
          you can send this token to your server.
        */
        // Once the token is generated, subscribe to topic with the userId
        FirebaseMessaging.getInstance().subscribeToTopic(SUBSCRIBE_TO)
        Log.i(TAG, "onTokenRefresh completed with token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val intent = Intent(this, MainActivity::class.java)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random().nextInt(3000)

        /*
        Apps targeting SDK 26 or above (Android O) must implement notification channels and add its notifications
        to at least one of them. Therefore, confirm if version is Oreo or higher, then setup notification channel
      */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels(notificationManager)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, pendingIntentImmutable
        )
        val largeIcon = BitmapFactory.decodeResource(
            resources, R.drawable.notify_icon
        )
        val notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder =
            NotificationCompat.Builder(this, ADMIN_CHANNEL_ID).setSmallIcon(R.drawable.notify_icon)
                .setLargeIcon(largeIcon).setContentTitle(remoteMessage.data["title"])
                .setContentText(remoteMessage.data["message"]).setAutoCancel(true)
                .setSound(notificationSoundUri).setContentIntent(pendingIntent)

        //Set notification color to match your app color template
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.color = resources.getColor(R.color.colorPrimaryDark)
        }
        notificationManager.notify(notificationID, notificationBuilder.build())
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels(notificationManager: NotificationManager?) {
        val adminChannelName: CharSequence = "New notification"
        val adminChannelDescription = "Device to devie notification"
        val adminChannel: NotificationChannel = NotificationChannel(
            ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH
        )
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        notificationManager?.createNotificationChannel(adminChannel)
    }

}


fun Context.sendNotification(edtTitle: EditText?, edtMessage: EditText?) {
    val topic = "/topics/userABC" //topic has to match what the receiver subscribed to
    val title = edtTitle?.text.toString()
    val message = edtMessage?.text.toString()
    val notification = JSONObject()
    val notificationBody = JSONObject()
    try {
        notificationBody.put("title", title)
        notificationBody.put("message", message)
        notification.put("to", topic)
        notification.put("data", notificationBody)
    } catch (e: JSONException) {
        Log.e("sendNotification", "onCreate: " + e.message)
    }
    val jsonObjectRequest: JsonObjectRequest =
        object : JsonObjectRequest(FCM_API, notification, Response.Listener { response ->
            Log.i("sendNotification", "onResponse: $response")
            edtTitle!!.setText("")
            edtMessage!!.setText("")
        }, Response.ErrorListener {
            Toast.makeText(this, "Request error", Toast.LENGTH_LONG).show()
            Log.i("sendNotification", "onErrorResponse: Didn't work")
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = serverKey
                params["Content-Type"] = contentType
                return params
            }
        }
    MySingleton.getInstance(applicationContext)?.addToRequestQueue(jsonObjectRequest)
}

fun isSPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

val pendingIntentImmutable = if (isSPlus())
    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
else
    PendingIntent.FLAG_UPDATE_CURRENT
