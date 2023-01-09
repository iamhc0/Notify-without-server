package com.sample.notify

import android.annotation.SuppressLint
import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import kotlin.jvm.Synchronized

class VolleyRequest private constructor(private val ctx: Context) {
    private var requestQueue: RequestQueue?

    init {
        requestQueue = getRequestQueue()
    }

    private fun getRequestQueue(): RequestQueue? {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.applicationContext)
        }
        return requestQueue
    }

    fun <T> addToRequestQueue(req: Request<T>?) {
        getRequestQueue()?.add(req)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: VolleyRequest? = null
        @Synchronized
        fun getInstance(context: Context): VolleyRequest? {
            if (instance == null) {
                instance = VolleyRequest(context)
            }
            return instance
        }
    }
}