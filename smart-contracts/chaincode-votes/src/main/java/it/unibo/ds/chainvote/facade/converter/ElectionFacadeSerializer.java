package it.unibo.ds.chainvote.facade.converter;

import com.owlike.genson.Context;
import com.owlike.genson.Serializer;
import com.owlike.genson.stream.ObjectWriter;
import it.unibo.ds.chainvote.facade.ElectionFacade;

import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * A custom serializer for {@link ElectionFacade} class.
 */
public final class ElectionFacadeSerializer implements Serializer<ElectionFacade> {

    @Override
    public void serialize(final ElectionFacade object, final ObjectWriter writer, final Context ctx) {
        writer.beginObject();
        writer.writeString("status", object.getStatus().getKey());
        writer.writeString("id", object.getId());
        writer.writeString("goal", object.getGoal());
        writer.writeName("startDate");
        writer.writeValue(object.getStartDate().format(DateTimeFormatter.ISO_DATE_TIME));
        writer.writeName("endDate");
        writer.writeValue(object.getEndDate().format(DateTimeFormatter.ISO_DATE_TIME));
        writer.writeString("affluence", object.getAffluence() * 100 + "%");
        writer.writeName("results");
        writer.beginObject();
        for (Map.Entry<String, Long> entry : object.getResults().entrySet()) {
            writer.writeName(entry.getKey());
            writer.writeValue(entry.getValue());
        }
        writer.endObject();
        writer.endObject();
    }
}
