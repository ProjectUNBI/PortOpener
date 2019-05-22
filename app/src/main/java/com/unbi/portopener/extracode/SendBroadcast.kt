package com.unbi.portopener.extracode

import android.content.Context
import android.content.Intent
import com.google.gson.Gson

class SendBroadcast {
    var sockport=-1
    var httpport=-1
    val PORTVALUE_INTENT = "com.unbi.poropener.PORT"

    fun send(context: Context){

        if(sockport< 0|| httpport<0){
         return
        }
        val intent =  Intent(PORTVALUE_INTENT);
        intent.putExtra("extrasock",sockport)
        intent.putExtra("extrahttp",httpport)
        context.sendBroadcast(intent);
        sockport=-1
        httpport=-1
    }

}

val sendbroadcast:SendBroadcast= SendBroadcast()

fun setsockport(int:Int){
    sendbroadcast.sockport=int
}
fun sethttpport(int:Int){
    sendbroadcast.httpport=int
}

fun broadcast(context:Context){
    sendbroadcast.send(context )
}

fun sample(int1: Int,int2: Int,context: Context){
    setsockport(int1)

    sethttpport(int2)

    broadcast(context)

}

class PortValueExtra(
    val sockPort: Int,
    val httpPortint: Int,
    val isconsume: Boolean = true
)