package com.my11heros.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ZerodhaConfigModel: Serializable,Cloneable  {

    @SerializedName("zerodha_user_id")
    @Expose
    val userId: String = ""

    @SerializedName("zerodha_secretkey")
    @Expose
    val secretKey: String = ""

    @SerializedName("zerodha_apikey")
    @Expose
    val apiKey: String = ""


}
