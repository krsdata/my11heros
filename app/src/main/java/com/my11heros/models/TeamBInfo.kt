package com.my11heros.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class TeamBInfo :Serializable,Cloneable {



    @SerializedName("match_id")
    @Expose
    val matchId: Int = 0

    @SerializedName("team_id")
    @Expose
    val teamId: Int = 0

    @SerializedName("name")
    @Expose
    val teamName: String = ""

    @SerializedName("short_name")
    @Expose
    val teamShortName: String = ""

    @SerializedName("logo_url")
    @Expose
    val logoUrl: String = ""

    @SerializedName("local_img_url")
    @Expose
    var localImgUrl: String = ""

    @SerializedName("thumb_url")
    @Expose
    var thumbUrl: String = ""

    @SerializedName("scores_full")
    @Expose
    var scoresFull: String = ""

    @SerializedName("scores")
    @Expose
    var scores: String = ""

    @SerializedName("overs")
    @Expose
    var overs: String = ""


    public override fun clone(): TeamBInfo {
        return super.clone() as TeamBInfo
    }


}
