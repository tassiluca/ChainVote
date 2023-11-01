package it.unibo.ds.chainvote.converters;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.JsonBindingException;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import it.unibo.ds.chainvote.assets.Ballot;
import it.unibo.ds.chainvote.assets.BallotImpl;
import it.unibo.ds.chainvote.utils.Choice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;

/**
 * A {@link Ballot} converter from class object to json string and vice-versa.
 */
public final class BallotConverter implements Converter<Ballot> {

    @Override
    public void serialize(final Ballot object, final ObjectWriter writer, final Context ctx) {
        writer.beginObject();
        writer.writeString("electionID", object.getElectionId());
        writer.writeString("voterID", object.getVoterId());
        writer.writeName("date");
        writer.writeValue(object.getDate().format(DateTimeFormatter.ISO_DATE_TIME));
        writer.writeName("choice");
        writer.beginObject();
        writer.writeString("choice", object.getChoice().getChoice());
        writer.endObject();
        writer.endObject();
    }

    @Override
    public Ballot deserialize(final ObjectReader reader, final Context ctx) {
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
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                date = LocalDateTime.parse(reader.valueAsString(), formatter);
            } else if ("choice".equals(reader.name())) {
                reader.beginObject();
                reader.next();
                choice = new Choice(reader.valueAsString());
                reader.endObject();
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
