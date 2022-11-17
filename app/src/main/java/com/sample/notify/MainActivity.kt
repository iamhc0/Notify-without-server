package com.sample.notify

import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.os.Bundle
import android.util.Log
import android.widget.Button
import org.json.JSONObject
import org.json.JSONException
import com.android.volley.toolbox.JsonObjectRequest
import android.widget.Toast
import kotlin.Throws
import com.android.volley.AuthFailureError
import com.android.volley.Response
import java.util.HashMap


class MainActivity : AppCompatActivity() {
    var edtTitle: EditText? = null
    var edtMessage: EditText? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        edtTitle = findViewById(R.id.edtTitle)
        edtMessage = findViewById(R.id.edtMessage)
        val btnSend = findViewById<Button>(R.id.btnSend)


        btnSend.setOnClickListener {


            sendNotification(edtTitle,edtMessage)
        }
    }


}