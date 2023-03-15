package xyz.mufanc.zsu;

interface IRootService {
    Bundle spawn(in String[] argv, in String[] envp, in ParcelFileDescriptor[] fds);
}
