package com.my11heros.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UpcomingMatchesModel : Serializable, Cloneable {

    @SerializedName("match_id")
    @Expose
    var matchId: Int = 0

    @SerializedName("short_title")
    @Expose
    var matchShortTitle: String = ""

    @SerializedName("title")
    @Expose
    var matchTitle: String = ""

    @SerializedName("is_lineup")
    @Expose
    var isLineup: Boolean = false

    @SerializedName("league_title")
    @Expose
    var leagueTitle: String = ""

    @SerializedName("has_free_contest")
    @Expose
    var freeContest: Boolean = false

    @SerializedName("prize_amount")
    @Expose
    var prizeAmount: String = ""

    @SerializedName("total_joined_team")
    @Expose
    var totalTeams: Int = 0

    @SerializedName("total_join_contests")
    @Expose
    var totalJoinContests: Int = 0


    @SerializedName("status")
    @Expose
    var status: Int = 0   //1 = Schedule, //2=Live //3=Completed

    @SerializedName("status_str")
    @Expose
    var statusString: String = "" //1 = Schedule, //2=Live //3=Completed

    @SerializedName("date_start")
    @Expose
    var dateStart: String = ""

    @SerializedName("timestamp_start")
    @Expose
    var timestampStart: Long = 0

    @SerializedName("timestamp_end")
    @Expose
    var timestampEnd: Long = 0

    @SerializedName("teama")
    @Expose
    var teamAInfo: TeamAInfo? = null

    @SerializedName("teamb")
    @Expose
    var teamBInfo: TeamAInfo? = null

    @SerializedName("contest_type")
    @Expose
    var contestName: String? = null

    @SerializedName("contest_prize")
    @Expose
    var contestPrize: String = ""


    public override fun clone(): UpcomingMatchesModel {
        return super.clone() as UpcomingMatchesModel
    }

}
