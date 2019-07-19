package com.unbi.portopener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.gson.Gson

val PORTVALUE_INTENT = "com.unbi.poropener.PORT"

class MyBroadCastReciever : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
//        if (!Userdata.instance.isReadedfromSpref) {
//            Userdata.instance.readfromSpref(context)
//        }

//        val actionId = intent?.action;
//        when (actionId) {
//            PORTVALUE_INTENT -> {
//                val sock = intent.getIntExtra("extrasock",-1)
//                val http = intent.getIntExtra("extrahttp",-1)
//                Appinstance.instance.portvalue = PortValue(sock,http,false)
//            }
//        }


    }
}


class PortValue(
    val sockPort: Int,
    val httpPortint: Int,
    var isconsume: Boolean = true
)