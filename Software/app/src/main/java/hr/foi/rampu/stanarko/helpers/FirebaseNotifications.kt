package hr.foi.rampu.stanarko.helpers

import java.io.IOException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject

class FirebaseNotifications {
    fun sendPushNotification(token: String, title: String, body: String) {
        val url = "https://fcm.googleapis.com/fcm/send"

        val bodyJson = JSONObject()
        bodyJson.put("to", token)
        bodyJson.put("notification",
            JSONObject().also {
                it.put("time", title)
                it.put("body", body)
            }
        )
        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")

            .addHeader("Authorization", "key=AAAArAQnXoA:APA91bEKGw6qK9pkxa_sN3mdb5GSPP0jHnjkXUu8y_21RLL8QtcIcmbdjK6cmx8xv5Di-pipw3wnNsKPRaOJRg-P3nz2PqK_jtWUrZDYB00_WMgPTCquBnfjvSbf6f5YPZEX9FRH7PiC")
            .post(
                bodyJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            )
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(
            object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    println("Received data: ${response.body?.string()}")
                }

                override fun onFailure(call: Call, e: IOException) {
                    println(e.message.toString())
                }
            }
        )
    }
}