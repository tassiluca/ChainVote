package it.unibo.ds.chainvote.presentation;

import com.owlike.genson.*;
import com.owlike.genson.annotation.HandleClassMetadata;
import com.owlike.genson.convert.ChainedFactory;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import it.unibo.ds.core.assets.Ballot;
import it.unibo.ds.core.assets.BallotImpl;
import it.unibo.ds.core.assets.Election;
import it.unibo.ds.core.assets.ElectionImpl;
import it.unibo.ds.core.codes.OneTimeCode;
import it.unibo.ds.core.codes.OneTimeCodeImpl;
import it.unibo.ds.core.utils.Choice;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Utility class for Genson (de)serialization stuffs.
 */
public final class GensonUtils {

    private GensonUtils() { }

    public static class LiteralAsObjectConverter<T> implements Converter<T> {
        private final Converter<T> concreteConverter;

        public LiteralAsObjectConverter(Converter<T> concreteConverter) {
            this.concreteConverter = concreteConverter;
        }

        @Override
        public void serialize(T object, ObjectWriter writer, Context ctx) throws Exception {
            writer.beginObject().writeName("value");
            concreteConverter.serialize(object, writer, ctx);
            writer.endObject();
        }

        @Override
        public T deserialize(ObjectReader reader, Context ctx) throws Exception {
            reader.beginObject();
            T instance = null;
            while (reader.hasNext()) {
                reader.next();
                if (reader.name().equals("value")) instance = concreteConverter.deserialize(reader, ctx);
                else throw new IllegalStateException(String.format("Encountered unexpected property named '%s'", reader.name()));
            }
            reader.endObject();
            return instance;
        }
    }

    /**
     * @return a new {@link Genson} instance, already configured.
     */
    public static Genson create() {
        return new GensonBuilder()
            .useRuntimeType(true)
            .useConstructorWithArguments(true)
            .withConverterFactory(new ChainedFactory() {
                @Override
                protected Converter<?> create(Type type, Genson genson, Converter<?> nextConverter) {
                    if (Wrapper.toAnnotatedElement(nextConverter).isAnnotationPresent(HandleClassMetadata.class)) {
                        return new LiteralAsObjectConverter(nextConverter);
                    } else {
                        return nextConverter;
                    }
                }
            })
            .withConverter(new OneTimeCodeConverter(), OneTimeCodeImpl.class)
            .withConverter(new OneTimeCodeConverter(), OneTimeCode.class)
            .withConverter(new LocalDateTimeConverter(), LocalDateTime.class)
            .withConverter(new BallotConverter(), BallotImpl.class)
            .withConverter(new BallotConverter(), Ballot.class)
            .withConverter(new ElectionConverter(), Election.class)
            .withConverter(new ElectionConverter(), ElectionImpl.class)
            .withConverter(new ChoiceConverter(), Choice.class)
            .withConverter(new ListOfChoiceConverter(), new GenericType<List<Choice>>(){})
            .create();
    }
}
