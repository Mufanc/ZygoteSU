package android.content;

import android.os.Bundle;

public interface IContentProvider {
    Bundle call(String callingPkg, String method, String arg, Bundle extras);

    Bundle call(AttributionSource attributionSource, String authority, String method, String arg, Bundle extras);
}
