package com.my11heros.ui.createteam.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.my11heros.R
import java.io.Serializable

class PlayersInfoModel :Serializable,Cloneable{

    private var playerIcon: Int= R.drawable.ic_player_bat_teama
    var isSelected: Boolean = false
    var viewType: Int = 0

    @SerializedName("captain")
    @Expose
    var isCaptain: Boolean = false

    @SerializedName("vice_captain")
    @Expose
    var isViceCaptain: Boolean = false

    @SerializedName("trump")
    @Expose
    var isTrump: Boolean = false

    @SerializedName("role")
    @Expose
    var playerRole: String = ""

    @SerializedName("playing11")
    @Expose
    var isPlaying11: Boolean = false

    @SerializedName("pid")
    @Expose
    var playerId: Int = 0

    @SerializedName("match_id")
    @Expose
    var matchId: Int = 0

    @SerializedName("team_id")
    @Expose
    var teamId: Int = 0

    @SerializedName("short_name")
    @Expose
    var shortName: String = ""

    @SerializedName("player_image")
    @Expose
    var playerImage: String = "https"

    @SerializedName("team_name")
    @Expose
    var teamShortName: String = ""

    @SerializedName("fantasy_player_rating")
    @Expose
    var fantasyPlayerRating: Double=0.0

    @SerializedName("playerPoints")
    @Expose
    var playerSeriesPoints: Int=0

    @SerializedName("points")
    @Expose
    var playerPoints: String=""

    @SerializedName("analytics")
    @Expose
    var analyticsModel: AnalyticsModel?=null

    fun setPlayerIcon(playerIcon:Int) {
        this.playerIcon = playerIcon
    }

    fun getPlayerIcon() : Int{
         return this.playerIcon
    }

    public override fun clone(): PlayersInfoModel {
        return super.clone() as PlayersInfoModel
    }



}
