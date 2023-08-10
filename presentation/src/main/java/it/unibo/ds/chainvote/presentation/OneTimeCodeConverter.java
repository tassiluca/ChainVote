package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.JsonBindingException;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import it.unibo.ds.core.codes.OneTimeCode;
import it.unibo.ds.core.codes.OneTimeCodeImpl;

/**
 * A {@link OneTimeCode} converter from class object to json string and vice-versa.
 */
public final class OneTimeCodeConverter implements Converter<OneTimeCode> {
    @Override
    public void serialize(final OneTimeCode object, final ObjectWriter writer, final Context ctx) {
        writer.beginObject();
        writer.writeString("otc", Long.toString(object.getCode()));
        writer.endObject();
    }

    @Override
    public OneTimeCode deserialize(final ObjectReader reader, final Context ctx) {
        reader.beginObject();
        reader.next();
        if ("otc".equals(reader.name())) {
            return new OneTimeCodeImpl(reader.valueAsLong());
        }
        throw new JsonBindingException("Malformed json");
    }
}
