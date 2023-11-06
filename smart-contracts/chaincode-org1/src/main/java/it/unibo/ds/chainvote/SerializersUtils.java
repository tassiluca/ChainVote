package it.unibo.ds.chainvote;

import com.owlike.genson.Genson;

/**
 * A serialization utility class which provides serializer instances.
 */
public final class SerializersUtils {

    /**
     * @return a ready to use genson serializer instance.
     */
    public static Genson gensonInstance() {
        return GensonUtils.defaultBuilder().create();
    }
}
