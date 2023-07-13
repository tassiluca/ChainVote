package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.GenericType;
import com.owlike.genson.JsonBindingException;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import it.unibo.ds.core.Voting;
import it.unibo.ds.core.VotingFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * A Genson converter for {@link Voting} classes.
 */
public final class VotingConverter implements Converter<Voting> {

    @Override
    public void serialize(final Voting object, final ObjectWriter writer, final Context ctx) {
        writer.beginObject();
        writer.writeString("name", object.name());
        writer.writeString("question", object.question());
        writer.writeName("choices");
        ctx.genson.serialize(object.choices(), writer, ctx);
        writer.writeString("openingDate", object.openingDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        writer.writeString("closingDate", object.closingDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        writer.endObject();
    }

    @Override
    public Voting deserialize(final ObjectReader reader, final Context ctx) {
        reader.beginObject();
        String name = null;
        String question = null;
        List<String> choices = null;
        LocalDateTime openingDate = null;
        LocalDateTime closingDate = null;
        while (reader.hasNext()) {
            reader.next();
            switch (reader.name()) {
                case "name" -> name = reader.valueAsString();
                case "question" -> question = reader.valueAsString();
                case "choices" -> choices = ctx.genson.deserialize(new GenericType<>() { }, reader, ctx);
                case "openingDate" -> openingDate = LocalDateTime.parse(reader.valueAsString());
                case "closingDate" -> closingDate = LocalDateTime.parse(reader.valueAsString());
                default -> throw new JsonBindingException("Malformed json");
            }
        }
        try {
            return new VotingFactory().create(name, question, choices, openingDate, closingDate);
        } catch (NullPointerException e) {
            throw new JsonBindingException("Tried to deserialized a partial Voting object.");
        } catch (Exception e) {
            throw new JsonBindingException("Malformed json: " + e.getMessage());
        }
    }
}
