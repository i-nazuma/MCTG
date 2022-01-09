package app.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

//this class is just for the updateUser function, so we can access the JSON String more easily
public class UserProfile {
    @Getter
    @Setter
    @JsonAlias({"Name"})
    private String name;
    @Getter
    @Setter
    @JsonAlias({"Bio"})
    private String bio;
    @Getter
    @Setter
    @JsonAlias({"Image"})
    private String image;

    public UserProfile() {} //needed for Jackson

    public UserProfile(String name, String bio, String image) {
        this.name = name;
        this.bio = bio;
        this.image = image;
    }
}
