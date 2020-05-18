package com.lightweh.facedetection

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.nfc.Tag
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.common.pos.api.util.Utils.*
import kotlinx.android.synthetic.main.activity_first.*

class FirstActivity : AppCompatActivity() {
    private var mNfcAdapter: NfcAdapter? = null
    private var mPendingIntent: PendingIntent? = null

    var apkInstallListener: AppInstallReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
        val deviceName = PreferencesManager.getInstance().deviceName
        checkStatus()
        deviceName?.let {
            device_name.setText(it)
        }

        if (!isInstalled(this@FirstActivity, "com.telpo.temperatureservice")) {
            showTip("not install TempatureServices.apk")
        }
        //注册监听temperatureservice.apk重装广播
        //注册监听temperatureservice.apk重装广播
        apkInstallListener = AppInstallReceiver()
        val intentFilter = IntentFilter(Intent.ACTION_MEDIA_MOUNTED)
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
        intentFilter.addDataScheme("package")
        registerReceiver(apkInstallListener, intentFilter)
//        val mNfcManager = getSystemService(Context.NFC_SERVICE) as NfcManager
//        mNfcAdapter = mNfcManager.defaultAdapter
//        mPendingIntent = PendingIntent.getActivity(this, 0, Intent(this, javaClass), 0)
//        init_NFC()
    }

    fun checkStatus() {
        val testMode = PreferencesManager.getInstance().testMode
        if (testMode) {
            test_mode.visibility = View.VISIBLE
        } else {
            test_mode.visibility = View.GONE
        }
    }

    fun showTip(msg: String) {
        Toast.makeText(this@FirstActivity, msg, Toast.LENGTH_LONG).show()
    }

    fun onClickConfirm(view: View) {
        val deviceName = device_name.text.toString()
        if (deviceName.isNotEmpty()) {
            PreferencesManager.getInstance().deviceName = deviceName
            startActivity(Intent(this@FirstActivity, MainActivity::class.java))
        }
    }

    private fun isInstalled(context: Context, packageName: String): Boolean {
        val packageManager = context.packageManager
        val pinfo = packageManager.getInstalledPackages(0)
        for (i in pinfo.indices) {
            if (pinfo[i].packageName.equals(packageName, ignoreCase = true)) return true
        }
        return false
    }


    class AppInstallReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val manager = context.packageManager
            if (intent.action == Intent.ACTION_PACKAGE_ADDED) {
                val packageName = intent.data.schemeSpecificPart
                //检测到安装成功是temperatureservice.apk
                if (packageName == "com.telpo.temperatureservice") {
                    Log.e("panya", "packageName : "+ packageName)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        apkInstallListener?.let {
            unregisterReceiver(apkInstallListener)
        }
    }

    var switchModeTabTime = 0
    fun onClickToSwitchMode(view: View) {
        if (switchModeTabTime == 6) {
            PreferencesManager.getInstance().testMode = !PreferencesManager.getInstance().testMode
            checkStatus()
        } else if (switchModeTabTime == 0) {
            Handler().postDelayed(Runnable {
                switchModeTabTime = 0
            }, 3000)
        }
        Log.e("panya", "switchModeTabTime : "+ switchModeTabTime)
        switchModeTabTime +=1
    }

}