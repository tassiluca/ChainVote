package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.JsonBindingException;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import it.unibo.ds.core.codes.AlreadyConsumedCodeException;
import it.unibo.ds.core.codes.OneTimeCode;
import it.unibo.ds.core.codes.OneTimeCodeImpl;

/**
 * A {@link OneTimeCode} converter from class object to json string and vice-versa.
 */
public final class OneTimeCodeConverter implements Converter<OneTimeCode> {

    @Override
    public void serialize(final OneTimeCode object, final ObjectWriter writer, final Context ctx) {
        writer.beginObject();
        writer.writeString("otc", object.getCode());
        writer.writeString("consumed", Boolean.toString(object.consumed()));
        writer.endObject();
    }

    @Override
    public OneTimeCode deserialize(final ObjectReader reader, final Context ctx) {
        OneTimeCode code = null;
        Boolean consumed = null;
        reader.beginObject();
        while (reader.hasNext()) {
            reader.next();
            if ("otc".equals(reader.name())) {
                code = new OneTimeCodeImpl(reader.valueAsString());
            } else if ("consumed".equals(reader.name())) {
                consumed = reader.valueAsBoolean();
            } else {
                throw new JsonBindingException("Malformed json");
            }
        }
        if (code == null || consumed == null) {
            throw new JsonBindingException("Malformed json: missing value");
        } else if (consumed) {
            try { code.consume(); } catch (AlreadyConsumedCodeException ignored) { }
        }
        return code;
    }
}
