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
 * @since 2015-04-17
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
        "fb_likes",
        "fb",
        "tw",
        "gp"
})
public class FeedImage {

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
    private String fbImageId;
    @JsonProperty("fb_likes")
    private String fbLikes;
    @JsonProperty("fb")
    private Object fb;
    @JsonProperty("tw")
    private Object tw;
    @JsonProperty("gp")
    private Object gp;
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
    public String getFbImageId() {
        return fbImageId;
    }

    /**
     *
     * @param fbImageId
     * The fb_image_id
     */
    @JsonProperty("fb_image_id")
    public void setFbImageId(String fbImageId) {
        this.fbImageId = fbImageId;
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
     * The tw
     */
    @JsonProperty("tw")
    public Object getTw() {
        return tw;
    }

    /**
     *
     * @param tw
     * The tw
     */
    @JsonProperty("tw")
    public void setTw(Object tw) {
        this.tw = tw;
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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}