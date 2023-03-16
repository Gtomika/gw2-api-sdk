package com.gaspar.gw2sdk;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class to wrap an {@link ApiPromise} and provide test functionality to it,
 * such as asserting on success.
 * @param <T> Type of the response data.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PromiseTester<T> {

    @Getter
    private final ApiPromise<T> promise;

    /**
     * Wait for the completion of the operation so that asserts can be made on the results.
     */
    public PromiseTester<T> waitForCompletion() {
        promise.join();
        return this;
    }

    /**
     * Wait for the completion of the operation asserting that an exception was thrown.
     */
    public PromiseTester<T> waitForCompletionAssertException(Class<? extends Throwable> expectedException) {
        assertThrows(expectedException, promise::join);
        return this;
    }

    /**
     * Assert that API response was successful: must only be called after operation has finished, which can
     * be forced with {@link #waitForCompletion()} for example.
     */
    public PromiseTester<T> assertSuccessful() {
        assertTrue(promise.isDone());
        var response = promise.getResponse().orElseThrow(AssertionError::new);
        assertTrue(response.isSuccessful());
        return this;
    }

    /**
     * Assert that API response was an error: must only be called after operation has finished, which can
     * be forced with {@link #waitForCompletion()} for example.
     */
    public PromiseTester<T> assertApiError(int statusCode) {
        assertTrue(promise.isDone());
        var response = promise.getResponse().orElseThrow(AssertionError::new);
        assertTrue(response.isApiError());

        var errorData = response.errorData().orElseThrow(AssertionError::new);
        assertEquals(statusCode, errorData.statusCode());
        return this;
    }

    /**
     * Assert that API has not provided any answer: must only be called after operation has finished, which can
     * be forced with {@link #waitForCompletion()} for example.
     */
    public PromiseTester<T> assertNoAnswer() {
        assertTrue(promise.isDone());
        var response = promise.getResponse().orElseThrow(AssertionError::new);
        assertTrue(response.isNoAnswer());
        return this;
    }

    /**
     * Assert that a predicate is true on the response data. Must only be called after the {@link #assertSuccessful()}
     * assertion has passed.
     */
    public PromiseTester<T> assertOnData(Predicate<T> dataCondition) {
        var response = promise.getResponse().orElseThrow(AssertionError::new);
        assertTrue(response.isSuccessful());

        var data = response.data().orElseThrow(AssertionError::new);
        assertTrue(dataCondition.test(data));
        return this;
    }

    public static <T> PromiseTester<T> of(ApiPromise<T> promise) {
        return new PromiseTester<>(promise);
    }
}
