package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.Genson;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import it.unibo.ds.core.assets.Ballot;
import it.unibo.ds.core.utils.Choice;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A results converter from class object to json string and vice-versa.
 */
public class MapOfChoiceLongConverter implements Converter<Map<Choice, Long>> {

    @Override
    public void serialize(final Map<Choice, Long> object, final ObjectWriter writer, final Context ctx) {
        Genson genson = GensonUtils.create();
        writer.beginObject();
        for (Choice choice: object.keySet()) {
            writer.writeString(genson.serialize(choice) + ":");
            writer.writeString(genson.serialize(object.get(choice)));
        }
        writer.endObject();
    }

    @Override
    public Map<Choice, Long> deserialize(final ObjectReader reader, final Context ctx) {
        Map<Choice, Long> retMap = new HashMap<>();
        reader.beginObject();
        while (reader.hasNext()) {
            reader.next();
            Choice choice = null;
            Pattern p = Pattern.compile("\\'.*?\\'");
            Matcher m = p.matcher(reader.name());
            if (m.find()) {
                choice = new Choice((String) m.group().subSequence(1, m.group().length()-1));
            }
            long voters = reader.valueAsLong();
            retMap.put(choice, voters);
        }
        reader.endObject();
        return retMap;
    }
}
