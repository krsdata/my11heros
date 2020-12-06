package com.my11heros.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class DocumentsDataModels:Serializable,Cloneable {

    @SerializedName("doc_type")
    @Expose
    var docType : String=""

    @SerializedName("bank_name")
    @Expose
    var bankName : String=""

    @SerializedName("account_name")
    @Expose
    var accountName : String=""

    @SerializedName("account_number")
    @Expose
    var accountNumber : String=""

    @SerializedName("bank_passbook_url")
    @Expose
    var bankPassbookUrl : String=""

    @SerializedName("doc_number")
    @Expose
    var docNumber : String=""

    @SerializedName("doc_url_front")
    @Expose
    var docUrlFront : String=""

    @SerializedName("doc_url_back")
    @Expose
    var docUrlBack : String=""

    @SerializedName("created_at")
    @Expose
    var createdAt : String=""



    public override fun clone(): DocumentsDataModels {
        return super.clone() as DocumentsDataModels
    }

}
