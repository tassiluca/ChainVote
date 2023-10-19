package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.Genson;
import com.owlike.genson.JsonBindingException;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import it.unibo.ds.core.assets.Ballot;
import it.unibo.ds.core.assets.BallotImpl;
import it.unibo.ds.core.utils.Choice;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

/**
 * A {@link Ballot} converter from class object to json string and vice-versa.
 */
public class BallotConverter implements Converter<Ballot> {

    @Override
    public void serialize(final Ballot object, final ObjectWriter writer, final Context ctx) {
        Genson genson = GensonUtils.create();
        writer.beginObject();
        writer.writeString("electionID", object.getElectionId());
        writer.writeString("voterID", object.getVoterId());
        writer.writeString("date", genson.serialize(object.getDate()));
        writer.writeString("choice", genson.serialize(object.getChoice()));
        writer.endObject();
    }

    @Override
    public Ballot deserialize(final ObjectReader reader, final Context ctx) {
        Genson genson = GensonUtils.create();
        reader.beginObject();
        String electionID = null;
        String voterID = null;
        LocalDateTime date = null;
        Choice choice = null;

        while (reader.hasNext()) {
            reader.next();
            if ("electionID".equals(reader.name())) {
                electionID = reader.valueAsString();
            } else if ("voterID".equals(reader.name())) {
                voterID = reader.valueAsString();
            } else if ("date".equals(reader.name())) {
                date = genson.deserialize(reader.valueAsString(), LocalDateTime.class);
            } else if ("choice".equals(reader.name())) {
                choice = genson.deserialize(reader.valueAsString(), Choice.class);
            } else {
                throw new JsonBindingException("Malformed json");
            }
        }
        reader.endObject();
        if (electionID == null || voterID == null || date == null || choice == null) {
            throw new JsonBindingException("Malformed json: missing value");
        }

        Ballot ballot;
        try {
            ballot = new BallotImpl.Builder().electionID(electionID)
                    .voterID(voterID)
                    .date(date)
                    .choice(choice)
                    .build();
        } catch (IllegalArgumentException | NoSuchElementException e) {
            throw new JsonBindingException("Malformed json");
        }
        return ballot;
    }
}
