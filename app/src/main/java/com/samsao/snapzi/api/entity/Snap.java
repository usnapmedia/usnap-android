package com.samsao.snapzi.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author jingsilu
 * @since 2015-04-17
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "email",
        "username",
        "url",
        "watermark_url",
        "video_url",
        "cloud_url",
        "text",
        "fb_likes",
        "campaign_id",
        "usnap_score"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Snap {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("email")
    private String email;
    @JsonProperty("username")
    private String username;
    @JsonProperty("url")
    private String url;
    @JsonProperty("watermark_url")
    private String watermarkUrl;
    @JsonProperty("video_url")
    private String videoUrl;
    @JsonProperty("cloud_url")
    private String cloudUrl;
    @JsonProperty("text")
    private String text;
    @JsonProperty("fb_likes")
    private Integer fbLikes;
    @JsonProperty("campaign_id")
    private String campaignId;
    @JsonProperty("usnap_score")
    private Integer usnapScore;

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

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
    @JsonProperty("cloud_url")
    public String getCloudUrl() {
        return cloudUrl;
    }

    public String getThumbnail(int width, int height) {
        String url = this.getCloudUrl();
        if (url.indexOf(".mp4") != -1) {
            url = url.replaceAll(".mp4", ".jpg");
        }
        return url.replaceAll("upload/", "upload/w_" + width + ",h_" + height + ",c_fill,g_face/");
    }
    /**
     *
     * @param cloudUrl
     * The cloud_url
     */
    @JsonProperty("cloud_url")
    public void setCloudUrl(String cloudUrl) {
        this.cloudUrl = cloudUrl;
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

    @JsonProperty("watermark_url")
    public String getWatermarkUrl() {
        return watermarkUrl;
    }

    @JsonProperty("watermark_url")
    public void setWatermarkUrl(String watermarkUrl) {
        this.watermarkUrl = watermarkUrl;
    }

    @JsonProperty("video_url")
    public String getVideoUrl() {
        return videoUrl;
    }

    @JsonProperty("video_url")
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}