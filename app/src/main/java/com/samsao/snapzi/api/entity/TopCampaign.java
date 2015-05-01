package com.samsao.snapzi.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
    private String thumbUrl;
    @JsonProperty("text")
    private String text;
    @JsonProperty("fb_likes")
    private Integer fbLikes;
    @JsonProperty("campaign_id")
    private String campaignId;
    @JsonProperty("usnap_score")
    private Integer usnapScore;

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
    public String getThumbUrl() {
        return thumbUrl;
    }

    /**
     *
     * @param thumbUrl
     * The thumb_url
     */
    @JsonProperty("thumb_url")
    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    /**
     *
     * @return
     * The text
     */
    @JsonProperty("text")
    public String getText() {
        return text;
    }

    /**
     *
     * @param text
     * The text
     */
    @JsonProperty("text")
    public void setText(String text) {
        this.text = text;
    }

    /**
     *
     * @return
     * The fbLikes
     */
    @JsonProperty("fb_likes")
    public Integer getFbLikes() {
        return fbLikes;
    }

    /**
     *
     * @param fbLikes
     * The fb_likes
     */
    @JsonProperty("fb_likes")
    public void setFbLikes(Integer fbLikes) {
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
    public Integer getUsnapScore() {
        return usnapScore;
    }

    /**
     *
     * @param usnapScore
     * The usnap_score
     */
    @JsonProperty("usnap_score")
    public void setUsnapScore(Integer usnapScore) {
        this.usnapScore = usnapScore;
    }
}
