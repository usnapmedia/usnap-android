package com.samsao.snapzi.api.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.samsao.snapzi.api.util.CustomJsonDateTimeDeserializer;

import org.joda.time.DateTime;

/**
 * @author jingsilu
 * @since 2015-04-24
 */

@ParcelablePlease
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
public class Campaign implements Parcelable {

    @JsonProperty("id")
    public Integer id;
    @JsonProperty("app_id")
    public Integer appId;
    @JsonProperty("name")
    public String name;
    @JsonProperty("description")
    public String description;
    @JsonProperty("banner_img_url")
    public String bannerImgUrl;
    @JsonProperty("start_date")
    @JsonDeserialize(using = CustomJsonDateTimeDeserializer.class)
    public DateTime startDate;
    @JsonProperty("end_date")
    @JsonDeserialize(using = CustomJsonDateTimeDeserializer.class)
    public DateTime endDate;
    @JsonProperty("prize")
    public String prize;
    @JsonProperty("rules")
    public String rules;

    /**
     *
     * @return
     * The id
     */
    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    @JsonProperty("id")
    public void setId(Integer id) {
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
    public void setAppId(Integer appId) {
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
    public DateTime getStartDate() {
        return startDate;
    }

    /**
     *
     * @param startDate
     * The start_date
     */
    @JsonProperty("start_date")
    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    /**
     *
     * @return
     * The endDate
     */
    @JsonProperty("end_date")
    public DateTime getEndDate() {
        return endDate;
    }

    /**
     *
     * @param endDate
     * The end_date
     */
    @JsonProperty("end_date")
    public void setEndDate(DateTime endDate) {
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        CampaignParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<Campaign> CREATOR = new Creator<Campaign>() {
        public Campaign createFromParcel(Parcel source) {
            Campaign target = new Campaign();
            CampaignParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public Campaign[] newArray(int size) {
            return new Campaign[size];
        }
    };
}
