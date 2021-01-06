package com.my11heros.ui.login

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.deliverdas.customers.utils.HardwareInfoManager
import com.my11heros.*
import com.my11heros.databinding.ActivityRegisterBinding
import com.my11heros.models.ResponseModel
import com.my11heros.network.IApiMethod
import com.my11heros.network.RequestModel
import com.my11heros.network.WebServiceClient
import com.my11heros.ui.BaseActivity
import com.my11heros.ui.login.viewmodel.LoginViewModel
import com.my11heros.utils.BindingUtils
import com.my11heros.utils.MyPreferences
import com.my11heros.utils.MyUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterScreenActivity : BaseActivity(), Callback<ResponseModel> {

    private var photoUrl: String = ""
    private var isActivityRequiredResult: Boolean? = false
    var name = ""
    var binding: ActivityRegisterBinding? = null
    var viewmodel: LoginViewModel? = null

    companion object {
        var ISACTIVITYRESULT = "activityresult"
        val REQUESTCODE_LOGIN: Int = 1005
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userInfo = (applicationContext as My11HerosApplication).userInformations
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        updateFireBase()

        binding!!.toolbar.title = getString(R.string.screen_name_register)
        binding!!.toolbar.setTitleTextColor(resources.getColor(R.color.white))
        binding!!.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        setSupportActionBar(binding!!.toolbar)

        binding!!.toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        if (intent.hasExtra(ISACTIVITYRESULT)) {
            isActivityRequiredResult =
                intent.getBooleanExtra(RegisterScreenActivity.ISACTIVITYRESULT, false)
        }

        val animation: Animation =
            AnimationUtils.loadAnimation(this, R.anim.grow_linear_animation)
        animation.duration = 500
        binding!!.userInfoBar.animation = animation
        binding!!.userInfoBar.animate()
        animation.start()

        bindUI()
        initClicks()

    }

    private fun bindUI() {
        //binding!!.inputRefferal.visibility = View.VISIBLE
        if (!TextUtils.isEmpty(userInfo!!.userEmail)) {
            binding!!.editEmail.setText(userInfo!!.userEmail)
            binding!!.editEmail.isEnabled = false
            binding!!.editEmail.isFocusable = false
        }
        if (!TextUtils.isEmpty(userInfo!!.mobileNumber)) {
            binding!!.editMobile.setText(userInfo!!.mobileNumber)
        }

        if (!TextUtils.isEmpty(userInfo!!.fullName)) {
            binding!!.editName.setText(userInfo!!.fullName)
        }

        if (MyPreferences.getTempReferCode(this) != null) {
            binding!!.editInvitecode.setText(
                java.lang.String.format(
                    "%s",
                    MyPreferences.getTempReferCode(this)
                )
            )
        }

    }

    private fun initClicks() {
        binding!!.txtTnc.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                val intent = Intent(this@RegisterScreenActivity, WebActivity::class.java)
                intent.putExtra(WebActivity.KEY_TITLE, BindingUtils.WEB_TITLE_PRIVACY_POLICY)
                intent.putExtra(WebActivity.KEY_URL, BindingUtils.WEBVIEW_PRIVACY)
                val options =
                    ActivityOptions.makeSceneTransitionAnimation(this@RegisterScreenActivity)
                startActivity(intent, options.toBundle())
            }

        })
        binding!!.registerButton.setOnClickListener(View.OnClickListener {

            var referralCode = binding!!.editInvitecode.text.toString()
            val teamName = binding!!.editTeamName.text.toString()
            val editName = binding!!.editName.text.toString()
            val mobileNumber = binding!!.editMobile.text.toString()
            val emailAddress = binding!!.editEmail.text.toString()

            if (TextUtils.isEmpty(teamName)) {
                MyUtils.showToast(this@RegisterScreenActivity, "Enter Your Team Name(Nick Name)")
                return@OnClickListener
            } else if (TextUtils.isEmpty(editName)) {
                MyUtils.showToast(this@RegisterScreenActivity, "Please enter your real name")
                return@OnClickListener
            } else if (TextUtils.isEmpty(mobileNumber)) {
                MyUtils.showToast(this@RegisterScreenActivity, "Please enter valid mobile number")
                return@OnClickListener
            } else if (mobileNumber.length < 10) {
                MyUtils.showToast(this@RegisterScreenActivity, "Please enter valid mobile number")
                return@OnClickListener
            } else if (TextUtils.isEmpty(emailAddress) || !MyUtils.isEmailValid(emailAddress)) {
                MyUtils.showToast(this@RegisterScreenActivity, "Please enter valid email address")
                return@OnClickListener
            }
            name = editName
            register(mobileNumber, emailAddress)
        })
    }

    override fun onBitmapSelected(bitmap: Bitmap) {
    }

    override fun onUploadedImageUrl(url: String) {
        this.photoUrl = url
    }

    fun register(
        mobile: String?,
        email: String?
    ) {
        if (!MyUtils.isConnectedWithInternet(this@RegisterScreenActivity)) {
            MyUtils.showToast(this@RegisterScreenActivity, "No Internet connection found")
            return
        }
        customeProgressDialog.show()
        val request = RequestModel()

        request.user_id = userInfo!!.userId.toString()
        request.name = name
        request.image_url = photoUrl
        request.email = email!!
        request.mobile_number = mobile!!
        if (intent.hasExtra(OtpVerifyActivity.EXTRA_KEY_PROVIDER_ID)) {
            request.provider_id = intent.getStringExtra(OtpVerifyActivity.EXTRA_KEY_PROVIDER_ID)
        }
        request.referral_code = binding!!.editInvitecode.text.toString()
        request.team_name = binding!!.editTeamName.text.toString()
        request.device_id = notificationToken
        print(notificationToken)
        request.deviceDetails = HardwareInfoManager(this).collectData(notificationToken)
        WebServiceClient(this).client.create(IApiMethod::class.java).customerLogin(request)
            .enqueue(this)
    }

    override fun onResponse(call: Call<ResponseModel>?, response: Response<ResponseModel>?) {
        if (!isFinishing) {
            customeProgressDialog.dismiss()

            val responseb = response!!.body()
            if (responseb != null) {
                if (responseb.status) {
                    val infoModels = responseb.infomodel
                    if (infoModels != null) {
                        if (TextUtils.isEmpty(responseb.infomodel!!.profileImage)) {
                            //MyPreferences.setProfilePicture(this, photoUrl)
                            responseb.infomodel!!.profileImage = photoUrl
                        }
                        MyPreferences.setOtpAuthRequired(this, responseb.isOTPRequired)
                        MyPreferences.setToken(this, responseb.token)
                        MyPreferences.setUserID(this, "" + responseb.infomodel!!.userId)
                        MyPreferences.setPaytmMid(this, responseb.paytmMid)
                        MyPreferences.setPaytmCallback(this, responseb.callbackurrl)
                        MyPreferences.setGooglePayId(this, responseb.gpayid)
                        MyPreferences.setRazorPayId(this, responseb.razorPay)

                        (applicationContext as My11HerosApplication).saveUserInformations(
                            responseb.infomodel
                        )
                        if (TextUtils.isEmpty(infoModels.mobileNumber) || TextUtils.isEmpty(
                                infoModels.userEmail
                            ) || TextUtils.isEmpty(
                                infoModels.fullName
                            )
                        ) {
                            val intent =
                                Intent(
                                    this@RegisterScreenActivity,
                                    RegisterScreenActivity::class.java
                                )
                            startActivity(intent)
                        } else
                            if (infoModels.isOtpVerified) {
                                MyPreferences.setLoginStatus(this@RegisterScreenActivity, true)
                                val intent =
                                    Intent(this@RegisterScreenActivity, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                setResult(Activity.RESULT_OK)
                                startActivity(intent)
                                finish()
                            } else {
                                val intent = Intent(this, OtpVerifyActivity::class.java)
                                startActivityForResult(
                                    intent,
                                    RegisterScreenActivity.REQUESTCODE_LOGIN
                                )
                            }
                    } else {
                        MyUtils.showToast(this@RegisterScreenActivity, responseb.message)
                    }
                } else {
                    MyUtils.showToast(this@RegisterScreenActivity, responseb.message)
                }
            } else {
                MyUtils.showToast(
                    this@RegisterScreenActivity,
                    getString(R.string.warning_somethingwentwront)
                )
            }
        }
    }

    override fun onFailure(call: Call<ResponseModel>?, t: Throwable?) {
        customeProgressDialog.dismiss()
        Toast.makeText(
            this, "Warning , ${t?.message}", Toast.LENGTH_LONG
        ).show()


    }

}
