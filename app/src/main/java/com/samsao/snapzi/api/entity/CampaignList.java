package com.samsao.snapzi.api.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jingsilu
 * @since 2015-04-24
 */

@ParcelablePlease
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "count",
        "response"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class CampaignList implements Parcelable {

    @JsonProperty("count")
    public Integer count;
    @JsonProperty("response")
    public List<Campaign> response = new ArrayList<>();

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
    public List<Campaign> getResponse() {
        return response;
    }

    /**
     * @param response The response
     */
    @JsonProperty("response")
    public void setResponse(List<Campaign> response) {
        this.response = response;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        CampaignListParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<CampaignList> CREATOR = new Creator<CampaignList>() {
        public CampaignList createFromParcel(Parcel source) {
            CampaignList target = new CampaignList();
            CampaignListParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public CampaignList[] newArray(int size) {
            return new CampaignList[size];
        }
    };
}