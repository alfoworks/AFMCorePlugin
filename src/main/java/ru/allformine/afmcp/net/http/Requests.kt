package ru.allformine.afmcp.net.http

import ru.allformine.afmcp.AFMCorePlugin
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.StandardCharsets
import javax.net.ssl.HttpsURLConnection

object Requests {
    fun sendPostJSON(JSON: String, urlString: String?) {
        try {
            val url = URL(urlString)
            val con = url.openConnection()
            val connection = con as HttpsURLConnection

            connection.requestMethod = "POST"
            connection.doOutput = true

            val out = JSON.toByteArray(StandardCharsets.UTF_8)
            val length = out.size

            connection.setFixedLengthStreamingMode(length)
            connection.addRequestProperty("Content-Type", "application/json; charset=UTF-8")
            connection.addRequestProperty("User-Agent", "Mozilla 5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.0.11) ")
            connection.connect()
            connection.outputStream.use { os -> os.write(out) }

            if (connection.errorStream != null) {
                val reader = BufferedReader(InputStreamReader(connection.errorStream))
                val result = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    result.append("\n").append(line)
                }

                AFMCorePlugin.logger.error("Can't send JSON to url $urlString.")
                AFMCorePlugin.logger.error("JSON: $JSON")
                AFMCorePlugin.logger.error("Response: $result")
            }
        } catch (e: Exception) {
            AFMCorePlugin.logger.error("Can't send JSON to url $urlString.")
            AFMCorePlugin.logger.error("JSON: $JSON")
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun sendGet(url: String?): Response? {
        return try {
            val obj = URL(url)
            val con = obj.openConnection() as HttpsURLConnection
            val code = con.responseCode

            if (code in 200..299) {
                val input = BufferedReader(InputStreamReader(con.inputStream))

                var inputLine: String?
                val response = StringBuilder()
                while (input.readLine().also { inputLine = it } != null) {
                    response.append(inputLine)
                }

                input.close()

                return Response(response.toString(), code)
            } else if (con.errorStream != null) {
                val reader = BufferedReader(InputStreamReader(con.errorStream))
                val result = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    result.append("\n").append(line)
                }

                return Response(result.toString(), code)
            }

            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}