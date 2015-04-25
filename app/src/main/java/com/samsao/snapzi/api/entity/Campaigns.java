package com.samsao.snapzi.api.entity;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jingsilu
 * @since 2015-04-24
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "app_id",
        "name",
        "description",
        "banner_img_url",
        "start_date",
        "end_date",
        "prize",
        "rules"
})

public class Campaigns {

    @JsonProperty("id")
    private String id;
    @JsonProperty("app_id")
    private Object appId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("banner_img_url")
    private String bannerImgUrl;
    @JsonProperty("start_date")
    private String startDate;
    @JsonProperty("end_date")
    private String endDate;
    @JsonProperty("prize")
    private String prize;
    @JsonProperty("rules")
    private String rules;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The id
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The appId
     */
    @JsonProperty("app_id")
    public Object getAppId() {
        return appId;
    }

    /**
     *
     * @param appId
     * The app_id
     */
    @JsonProperty("app_id")
    public void setAppId(Object appId) {
        this.appId = appId;
    }

    /**
     *
     * @return
     * The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The description
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     * The bannerImgUrl
     */
    @JsonProperty("banner_img_url")
    public String getBannerImgUrl() {
        return bannerImgUrl;
    }

    /**
     *
     * @param bannerImgUrl
     * The banner_img_url
     */
    @JsonProperty("banner_img_url")
    public void setBannerImgUrl(String bannerImgUrl) {
        this.bannerImgUrl = bannerImgUrl;
    }

    /**
     *
     * @return
     * The startDate
     */
    @JsonProperty("start_date")
    public String getStartDate() {
        return startDate;
    }

    /**
     *
     * @param startDate
     * The start_date
     */
    @JsonProperty("start_date")
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     *
     * @return
     * The endDate
     */
    @JsonProperty("end_date")
    public String getEndDate() {
        return endDate;
    }

    /**
     *
     * @param endDate
     * The end_date
     */
    @JsonProperty("end_date")
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     *
     * @return
     * The prize
     */
    @JsonProperty("prize")
    public String getPrize() {
        return prize;
    }

    /**
     *
     * @param prize
     * The prize
     */
    @JsonProperty("prize")
    public void setPrize(String prize) {
        this.prize = prize;
    }

    /**
     *
     * @return
     * The rules
     */
    @JsonProperty("rules")
    public String getRules() {
        return rules;
    }

    /**
     *
     * @param rules
     * The rules
     */
    @JsonProperty("rules")
    public void setRules(String rules) {
        this.rules = rules;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
