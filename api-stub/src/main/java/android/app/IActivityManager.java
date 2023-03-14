package android.app;

import android.os.IBinder;

public interface IActivityManager {

    ContentProviderHolder getContentProviderExternal(String name, int userId, IBinder token, String tag);

    void removeContentProviderExternalAsUser(String name, IBinder token, int userId);
}
