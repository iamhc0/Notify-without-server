package com.sample.notify

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sample.notify.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    var binding: ActivityMainBinding? = null

    companion object {
        var isTopic: Boolean = false
    }


    @SuppressLint("LongLogTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.init()


    }

    private fun ActivityMainBinding.init() {
        getToken()

        btnSend.setOnClickListener {
            sendNotification(edtTitle, edtMessage, isTopic)
        }

        topicSwitch.setOnCheckedChangeListener { _, isChecked -> isTopic = isChecked }
    }


}