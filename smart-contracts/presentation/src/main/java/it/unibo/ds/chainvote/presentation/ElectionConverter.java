package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.*;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import it.unibo.ds.core.assets.Ballot;
import it.unibo.ds.core.assets.Election;
import it.unibo.ds.core.assets.ElectionImpl;
import it.unibo.ds.core.utils.Choice;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * A {@link Election} converter from class object to json string and vice-versa.
 */
public class ElectionConverter implements Converter<Election> {

    @Override
    public void serialize(final Election object, final ObjectWriter writer, final Context ctx) {
        Genson genson = GensonUtils.create();
        writer.beginObject();
        writer.writeString("results", genson.serialize(object.getResults()));
        writer.writeString("ballots", genson.serialize(object.getBallots()));
        writer.endObject();
    }

    @Override
    public Election deserialize(final ObjectReader reader, final Context ctx) {
        Genson genson = GensonUtils.create();
        reader.beginObject();
        Election election = null;
        Map<Choice, Long> results = null;
        List<Choice> ballots = null;

        while (reader.hasNext()) {
            reader.next();
            if ("results".equals(reader.name())) {
                results = genson.deserialize(reader.valueAsString(), new GenericType<>(){});
            } else if ("ballots".equals(reader.name())) {
                ballots = genson.deserialize(reader.valueAsString(), new GenericType<>(){});
            } else {
                throw new JsonBindingException("Malformed json");
            }
        }
        if (results == null || ballots == null) {
            throw new JsonBindingException("Malformed json: missing value");
        }

        try {
            election = new ElectionImpl.Builder()
                    .results(results)
                    .ballots(ballots)
                    .build();
        } catch (IllegalArgumentException | NoSuchElementException e) {
            throw new JsonBindingException("Malformed json");
        }
        reader.endObject();
        return election;
    }
}
