package com.gaspar.gw2sdk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gaspar.gw2sdk.http.Gw2HttpResponse;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class Gw2ApiPromise<T> {

    private final CompletableFuture<Optional<Gw2HttpResponse>> rawResponse;

    protected Gw2ApiPromise(
            CompletableFuture<Optional<Gw2HttpResponse>> rawResponse,
            TypeReference<T> dataType,
            Gw2ApiCallbacks<T> callbacks
    ) {
        rawResponse.thenAccept(gw2HttpResponse -> {
            Gw2ApiResponse<T> apiResponse = new Gw2ApiResponse<>(gw2HttpResponse, dataType);
            if(apiResponse.isSuccessful()) {
                callbacks.onSuccess(apiResponse.data());
            } else if(apiResponse.isApiError()) {
                callbacks.onError(apiResponse.errorData());
            } else {
                callbacks.onNoAnswer();
            }
        });
        this.rawResponse = rawResponse;
    }

    public boolean isDone() {
        return rawResponse.isDone();
    }

    public void cancel() {
        rawResponse.cancel(true);
    }

    public void join() {
        rawResponse.join();
    }

}
