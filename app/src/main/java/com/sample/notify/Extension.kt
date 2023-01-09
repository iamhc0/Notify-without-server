package com.sample.notify

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Hamza Chaudhary
 * Sr. Software Engineer Android
 * Created on 28 Dec,2022 15:05
 * Copyright (c) All rights reserved.
 */


const val FCM_API = "https://fcm.googleapis.com/fcm/send"
const val serverKey =
    "key=" + "AAAAnPHwtRs:APA91bG-bNErodH86h6VLFO7pp-bJHSNAeHORGLqMJw2_5nxdX7MtcUU3aa9jXgFKvclhN62l6i3Oh7AdVBs5deEtQMRif6jjz3_WBSIPZJsmhhOy8LKcHuxBS-1hJqNPDr8_mRQibZh"
const val contentType = "application/json"
const val ADMIN_CHANNEL_ID = "admin_channel"
const val SUBSCRIBE_TO = "userABC"
const val TAG = "MyFirebaseMessagingService" + "_"
var token = ""


// send notification using volley

fun Context.sendNotification(edtTitle: EditText?, edtMessage: EditText?, isTopic: Boolean) {

    val topic = "/topics/userABC" //topic has to match what the receiver subscribed to
    if (FcmWithoutServerActivity.isTopic) FirebaseMessaging.getInstance().subscribeToTopic(SUBSCRIBE_TO)

    val title = edtTitle?.text.toString()
    val message = edtMessage?.text.toString()
    val notification = JSONObject()
    val notificationBody = JSONObject()
    try {
        notificationBody.put("title", title)
        notificationBody.put("message", message)
        if (isTopic) notification.put("to", topic)
        else notification.put("to", token)
        notification.put("data", notificationBody)
    } catch (e: JSONException) {
        Log.e("sendNotification", "onCreate: " + e.message)
    }
    val jsonObjectRequest: JsonObjectRequest =
        @SuppressLint("LongLogTag")
        object : JsonObjectRequest(FCM_API, notification, Response.Listener { response ->
            Log.i(TAG, "onResponse: $response")
            edtTitle?.setText("")
            edtMessage?.setText("")
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
    VolleyRequest.getInstance(applicationContext)?.addToRequestQueue(jsonObjectRequest)
}


// TODO Get Token from Firebase cloud messaging


@SuppressLint("LongLogTag")
fun getToken() {

    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->

        if (!task.isSuccessful) {
            Log.w(
                "getNotificationToken", "Fetching FCM registration token failed", task.exception
            )

            return@addOnCompleteListener
        }
        // Get new FCM registration token
        token = task.result
        Log.i(TAG, "onTokenRefresh completed with token: $token")
    }
}


//TODO Pending Intent for 12 +

fun isSPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

val pendingIntentImmutable =
    if (isSPlus()) PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    else PendingIntent.FLAG_UPDATE_CURRENT
