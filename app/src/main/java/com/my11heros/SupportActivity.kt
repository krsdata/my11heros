package com.my11heros

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.my11heros.databinding.ActivitySupportsBinding
import com.my11heros.models.UserInfo
import com.my11heros.utils.BindingUtils

class SupportActivity : AppCompatActivity() {

    var userInfo: UserInfo? = null
    private var mBinding: ActivitySupportsBinding? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_supports
        )

        mBinding!!.toolbar.title = "Support"
        mBinding!!.toolbar.setTitleTextColor(resources.getColor(R.color.white))
        mBinding!!.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        setSupportActionBar(mBinding!!.toolbar)
        mBinding!!.toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })

        mBinding!!.email.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", BindingUtils.EMAIL, null
                )
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_support_Team))
            emailIntent.putExtra(Intent.EXTRA_TEXT, "")
            startActivity(Intent.createChooser(emailIntent, "Send Email"))
        }

        mBinding!!.telegramId.setOnClickListener {
            raiseIssuesOnTelegram()
        }
    }

    fun raiseIssuesOnWhatsApp() {
        try {
            val text = ""
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data =
                Uri.parse("http://api.whatsapp.com/send?phone=${BindingUtils.PHONE_NUMBER}&text=$text")
            startActivity(intent)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun raiseIssuesOnTelegram() {
        try {
            /*val telegramIntent = Intent(Intent.ACTION_SEND)
            tIntent.setDataAndType(Uri.parse("http://telegram.me/username"), "text/plain")
            val appName = "org.telegram.messenger"
            tIntent.setPackage(appName)
            tIntent.putExtra(Intent.EXTRA_TEXT, "hello")
            startActivity(tIntent)*/
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data =
                Uri.parse("https://telegram.me/${BindingUtils.TELEGRAM_USER_NAME}")
            startActivity(intent)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}