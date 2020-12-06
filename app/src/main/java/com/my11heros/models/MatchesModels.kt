package com.my11heros.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class MatchesModels : Serializable, Cloneable {

    var viewType: Int = 0

    @SerializedName("joinedmatches")
    @Expose
    var joinedMatchModel: ArrayList<JoinedMatchModel>? = null

    @SerializedName("banners")
    @Expose
    var matchBanners: ArrayList<MatchBannersModel>? = null

    @SerializedName("upcomingmatches")
    @Expose
    var upcomingMatches: ArrayList<UpcomingMatchesModel>? = null

    @SerializedName("upcomingMatch")
    @Expose
    var upcomingMatchHistory: ArrayList<UpcomingMatchesModel>? = null

    @SerializedName("live")
    @Expose
    var liveMatchHistory: ArrayList<JoinedMatchModel>? = null

    @SerializedName("completed")
    @Expose
    var completedMatchHistory: ArrayList<JoinedMatchModel>? = null


}
