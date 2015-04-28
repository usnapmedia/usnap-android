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
        "id",
        "email",
        "image_data",
        "meta",
        "text",
        "filename",
        "url",
        "fb_image_id",
        "tw_image_id",
        "fb_likes",
        "fb",
        "tw_key",
        "tw_secret",
        "gp",
        "status",
        "app_id"
})
public class LatestUploads {
    @JsonProperty("id")
    private String id;
    @JsonProperty("email")
    private String email;
    @JsonProperty("image_data")
    private String imageData;
    @JsonProperty("meta")
    private String meta;
    @JsonProperty("text")
    private String text;
    @JsonProperty("filename")
    private String filename;
    @JsonProperty("url")
    private String url;
    @JsonProperty("fb_image_id")
    private Object fbImageId;
    @JsonProperty("tw_image_id")
    private Object twImageId;
    @JsonProperty("fb_likes")
    private Object fbLikes;
    @JsonProperty("fb")
    private Object fb;
    @JsonProperty("tw_key")
    private Object twKey;
    @JsonProperty("tw_secret")
    private Object twSecret;
    @JsonProperty("gp")
    private Object gp;
    @JsonProperty("status")
    private String status;
    @JsonProperty("app_id")
    private Object appId;
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
     * The imageData
     */
    @JsonProperty("image_data")
    public String getImageData() {
        return imageData;
    }

    /**
     *
     * @param imageData
     * The image_data
     */
    @JsonProperty("image_data")
    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    /**
     *
     * @return
     * The meta
     */
    @JsonProperty("meta")
    public String getMeta() {
        return meta;
    }

    /**
     *
     * @param meta
     * The meta
     */
    @JsonProperty("meta")
    public void setMeta(String meta) {
        this.meta = meta;
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
     * The filename
     */
    @JsonProperty("filename")
    public String getFilename() {
        return filename;
    }

    /**
     *
     * @param filename
     * The filename
     */
    @JsonProperty("filename")
    public void setFilename(String filename) {
        this.filename = filename;
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
     * The fbImageId
     */
    @JsonProperty("fb_image_id")
    public Object getFbImageId() {
        return fbImageId;
    }

    /**
     *
     * @param fbImageId
     * The fb_image_id
     */
    @JsonProperty("fb_image_id")
    public void setFbImageId(Object fbImageId) {
        this.fbImageId = fbImageId;
    }

    /**
     *
     * @return
     * The twImageId
     */
    @JsonProperty("tw_image_id")
    public Object getTwImageId() {
        return twImageId;
    }

    /**
     *
     * @param twImageId
     * The tw_image_id
     */
    @JsonProperty("tw_image_id")
    public void setTwImageId(Object twImageId) {
        this.twImageId = twImageId;
    }

    /**
     *
     * @return
     * The fbLikes
     */
    @JsonProperty("fb_likes")
    public Object getFbLikes() {
        return fbLikes;
    }

    /**
     *
     * @param fbLikes
     * The fb_likes
     */
    @JsonProperty("fb_likes")
    public void setFbLikes(Object fbLikes) {
        this.fbLikes = fbLikes;
    }

    /**
     *
     * @return
     * The fb
     */
    @JsonProperty("fb")
    public Object getFb() {
        return fb;
    }

    /**
     *
     * @param fb
     * The fb
     */
    @JsonProperty("fb")
    public void setFb(Object fb) {
        this.fb = fb;
    }

    /**
     *
     * @return
     * The twKey
     */
    @JsonProperty("tw_key")
    public Object getTwKey() {
        return twKey;
    }

    /**
     *
     * @param twKey
     * The tw_key
     */
    @JsonProperty("tw_key")
    public void setTwKey(Object twKey) {
        this.twKey = twKey;
    }

    /**
     *
     * @return
     * The twSecret
     */
    @JsonProperty("tw_secret")
    public Object getTwSecret() {
        return twSecret;
    }

    /**
     *
     * @param twSecret
     * The tw_secret
     */
    @JsonProperty("tw_secret")
    public void setTwSecret(Object twSecret) {
        this.twSecret = twSecret;
    }

    /**
     *
     * @return
     * The gp
     */
    @JsonProperty("gp")
    public Object getGp() {
        return gp;
    }

    /**
     *
     * @param gp
     * The gp
     */
    @JsonProperty("gp")
    public void setGp(Object gp) {
        this.gp = gp;
    }

    /**
     *
     * @return
     * The status
     */
    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
