package com.thirdandloom.storyflow.models;

import com.google.gson.annotations.SerializedName;


public class User extends BaseModel {
    @SerializedName("id")
    private int id;
    @SerializedName("email")
    private String email;
    @SerializedName("lastRequestAt")
    private String lastRequestedAt;
    @SerializedName("login")
    private String login;
    @SerializedName("shipAddressId")
    private int shipAddressId;
    @SerializedName("billAddressId")
    private int billAddressId;
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("updatedAt")
    private String updatedAt;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("defaultMeasurementsId")
    private int defaultMeasurementsId;
    @SerializedName("continueRegistrationToken")
    private boolean continueRegistrationToken;
    @SerializedName("identityId")
    private int identityId;
    @SerializedName("location")
    private String uid;
    @SerializedName("personalQuote")
    private String personalQuote;
    @SerializedName("username")
    private String username;
    @SerializedName("confirmed")
    private boolean confirmed;
    @SerializedName("lastSignOut")
    private String lastSignOut;
    @SerializedName("coverBackgroundColor")
    private String coverBackgroundColor;
    @SerializedName("online")
    private String online;

    //"profileImage":{  },
    //"profileImages":[  ],
    //"coverImage":{  },
    //"coverImages":[  ],
    //"onlineImage":{  },
    //"chatImage":{  },
}
