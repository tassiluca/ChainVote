package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.*;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import it.unibo.ds.core.assets.Election;
import it.unibo.ds.core.assets.ElectionImpl;
import it.unibo.ds.core.utils.Choice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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
        Genson genson = GensonUtils.create();

        List<Choice> choices = new ArrayList<>();

        reader.next();
        if ("value".equals(reader.name())) {
            reader.beginArray();
            while (reader.hasNext()) {
                reader.beginObject();
                reader.next();
                if ("choice".equals(reader.name())) {
                    System.out.println(reader.valueAsString());
                    choices.add(new Choice(reader.valueAsString()));
                }
            }
            reader.endArray();
        }
        reader.endObject();
        return choices;
    }
}
