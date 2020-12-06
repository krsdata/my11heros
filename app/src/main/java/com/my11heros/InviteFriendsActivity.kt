package com.my11heros

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.Branch
import io.branch.referral.Branch.BranchLinkCreateListener
import io.branch.referral.BranchError
import io.branch.referral.util.LinkProperties
import io.branch.referral.util.ShareSheetStyle
import com.my11heros.databinding.InviteFriendsBinding
import com.my11heros.models.UserInfo
import com.my11heros.utils.BindingUtils
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*

class InviteFriendsActivity : AppCompatActivity() {

    var userInfo: UserInfo? = null

    private var mBinding: InviteFriendsBinding? = null
    var url: String? = null
    var lp: LinkProperties? = null
    var buo: BranchUniversalObject? = null
    var shareSheetStyle: ShareSheetStyle? = null
    var mContext: Context? = null

    var TAG: String = InviteFriendsActivity::class.java.simpleName


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.invite_friends)
        mBinding = DataBindingUtil.setContentView(
            this,
            R.layout.invite_friends
        )

        mContext = this

        mBinding!!.onRefernEarn = OnClickListners()

        mBinding!!.toolbar.title = "Refer & Earn"
        mBinding!!.toolbar.setTitleTextColor(resources.getColor(R.color.white))
        mBinding!!.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        setSupportActionBar(mBinding!!.toolbar)
        mBinding!!.toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })

        userInfo = (application as SportsFightApplication).userInformations
        //findViewById<TextView>(R.id.invitecode).setText("Your Referral Code is "+userInfo.referalCode)

        mBinding!!.rereralCode.text = "" + userInfo!!.referalCode
        mBinding!!.moreOptions.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val msgText: String = ("" +
                        getString(R.string.label_register_on_sf) +
                        "*" + userInfo!!.referalCode + "*" +
                        " and get Rs 100 Bonus on Joining.\n" +
                        " Click on " +
                        BindingUtils.BILTY_APK_LINK)
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_TEXT, msgText)
                shareIntent.type = "text/plain"


                startActivity(Intent.createChooser(shareIntent, "Reffer and Earn Rs 100"))
            }
        })

        buo = BranchUniversalObject()
            .setTitle("Ninja11")
            .setContentDescription("Cricket Fantasy App")
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)

        lp = LinkProperties()
            .setFeature("sharing")
            .setCampaign("launch")
            .setStage("new user")
            .addControlParameter(
                "refer_code",
                userInfo!!.referalCode
            )
            .addControlParameter(
                "custom_random",
                java.lang.Long.toString(Calendar.getInstance().timeInMillis)
            )

        buo!!.generateShortUrl(mContext!!, lp!!,
            BranchLinkCreateListener { url, error ->
                if (error == null) {
                    //Log.e(TAG, "got my Branch link to share  =====> " + url);
                    this@InviteFriendsActivity.url = url
                }
            })

        shareSheetStyle = ShareSheetStyle(
            mContext!!, "Ninja11",
            "Welcome to Ninja11. Register on Ninja11 application with this link.\n\nUse my referral code \"" + userInfo!!.referalCode +
                    "\" and get extra Rs. 100 Bonus on Joining.".trimIndent()
        ) //.setCopyUrlStyle(getResources().getDrawable(android.R.drawable.ic_menu_send), "Copy", "Added to clipboard")
            //.setMoreOptionStyle(getResources().getDrawable(android.R.drawable.ic_menu_search), "Show more")
            .setAsFullWidthStyle(true)
            .setDefaultURL(url)
            .setSharingTitle("Refer and Earn Rs 100")


        mBinding!!.inviteFriends.setOnClickListener {
            //shareReferCode()
        }
    }

    fun setAnimation() {
        val slide = Slide()
        slide.slideEdge = Gravity.START
        slide.duration = 400
        slide.interpolator = DecelerateInterpolator()
        window.exitTransition = slide
        window.enterTransition = slide
    }

    inner class OnClickListners {
        fun onInvite(view: View?): Unit {
            val msgText: String = ("" +
                    getString(R.string.label_register_on_sf) +
                    "*" + userInfo!!.referalCode + "*" +
                    " and get Rs 100 Bonus on Joining.\n" +
                    " Click on " +
                    BindingUtils.BILTY_APK_LINK)

            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.type = "text/plain"
            sendIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                "Referral Affiliate programs"
            )
            sendIntent.putExtra(Intent.EXTRA_TEXT, msgText)
            try {
                startActivity(sendIntent)
            } catch (ex: ActivityNotFoundException) {
            }
        }

        fun onWhatsApp(view: View?): Unit {
            val pm: PackageManager = packageManager
            try {
                val msgText: String = ("" +
                        getString(R.string.label_register_on_sf) +
                        "*" + userInfo!!.referalCode + "*" +
                        " and get Rs 100 Bonus on Joining.\n" +
                        " Click on " +
                        BindingUtils.BILTY_APK_LINK)
                val waIntent = Intent(Intent.ACTION_SEND)
                waIntent.type = "text/plain"
                val info: PackageInfo =
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA)
                //Check if package exists or not. If not then code
                //in catch block will be called
                waIntent.setPackage("com.whatsapp")
                waIntent.putExtra(Intent.EXTRA_TEXT, msgText)
                startActivity(
                    Intent
                        .createChooser(
                            waIntent,
                            "Please join us with " + userInfo!!.referalCode
                        )
                )
            } catch (e: PackageManager.NameNotFoundException) {
            }
        }

        fun onFacebook(view: View?): Unit {
            val msgText: String = ("" +
                    getString(R.string.label_register_on_sf) +
                    "*" + userInfo!!.referalCode + "*" +
                    " and get Rs 100 Bonus on Joining.\n" +
                    " Click on " +
                    BindingUtils.BILTY_APK_LINK)
            var facebookAppFound: Boolean = false
            var shareIntent: Intent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, msgText)
            shareIntent.putExtra(
                Intent.EXTRA_STREAM,
                Uri.parse(BindingUtils.BILTY_APK_LINK)
            )
            val pm: PackageManager = packageManager
            val activityList: List<ResolveInfo> =
                pm.queryIntentActivities(shareIntent, 0)
            for (app: ResolveInfo in activityList) {
                if ((app.activityInfo.packageName).contains("com.facebook.katana")) {
                    val activityInfo: ActivityInfo = app.activityInfo
                    val name: ComponentName =
                        ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name)
                    shareIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                    shareIntent.component = name
                    facebookAppFound = true
                    break
                }
            }
            if (!facebookAppFound) {
                val sharerUrl: String =
                    "https://www.facebook.com/sharer/sharer.php?u=" + BindingUtils.BILTY_APK_LINK

                shareIntent = Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl))
            }
            startActivity(shareIntent)
        }

        fun onTwitter(view: View?): Unit {
            val msgText: String = ("" +
                    getString(R.string.label_register_on_sf) +
                    "*" + userInfo!!.referalCode + "*" +
                    " and get Rs 100 Bonus on Joining.\n" +
                    " Click on " +
                    BindingUtils.BILTY_APK_LINK)
            val tweetUrl: StringBuilder =
                StringBuilder("https://twitter.com/intent/tweet?text=")
            tweetUrl.append(TextUtils.isEmpty(msgText))

            if (!TextUtils.isEmpty(msgText)) {
                tweetUrl.append("&hastags=")
                tweetUrl.append(
                    urlEncode(
                        msgText
                    )
                )
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl.toString()))
            val matches: List<ResolveInfo> =
                packageManager.queryIntentActivities(intent, 0)
            for (info: ResolveInfo in matches) {
                if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                    intent.setPackage(info.activityInfo.packageName)
                }
            }
            startActivity(intent)
        }

        fun urlEncode(s: String): String? {
            return try {
                URLEncoder.encode(s, "UTF-8")
            } catch (e: UnsupportedEncodingException) {
                Log.wtf("wtf", "UTF-8 should always be supported", e)
                throw RuntimeException("URLEncoder.encode() failed for $s")
            }
        }
    }

    private fun shareReferCode() {
        buo!!.showShareSheet(this, lp!!, shareSheetStyle!!, object : Branch.BranchLinkShareListener {
            override fun onShareLinkDialogLaunched() {}
            override fun onShareLinkDialogDismissed() {}
            override fun onLinkShareResponse(
                sharedLink: String,
                sharedChannel: String,
                error: BranchError
            ) {
                Log.e(TAG, "error ====>  " + error.message!!);
                Log.e(TAG, "error Code ====>  " + error.errorCode);
            }

            override fun onChannelSelected(channelName: String) {}
        })
    }

}