package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.*;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import it.unibo.ds.core.assets.Ballot;
import it.unibo.ds.core.assets.ElectionInfo;
import it.unibo.ds.core.assets.ElectionInfoImpl;
import it.unibo.ds.core.utils.Choice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A {@link ElectionInfo} converter from class object to json string and vice-versa.
 */
public class ElectionInfoConverter implements Converter<ElectionInfo> {

    @Override
    public void serialize(final ElectionInfo object, final ObjectWriter writer, final Context ctx) {
        Genson genson = GensonUtils.create();
        writer.beginObject();
        writer.writeString("goal", object.getGoal());
        writer.writeString("voters", String.valueOf(object.getVotersNumber()));
        writer.writeString("startingDate", genson.serialize(object.getStartingDate()));
        writer.writeString("endingDate", genson.serialize(object.getEndingDate()));
        writer.writeString("choices", genson.serialize(object.getChoices()));
        writer.endObject();
    }

    @Override
    public ElectionInfo deserialize(final ObjectReader reader, final Context ctx) {
        Genson genson = GensonUtils.create();
        reader.beginObject();
        ElectionInfo election = null;
        String goal = null;
        long voters = 0;
        LocalDateTime start = null;
        LocalDateTime end = null;
        List<Choice> choices = null;

        while (reader.hasNext()) {
            reader.next();
            if ("goal".equals(reader.name())) {
                goal = reader.valueAsString();
            } else if ("voters".equals(reader.name())) {
                voters = reader.valueAsLong();
            } else if ("startingDate".equals(reader.name())) {
                start = genson.deserialize(reader.valueAsString(), LocalDateTime.class);
            } else if ("endingDate".equals(reader.name())) {
                end = genson.deserialize(reader.valueAsString(), LocalDateTime.class);
            } else if ("choices".equals(reader.name())) {
                choices = genson.deserialize(reader.valueAsString(), new GenericType<>() {});
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