package it.unibo.ds.chainvote;

import com.owlike.genson.annotation.JsonProperty;

import java.util.Objects;

/**
 * <p>A wrapper of a result which can be easily (de)serialized into a json string
 * with standardized format across all the project.</p>
 *
 * <p>The resulting JSON object has the following structure:</p>
 * <pre>
 * {
 *     "result": {
 *         "type": "primitive | object | null",
 *         "description": "Contains the operation's result if was successful, or null if the operation failed.",
 *         "required": true,
 *         "example": {
 *             "electionID": "-1720367461",
 *             "voterID": "test-user",
 *             "date": "2023-08-20T10:00:00",
 *             "choice":{
 *                 "choice": "no",
 *                 "choice": "yes"
 *             }
 *         }
 *     }
 * }
 * </pre>
 *
 * @param <T> the result type.
 */
public final class Response<T> {

    private final T result;

    public Response(@JsonProperty("result") final T result) {
        this.result = result;
    }

    /**
     * @return the wrapped result.
     */
    public T getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "Response{result=" + result + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Response<?> response = (Response<?>) o;
        return Objects.equals(result, response.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result);
    }
}
