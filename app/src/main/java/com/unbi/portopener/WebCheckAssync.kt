package com.unbi.portopener

import android.os.AsyncTask
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.URL

class WebCheckAssync : AsyncTask<Int, Void, Unit>() {
    private val DEFAULT_IP = "127.0.0.1"
    override fun doInBackground(vararg params: Int?): Unit {
        val port = params[0]
        if (port == null||port<0) {
            return
        }

        try {
            val ipadress = "https://api.ipify.org?format=json"
            val url = URL(ipadress)
            val ipcheker = url.openConnection(getproxy(port)) as HttpURLConnection
            ipcheker.requestMethod = "GET"
            val rd = BufferedReader(InputStreamReader(ipcheker.inputStream))
            val ipchekerString = rd.readLine()
            rd.close()
            val ipadressDetail = ipchekerString.toString()
            val toastHandler = ToastHandler.instance
            toastHandler.showToast(ipadressDetail, LENGTH_SHORT)
        }catch (e:Exception){
            val toastHandler = ToastHandler.instance
            toastHandler.showToast(e.message.toString(), LENGTH_SHORT)
        }


    }

    private fun getproxy(i: Int): Proxy? {
        return java.net.Proxy(Proxy.Type.HTTP, InetSocketAddress(DEFAULT_IP, i));
    }
}