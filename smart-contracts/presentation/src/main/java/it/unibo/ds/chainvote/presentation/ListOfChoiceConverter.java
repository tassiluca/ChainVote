package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.Genson;
import com.owlike.genson.JsonBindingException;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import it.unibo.ds.core.assets.Ballot;
import it.unibo.ds.core.utils.Choice;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of {@link Choice} converter from class object to json string and vice-versa.
 */
public class ListOfChoiceConverter implements Converter<List<Choice>> {

    @Override
    public void serialize(final List<Choice> object, final ObjectWriter writer, final Context ctx) {
        Genson genson = GensonUtils.create();
        writer.beginArray();
        for (Choice choice: object) {
            writer.writeString("choice", genson.serialize(choice));
        }
        writer.endArray();
    }

    @Override
    public List<Choice> deserialize(final ObjectReader reader, final Context ctx) {
        List<Choice> choices = new ArrayList<>();
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
        return choices;
    }
}
