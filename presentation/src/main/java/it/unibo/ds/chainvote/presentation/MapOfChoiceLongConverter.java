package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.Genson;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import it.unibo.ds.core.utils.Choice;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapOfChoiceLongConverter implements Converter<Map<Choice, Long>> {

    @Override
    public void serialize(final Map<Choice, Long> object, final ObjectWriter writer, final Context ctx) {
        Genson genson = GensonUtils.create();
        writer.beginObject();
        writer.beginArray();
        for (Choice choice: object.keySet()) {
            writer.beginObject();
            writer.writeString("key", genson.serialize(choice));
            writer.writeString("value", genson.serialize(object.get(choice)));
            writer.endObject();
        }
        writer.endArray();
        writer.endObject();
    }

    @Override
    public Map<Choice, Long> deserialize(final ObjectReader reader, final Context ctx) {
        reader.beginObject();
        Map<Choice, Long> retMap = new HashMap<>();
        reader.next();
        if ("value".equals(reader.name())) {
            reader.beginObject();
            while (reader.hasNext()) {
                reader.next();
                Pattern p = Pattern.compile("\\'.*?\\'");
                Matcher m = p.matcher(reader.name());
                Choice choice = null;
                if (m.find()) {
                    choice = new Choice((String) m.group().subSequence(1, m.group().length()-1));
                }
                reader.beginObject();
                reader.next();
                long voters = reader.valueAsLong();
                reader.endObject();
                retMap.put(choice, voters);
            }
            reader.endObject();
        }
        reader.endObject();
        return retMap;
    }
}
