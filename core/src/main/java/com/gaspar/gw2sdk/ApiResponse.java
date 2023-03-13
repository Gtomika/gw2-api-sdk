package com.gaspar.gw2sdk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gaspar.gw2sdk.annotations.SdkInternal;
import com.gaspar.gw2sdk.http.HttpResponse;
import com.gaspar.gw2sdk.serialization.SdkSerialization;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Formatted response from the API. This response can have 3 main states:
 * <ol>
 *     <li>
 *         {@link #isSuccessful()}: The API has responded with a correct response. You can get it with {@link #data()}.
 *     </li>
 *     <li>
 *         {@link #isApiError()}: The API has responded with an error response (such as HTTP 500).
 *         You can get the details with {@link #errorData()}.
 *     </li>
 *     <li>
 *         {@link #isNoAnswer()}: The API has not responded at all. Could be down, or a request timeout, or other reasons.
 *     </li>
 * </ol>
 * @param <T> Type of the successful response.
 */
@Slf4j
@SdkInternal
public class ApiResponse<T> {

    private Optional<T> data;
    private Optional<ApiErrorData> errorData;

    @Getter
    private boolean successful = false;

    @Getter
    private boolean apiError = false;

    @Getter
    private boolean noAnswer = false;

    protected ApiResponse(@Nonnull Optional<HttpResponse> rawResponseOpt, @Nonnull TypeReference<T> dataType) {
        rawResponseOpt.ifPresentOrElse(
                rawResponse -> initializeWhenResponse(rawResponse, dataType),
                this::initializeWhenNoResponse
        );
    }

    private void initializeWhenResponse(@Nonnull HttpResponse rawResponse, @Nonnull TypeReference<T> dataType) {
        successful = rawResponse.statusCode() == 200;
        if(successful) {
            log.debug("Response is considered successful, starting deserializing data into '{}'", dataType.getType().getTypeName());
            T t = deserializeData(rawResponse.content(), dataType);
            data = Optional.of(t);
        } else {
            data = Optional.empty();
        }

        apiError = !successful;
        if(apiError) {
            errorData = Optional.of(deserializeErrorData(rawResponse));
            log.debug("Response is considered an error, created error data {}", errorData.get());
        } else {
            errorData = Optional.empty();
        }

        noAnswer = false;
    }

    private void initializeWhenNoResponse() {
        log.debug("Response has not arrived at all");
        noAnswer = true;
        data = Optional.empty();
        errorData = Optional.empty();
    }

    private T deserializeData(String content, TypeReference<T> dataType) {
        return SdkSerialization.deserializeJson(content, dataType);
    }

    private ApiErrorData deserializeErrorData(HttpResponse rawResponse) {
        return new ApiErrorData(rawResponse.content(), rawResponse.statusCode());
    }

    /**
     * Get the serialized data returned from the API. <b>Only call this after you made sure that the response</b>
     * {@link #isSuccessful()}. Otherwise, exception is thrown to warn about misusing this method.
     */
    @SdkInternal
    public T data() {
        return data.orElseThrow(() -> new SdkException("SDK error: calling 'data' when response is not successful"));
    }

    /**
     * Get the serialized error data from the API. <b>Only call this after you made sure that the response</b>
     * {@link #isApiError()}. Otherwise, exception is thrown to warn about misusing this method.
     */
    @SdkInternal
    public ApiErrorData errorData() {
        return errorData.orElseThrow(() -> new SdkException("SDK error: calling 'errorData' when response is not error"));
    }

    /**
     * Convenience method of checking if the API has returned any response at all. The
     * response can be successful or error.
     */
    public boolean isCompleted() {
        return successful || apiError;
    }
}
