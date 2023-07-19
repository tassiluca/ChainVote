package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.GenericType;
import com.owlike.genson.JsonBindingException;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import it.unibo.ds.core.Voting;
import it.unibo.ds.core.VotingImpl;

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
        final var votingBuilder = new VotingImpl.Builder();
        reader.beginObject();
        while (reader.hasNext()) {
            reader.next();
            switch (reader.name()) {
                case "name" -> votingBuilder.name(reader.valueAsString());
                case "question" -> votingBuilder.question(reader.valueAsString());
                case "openingDate" -> votingBuilder.openAt(LocalDateTime.parse(reader.valueAsString()));
                case "closingDate" -> votingBuilder.closeAt(LocalDateTime.parse(reader.valueAsString()));
                case "choices" -> {
                    final List<String> choices = ctx.genson.deserialize(new GenericType<>() { }, reader, ctx);
                    choices.forEach(votingBuilder::choice);
                }
                default -> throw new JsonBindingException("Malformed json");
            }
        }
        return votingBuilder.build();
    }
}
