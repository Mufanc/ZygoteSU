package xyz.mufanc.zsu

import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.system.Os
import android.util.Log
import androidx.core.os.bundleOf

class RootService : IRootService.Stub() {

    private external fun fork(): Int

    override fun spawn(
        filename: String?,
        argv: Array<out String>?,
        envp: Array<out String>?
    ) : Bundle {
        Log.i(App.TAG, "Spawn request: $filename")

        val (r_stdin, w_stdin) = ParcelFileDescriptor.createPipe()
        val (r_stdout, w_stdout) = ParcelFileDescriptor.createPipe()
        val (r_stderr, w_stderr) = ParcelFileDescriptor.createPipe()
        if (fork() == 0) {
            Os.dup2(r_stdin.fileDescriptor, 0)
            Os.dup2(w_stdout.fileDescriptor, 1)
            Os.dup2(w_stderr.fileDescriptor, 2)
            Os.execve(filename, argv, envp)
            throw IllegalStateException()
        }
        r_stdin.close()
        w_stdout.close()
        w_stderr.close()

        // Todo: check fds
        return bundleOf("stdin" to w_stdin, "stdout" to r_stdout, "stderr" to r_stderr)
    }
}
