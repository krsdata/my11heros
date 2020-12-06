package com.my11heros.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class DocumentsModels:Serializable,Cloneable {

    var status : Int=0
    var message : String=""

    @SerializedName("data")
    @Expose
    var documentDataModel: DocumentsDataModels ?=null

    public override fun clone(): DocumentsModels {
        return super.clone() as DocumentsModels
    }
}
