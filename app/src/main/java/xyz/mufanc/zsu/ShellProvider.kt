package xyz.mufanc.zsu

import android.annotation.SuppressLint
import android.app.ActivityManagerHidden
import android.content.*
import android.database.Cursor
import android.net.Uri
import android.os.*
import android.util.Log
import dev.rikka.tools.refine.Refine
import org.lsposed.hiddenapibypass.HiddenApiBypass
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ShellProvider : ContentProvider() {

    private val mLatch = CountDownLatch(1)
    private var mService: IRootService? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) = Unit
        override fun onServiceDisconnected(name: ComponentName?) = Unit
    }

    override fun call(method: String, args: String?, extras: Bundle?): Bundle? {
        if (method == METHOD_PUT_SERVICE) {
            mService = IRootService.Stub.asInterface(extras?.getBinder("#"))
            Log.i(App.TAG, "Service received from daemon: $mService")
            mLatch.countDown()
            return null
        }

        val context = context ?: return null

        if (mService == null) {
            val result = context.bindIsolatedService(
                Intent(context, SuLoader.TriggerService::class.java),
                Context.BIND_AUTO_CREATE,
                "ZygoteSU",
                context.mainExecutor,
                connection
            )
            if (!result) {
                Log.e(App.TAG, "Failed to bind isolated service!")
                return null
            }
            mLatch.await(5, TimeUnit.SECONDS)
        }

        if (mService == null) {
            return Bundle().apply { putString("error", "Connect to daemon timeout!") }
        }

        if (extras == null) {
            return Bundle().apply { putString("error", "Call provider extras cannot be null!") }
        }

        val argv = extras.getStringArray("argv")
        val envp = extras.getStringArray("envp")
        val fds = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            extras.getParcelableArray("fds", ParcelFileDescriptor::class.java)
        } else {
            @Suppress("UNCHECKED_CAST")
            extras.getParcelableArray("fds") as Array<out ParcelFileDescriptor>
        }

        return mService?.spawn(argv, envp, fds)
    }

    override fun onCreate(): Boolean = true

    override fun query(p0: Uri, p1: Array<String?>?, p2: String?, p3: Array<String?>?, p4: String?): Cursor? = null

    override fun getType(p0: Uri): String? = null

    override fun insert(p0: Uri, p1: ContentValues?): Uri? = null

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int = 0

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int = 0

    companion object {
        const val METHOD_PUT_SERVICE = "*svc*"
        const val METHOD_RUN_COMMAND = "*cmd*"

        private const val AUTHORITY = "${BuildConfig.APPLICATION_ID}.spawn"

        @SuppressLint("DiscouragedPrivateApi")
        fun call(method: String, args: String?, extras: Bundle?): Bundle? {
            HiddenApiBypass.addHiddenApiExemptions("")

            val iam = ActivityManagerHidden.getService()
            val token = Binder()
            val userId = Process.myUserHandle().hashCode()
            val cph = iam?.getContentProviderExternal(AUTHORITY, userId, token, method)
            val icp = cph?.javaClass?.getDeclaredField("provider")?.get(cph) as IContentProvider?

            var message = "[${Process.myPid()}:${Process.myUid()}] Call provider: iam: $iam cph: $cph icp: $icp"
            println(message)
            Log.i(App.TAG, message)

            if (icp != null) {
                // Todo: 适配旧版 Android 的 IContentProvider.call
                val result = icp.call(
                    Refine.unsafeCast(AttributionSourceHidden(Binder.getCallingUid(), resolveCallingPackage(), null)),
                    AUTHORITY,
                    method, args, extras
                )

                message = "[${Process.myPid()}:${Process.myUid()}] Provider reply: $result"
                Log.i(App.TAG, message)
                println(message)

                iam.removeContentProviderExternalAsUser(AUTHORITY, token, userId)
                return result
            }

            return null
        }

        private fun resolveCallingPackage(): String? {
            return when (Process.myUid()) {
                0 -> "root"
                2000 -> "com.android.shell"
                else -> null
            }
        }
    }
}
