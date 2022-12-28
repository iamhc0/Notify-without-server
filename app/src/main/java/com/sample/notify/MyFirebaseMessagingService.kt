package com.sample.notify

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*


class MyFirebaseMessagingService : FirebaseMessagingService() {


    @SuppressLint("LongLogTag")
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        /*
          This method is invoked whenever the token refreshes
          OPTIONAL: If you want to send messages to this application instance
          or manage this apps subscriptions on the server side,
          you can send this token to your server.
        */
        // Once the token is generated, subscribe to topic with the userId
        // But Test Notification FCM from firebase is not received using this
        // and campaign message is also received

        if (MainActivity.isTopic) FirebaseMessaging.getInstance().subscribeToTopic(SUBSCRIBE_TO)


        Log.i(TAG, "onTokenRefresh completed with token: $token")
    }

    @SuppressLint("LongLogTag")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d(TAG, "onMessageReceived: $remoteMessage")
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


        val notificationBuilder = if (remoteMessage.notification != null) {
            // TODO This is used for notification using Firebase console
            NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_notifications_none_24).setLargeIcon(largeIcon)
                .setContentTitle(remoteMessage.notification?.title)
                .setContentText(remoteMessage.notification?.body).setAutoCancel(true)
                .setSound(notificationSoundUri).setContentIntent(pendingIntent)
        } else {
            // TODO This is used for notification using app side using api
            NotificationCompat.Builder(this, ADMIN_CHANNEL_ID).setSmallIcon(R.drawable.ic_baseline_notifications_none_24)
                .setLargeIcon(largeIcon).setContentTitle(remoteMessage.data["title"])
                .setContentText(remoteMessage.data["message"]).setAutoCancel(true)
                .setSound(notificationSoundUri).setContentIntent(pendingIntent)
        }

        //Set notification color to match your app color template
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.color = resources.getColor(R.color.colorPrimaryDark)
        }
        notificationManager.notify(notificationID, notificationBuilder.build())
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels(notificationManager: NotificationManager?) {
        val adminChannelName: CharSequence = "New notification"
        val adminChannelDescription = "Device to device notification"
        val adminChannel = NotificationChannel(
            ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH
        )
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        notificationManager?.createNotificationChannel(adminChannel)
    }

}


