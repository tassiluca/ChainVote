package it.unibo.ds.chainvote;

import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.facades.ElectionCompleteFacade;
import it.unibo.ds.chainvote.facades.ElectionCompleteFacadeImpl;
import it.unibo.ds.chainvote.facades.ElectionFacade;
import it.unibo.ds.chainvote.facades.ElectionFacadeImpl;
import it.unibo.ds.chainvote.facades.converters.ElectionFacadeSerializer;
import it.unibo.ds.chainvote.facades.converters.ElectionCompleteFacadeSerializer;

/**
 * A serialization utility class which provides serializer instances.
 */
public final class SerializersUtils {

    /**
     * @return a ready to use genson serializer instance.
     */
    public static Genson gensonInstance() {
        return GensonUtils.defaultBuilder()
            .withSerializer(new ElectionFacadeSerializer(), ElectionFacade.class)
            .withSerializer(new ElectionFacadeSerializer(), ElectionFacadeImpl.class)
            .withSerializer(new ElectionCompleteFacadeSerializer(), ElectionCompleteFacade.class)
            .withSerializer(new ElectionCompleteFacadeSerializer(), ElectionCompleteFacadeImpl.class)
            .create();
    }
}
