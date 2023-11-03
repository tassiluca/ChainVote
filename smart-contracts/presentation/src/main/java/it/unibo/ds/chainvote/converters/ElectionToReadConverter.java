package it.unibo.ds.chainvote.converters;

import com.owlike.genson.Context;
import com.owlike.genson.Serializer;
import com.owlike.genson.stream.ObjectWriter;
import it.unibo.ds.chainvote.assets.presentation.ElectionFacade;

import java.time.format.DateTimeFormatter;

public class ElectionToReadConverter implements Serializer<ElectionFacade> {

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
        writer.writeString("affluence", object.getAffluence()*100 + "%");
        writer.endObject();
    }
}
