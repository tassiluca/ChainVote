package it.unibo.ds.chainvote;

import com.owlike.genson.annotation.JsonProperty;

import java.util.Objects;

public final class Result<T> {

    private final boolean success;
    private final T result;
    private final String error;

    public static <T> Result<T> success(final T result) {
        return new Result<>(true, result, null);
    }

    public static <T> Result<T> error(final String cause) {
        return new Result<>(false, null, cause);
    }

    Result(
        @JsonProperty("success") final boolean success,
        @JsonProperty("result") final T result,
        @JsonProperty("error") final String errorMessage
    ) {
        this.success = success;
        this.result = result;
        this.error = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getResult() {
        return result;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return "Result{success=" + success + ", result=" + result + ", errorMessage='" + error + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result<?> result1 = (Result<?>) o;
        return success == result1.success && Objects.equals(result, result1.result) && Objects.equals(error, result1.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, result, error);
    }
}

