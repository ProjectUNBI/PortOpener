package com.unbi.portopener

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.google.gson.Gson
import java.util.ArrayList

class UserInstance private constructor() {
    companion object {
        var instance = UserInstance()
        private val MY_PREFS_NAME = "qwertyuiopasdfg"
        val DEFAULT_THREAD = 8
    }

    var auto_connect: Boolean=false
    var portPairs=ArrayList<PortPair>()
    var noProxySock=3127

    fun saveTosharePref(context: Context) {
        val savable = Gson().toJson(instance)
        val editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit()
        editor.putString(UserInstance::class.java.name, savable)
        editor.apply()
    }

    fun readfromSharePref(context: Context): Boolean {
        val prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE)
        val string = prefs.getString(UserInstance::class.java.name, null)
            ?: return false//"No name defined" is the default value.
        instance = Gson().fromJson(string, UserInstance::class.java) ?: return false
        return true
    }


}

class Appinstance{
    companion object {
        var instance = Appinstance()
    }

    var portOpenerAssync1=AssyncPortOpenner()
    var portOpenerAssync2=AssyncPortOpenner()
    var isConnected=false;

    var portvalue:PortValue= PortValue(-1,-1,true)


}

class PortPair(var localhost:Int=-1, var openport:Int=-1)
