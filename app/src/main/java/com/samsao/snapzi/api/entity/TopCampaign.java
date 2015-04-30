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
 * @since 2015-04-27
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "email",
        "username",
        "url",
        "thumb_url",
        "text",
        "fb_likes",
        "campaign_id",
        "usnap_score"
})

public class TopCampaign {
    @JsonProperty("email")
    private String email;
    @JsonProperty("username")
    private String username;
    @JsonProperty("url")
    private String url;
    @JsonProperty("thumb_url")
    private Object thumbUrl;
    @JsonProperty("text")
    private Object text;
    @JsonProperty("fb_likes")
    private String fbLikes;
    @JsonProperty("campaign_id")
    private String campaignId;
    @JsonProperty("usnap_score")
    private Object usnapScore;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The email
     */
    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     * The email
     */
    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return
     * The username
     */
    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     * The username
     */
    @JsonProperty("username")
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return
     * The url
     */
    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     * The url
     */
    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     *
     * @return
     * The thumbUrl
     */
    @JsonProperty("thumb_url")
    public Object getThumbUrl() {
        return thumbUrl;
    }

    /**
     *
     * @param thumbUrl
     * The thumb_url
     */
    @JsonProperty("thumb_url")
    public void setThumbUrl(Object thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    /**
     *
     * @return
     * The text
     */
    @JsonProperty("text")
    public Object getText() {
        return text;
    }

    /**
     *
     * @param text
     * The text
     */
    @JsonProperty("text")
    public void setText(Object text) {
        this.text = text;
    }

    /**
     *
     * @return
     * The fbLikes
     */
    @JsonProperty("fb_likes")
    public String getFbLikes() {
        return fbLikes;
    }

    /**
     *
     * @param fbLikes
     * The fb_likes
     */
    @JsonProperty("fb_likes")
    public void setFbLikes(String fbLikes) {
        this.fbLikes = fbLikes;
    }

    /**
     *
     * @return
     * The campaignId
     */
    @JsonProperty("campaign_id")
    public String getCampaignId() {
        return campaignId;
    }

    /**
     *
     * @param campaignId
     * The campaign_id
     */
    @JsonProperty("campaign_id")
    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    /**
     *
     * @return
     * The usnapScore
     */
    @JsonProperty("usnap_score")
    public Object getUsnapScore() {
        return usnapScore;
    }

    /**
     *
     * @param usnapScore
     * The usnap_score
     */
    @JsonProperty("usnap_score")
    public void setUsnapScore(Object usnapScore) {
        this.usnapScore = usnapScore;
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
