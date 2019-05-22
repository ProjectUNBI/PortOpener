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

        val actionId = intent?.action;
        when (actionId) {
            PORTVALUE_INTENT -> {
                val string = intent.getStringExtra("extra")
                Appinstance.instance.portvalue = Gson().fromJson(string, PortValue::class.java)
            }
        }


    }
}


class PortValue(
    val sockPort: Int,
    val httpPortint: Int,
    val isconsume: Boolean = true
)