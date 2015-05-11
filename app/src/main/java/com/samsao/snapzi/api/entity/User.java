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
 * @since 2015-05-11
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "email",
        "username",
        "first_name",
        "last_name",
        "dob",
        "profile_pic"
})

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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
