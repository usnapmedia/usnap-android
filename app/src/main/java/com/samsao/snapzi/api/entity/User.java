package com.samsao.snapzi.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.samsao.snapzi.api.util.CustomJsonDateTimeDeserializer;

/**
 * @author jingsilu
 * @since 2015-05-11
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "email",
        "username",
        "first_name",
        "last_name",
        "dob",
        "profile_pic",
        "contribution",
        "score"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    @JsonProperty("email")
    private String email;
    @JsonProperty("username")
    private String username;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("dob")
    private String dob;
    @JsonProperty("profile_pic")
    private String profilePic;
    @JsonProperty("contribution")
    private Integer contribution;
    @JsonProperty("score")
    private Integer score;

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
     * The firstName
     */
    @JsonProperty("first_name")
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     * @param firstName
     * The first_name
     */
    @JsonProperty("first_name")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     *
     * @return
     * The lastName
     */
    @JsonProperty("last_name")
    public String getLastName() {
        return lastName;
    }

    /**
     *
     * @param lastName
     * The last_name
     */
    @JsonProperty("last_name")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     *
     * @return
     * The dob
     */
    @JsonProperty("dob")
    public String getDob() {
        return dob;
    }

    /**
     *
     * @param dob
     * The dob
     */
    @JsonProperty("dob")
    public void setDob(String dob) {
        this.dob = dob;
    }

    public Long getBirthdayLong() {
        try {
            return CustomJsonDateTimeDeserializer.getDateFormatter().parseMillis(getDob());
        } catch (Exception e) {
            return 0l;
        }
    }

    /**
     *
     * @return
     * The profilePic
     */
    @JsonProperty("profile_pic")
    public String getProfilePic() {
        return profilePic;
    }

    /**
     *
     * @param profilePic
     * The profile_pic
     */
    @JsonProperty("profile_pic")
    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    @JsonProperty("contribution")
    public Integer getContribution() {
        return contribution;
    }

    @JsonProperty("contribution")
    public void setContribution(Integer contribution) {
        this.contribution = contribution;
    }

    @JsonProperty("score")
    public Integer getScore() {
        return score;
    }

    @JsonProperty("score")
    public void setScore(Integer score) {
        this.score = score;
    }
}
