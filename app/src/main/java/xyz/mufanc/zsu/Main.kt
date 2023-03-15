package xyz.mufanc.zsu

import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.os.Process
import android.util.Log
import java.io.File
import java.util.concurrent.CountDownLatch

object Main {
    @JvmStatic
    fun main(vararg args: String) {
        try {
            if (Process.myUid() == 0) {
                @Suppress("UNCHECKED_CAST")
                val libraryDirs = javaClass.classLoader!!["pathList"]["nativeLibraryDirectories"] as ArrayList<File>
                libraryDirs.add(File(System.getProperty("libsu.library.path")!!))
                System.loadLibrary("su")

                ShellProvider.call(ShellProvider.METHOD_PUT_SERVICE, "", Bundle().apply { putBinder("#", RootService()) })
                CountDownLatch(1).await()
            } else {
                val fds = arrayOf(
                    ParcelFileDescriptor.fromFd(0),
                    ParcelFileDescriptor.fromFd(1),
                    ParcelFileDescriptor.fromFd(2),
                )
                val result = ShellProvider.call(
                    ShellProvider.METHOD_RUN_COMMAND,
                    null,
                    Bundle().apply {
                        putParcelableArray("fds", fds)
                        putStringArray("argv", if (args.isEmpty()) arrayOf("/system/bin/sh") else args)
                    }
                )

                if (result == null) {
                    println("Null reply!")
                    return
                }

                val error = result.getString("error")
                if (error != null) {
                    println("Error: $error")
                    return
                }

                println(result)
                CountDownLatch(1).await()
            }
        } catch (err: Throwable) {
            Log.e(App.TAG, "", err)
        }
    }

    private operator fun Any.get(name: String): Any {
        var klass = javaClass
        while (true) {
            try {
                return klass.getDeclaredField(name)
                    .apply { isAccessible = true }
                    .get(this)!!
            } catch (err: NoSuchFieldException) {
                klass = klass.superclass!!
            }
        }
    }
}
