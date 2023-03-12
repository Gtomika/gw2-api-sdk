package com.gaspar.gw2sdk;

@FunctionalInterface
public interface Gw2ApiCallbacks<T> {

    void onSuccess(T data);

    default void onError(Gw2ApiErrorData errorData) {}

    default void onNoAnswer() {}

}
