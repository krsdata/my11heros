package com.my11heros.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ResponseModel :Serializable {



    @SerializedName("status")
    val status: Boolean = false

    @SerializedName("code")
    val statusCode: Int = 0

    @SerializedName("is_otp_required")
    val isOTPRequired: Boolean = false

    @SerializedName("message")
    val message: String = ""

    @SerializedName("is_account_verified")
    val isAccountVerified: Int = 0

    val image_url: String = ""

    @SerializedName("token")
    @Expose
    val token: String = "1"

    @SerializedName("g_pay")
    @Expose
    val gpayid: String = ""

    @SerializedName("pmid")
    @Expose
    val paytmMid: String = ""

    @SerializedName("call_url")
    @Expose
    val callbackurrl: String = ""

    //{"CHECKSUMHASH":"oH28p6S0SC\/Tsh\/y2R2vZWvuUVlyWynVrvAkiHU9Oahe\/gt+Jg3h0s2iPR+i6iufIGFamPRpO4UPvX+7YG0bEqu36afqYNNCgNXOeVXSXrM=","order_id":"947014","status":"1"}

    @SerializedName("CHECKSUMHASH")
    val checksum: String = ""

    @SerializedName("data")
    var infomodel: UserInfo? = null

    @SerializedName("response")
    @Expose
    var responseModels: Response? = null

    @SerializedName("rozar_key")
    @Expose
    var razorPay: String = ""

    inner class  Response {

        @SerializedName("matchcontests")
        @Expose
        var matchContestlist: List<ContestsParentModels>? = null

    }

}
