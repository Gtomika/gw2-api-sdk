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
    private ApiResponse<T> apiResponse;

    private ApiPromise(
            CompletableFuture<Optional<HttpResponse>> rawResponse,
            TypeReference<T> dataType
    ) {
        this.rawResponse = rawResponse;
        initializeDefaultCallbacks();
        initializeRequestFinishedListener(dataType);
    }

    public static <T> ApiPromise<T> of(CompletableFuture<Optional<HttpResponse>> rawResponse, TypeReference<T> dataType) {
        return new ApiPromise<>(rawResponse, dataType);
    }

    private void initializeDefaultCallbacks() {
        onSuccess = data -> log.warn("Successfully obtained data, but no 'onSuccess' callback is set. Ignoring result...");
        onError = errorData -> log.warn("GW2 API responded with error, but no 'onError' callback is set. Ignoring error...");
        onNoAnswer = () -> log.warn("GW2 API failed to respond, but no 'onNoAnswer' callback is set. Ignoring...");
    }

    private void initializeRequestFinishedListener(TypeReference<T> dataType) {
        rawResponse.thenAccept(gw2HttpResponse -> {
            apiResponse = new ApiResponse<>(gw2HttpResponse, dataType);
            if(apiResponse.isSuccessful()) {
                onSuccess.accept(apiResponse.data().get());
            } else if(apiResponse.isApiError()) {
                onError.accept(apiResponse.errorData().get());
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
     * Get the response object: {@link ApiResponse}. If the request has not completed at the time of calling this
     * method, an empty optional will be returned.
     * <p>
     * Using the callbacks to process the response is preferred. But in case of blocking the thread and waiting for
     * the response, this method can be used:
     * <pre>{@code
     * ApiPromise<Achievement> promise = achievementsApi.findAchievementById(1235);
     * promise.join(); //block
     * ApiResponse<Achievement> achievementResponse = promise.getResponse().get();
     *
     * if(achievementResponse.isSuccessful()) {
     *      Achievement a = achievementResponse.data();
     *      //do something with achievement
     * } else {
     *     //do something with failed or missing response
     * }
     * }</pre>
     * Due to the clumsiness of this approach, it is recommended to use the callbacks to process the response instead.
     */
    public Optional<ApiResponse<T>> getResponse() {
        return Optional.ofNullable(apiResponse);
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
