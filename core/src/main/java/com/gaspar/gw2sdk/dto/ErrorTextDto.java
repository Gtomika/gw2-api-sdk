package com.gaspar.gw2sdk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is the format in which the GW2 API returns with errors.
 * <pre>{@code
 * {
 *     "text": "some error occurred"
 * }
 * }</pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorTextDto {
    private String text;
}
