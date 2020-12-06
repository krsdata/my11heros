package com.my11heros.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


 class CheckInListInfo :Serializable,Cloneable {


    @SerializedName("id")
    @Expose
    val checkInId: String = ""

    @SerializedName("user_name")
    @Expose
    val name: String = ""

    @SerializedName("user_email")
    @Expose
    val email: String = ""


    @SerializedName("user_mobile")
    @Expose
    val mobile: String = ""

    @SerializedName("account_status")
    @Expose
    val status: Int = 0

    @SerializedName("created_date")
    @Expose
    val activationDate: String = ""

    public override fun clone(): CheckInListInfo {
        return super.clone() as CheckInListInfo
    }



}
