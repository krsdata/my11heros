package com.my11heros.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class SelectedTeamModels:Serializable,Cloneable {

    var viewType : Int=0


    @SerializedName("close_team")
    @Expose
    var closeTeamList: ArrayList<MyJoinedTeamModels> ?=null

    @SerializedName("open_team")
    @Expose
    var openTeamList: ArrayList<MyTeamModels> ?=null

}
