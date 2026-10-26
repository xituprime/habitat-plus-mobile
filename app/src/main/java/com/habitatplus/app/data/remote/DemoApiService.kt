package com.habitatplus.app.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class DemoApiService {

    suspend fun fetchPost(): String =
        withContext(Dispatchers.IO) {

            val connection = URL(
                "https://jsonplaceholder.typicode.com/posts/1"
            ).openConnection() as HttpURLConnection

            try {
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                if (connection.responseCode !in 200..299) {
                    throw Exception(
                        "Error HTTP: ${connection.responseCode}"
                    )
                }

                connection.inputStream.bufferedReader().use {
                    it.readText()
                }
            } finally {
                connection.disconnect()
            }
        }
}
