package com.my11heros.ui.home.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.my11heros.models.*
import com.my11heros.ui.contest.models.ContestModelLists
import com.my11heros.ui.createteam.models.PlayersInfoModel
import com.my11heros.ui.leadersboard.models.LeadersBoardModels
import com.my11heros.ui.leadersboard.models.PrizeBreakUpModels

class UsersPostDBResponse {
    @SerializedName("status")
    @Expose
    var status: Boolean = false

    @SerializedName("order_id")
    @Expose
    var orderId: String = ""

    @SerializedName("maintainance")
    @Expose
    var appMaintainance: Boolean = false

    @SerializedName("session_expired")
    @Expose
    var sessionExpired: Boolean = false

    @SerializedName("url")
    @Expose
    var updatedApkUrl: String = ""

    @SerializedName("splashScreen")
    @Expose
    var splash: String = ""

    @SerializedName("force_update")
    @Expose
    var forceupdate: Boolean = false

    @SerializedName("release_note")
    @Expose
    var releaseNote: String = ""

    @SerializedName("action")
    @Expose
    var actionForTeam: Int = 0

    @SerializedName("team_list")
    @Expose
    var selectedTeamModel: ArrayList<SelectedTeamModels>? = null

    @SerializedName("notification_list")
    @Expose
    var notificationList: ArrayList<NotifyModels>? = null

    @SerializedName("system_time")
    @Expose
    var systemTime: Long = 0

    @SerializedName("total_points")
    @Expose
    var totalPoints: String = ""

    @SerializedName("message")
    @Expose
    var message: String = ""

    @SerializedName("code")
    @Expose
    var code: Int = 0

    @SerializedName("walletInfo")
    @Expose
    var walletObjects: WalletInfo? = null

    @SerializedName("transaction_history")
    @Expose
    var transactionHistory: WalletInfo? = null

    @SerializedName("referal_user")
    @Expose
    var referalUserList: ArrayList<RefferalUsersModel>? = null

    @SerializedName("response")
    @Expose
    var responseObject: Response? = null

    @SerializedName("scores")
    @Expose
    var scoresModel: ScoresBoardModels? = null

    @SerializedName("leaderBoard")
    @Expose
    var leaderBoardList: ArrayList<LeadersBoardModels>? = null

    inner class Response {

        @SerializedName("total_team_joined")
        @Expose
        var totalTeamJoined: Int? = null

        @SerializedName("total_match_played")
        @Expose
        var totalMatchPlayed: Int? = null

        @SerializedName("total_contest_joined")
        @Expose
        var totalContestJoined: Int? = null

        @SerializedName("total_unique_contest")
        @Expose
        var totalUniqueContest: Int? = null

        @SerializedName("total_match_win")
        @Expose
        var totalMatchWin: Int? = null

        @SerializedName("total_winning_amount")
        @Expose
        var totalWinningAmount: Int? = null


        @SerializedName("matchdata")
        @Expose
        var matchdatalist: List<MatchesModels>? = null

        @SerializedName("matchcontests")
        @Expose
        var matchContestlist: List<ContestsParentModels>? = null

        @SerializedName("prizeBreakup")
        @Expose
        var prizeBreakUpModelsList: List<PrizeBreakUpModels>? = null

        @SerializedName("players")
        @Expose
        var playersList: PlayerModels? = null

        @SerializedName("myteam")
        @Expose
        var myTeamList: List<MyTeamModels>? = null

        @SerializedName("my_joined_contest")
        @Expose
        var myJoinedContest: List<ContestModelLists>? = null


        @SerializedName("myjoinedTeams")
        @Expose
        var myjoinedTeams: ArrayList<MyTeamModels>? = null

        @SerializedName("myjoinedContest")
        @Expose
        var joinedContestDetails: ArrayList<ContestModelLists>? = null


        @SerializedName("player_points")
        @Expose
        var playerPointsList: ArrayList<PlayersInfoModel>? = null


        /**
         * bank Accounts
         */
        @SerializedName("documents")
        @Expose
        var documentsList: ArrayList<DocumentsModels>? = null

        @SerializedName("paytm")
        @Expose
        var paytmNoList: ArrayList<DocumentsModels>? = null

        @SerializedName("bank_accounts")
        @Expose
        var bankAccountsList: ArrayList<DocumentsModels>? = null

    }

    inner class ScoresBoardModels {

        @SerializedName("status")
        @Expose
        var matchStatus: Int = 0

        @SerializedName("status_note")
        @Expose
        var statusNote: String = ""

        @SerializedName("teama")
        @Expose
        var teama: TeamAInfo? = null

        @SerializedName("teamb")
        @Expose
        var teamb: TeamBInfo? = null
    }

    @SerializedName("rozar_key")
    @Expose
    var razorPay: String = ""

    @SerializedName("paytm_show")
    @Expose
    var paytm_show: Boolean = false

    @SerializedName("rozarpay_show")
    @Expose
    var rozarpay_show: Boolean = false

    @SerializedName("gpay_show")
    @Expose
    var gpay_show: Boolean = false
}


