package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.JsonBindingException;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import it.unibo.ds.core.assets.BallotImpl;
import it.unibo.ds.core.assets.Ballot;
import it.unibo.ds.core.utils.Choice;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

public class BallotConverter implements Converter<Ballot> {

    @Override
    public void serialize(final Ballot object, final ObjectWriter writer, final Context ctx) {
        writer.beginObject();
        writer.writeString("electionID", object.getElectionID());
        writer.writeString("voterID", object.getVoterID());
        String date = GensonUtils.create().serialize(object.getDate());
        writer.writeString("date", date);
        writer.writeString("choice", object.getChoice().getChoice());
        writer.endObject();
    }

    @Override
    public Ballot deserialize(final ObjectReader reader, final Context ctx) {
        reader.beginObject();
        String electionID = null;
        String voterID = null;
        LocalDateTime date = null;
        String choice = null;

        reader.next();
        if ("electionID".equals(reader.name())) {
            electionID = reader.valueAsString();
        }
        reader.next();
        if ("voterID".equals(reader.name())) {
            voterID = reader.valueAsString();
        }
        reader.next();
        if ("date".equals(reader.name())) {
            date = GensonUtils.create().deserialize(reader.valueAsString(), LocalDateTime.class);
        }
        reader.next();
        if ("choice".equals(reader.name())) {
            choice = reader.valueAsString();
        }

        Ballot ballot;
        try {
            ballot = new BallotImpl.Builder().electionID(electionID)
                    .voterID(voterID)
                    .dateUnchecked(date)
                    .choiceUnchecked(new Choice(choice))
                    .build();
        } catch (IllegalArgumentException | NoSuchElementException e) {
            throw new JsonBindingException("Malformed json");
        }
        reader.endObject();
        return ballot;
    }
}