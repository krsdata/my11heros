package com.edify.atrist.listener

interface OnPageRefreshedListener {
    fun onPageCreated(pageName:String)
    fun onRefreshed(pageName:String)
}