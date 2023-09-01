package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import com.owlike.genson.JsonBindingException;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import it.unibo.ds.core.assets.Election;
import it.unibo.ds.core.assets.ElectionImpl;
import it.unibo.ds.core.utils.Choice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class ElectionConverter implements Converter<Election> {

    @Override
    public void serialize(final Election object, final ObjectWriter writer, final Context ctx) {
        Genson genson = GensonUtils.create();
        writer.beginObject();
        writer.writeString("goal", object.getGoal());
        writer.writeString("voters", String.valueOf(object.getVotersNumber()));
        writer.writeString("startingDate", genson.serialize(object.getStartingDate()));
        writer.writeString("endingDate", genson.serialize(object.getStartingDate()));
        writer.writeString("choices", genson.serialize(object.getChoices()));//.stream().map(Choice::getChoice)
                //.collect(Collectors.toList())));
        writer.writeString("results", genson.serialize(object.getResults()));
        writer.writeString("ballots", genson.serialize(object.getBallots()));
        writer.endObject();
    }

    @Override
    public Election deserialize(final ObjectReader reader, final Context ctx) {
        reader.beginObject();
        Genson genson = GensonUtils.create();
        Election election = null;
        String goal = null;
        long voters = 0;
        LocalDateTime start = null;
        LocalDateTime end = null;
        List<Choice> choices = null;
        Map<Choice, Long> results = null;
        List<Choice> ballots = null;

        reader.next();
        if ("goal".equals(reader.name())) {
            goal = reader.valueAsString();
        }

        reader.next();
        if ("voters".equals(reader.name())) {
            voters = reader.valueAsLong();
        }

        reader.next();
        if ("startingDate".equals(reader.name())) {
            start = genson.deserialize(reader.valueAsString(), LocalDateTime.class);
        }

        reader.next();
        if ("endingDate".equals(reader.name())) {
            end = genson.deserialize(reader.valueAsString(), LocalDateTime.class);
        }

        reader.next();
        if ("choices".equals(reader.name())) {
            String toPrint = reader.valueAsString();
            System.out.println("Here: " + toPrint);
            List<Choice> deser = genson.deserialize(toPrint, new GenericType<List<Choice>>(){});
            System.out.println(deser.getClass());
        }

        reader.next();
        if ("results".equals(reader.name())) {
            results = genson.deserialize(reader.valueAsString(), new GenericType<Map<Choice, Long>>(){});
        }

        reader.next();
        if ("ballots".equals(reader.name())) {
            ballots = genson.deserialize(reader.valueAsString(), new GenericType<List<Choice>>(){});
        }

        try {
            election = new ElectionImpl.Builder()
                    .goal(goal)
                    .voters(voters)
                    .start(start)
                    .end(end)
                    .choices(choices)
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
