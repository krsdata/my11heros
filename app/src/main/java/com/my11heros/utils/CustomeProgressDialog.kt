package com.my11heros.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.WanderingCubes
import kotlinx.android.synthetic.main.dialog_progress.*
import com.my11heros.R


class CustomeProgressDialog(context: Context?) : Dialog(context!!) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(R.layout.dialog_progress)
        setCancelable(false)
        setCanceledOnTouchOutside(false)

    }

}