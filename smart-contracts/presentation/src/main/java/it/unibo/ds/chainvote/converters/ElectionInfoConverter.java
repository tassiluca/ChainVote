package it.unibo.ds.chainvote.converters;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.JsonBindingException;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import it.unibo.ds.chainvote.assets.ElectionInfo;
import it.unibo.ds.chainvote.assets.ElectionInfoImpl;
import it.unibo.ds.chainvote.utils.Choice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A {@link ElectionInfo} converter from class object to json string and vice-versa.
 */
public class ElectionInfoConverter implements Converter<ElectionInfo> {

    @Override
    public void serialize(final ElectionInfo object, final ObjectWriter writer, final Context ctx) {
        writer.beginObject();
        writer.writeString("goal", object.getGoal());
        writer.writeName("voters");
        writer.writeValue(object.getVotersNumber());
        writer.writeName("startDate");
        writer.writeValue(object.getStartingDate().format(DateTimeFormatter.ISO_DATE_TIME));
        writer.writeName("endDate");
        writer.writeValue(object.getEndingDate().format(DateTimeFormatter.ISO_DATE_TIME));
        writer.writeName("choices");
        writer.beginArray();
        for (Choice vote : object.getChoices()) {
            writer.beginObject();
            writer.writeName("choice");
            writer.writeValue(vote.getChoice());
            writer.endObject();
        }
        writer.endArray();
        writer.endObject();
    }

    @Override
    public ElectionInfo deserialize(final ObjectReader reader, final Context ctx) {
        reader.beginObject();
        ElectionInfo election = null;
        String goal = null;
        long voters = 0;
        LocalDateTime start = null;
        LocalDateTime end = null;
        List<Choice> choices = null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        while (reader.hasNext()) {
            reader.next();
            if ("goal".equals(reader.name())) {
                goal = reader.valueAsString();
            } else if ("voters".equals(reader.name())) {
                voters = reader.valueAsLong();
            } else if ("startDate".equals(reader.name())) {
                start = LocalDateTime.parse(reader.valueAsString(), formatter);
            } else if ("endDate".equals(reader.name())) {
                end = LocalDateTime.parse(reader.valueAsString(), formatter);
            } else if ("choices".equals(reader.name())) {
                choices = new ArrayList<>();
                reader.beginArray();
                while (reader.hasNext()) {
                    reader.next();
                    reader.beginObject();
                    reader.next();
                    choices.add(new Choice(reader.valueAsString()));
                    reader.endObject();
                }
                reader.endArray();
            } else {
                throw new JsonBindingException("Malformed json");
            }
        }
        if (goal == null || voters == 0 || start == null || end == null || choices == null) {
            throw new JsonBindingException("Malformed json: missing value");
        }

        try {
            election = new ElectionInfoImpl.Builder()
                    .goal(goal)
                    .voters(voters)
                    .start(start)
                    .end(end)
                    .choices(choices)
                    .build();
        } catch (IllegalArgumentException | NoSuchElementException e) {
            throw new JsonBindingException("Malformed json");
        }
        reader.endObject();
        return election;
    }
}