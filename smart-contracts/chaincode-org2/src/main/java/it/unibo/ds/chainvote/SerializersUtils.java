package it.unibo.ds.chainvote;

import com.owlike.genson.Genson;
import it.unibo.ds.chainvote.facades.ElectionCompleteFacade;
import it.unibo.ds.chainvote.facades.ElectionCompleteFacadeImpl;
import it.unibo.ds.chainvote.facades.ElectionFacade;
import it.unibo.ds.chainvote.facades.ElectionFacadeImpl;
import it.unibo.ds.chainvote.facades.converters.ElectionToReadConverter;
import it.unibo.ds.chainvote.facades.converters.ElectionWithResultsToReadConverter;

public final class SerializersUtils {

    public static Genson gensonInstance() {
        return GensonUtils.defaultBuilder()
            .withSerializer(new ElectionToReadConverter(), ElectionFacade.class)
            .withSerializer(new ElectionToReadConverter(), ElectionFacadeImpl.class)
            .withSerializer(new ElectionWithResultsToReadConverter(), ElectionCompleteFacade.class)
            .withSerializer(new ElectionWithResultsToReadConverter(), ElectionCompleteFacadeImpl.class)
            .create();
    }
}
