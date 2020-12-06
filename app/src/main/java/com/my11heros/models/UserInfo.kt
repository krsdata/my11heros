package com.my11heros.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UserInfo : Serializable, Cloneable {


    @SerializedName("email")
    @Expose
    var userEmail: String = ""

    @SerializedName("referal_code")
    @Expose
    var referalCode: String = ""

    @SerializedName("profile_image")
    @Expose
    var profileImage: String = ""


    @SerializedName("otpverified")
    @Expose
    var isOtpVerified: Boolean = false


    @SerializedName("user_id")
    @Expose
    val userId: String = ""

    @SerializedName("mobile_number")
    @Expose
    var mobileNumber: String = ""

    @SerializedName("name")
    @Expose
    var fullName: String = ""

    @SerializedName("team_name")
    @Expose
    var teamName: String = ""

    @SerializedName("city")
    @Expose
    var city: String = ""

    @SerializedName("gender")
    @Expose
    var gender: String = ""

    @SerializedName("dateOfBirth")
    @Expose
    var dateOfBirth: String = ""

    @SerializedName("pinCode")
    @Expose
    var pinCode: String = ""

    @SerializedName("state")
    @Expose
    var state: String = ""


    @SerializedName("apk_url")
    @Expose
    var apkUrl: String = ""


    public override fun clone(): UserInfo {
        return super.clone() as UserInfo
    }
}
