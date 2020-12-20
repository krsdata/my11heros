package com.my11heros

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.my11heros.databinding.InviteFriendsBinding
import com.my11heros.models.UserInfo
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.Branch.BranchLinkCreateListener
import io.branch.referral.util.LinkProperties
import io.branch.referral.util.ShareSheetStyle
import java.util.*

class InviteFriendsActivity : AppCompatActivity() {

    var userInfo: UserInfo? = null
    var mBinding: InviteFriendsBinding? = null
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

        mBinding!!.toolbar.title = "Refer & Earn"
        mBinding!!.toolbar.setTitleTextColor(resources.getColor(R.color.white))
        mBinding!!.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        setSupportActionBar(mBinding!!.toolbar)
        mBinding!!.toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })

        userInfo = (application as SportsFightApplication).userInformations

        mBinding!!.rereralCode.text = userInfo!!.referalCode
        buo = BranchUniversalObject()
            .setTitle("My11Heros")
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
            mContext!!, "My11Heros",
            "Welcome to My11Heros.\nRegister on My11Heros application with this link.\n\nUse my referral code \"" + userInfo!!.referalCode +
                    "\" and get extra Rs. 100 Bonus on Joining.\n".trimIndent()
        )
            .setAsFullWidthStyle(false)
            .setDefaultURL(url)
            .setSharingTitle("Refer and Earn Rs 100")


        mBinding!!.inviteFriends.setOnClickListener {
            shareReferCode()
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

    private fun shareReferCode() {

        val msg: String =
            "Welcome to My11Heros.\nRegister on My11Heros application with this link.\n\nUse my referral code \"" + userInfo!!.referalCode +
                    "\" and get extra Rs. 100 Bonus on Joining.\n\n $url".trimIndent()
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg)
        sendIntent.putExtra(Intent.EXTRA_TITLE, "My11Heros")
        sendIntent.type = "text/plain"
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)


        /*buo!!.showShareSheet(
            this,
            lp!!,
            shareSheetStyle!!,
            object : Branch.BranchLinkShareListener {
                override fun onShareLinkDialogLaunched() {}
                override fun onShareLinkDialogDismissed() {}
                override fun onLinkShareResponse(
                    sharedLink: String,
                    sharedChannel: String,
                    error: BranchError
                ) {
                    Log.e(TAG, "error ====>  " + error.message!!)
                    Log.e(TAG, "error Code ====>  " + error.errorCode)
                }

                override fun onChannelSelected(channelName: String) {}
            })*/
    }
}