package com.my11heros.ui.createteam.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class AnalyticsModel :Serializable,Cloneable{

    @SerializedName("selection")
    @Expose
    var selectionPc: Double = 0.0

    @SerializedName("captain")
    @Expose
    var captainPc: Double = 0.0

    @SerializedName("vice_captain")
    @Expose
    var viceCaptainPc: Double = 0.0

    @SerializedName("trump")
    @Expose
    var trumpPc: Double = 0.0



    public override fun clone(): AnalyticsModel {
        return super.clone() as AnalyticsModel
    }

}
