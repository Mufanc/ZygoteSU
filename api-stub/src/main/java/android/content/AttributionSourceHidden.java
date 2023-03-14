package android.content;

import dev.rikka.tools.refine.RefineAs;

@RefineAs(AttributionSource.class)
public class AttributionSourceHidden {
    public AttributionSourceHidden(int uid, String packageName, String attributionTag) {
        throw new RuntimeException("STUB");
    }
}
