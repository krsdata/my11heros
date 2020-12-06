package com.my11heros.network

import com.my11heros.models.UserInfo
import com.my11heros.utils.HardwareInfo


class RequestEvent {


    var user_info: UserInfo?=null
    var event_name: String=""
    var match_id: Int=0
    var contest_id: Int=0
    var storage_permission: Int=0
    var device_id: String = ""
    var deviceDetails: HardwareInfo?=null

}
