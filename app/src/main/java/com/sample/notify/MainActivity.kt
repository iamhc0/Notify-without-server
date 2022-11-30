package com.sample.notify

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity() {
    var edtTitle: EditText? = null
    var edtMessage: EditText? = null


    @SuppressLint("LongLogTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        edtTitle = findViewById(R.id.edtTitle)
        edtMessage = findViewById(R.id.edtMessage)
        val btnSend = findViewById<Button>(R.id.btnSend)


        btnSend.setOnClickListener {


            sendNotification(edtTitle, edtMessage)
        }


        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->

            if (!task.isSuccessful) {
                Log.w(
                    "getNotificationToken",
                    "Fetching FCM registration token failed",
                    task.exception
                )

                return@addOnCompleteListener
            }
            // Get new FCM registration token
            val token: String = task.result
            Log.i(TAG, "onTokenRefresh completed with token: $token")
        }
    }


}