package com.my11heros.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class AccountDocumentStatus : Serializable, Cloneable {


    @SerializedName("email_verified")
    @Expose
    val emailStatus: Int = 0

    @SerializedName("documents_verified")
    @Expose
    val documentsVerified: Int = 0

    @SerializedName("address_verified")
    @Expose
    val addressVerified: Int = 0

    @SerializedName("paytm_verified")
    @Expose
    val paytmVerified: Int = 0


    public override fun clone(): AccountDocumentStatus {
        return super.clone() as AccountDocumentStatus
    }


}
