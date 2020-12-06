package com.my11heros.models

import com.my11heros.R
import java.io.Serializable


class MoreOptionsModel :Serializable,Cloneable {

    var id: Int = 0
    var drawable: Int = R.drawable.logo_google
    var title: String = ""

}
