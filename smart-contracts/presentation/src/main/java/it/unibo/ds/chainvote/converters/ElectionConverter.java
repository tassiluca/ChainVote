package it.unibo.ds.chainvote.converters;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.Genson;
import com.owlike.genson.JsonBindingException;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import it.unibo.ds.chainvote.GensonUtils;
import it.unibo.ds.chainvote.assets.Election;
import it.unibo.ds.chainvote.assets.ElectionImpl;
import it.unibo.ds.chainvote.utils.Choice;

import java.util.*;

/**
 * A {@link Election} converter from class object to json string and vice-versa.
 */
public class ElectionConverter implements Converter<Election> {

    @Override
    public void serialize(final Election object, final ObjectWriter writer, final Context ctx) {
        Genson genson = GensonUtils.create();
        writer.beginObject();
        writer.writeName("results");
        writer.beginObject();
        for (Map.Entry<String, Long> entry : object.getResults().entrySet()) {
            writer.writeName(entry.getKey());
            writer.writeValue(entry.getValue());
        }
        writer.endObject();
        writer.writeName("ballots");
        writer.beginArray();
        for (Choice vote : object.getBallots()) {
            writer.beginObject();
            writer.writeName("choice");
            writer.writeValue(vote.getChoice());
            writer.endObject();
        }
        writer.endArray();
        writer.endObject();
    }

    @Override
    public Election deserialize(final ObjectReader reader, final Context ctx) {
        Genson genson = GensonUtils.create();
        reader.beginObject();
        Election election = null;
        Map<String, Long> results = null;
        List<Choice> ballots = null;

        while (reader.hasNext()) {
            reader.next();
            if ("results".equals(reader.name())) {
                results = new HashMap<>();
                reader.beginObject();
                while (reader.hasNext()) {
                    reader.next();
                    String choice = reader.name();
                    long votes = reader.valueAsLong();
                    results.put(choice, votes);
                }
                reader.endObject();
            } else if ("ballots".equals(reader.name())) {
                ballots = new ArrayList<>();
                reader.beginArray();
                while (reader.hasNext()) {
                    reader.next();
                    reader.beginObject();
                    reader.next();
                    ballots.add(new Choice(reader.valueAsString()));
                    reader.endObject();
                }
                reader.endArray();
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
