package framework.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserData {
    @JsonProperty("username")
    public String username;

    @JsonProperty("password")
    public String password;

    @JsonProperty("role")
    public String role;

    @JsonProperty("expectSuccess")
    public boolean expectSuccess;

    @JsonProperty("description")
    public String description;
}