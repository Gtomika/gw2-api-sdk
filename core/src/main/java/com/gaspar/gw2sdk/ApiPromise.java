package com.gaspar.gw2sdk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gaspar.gw2sdk.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Returned by the SDK after asynchronous GW2 API calls. You can attach callbacks to this promise:
 * <ul>
 *     <li>{@link #onSuccess(Consumer)}: Use it to attach a callback that is invoked with the response data.</li>
 *     <li>{@link #onError(Consumer)}: Use it to attach a callback that is invoked if the GW2 API returns an error (non 200 status code).</li>
 *     <li>{@link #onNoAnswer(Runnable)}: Use it to attach a callback that is invoked if the GW2 API fails to respond.</li>
 * </ul>
 * For example, to get the list of achievement IDs, and listen to the responses, you'd do:
 * <pre>{@code
 *  achievementsApi.getAchievementIds()
 *                 .onSuccess(idList -> log.info("GW2 has '{}' achievements!", idList.size()))
 *                 .onError(errorData -> log.error("GW2 API error: {}", errorData))
 *                 .onNoAnswer(() -> log.error("GW2 API failed to answer"))
 *                 .join(); //to wait for it to finish
 * }</pre>
 * @param <T>
 */
@Slf4j
public class ApiPromise<T> {

    private Consumer<T> onSuccess;
    private Consumer<ApiErrorData> onError;
    private Runnable onNoAnswer;

    private final CompletableFuture<Optional<HttpResponse>> rawResponse;

    protected ApiPromise(
            CompletableFuture<Optional<HttpResponse>> rawResponse,
            TypeReference<T> dataType
    ) {
        this.rawResponse = rawResponse;
        initializeDefaultCallbacks();
        initializeRequestFinishedListener(dataType);
    }

    private void initializeDefaultCallbacks() {
        onSuccess = data -> log.warn("Successfully obtained data, but no 'onSuccess' callback is set. Ignoring result...");
        onError = errorData -> log.warn("GW2 API responded with error, but no 'onError' callback is set. Ignoring error...");
        onNoAnswer = () -> log.warn("GW2 API failed to respond, but no 'onNoAnswer' callback is set. Ignoring...");
    }

    private void initializeRequestFinishedListener(TypeReference<T> dataType) {
        rawResponse.thenAccept(gw2HttpResponse -> {
            ApiResponse<T> apiResponse = new ApiResponse<>(gw2HttpResponse, dataType);
            if(apiResponse.isSuccessful()) {
                onSuccess.accept(apiResponse.data());
            } else if(apiResponse.isApiError()) {
                onError.accept(apiResponse.errorData());
            } else {
                onNoAnswer.run();
            }
        });
    }

    /**
     * Set the callback to be invoked if the operation is successful. The SDK will pass the deserialized
     * data to your callback.
     */
    public ApiPromise<T> onSuccess(Consumer<T> onSuccess) {
        this.onSuccess = onSuccess;
        return this;
    }

    /**
     * Set the callback to be invoked if the operation resulted in an API error. The SDK will pass the
     * {@link ApiErrorData} to your callback.
     */
    public ApiPromise<T> onError(Consumer<ApiErrorData> onError) {
        this.onError = onError;
        return this;
    }

    /**
     * Set the callback to be invoked if the operation received no answer at all from the API. It can be because
     * of a timeout, for example.
     */
    public ApiPromise<T> onNoAnswer(Runnable onNoAnswer) {
        this.onNoAnswer = onNoAnswer;
        return this;
    }

    /**
     * Used to check if the operation has finished. It can finish on success, on error, or if no response arrives.
     */
    public boolean isDone() {
        return rawResponse.isDone();
    }

    /**
     * Cancel the operation manually. Note that in this case, none of the available callback will be invoked, and
     * you will face a {@link java.util.concurrent.CancellationException} when calling {@link #join()}.
     */
    public void cancel() {
        rawResponse.cancel(true);
    }

    /**
     * Block the current thread until the operation finishes. Note that any exception triggered by the
     * callbacks, or cancellation will be rethrown by this method.
     */
    public void join() {
        rawResponse.join();
    }

}
