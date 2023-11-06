package it.unibo.ds.chainvote.facades.converters;

import com.owlike.genson.Context;
import com.owlike.genson.Serializer;
import com.owlike.genson.stream.ObjectWriter;
import it.unibo.ds.chainvote.facades.ElectionCompleteFacade;
import it.unibo.ds.chainvote.facades.ElectionFacade;

import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ElectionWithResultsToReadConverter implements Serializer<ElectionCompleteFacade> {

    @Override
    public void serialize(final ElectionCompleteFacade object, final ObjectWriter writer, final Context ctx) {
        ElectionFacade etr = object.getElectionFacade();
        writer.beginObject();
        writer.writeString("status", etr.getStatus().getKey());
        writer.writeString("id", etr.getId());
        writer.writeString("goal", etr.getGoal());
        writer.writeName("startDate");
        writer.writeValue(etr.getStartDate().format(DateTimeFormatter.ISO_DATE_TIME));
        writer.writeName("endDate");
        writer.writeValue(etr.getEndDate().format(DateTimeFormatter.ISO_DATE_TIME));
        writer.writeString("affluence", etr.getAffluence()*100 + "%");
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
