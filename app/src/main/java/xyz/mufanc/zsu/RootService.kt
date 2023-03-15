package xyz.mufanc.zsu

import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.system.Os
import android.util.Log

class RootService : IRootService.Stub() {

    private external fun fork(): Int

    override fun spawn(
        argv: Array<out String>,
        envp: Array<out String>?,
        fds: Array<ParcelFileDescriptor>
    ) : Bundle {
        Log.i(App.TAG, "Spawn request: ${argv[0]}")
        if (fork() == 0) {
            Os.dup2(fds[0].fileDescriptor, 0)
            Os.dup2(fds[1].fileDescriptor, 1)
            Os.dup2(fds[2].fileDescriptor, 2)
            Os.execve(argv[0], argv, envp)
            throw IllegalStateException()
        }
        return Bundle()
    }
}
