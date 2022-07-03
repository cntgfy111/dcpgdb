package util;

import java.util.function.Supplier;

public class Result<T> {
    protected T result;
    protected Throwable error;

    private Result(T result, Throwable error) {
        this.result = result;
        this.error = error;
    }

    public static <T> Result<T> of(T result) {
        return new Result<>(result, null);
    }

    public static <T> Result<T> of (Throwable error) {
        return new Result<>(null, error);
    }

    public T unwrap() {
        if (result == null) {
            throw new IllegalStateException(error);
        }

        return result;
    }

    public T unwrapOrElse(T other) {
        return result == null ? other : result;
    }

    public T unwrapOrElse(Supplier<T> elseSupplier) {
        return result == null ? elseSupplier.get() : result;
    }
}
