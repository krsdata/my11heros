package com.my11heros.models

import java.io.Serializable


class RefferalUsersModel :Serializable,Cloneable {

    var id: String = ""
    var name: String = ""
    var created_at: String = ""
    var referral_amount: String = ""

    public override fun clone(): RefferalUsersModel {
        return super.clone() as RefferalUsersModel
    }


}
