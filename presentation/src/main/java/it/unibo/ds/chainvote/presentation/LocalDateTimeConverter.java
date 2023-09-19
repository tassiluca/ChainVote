package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.JsonBindingException;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;

import java.time.LocalDateTime;
import java.util.*;

public final class LocalDateTimeConverter implements Converter<LocalDateTime> {

    @Override
    public void serialize(final LocalDateTime date, final ObjectWriter writer, final Context ctx) {
        writer.beginObject();
        writer.writeString("year", String.valueOf(date.getYear()));
        writer.writeString("month", String.valueOf(date.getMonthValue()));
        writer.writeString("day", String.valueOf(date.getDayOfMonth()));
        writer.writeString("hour", String.valueOf(date.getHour()));
        writer.writeString("minute", String.valueOf(date.getMinute()));
        writer.writeString("second", String.valueOf(date.getSecond()));
        writer.endObject();
    }

    private void conditionallyPutInMap(final Map<String, Integer> map, final ObjectReader reader, final String property) {
        reader.next();
        if (property.equals(reader.name())) {
           map.put(property, reader.valueAsInt());
        } else {
           throw new JsonBindingException("Malformed LocalDateTime json");
        }
    }

    @Override
    public LocalDateTime deserialize(final ObjectReader reader, final Context ctx) {
        reader.beginObject();
        Map<String, Integer> dateMap = new HashMap<>();
        List<String> properties = new ArrayList<>(Arrays.asList("year", "month", "day", "hour", "minute", "second"));
        properties.forEach(property -> conditionallyPutInMap(dateMap, reader, property));
        reader.endObject();
        return LocalDateTime.of(dateMap.get("year"), dateMap.get("month"), dateMap.get("day"),
               dateMap.get("hour"), dateMap.get("minute"), dateMap.get("second"));
    }
}
