package it.unibo.ds.chainvote;

import com.owlike.genson.Genson;

public final class SerializersUtils {

    public static Genson gensonInstance() {
        return GensonUtils.defaultBuilder().create();
    }
}
