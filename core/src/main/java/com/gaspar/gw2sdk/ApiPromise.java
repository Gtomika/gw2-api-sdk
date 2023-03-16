package com.gaspar.gw2sdk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gaspar.gw2sdk.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
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

    private AtomicReference<Consumer<T>> onSuccess;
    private AtomicReference<Consumer<ApiErrorData>> onError;
    private AtomicReference<Runnable> onNoAnswer;

    private final CompletableFuture<Optional<HttpResponse>> rawResponse;

    private AtomicReference<ApiResponse<T>> apiResponse;

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
        onSuccess = new AtomicReference<>(data -> log.warn("Successfully obtained data, but no 'onSuccess' callback is set. Ignoring result..."));
        onError = new AtomicReference<>(errorData -> log.warn("GW2 API responded with error, but no 'onError' callback is set. Ignoring error..."));
        onNoAnswer = new AtomicReference<>(() -> log.warn("GW2 API failed to respond, but no 'onNoAnswer' callback is set. Ignoring..."));
    }

    private void initializeRequestFinishedListener(TypeReference<T> dataType) {
        rawResponse.thenAccept(gw2HttpResponse -> {
            apiResponse = new AtomicReference<>(new ApiResponse<>(gw2HttpResponse, dataType));
            if(apiResponse.get().isSuccessful()) {
                onSuccess.get().accept(apiResponse.get().data().get());
            } else if(apiResponse.get().isApiError()) {
                onError.get().accept(apiResponse.get().errorData().get());
            } else {
                onNoAnswer.get().run();
            }
        });
    }

    /**
     * Set the callback to be invoked if the operation is successful. The SDK will pass the deserialized
     * data to your callback.
     */
    public synchronized ApiPromise<T> onSuccess(Consumer<T> onSuccess) {
        this.onSuccess.set(onSuccess);
        return this;
    }

    /**
     * Set the callback to be invoked if the operation resulted in an API error. The SDK will pass the
     * {@link ApiErrorData} to your callback.
     */
    public synchronized ApiPromise<T> onError(Consumer<ApiErrorData> onError) {
        this.onError.set(onError);
        return this;
    }

    /**
     * Set the callback to be invoked if the operation received no answer at all from the API. It can be because
     * of a timeout, for example.
     */
    public synchronized ApiPromise<T> onNoAnswer(Runnable onNoAnswer) {
        this.onNoAnswer.set(onNoAnswer);
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
    public synchronized Optional<ApiResponse<T>> getResponse() {
        return Optional.ofNullable(apiResponse)
                .map(AtomicReference::get);
    }

    /**
     * Used to check if the operation has finished. It can finish on success, on error, or if no response arrives.
     */
    public synchronized boolean isDone() {
        return rawResponse.isDone();
    }

    /**
     * Block the current thread until the operation finishes. Note that any exception triggered by the
     * callbacks, or cancellation will be rethrown by this method.
     */
    public synchronized void join() {
        rawResponse.join();
    }

}
