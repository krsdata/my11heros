package com.my11heros.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


 class NotifyModels :Serializable,Cloneable {


    @SerializedName("title")
    @Expose
    val notificationTitle: String = ""

    @SerializedName("messages")
    @Expose
    val notificationMessages: String = ""


    @SerializedName("created_date")
    @Expose
    val activationDate: String = ""

    public override fun clone(): NotifyModels {
        return super.clone() as NotifyModels
    }



}
