package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A {@link LocalDateTime} converter from class object to json string and vice-versa.
 */
public final class LocalDateTimeConverter implements Converter<LocalDateTime> {

    @Override
    public void serialize(final LocalDateTime date, final ObjectWriter writer, final Context ctx) {
        writer.writeString(date.format(DateTimeFormatter.ISO_DATE_TIME));
    }

    @Override
    public LocalDateTime deserialize(final ObjectReader reader, final Context ctx) {
        final String serialized = reader.valueAsString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return LocalDateTime.parse(serialized, formatter);
    }
}
