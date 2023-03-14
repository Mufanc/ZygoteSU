package xyz.mufanc.zsu

import android.app.Service
import android.app.ZygotePreload
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.IBinder
import android.os.Process
import android.os.SystemProperties
import android.system.Os
import android.util.Log
import java.io.File

class SuLoader : ZygotePreload {
    override fun doPreload(info: ApplicationInfo) {
        System.loadLibrary("su")
        Log.i(App.TAG, "Uid: ${Process.myUid()}")
        try {
            if (Os.geteuid() == 0) {
                val abi = SystemProperties.get("ro.product.cpu.abi")
                val appDir = File(info.nativeLibraryDir).parentFile!!.parent!!

                val proc = Runtime.getRuntime().exec(arrayOf(
                    "/system/bin/app_process",
                    "-Djava.class.path=${appDir}/base.apk",
                    "-Dlibsu.library.path=${appDir}/lib/${if (abi == "arm64-v8a") "arm64" else "x86_64"}",
                    "/system/bin",
                    "--nice-name=zsud",
                    Main::class.java.name
                ))
                Log.i(App.TAG, "Spawn daemon: $proc")
            }
        } catch (err: Throwable) {
            Log.e(App.TAG, "", err)
        }
    }

    class TriggerService : Service() {
        override fun onBind(intent: Intent?): IBinder? {
            Log.i(App.TAG, "Trigger onBind()")
            return null
        }
    }
}
