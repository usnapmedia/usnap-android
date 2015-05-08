package com.samsao.snapzi.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author jfcartier
 * @since 15-03-25
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "response"
})
public class Error {
    @JsonProperty("response")
    private String response;

    /**
     * @return The response
     */
    @JsonProperty("response")
    public String getResponse() {
        return response;
    }

    /**
     * @param response The response
     */
    @JsonProperty("response")
    public void setResponse(String response) {
        this.response = response;
    }
}
