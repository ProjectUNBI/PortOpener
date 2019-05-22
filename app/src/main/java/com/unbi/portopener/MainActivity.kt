package com.unbi.portopener

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG

class MainActivity : ServiceConnection, AppCompatActivity(), View.OnClickListener {


    private var mBound: Boolean = false//true whwn service is bounded
    var mService: PortOpenerService? = null
    override fun onServiceDisconnected(name: ComponentName?) {
        mBound = false;

    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as PortOpenerService.LocalBinder
        mService = binder.service as PortOpenerService
        mBound = true
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.but_open_now -> {
                    saveeditvalues()
                    if (
                            UserInstance.instance.portPairs[0].openport == UserInstance.instance.portPairs[1].openport||
                            UserInstance.instance.portPairs[0].localhost== UserInstance.instance.portPairs[1].localhost
                    ) {
                        Toast.makeText(this, "Open port should be different", Toast.LENGTH_SHORT).show()
                        return
                    }

                    if (
                            !(validport(UserInstance.instance.portPairs[0]) ||
                                    validport(UserInstance.instance.portPairs[1]))
                    ) {
                        Toast.makeText(this, "None of the port pairs is valid", Toast.LENGTH_SHORT).show()
                        return
                    }

                    mService?.StartService()
                    Appinstance.instance.isConnected = true
                    refreshview()
                }
                R.id.but_close_now -> {
                    mService?.StopService()
                    Appinstance.instance.isConnected = false
                    refreshview()
                }
                R.id.but_test_localhost_port1 -> {
                    saveeditvalues()
                    test(UserInstance.instance.portPairs[0].localhost)

                }
                R.id.but_test_open_port1 -> {
                    saveeditvalues()
                    test(UserInstance.instance.portPairs[0].openport)

                }

                R.id.but_test_localhost_port2 -> {
                    saveeditvalues()
                    test(UserInstance.instance.portPairs[1].localhost)

                }
                R.id.but_test_open_port2 -> {
                    saveeditvalues()
                    test(UserInstance.instance.portPairs[1].openport)

                }
            }
        }
    }

    private fun validport(portPair: PortPair): Boolean {
        return portPair.localhost > -1 && portPair.localhost < 65535 &&
                portPair.openport > -1 && portPair.openport < 65535
    }

    private fun test(port: Int) {
        WebCheckAssync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, port)
    }

    private fun saveeditvalues() {
        try {
            UserInstance.instance.portPairs[0].localhost = edit_local1.text.toString().toInt()
        } catch (e: Exception) {
            UserInstance.instance.portPairs[0].localhost = -1
            Log.v("Error", "EmptyString")
        }
        try {
            UserInstance.instance.portPairs[0].openport = edit_open1.text.toString().toInt()
        } catch (e: Exception) {
            UserInstance.instance.portPairs[0].openport = -1
            Log.v("Error", "EmptyString")
        }
        try {
            UserInstance.instance.portPairs[1].localhost = edit_local2.text.toString().toInt()
        } catch (e: Exception) {
            UserInstance.instance.portPairs[1].localhost = -1
            Log.v("Error", "EmptyString")
        }
        try {
            UserInstance.instance.portPairs[1].openport = edit_open2.text.toString().toInt()
        } catch (e: Exception) {
            Log.v("Error", "EmptyString")
            UserInstance.instance.portPairs[1].openport = -1
        }
        UserInstance.instance.saveTosharePref(applicationContext)

    }

    lateinit var but_start: Button
    lateinit var but_stop: Button


    lateinit var but_test_local1: Button
    lateinit var but_test_open1: Button
    lateinit var edit_local1: EditText
    lateinit var edit_open1: EditText

    lateinit var but_test_local2: Button
    lateinit var but_test_open2: Button
    lateinit var edit_local2: EditText
    lateinit var edit_open2: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        UserInstance.instance.readfromSharePref(applicationContext)
        if (UserInstance.instance.portPairs.size != 2) {
            UserInstance.instance.portPairs = ArrayList<PortPair>()
            UserInstance.instance.portPairs.add(PortPair())
            UserInstance.instance.portPairs.add(PortPair())
            UserInstance.instance.saveTosharePref(this)
        }
        but_start = findViewById(R.id.but_open_now)
        but_start.setOnClickListener(this)
        but_stop = findViewById(R.id.but_close_now)
        but_stop.setOnClickListener(this)
        but_test_local1 = findViewById(R.id.but_test_localhost_port1)
        but_test_local1.setOnClickListener(this)
        but_test_open1 = findViewById(R.id.but_test_open_port1)
        but_test_open1.setOnClickListener(this)
        edit_local1 = findViewById(R.id.edit_local_host_port1)
        edit_open1 = findViewById(R.id.edit_open_port1)


        but_test_local2 = findViewById(R.id.but_test_localhost_port2)
        but_test_local2.setOnClickListener(this)
        but_test_open2 = findViewById(R.id.but_test_open_port2)
        but_test_open2.setOnClickListener(this)
        edit_local2 = findViewById(R.id.edit_local_host_port2)
        edit_open2 = findViewById(R.id.edit_open_port2)

        for (int in 0 until UserInstance.instance.portPairs.size) {
            when (int) {
                0 -> {
                    if (UserInstance.instance.portPairs[int].localhost > -1) {
                        edit_local1.setText(UserInstance.instance.portPairs[int].localhost.toString())
                    }
                    if (UserInstance.instance.portPairs[int].openport > -1) {
                        edit_open1.setText(UserInstance.instance.portPairs[int].openport.toString())
                    }
                }

                1 -> {
                    if (UserInstance.instance.portPairs[int].openport > -1) {
                        edit_local2.setText(UserInstance.instance.portPairs[int].localhost.toString())
                    }
                    if (UserInstance.instance.portPairs[int].openport > -1) {
                        edit_open2.setText(UserInstance.instance.portPairs[int].openport.toString())
                    }

                }


            }


        }


        ToastHandler.instance.setmContext(this)
        refreshview()
    }

    private fun refreshview() {
        if (Appinstance.instance.isConnected) {
            but_start.isEnabled = false
            but_stop.isEnabled = true
        } else {
            but_start.isEnabled = true
            but_stop.isEnabled = false
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, PortOpenerService::class.java)
        intent.putExtra(PORT_SERVICE_EXTRA, MainActivity::class.java.canonicalName)
        bindService(intent, this, Context.BIND_AUTO_CREATE)
        startService(intent)
    }

    override fun onResume() {
        super.onResume()
        ToastHandler.instance.setmContext(this)

    }

    override fun onPause() {
        super.onPause()
        ToastHandler.instance.release()

    }

    override fun onStop() {
        super.onStop()
        if (mBound) {
            unbindService(this)
            mBound = false
        }
        ToastHandler.instance.release()
    }


}
