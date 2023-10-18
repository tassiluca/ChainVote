package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
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
        writer.beginObject();
        writer.writeString("electionID", object.getElectionId());
        writer.writeString("voterID", object.getVoterId());
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

        while (reader.hasNext()) {
            reader.next();
            if ("electionID".equals(reader.name())) {
                electionID = reader.valueAsString();
            } else if ("voterID".equals(reader.name())) {
                voterID = reader.valueAsString();
            } else if ("date".equals(reader.name())) {
                date = GensonUtils.create().deserialize(reader.valueAsString(), LocalDateTime.class);
            } else if ("choice".equals(reader.name())) {
                choice = reader.valueAsString();
            } else {
                throw new JsonBindingException("Malformed json");
            }
        }
        if (electionID == null || voterID == null || date == null || choice == null) {
            throw new JsonBindingException("Malformed json: missing value");
        }

        Ballot ballot;
        try {
            ballot = new BallotImpl.Builder().electionID(electionID)
                    .voterID(voterID)
                    .date(date)
                    .choice(new Choice(choice))
                    .build();
        } catch (IllegalArgumentException | NoSuchElementException e) {
            throw new JsonBindingException("Malformed json");
        }
        reader.endObject();
        return ballot;
    }
}
