package com.gaspar.gw2sdk;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gaspar.gw2sdk.http.Gw2HttpResponse;
import com.gaspar.gw2sdk.serialization.Gw2SdkSerialization;
import lombok.Getter;

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
public class Gw2ApiResponse<T> {

    private Optional<T> data;
    private Optional<Gw2ApiErrorData> errorData;

    @Getter
    private boolean successful = false;

    @Getter
    private boolean apiError = false;

    @Getter
    private boolean noAnswer = false;

    /**
     * Build a new API response. You should not do this manually, leave it to the SDK.
     */
    public Gw2ApiResponse(@Nonnull Optional<Gw2HttpResponse> rawResponseOpt, @Nonnull TypeReference<T> dataType) {
        rawResponseOpt.ifPresentOrElse(
                rawResponse -> initializeWhenResponse(rawResponse, dataType),
                this::initializeWhenNoResponse
        );
    }

    private void initializeWhenResponse(@Nonnull Gw2HttpResponse rawResponse, @Nonnull TypeReference<T> dataType) {
        successful = rawResponse.statusCode() == 200;
        if(successful) {
            data = Optional.of(serializeData(rawResponse.content(), dataType));
        } else {
            data = Optional.empty();
        }

        apiError = !successful;
        if(apiError) {
            errorData = Optional.of(serializeErrorData(rawResponse));
        } else {
            errorData = Optional.empty();
        }
    }

    private void initializeWhenNoResponse() {
        noAnswer = true;
        data = Optional.empty();
        errorData = Optional.empty();
    }

    private T serializeData(String content, TypeReference<T> dataType) {
        return Gw2SdkSerialization.deserializeJson(content, dataType);
    }

    private Gw2ApiErrorData serializeErrorData(Gw2HttpResponse rawResponse) {
        return new Gw2ApiErrorData(rawResponse.content(), rawResponse.statusCode());
    }

    /**
     * Get the serialized data returned from the API. <b>Only call this after you made sure that the response</b>
     * {@link #isSuccessful()}.
     * @throws Gw2ApiResponseException If the response is not successful, and nothing can be returned.
     */
    public T data() throws Gw2ApiResponseException {
        return data.orElseThrow(() -> {
            if(apiError) {
                return new Gw2ApiResponseException("Unable to get data because the API has answered with an error");
            } else { //no answer
                return new Gw2ApiResponseException("Unable to get data because the API has not responded");
            }
        });
    }

    /**
     * Get the serialized error data from the API. <b>Only call this after you made sure that the response</b>
     * {@link #isApiError()}.
     * @throws Gw2ApiResponseException If the response was not an error, and nothing can be returned.
     */
    public Gw2ApiErrorData errorData() throws Gw2ApiResponseException {
        return errorData.orElseThrow(() -> {
            if(successful) {
                return new Gw2ApiResponseException("Unable to get error data because the API responded successfully, there are no errors");
            } else { //no answer
                return new Gw2ApiResponseException("Unable to get error data because the API has not responded");
            }
        });
    }

    /**
     * Convenience method of checking if the API has returned any response at all. The
     * response can be successful or error.
     */
    public boolean isCompleted() {
        return successful || apiError;
    }
}
