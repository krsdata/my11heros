package com.my11heros.ui.leadersboard.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.my11heros.models.UserInfo

class LeadersBoardModels {

    @SerializedName("match_id")
    @Expose
    val matchId: Int =0

    @SerializedName("team_id")
    @Expose
    val teamId: Int = 0

    @SerializedName("user_id")
    @Expose
    val userId: String = ""

    @SerializedName("team")
    @Expose
    val teamName: String = ""

    @SerializedName("point")
    @Expose
    val teamPoints: String = ""

    @SerializedName("rank")
    @Expose
    val teamRanks: String = ""

    @SerializedName("prize_amount")
    @Expose
    var teamWonStatus: String =""

    @SerializedName("user")
    @Expose
    val userInfo: UserInfo? = null

}
