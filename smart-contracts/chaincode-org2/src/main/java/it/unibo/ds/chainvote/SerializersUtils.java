package it.unibo.ds.chainvote;

import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.facade.ElectionFacade;
import it.unibo.ds.chainvote.facade.ElectionFacadeImpl;
import it.unibo.ds.chainvote.facade.converter.ElectionFacadeSerializer;

/**
 * A serialization utility class which provides serializer instances.
 */
public final class SerializersUtils {

    private SerializersUtils() { }

    /**
     * @return a ready to use genson serializer instance.
     */
    public static Genson gensonInstance() {
        return GensonUtils.defaultBuilder()
            .withSerializer(new ElectionFacadeSerializer(), ElectionFacade.class)
            .withSerializer(new ElectionFacadeSerializer(), ElectionFacadeImpl.class)
            .create();
    }
}
