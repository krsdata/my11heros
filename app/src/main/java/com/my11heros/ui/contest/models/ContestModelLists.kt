package com.my11heros.ui.contest.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.my11heros.models.MyTeamModels
import java.io.Serializable


class ContestModelLists :Serializable,Cloneable {

    @SerializedName("contestId")
    @Expose
    var id: Int = 0

    @SerializedName("isCancelled")
    @Expose
    var isContestCancelled: Boolean = false

    @SerializedName("match_id")
    @Expose
    var matchId: String = ""

    @SerializedName("totalWinningPrize")
    @Expose
    var totalWinningPrize: String = ""

    @SerializedName("entryFees")
    @Expose
    var entryFees: String = ""

    @SerializedName("match_status")
    @Expose
    var matchStatus: String = ""

    @SerializedName("totalSpots")
    @Expose
    var totalSpots: Int = 0

    @SerializedName("filledSpots")
    @Expose
    var filledSpots: Int = 0

    @SerializedName("firstPrice")
    @Expose
    var firstPrice: String = ""

    @SerializedName("winnerPercentage")
    @Expose
    var winnerPercentage: String = ""

    @SerializedName("winnerCount")
    @Expose
    var winnerCounts: String? = ""

    @SerializedName("maxAllowedTeam")
    @Expose
    var maxAllowedTeam: Int = 0

    @SerializedName("usable_bonus")
    @Expose
    var usableBonus: String = ""

    @SerializedName("bonus_contest")
    @Expose
    var isBonusContest: Boolean = false

    @SerializedName("cancellation")
    @Expose
    var cancellation: Boolean = false

    @SerializedName("joinedTeams")
    @Expose
    var joinedTeams: ArrayList<MyTeamModels>? = null

    public override fun clone(): ContestModelLists {
        return super.clone() as ContestModelLists
    }
}