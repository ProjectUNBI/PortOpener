package com.unbi.portopener

import android.content.Context
import android.os.Handler
import android.widget.Toast


class ToastHandler private constructor() {

    // General attributes
    private var mContext: Context? = null
    private var mHandler: Handler? = null

    /**
     * Shows a `Toast` using a `Handler`. Can be used in
     * background processes.
     *
     * @param _text     The text to show. Can be formatted text.
     * @param _duration How long to display the message. Only use LENGTH_LONG or
     * LENGTH_SHORT from `Toast`.
     */

    private var cooldown = 0L
    internal var minimumCool = 5000L

    /**
     * Class constructor.
     *
     * @param context The `Context` for showing the `Toast`
     */

    fun setmContext(context: Context) {
        this.mContext = context
        this.mHandler = Handler()
    }

    fun release() {
        this.mContext = null
        this.mHandler = null
    }

    /**
     * Runs the `Runnable` in a separate `Thread`.
     *
     * @param _runnable The `Runnable` containing the `Toast`
     */
    private fun runRunnable(_runnable: Runnable) {
        var thread: Thread? = object : Thread() {
            override fun run() {
                if (mHandler == null) {
                    return
                }
                mHandler!!.post(_runnable)
            }
        }

        thread!!.start()
        thread.interrupt()
        thread = null
    }

    /**
     * Shows a `Toast` using a `Handler`. Can be used in
     * background processes.
     *
     * @param _resID    The resource id of the string resource to use. Can be
     * formatted text.
     * @param _duration How long to display the message. Only use LENGTH_LONG or
     * LENGTH_SHORT from `Toast`.
     */
    fun showToast(_resID: Int, _duration: Int) {
        val runnable = Runnable {
            // Get the text for the given resource ID
            val text = mContext!!.resources.getString(_resID)

            Toast.makeText(mContext, text, _duration).show()
        }

        runRunnable(runnable)
    }

    fun showToast(_text: CharSequence, _duration: Int) {
        if (System.currentTimeMillis() - this.cooldown > minimumCool) {
            this.cooldown = System.currentTimeMillis()
            val runnable = Runnable { Toast.makeText(mContext, _text, _duration).show() }
            runRunnable(runnable)
        }
    }

    fun showToast(_text: CharSequence, _duration: Int, cooldown: Int) {
        if (System.currentTimeMillis() - this.cooldown > cooldown * 1000) {
            this.cooldown = System.currentTimeMillis()
            val runnable = Runnable { Toast.makeText(mContext, _text, _duration).show() }
            runRunnable(runnable)
        }
    }

    fun showToast(_text: CharSequence, _duration: Int, repeated: Boolean) {
        if (repeated) {
            val runnable = Runnable { Toast.makeText(mContext, _text, _duration).show() }

            runRunnable(runnable)
        } else {
            showToast(_text, _duration)
        }
    }

    companion object {
        val instance = ToastHandler()
    }
}
