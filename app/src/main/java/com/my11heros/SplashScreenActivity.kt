package com.my11heros

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import androidx.databinding.DataBindingUtil
import io.branch.referral.Branch
import com.my11heros.databinding.ActivitySplashBinding
import com.my11heros.network.IApiMethod
import com.my11heros.network.RequestModel
import com.my11heros.network.WebServiceClient
import com.my11heros.ui.BaseActivity
import com.my11heros.ui.home.models.UsersPostDBResponse
import com.my11heros.ui.login.LoginScreenActivity
import com.my11heros.utils.MyPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SplashScreenActivity : BaseActivity() {

    private lateinit var mContext: SplashScreenActivity
    private var mBinding: ActivitySplashBinding? = null
    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAYED: Long = 2000

    //  val num = arrayOf(R.drawable.splash)
    internal val mRunnable: Runnable = Runnable {
        if (!isFinishing) {
            checkUserLoggedIn()
        }
    }

    private fun checkUserLoggedIn() {

        if (!MyPreferences.getLoginStatus(mContext)!!) {
            loginRequired()
        } else {
            val intent = Intent(
                applicationContext,
                MainActivity::class.java
            )
            startActivity(intent)
            finish()
        }
    }


    private fun loginRequired() {
        val intent = Intent(
            applicationContext,
            LoginScreenActivity::class.java
        )
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.CHECK_APK_UPDATE_API = false
        MainActivity.CHECK_WALLET_ONCE = false
        updateFireBase()
        mContext = this@SplashScreenActivity
        mBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_splash
        )
        updateCheckApk()
        val splashCreen = MyPreferences.getSplashScreen(this@SplashScreenActivity)

        if (!TextUtils.isEmpty(splashCreen)) {
//            Glide.with(this)
//                .load(splashCreen)
//                .placeholder(R.drawable.splash)
//                .into(mBinding!!.parentSplashBackground)
        }

        mDelayHandler = Handler()
        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAYED)

        val branch = Branch.getAutoInstance(mContext)
        //set retry count
        //set retry count
        branch.setRetryCount(5)

        branch.initSession({ referringParams, error ->
            if (error != null) {
                //Log.e("onCreate", error.getMessage());
            } else if (referringParams != null) {
                //Log.e("onCreate", referringParams.toString());
                if (referringParams.has("refer_code")) {
                    MyPreferences.setTempReferCode(
                        mContext,
                        referringParams.optString("refer_code")
                    )
                }
            }
        }, this.intent.data, this)
    }

    override fun onBitmapSelected(bitmap: Bitmap) {
        TODO("Not yet implemented")
    }

    override fun onUploadedImageUrl(url: String) {

    }

    private fun updateCheckApk() {
        val models = RequestModel()
        models.user_id = MyPreferences.getUserID(this)!!
        models.version_code = BuildConfig.VERSION_CODE

        WebServiceClient(this).client.create(IApiMethod::class.java).apkUpdate(models)
            .enqueue(object : Callback<UsersPostDBResponse?> {
                override fun onFailure(call: Call<UsersPostDBResponse?>?, t: Throwable?) {
                    MainActivity.CHECK_APK_UPDATE_API = false
                }

                override fun onResponse(
                    call: Call<UsersPostDBResponse?>?,
                    response: Response<UsersPostDBResponse?>?
                ) {

                    var res = response!!.body()
                    if (!isFinishing) {
                        if (res != null) {
                            if (res.status) {
                                MainActivity.CHECK_APK_UPDATE_API = true
                                MainActivity.CHECK_FORCE_UPDATE = res.forceupdate
                                MainActivity.updatedApkUrl = res.updatedApkUrl
                                MainActivity.releaseNote = res.releaseNote

                                MyPreferences.setSplashScreen(this@SplashScreenActivity, res.splash)
                            }

                        }
                    }
                }
            })
    }

    override fun onStart() {
        super.onStart()
        val intent = this.intent
        intent.putExtra("branch_force_new_session", true)
        Branch.getInstance()
            .initSession({ branchUniversalObject, linkProperties, error ->
                if (error != null) {
                    //Log.e("initSession", "branch init failed. Caused by -" + error.getMessage());
                } else {
                    //Log.e("initSession", "branch init complete!");
                    if (branchUniversalObject != null) {
                        //Log.e("initSession", "title " + branchUniversalObject.getTitle());
                        //Log.e("initSession", "CanonicalIdentifier " + branchUniversalObject.getCanonicalIdentifier());
                        //Log.e("initSession", "ContentMetaData metadata " + branchUniversalObject.getContentMetadata().convertToJson());
                    }
                    if (linkProperties != null) {
                        //Log.e("initSession", "Channel " + linkProperties.getChannel());
                        //Log.e("initSession", "control params " + linkProperties.getControlParams());
                    }
                }
            }, intent.data, this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        //this.setIntent(intent);
        Branch.getInstance()
            .reInitSession(
                this
            ) { referringParams, error ->
                if (error != null) {
                    //Log.e("onCreate", error.getMessage());
                } else if (referringParams != null) {
                    //Log.e("onCreate", referringParams.toString());
                    if (referringParams.has("refer_code")) {
                        MyPreferences.setTempReferCode(
                            mContext,
                            referringParams.optString("refer_code")
                        )
                    }
                }
            }
    }


}
