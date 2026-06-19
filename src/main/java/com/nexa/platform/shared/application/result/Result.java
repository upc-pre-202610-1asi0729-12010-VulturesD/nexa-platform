package com.nexa.platform.shared.application.result;

import java.util.Optional;
import java.util.function.Function;

/**
 * Generic application-layer outcome wrapper.
 *
 * <p>Follows the sealed-interface pattern from the course reference implementation
 * (catch-up-platform) so that command services can return typed success/failure
 * results without throwing exceptions across layer boundaries.
 *
 * <p>Usage example:
 * <pre>{@code
 *   Result<Promotion, PromotionCommandFailure> result = commandService.handle(command);
 *   result.fold(
 *       promotion -> ResponseEntity.status(HttpStatus.CREATED).body(PromotionAssembler.toResource(promotion)),
 *       failure   -> ResponseEntity.status(HttpStatus.CONFLICT).build()
 *   );
 * }</pre>
 *
 * @param <T> success value type
 * @param <E> failure value type
 * @since 1.0
 */
public sealed interface Result<T, E> permits Result.Success, Result.Failure {

    /**
     * Creates a successful result.
     *
     * @param value success value
     * @param <T>   success value type
     * @param <E>   failure value type
     * @return success result
     */
    static <T, E> Result<T, E> success(T value) {
        return new Success<>(value);
    }

    /**
     * Creates a failed result.
     *
     * @param error failure value
     * @param <T>   success value type
     * @param <E>   failure value type
     * @return failure result
     */
    static <T, E> Result<T, E> failure(E error) {
        return new Failure<>(error);
    }

    /**
     * @return {@code true} when this result represents a success
     */
    default boolean isSuccess() {
        return this instanceof Success<?, ?>;
    }

    /**
     * @return {@code true} when this result represents a failure
     */
    default boolean isFailure() {
        return this instanceof Failure<?, ?>;
    }

    /**
     * @return an {@link Optional} containing the success value, or empty if failure
     */
    default Optional<T> success() {
        if (this instanceof Success<?, ?> success) {
            @SuppressWarnings("unchecked")
            T value = ((Success<T, E>) success).value();
            return Optional.of(value);
        }
        return Optional.empty();
    }

    /**
     * @return an {@link Optional} containing the failure error, or empty if success
     */
    default Optional<E> failure() {
        if (this instanceof Failure<?, ?> failure) {
            @SuppressWarnings("unchecked")
            E error = ((Failure<T, E>) failure).error();
            return Optional.of(error);
        }
        return Optional.empty();
    }

    /**
     * Folds this result into a single value by applying the appropriate mapper.
     *
     * @param onSuccess mapper applied to the success value
     * @param onFailure mapper applied to the failure error
     * @param <R>       the return type
     * @return the folded value
     */
    default <R> R fold(Function<? super T, ? extends R> onSuccess, Function<? super E, ? extends R> onFailure) {
        if (this instanceof Success<?, ?> success) {
            @SuppressWarnings("unchecked")
            T value = ((Success<T, E>) success).value();
            return onSuccess.apply(value);
        }
        @SuppressWarnings("unchecked")
        E error = ((Failure<T, E>) this).error();
        return onFailure.apply(error);
    }

    /**
     * Successful result record.
     *
     * @param value the success payload
     * @param <T>   success value type
     * @param <E>   failure value type
     */
    record Success<T, E>(T value) implements Result<T, E> { }

    /**
     * Failed result record.
     *
     * @param error the failure payload
     * @param <T>   success value type
     * @param <E>   failure value type
     */
    record Failure<T, E>(E error) implements Result<T, E> { }
}
