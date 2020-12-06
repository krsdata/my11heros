package com.my11heros.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class WalletInfo : Serializable, Cloneable {


    @SerializedName("wallet_amount")
    @Expose
    var walletAmount: Double = 0.0

    @SerializedName("bonus_amount")
    @Expose
    var bonusAmount: Double = 0.0

    @SerializedName("deposit_amount")
    @Expose
    var depositAmount: Double = 0.0

    @SerializedName("prize_amount")
    @Expose
    var prizeAmount: Double = 0.0

    @SerializedName("referral_amount")
    @Expose
    var referralAmount: Double = 0.0

    @SerializedName("is_account_verified")
    @Expose
    var accountStatus: AccountDocumentStatus? = null

    @SerializedName("refferal_friends_count")
    @Expose
    var refferalCounts: Int = 0


    @SerializedName("bank_account_verified")
    @Expose
    var bankAccountVerified: Int = 0

    @SerializedName("min_deposit")
    @Expose
    var minDeposit: Int = 0

    @SerializedName("pmid")
    @Expose
    var paytmMid: String = ""

    @SerializedName("g_pay")
    @Expose
    var gPay: String = ""

    @SerializedName("call_url")
    @Expose
    var callUrl: String = ""

    @SerializedName("transaction")
    @Expose
    var transactionList: ArrayList<TransactionModel>? = null

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

    public override fun clone(): WalletInfo {
        return super.clone() as WalletInfo
    }
}