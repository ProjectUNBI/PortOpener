package com.unbi.portopener

import android.app.Service
import android.os.AsyncTask
import android.util.Log
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.lang.Exception
import java.net.ServerSocket
import java.net.Socket
import android.R.attr.host


abstract class PortOpenerExtra : Service() {
    protected val TAG = "PortOpenerExtra"

    protected fun portservicestart() {
        Appinstance.instance.portOpenerAssync1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, UserInstance.instance.portPairs[0])
        Appinstance.instance.portOpenerAssync2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, UserInstance.instance.portPairs[1])
    }

    protected fun portservicestop() {
        Appinstance.instance.portOpenerAssync1.stop()
        Appinstance.instance.portOpenerAssync1 = AssyncPortOpenner()
        Appinstance.instance.portOpenerAssync2.stop()
        Appinstance.instance.portOpenerAssync2 = AssyncPortOpenner()
    }
}

class AssyncPortOpenner : AsyncTask<PortPair, Unit, Unit>() {
    private var threadProxy: ThreadProxy?=null
    private var willServiceRun = true

    private var server: ServerSocket? = null
    private var mPortPair: PortPair? = null

    override fun doInBackground(vararg portPairs: PortPair) {
        val pp = portPairs[0]
        mPortPair = pp
        try {
            willServiceRun = true
            val host = "0.0.0.0"
            val openport = pp.openport;
            val localport = pp.localhost
            server = ServerSocket(openport);
            val s = server
            while (willServiceRun) {
                if (s != null && !s.isClosed) {
                    threadProxy=ThreadProxy(s.accept(), host, localport);
                }
            }
        } catch (e: Exception) {
        }

    }


    fun stop() {
        willServiceRun = false
        threadProxy?.closeStreams()
        server?.close()
        val pp = mPortPair
        if (pp == null) {
            return
        }
        try {
            val s = Socket("127.0.0.1", pp.openport);
            s.close();
        } catch (e: Exception) {
            System.out.println(e);
        }

    }


}

