package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.Genson;
import com.owlike.genson.JsonBindingException;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import it.unibo.ds.core.utils.Choice;

import java.util.ArrayList;
import java.util.List;

public class ListOfChoiceConverter implements Converter<List<Choice>> {

    @Override
    public void serialize(final List<Choice> object, final ObjectWriter writer, final Context ctx) {
        Genson genson = GensonUtils.create();
        writer.beginObject();
        for (Choice choice: object) {
            writer.writeString("choice", genson.serialize(choice));
        }
        writer.endObject();
    }

    @Override
    public List<Choice> deserialize(final ObjectReader reader, final Context ctx) {
        reader.beginObject();
        List<Choice> choices = new ArrayList<>();
        reader.next();
        if ("value".equals(reader.name())) {
            reader.beginArray();
            while (reader.hasNext()) {
                reader.next();
                reader.beginObject();
                reader.next();
                if ("choice".equals(reader.name())) {
                    choices.add(new Choice(reader.valueAsString()));
                } else {
                    throw new JsonBindingException("Malformed json: missing value");
                }
                reader.endObject();
            }
            reader.endArray();
        }
        reader.endObject();
        return choices;
    }
}
