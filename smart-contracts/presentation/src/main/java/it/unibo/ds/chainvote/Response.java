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
 *     "success": {
 *         "type": "boolean",
 *         "description": "Indicates whether or not the operation was successful",
 *         "required": true,
 *         "example": true
 *     },
 *     "error": {
 *         "type": "string | null",
 *         "description": "Contains the error message if the operation failed, or null if the operation was successful",
 *         "required": true,
 *         "example": "Seed cannot be blank"
 *     },
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

    private final boolean success;
    private final T result;
    private final String error;

    /**
     * Creates a new successful result.
     * @param result the result to be wrapped.
     * @return a new instance of {@link Response}.
     * @param <T> the result type.
     */
    public static <T> Response<T> success(final T result) {
        return new Response<>(true, result, null);
    }

    /**
     * Creates a new error result.
     * @param cause the cause of the error, i.e. its message.
     * @return a new instance of {@link Response}.
     * @param <T> the result type.
     */
    public static <T> Response<T> error(final String cause) {
        return new Response<>(false, null, cause);
    }

    Response(
        @JsonProperty("success") final boolean success,
        @JsonProperty("result") final T result,
        @JsonProperty("error") final String error
    ) {
        this.success = success;
        this.result = result;
        this.error = error;
    }

    /**
     * @return true if this object represent a successful result, false otherwise.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @return the wrapped result.
     */
    public T getResult() {
        return result;
    }

    /**
     * @return the wrapped error message.
     */
    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return "Response{success=" + success + ", result=" + result + ", errorMessage='" + error + '\'' + '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Response<?> result1 = (Response<?>) o;
        return success == result1.success
            && Objects.equals(result, result1.result) && Objects.equals(error, result1.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, result, error);
    }
}
