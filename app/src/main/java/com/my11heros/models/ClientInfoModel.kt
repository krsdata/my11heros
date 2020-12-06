package com.my11heros.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class ClientInfoModel :Serializable,Cloneable {



    @SerializedName("zerodha_user_id")
    @Expose
    val zerodhaUserId: String = ""

    @SerializedName("zerodha_secretkey")
    @Expose
    var zerodhaSecretKey: String = ""

    @SerializedName("zerodha_apikey")
    @Expose
    var zerodhaApiKey: String = ""

    @SerializedName("exchange")
    @Expose
    var exchg: String = ""


    public override fun clone(): ClientInfoModel {
        return super.clone() as ClientInfoModel
    }


}
