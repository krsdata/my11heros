package com.edify.atrist.listener

import com.my11heros.ui.createteam.models.PlayersInfoModel

interface OnRolesSelected {
    fun onTrumpSelected(objects: PlayersInfoModel,position: Int)
    fun onCaptainSelected(objects: PlayersInfoModel,position: Int)
    fun onViceCaptainSelected(objects: PlayersInfoModel,position: Int)
    fun onReady()

}