package com.unbi.portopener

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log


val PORT_SERVICE_EXTRA: String = "PORT_SERVICE_INTENT"

class PortOpenerService : PortOpenerExtra() {


    ////////////////
    private val NOTY_ID_NOTSET = -99
    val binder = LocalBinder()
    var notiId: Int = NOTY_ID_NOTSET

    inner class LocalBinder : Binder() {
        internal// Return this instance of LocalService so clients can call public methods
        val service: Service
            get() = this@PortOpenerService
    }


    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        /**
         * Lets check if the bundle has some exxtra and if this has no extra just leave
         * if extra then we have to restart the service
         * or stop the playing find phone sound if started
         */
        val extra_string = intent?.getStringExtra(PORT_SERVICE_EXTRA)
        when (extra_string) {
            MainActivity::class.java.canonicalName -> Log.v(TAG, "Started")

        }
        return START_STICKY
    }

    fun StopService(vararg objects: Any): Unit {
        this.stopForeground(true);
        portservicestop()
    }


    fun StartService(vararg objects: Any): Unit {
        showforeground()
        portservicestart()

    }


    private fun showforeground() {
        ///todo do yout stuff here
        val iconId = R.drawable.ic_launcher_foreground
        var str1 = ""
        var str2 = ""
        if (
                UserInstance.instance.portPairs[0].localhost > -1 &&
                UserInstance.instance.portPairs[0].openport > -1
        ){
            str1="${UserInstance.instance.portPairs[0].localhost} ---> ${UserInstance.instance.portPairs[0].openport}"
        }

        if (
                UserInstance.instance.portPairs[1].localhost > -1 &&
                UserInstance.instance.portPairs[1].openport > -1
        ){
            str2="${UserInstance.instance.portPairs[1].localhost} ---> ${UserInstance.instance.portPairs[1].openport}"
        }

        val title = "Port forwarding: $str1 ${if(str2.isNotEmpty())"and ${str2}" else ""}"
        val text_body = "Running background....."
        val notification = OngoingNotificationBuilder().buildOngoingNotification(
                applicationContext,
                iconId,
                title,
                text_body
        )
        this.notiId = getID()
        startForeground(this.notiId, notification)

    }


}

