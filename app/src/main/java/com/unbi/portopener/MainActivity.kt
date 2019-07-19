package com.unbi.portopener

import android.app.Application
import android.content.*
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.support.v7.widget.CardView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.support.v4.content.LocalBroadcastManager
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Intent.CATEGORY_DEFAULT
import android.opengl.Visibility
import android.view.View.GONE
import android.widget.TextView
import com.unbi.portopener.extracode.SendBroadcast


class MainActivity : ServiceConnection, AppCompatActivity(), View.OnClickListener {


    private var mBound: Boolean = false//true whwn service is bounded
    var mService: PortOpenerService? = null

    lateinit var but_start: Button
    lateinit var but_stop: Button


    lateinit var but_test_local1: Button
    lateinit var but_test_open1: Button
    lateinit var but_set: Button
    lateinit var edit_local1: EditText
    lateinit var edit_open1: EditText

    lateinit var but_test_local2: Button
    lateinit var but_test_open2: Button
    lateinit var edit_local2: EditText
    lateinit var edit_open2: EditText

    lateinit var card_porxy_port: CardView
    lateinit var tvsockport: TextView
    lateinit var tvhttpport: TextView

    lateinit var edit_noproxy_sockport:EditText
    lateinit var but_noproxy_sockport_test:Button

    val reciever = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val actionId = intent?.action;
            when (actionId) {
                PORTVALUE_INTENT -> {
                    val sock = intent.getIntExtra("extrasock",-1)
                    val http = intent.getIntExtra("extrahttp",-1)
                    Appinstance.instance.portvalue = PortValue(sock,http,false)
                }
            }
            updatecardiewProxy()
        }
    }

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

        but_noproxy_sockport_test=findViewById(R.id.but_noproxy_sock_port)
        but_noproxy_sockport_test.setOnClickListener(this)
        edit_noproxy_sockport=findViewById(R.id.edit_noproxy_sock_port)
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

        card_porxy_port = findViewById(R.id.cardview_sock_http)
        tvsockport = findViewById(R.id.textview_sock)
        tvhttpport = findViewById(R.id.textview_http_port)
        but_set = findViewById(R.id.but_setvalue_from_intent)
        but_set.setOnClickListener(this)
        updatecardiewProxy()

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
        edit_noproxy_sockport.setText(UserInstance.instance.noProxySock.toString())

        ToastHandler.instance.setmContext(this)
        refreshview()

    }

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
                        UserInstance.instance.portPairs[0].openport == UserInstance.instance.portPairs[1].openport ||
                        UserInstance.instance.portPairs[0].localhost == UserInstance.instance.portPairs[1].localhost
                    ) {
                        Toast.makeText(this, "Open port should be different", Toast.LENGTH_SHORT)
                            .show()
                        return
                    }

                    if (
                        !(validport(UserInstance.instance.portPairs[0]) ||
                                validport(UserInstance.instance.portPairs[1]))
                    ) {
                        Toast.makeText(this, "None of the port pairs is valid", Toast.LENGTH_SHORT)
                            .show()
                        return
                    }
                    if(!validport(UserInstance.instance.noProxySock)){
                        Toast.makeText(this, "No proxy sock port is not valid", Toast.LENGTH_SHORT)
                            .show()
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
                R.id.but_setvalue_from_intent -> {
                    UserInstance.instance.portPairs[0].localhost =
                        Appinstance.instance.portvalue.sockPort
                    UserInstance.instance.portPairs[1].localhost =
                        Appinstance.instance.portvalue.httpPortint
                    card_porxy_port.visibility=GONE
                    Appinstance.instance.portvalue.isconsume=true
                    refreshview()
                }
                R.id.but_noproxy_sock_port->{
                    saveeditvalues()
                    test(UserInstance.instance.noProxySock)

                    // the following commentted is for just testing code
//                    val sendBroadcast=SendBroadcast()
//                    sendBroadcast.httpport=1
//                    sendBroadcast.sockport=22
//                    sendBroadcast.send(applicationContext)
                }
            }
        }
    }

    private fun validport(portPair: PortPair): Boolean {
        return portPair.localhost > -1 && portPair.localhost < 65535 &&
                portPair.openport > -1 && portPair.openport < 65535
    }
    private fun validport(port: Int): Boolean {
        return port> -1 && port < 65535
    }

    private fun test(port: Int) {
        WebCheckAssync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, port)
    }

    private fun saveeditvalues() {
        try {
            UserInstance.instance.noProxySock = edit_noproxy_sockport.text.toString().toInt()
        } catch (e: Exception) {
            Log.v("Error", "EmptyString")
        }
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

    private fun updatecardiewProxy() {
        if (!Appinstance.instance.portvalue.isconsume) {
            card_porxy_port.visibility = View.VISIBLE
            tvsockport.setText(Appinstance.instance.portvalue.sockPort.toString())
            tvhttpport.setText(Appinstance.instance.portvalue.httpPortint.toString())

        } else {
            card_porxy_port.visibility = View.GONE
        }
    }

    private fun refreshview() {
        if (Appinstance.instance.isConnected) {
            but_start.isEnabled = false
            but_stop.isEnabled = true
        } else {
            but_start.isEnabled = true
            but_stop.isEnabled = false
        }
        edit_noproxy_sockport.setText(UserInstance.instance.noProxySock.toString())

        if (UserInstance.instance.portPairs[0].localhost > -1) {
            edit_local1.setText(UserInstance.instance.portPairs[0].localhost.toString())
        }
        if (UserInstance.instance.portPairs[0].openport > -1) {
            edit_open1.setText(UserInstance.instance.portPairs[0].openport.toString())
        }

        if (UserInstance.instance.portPairs[1].localhost > -1) {
            edit_local2.setText(UserInstance.instance.portPairs[1].localhost.toString())
        }
        if (UserInstance.instance.portPairs[1].openport > -1) {
            edit_open2.setText(UserInstance.instance.portPairs[1].openport.toString())
        }

    }


    override fun onStart() {
        super.onStart()
        val intentFilter=IntentFilter("com.unbi.poropener.PORT")
        intentFilter.addCategory(CATEGORY_DEFAULT)
        registerReceiver(reciever,intentFilter)
        LocalBroadcastManager.getInstance(this).registerReceiver(
            (reciever),
            IntentFilter(RECEVER_LOCAL_BROADCAS)
        );
        val intent = Intent(this, PortOpenerService::class.java)
        intent.putExtra(PORT_SERVICE_EXTRA, MainActivity::class.java.canonicalName)
        bindService(intent, this, Context.BIND_AUTO_CREATE)
        startService(intent)
    }


    override fun onResume() {
        super.onResume()
        ToastHandler.instance.setmContext(this)
        updatecardiewProxy()
    }

    override fun onPause() {
        super.onPause()
        ToastHandler.instance.release()

    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciever);
        if (mBound) {
            unbindService(this)
            mBound = false
        }
        ToastHandler.instance.release()
    }


}
