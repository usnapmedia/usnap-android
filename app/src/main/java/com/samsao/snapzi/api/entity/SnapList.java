package com.samsao.snapzi.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jingsilu
 * @since 2015-04-17
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "count",
        "response"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class SnapList {

    @JsonProperty("count")
    private Integer count;
    @JsonProperty("response")
    private List<Snap> response = new ArrayList<>();

    /**
     * @return The count
     */
    @JsonProperty("count")
    public Integer getCount() {
        return count;
    }

    /**
     * @param count The count
     */
    @JsonProperty("count")
    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * @return The response
     */
    @JsonProperty("response")
    public List<Snap> getResponse() {
        return response;
    }

    /**
     * @param response The response
     */
    @JsonProperty("response")
    public void setResponse(List<Snap> response) {
        this.response = response;
    }
}