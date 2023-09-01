package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import it.unibo.ds.core.utils.Choice;

public class ChoiceConverter  implements Converter<Choice> {

    @Override
    public void serialize(final Choice object, final ObjectWriter writer, final Context ctx) {
        writer.beginObject();
        writer.writeString("choice", object.getChoice());
        writer.endObject();
    }

    @Override
    public Choice deserialize(final ObjectReader reader, final Context ctx) {
        reader.beginObject();
        String choice = null;

        reader.next();
        if ("choice".equals(reader.name())) {
            choice = reader.valueAsString();
        }
        reader.endObject();
        return new Choice(choice);
    }
}
