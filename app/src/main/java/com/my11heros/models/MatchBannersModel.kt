package com.my11heros.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class MatchBannersModel :Serializable,Cloneable{

    @SerializedName("url")
    @Expose
    val bannerUrl: String = ""

    @SerializedName("title")
    @Expose
    val title: String = ""

    @SerializedName("description")
    @Expose
    val descriptions: String = ""



    public override fun clone(): MatchBannersModel {
        return super.clone() as MatchBannersModel
    }

    override fun toString(): String {
        return "bannerUrl(Id='$bannerUrl')"
    }


}
