package com.my11heros.ui.login

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.deliverdas.customers.utils.HardwareInfoManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.my11heros.*
import com.my11heros.databinding.ActivityLoginBinding
import com.my11heros.models.ResponseModel
import com.my11heros.models.UserInfo
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


class LoginScreenActivity : BaseActivity(), Callback<ResponseModel> {

    private var firebaseProvider: String = ""
    var photoUrl: String = ""
    private var isActivityRequiredResult: Boolean? = false
    val RC_SIGN_IN: Int = 1
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions
    private lateinit var firebaseAuth: FirebaseAuth
    var name = ""
    var emailid = ""
    var binding: ActivityLoginBinding? = null
    var viewmodel: LoginViewModel? = null

    companion object {
        var AUTH_TYPE_GMAIL = "googleAuth"
        var TAG: String = LoginScreenActivity::class.java.simpleName
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        updateFireBase()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        firebaseAuth = FirebaseAuth.getInstance()

        if (intent.hasExtra(RegisterScreenActivity.ISACTIVITYRESULT)) {
            isActivityRequiredResult =
                intent.getBooleanExtra(RegisterScreenActivity.ISACTIVITYRESULT, false)
        }

        val animation: Animation =
            AnimationUtils.loadAnimation(this, R.anim.grow_linear_animation)
        animation.duration = 500
        binding!!.loginPanel.animation = animation
        binding!!.loginPanel.animate()
        animation.start()

        configureGoogleSignIn()
        processStep1()
        initClicks()

    }

    private fun initClicks() {
        binding!!.signInButton.getChildAt(0)?.let {
            val smaller = Math.min(it.paddingLeft, it.paddingRight)
            it.setPadding(smaller, it.paddingTop, smaller, it.paddingBottom)
        }
        binding!!.signInButton.setOnClickListener(View.OnClickListener {
            signIn()
        })

        binding!!.termsCondition.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@LoginScreenActivity, WebActivity::class.java)
            intent.putExtra(WebActivity.KEY_TITLE, BindingUtils.WEB_TITLE_TERMS_CONDITION)
            intent.putExtra(WebActivity.KEY_URL, BindingUtils.WEBVIEW_TNC)
            startActivity(intent)

        })

        binding!!.privacyPolicy.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@LoginScreenActivity, WebActivity::class.java)
            intent.putExtra(WebActivity.KEY_TITLE, BindingUtils.WEB_TITLE_PRIVACY_POLICY)
            intent.putExtra(WebActivity.KEY_URL, BindingUtils.WEBVIEW_PRIVACY)
            startActivity(intent)

        })

    }

    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //callbackManager!!.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onBitmapSelected(bitmap: Bitmap) {


    }

    override fun onUploadedImageUrl(url: String) {
        photoUrl = url
    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
//        if (user != null) {
//            name = user!!.displayName!!
//            emailid = user!!.email!!
//            login(user!!.email)
//
//        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        customeProgressDialog.show()

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            customeProgressDialog.dismiss()
            if (it.isSuccessful) {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    firebaseProvider = credential.provider
                    name = user.displayName!!
                    emailid = user.email!!
                    photoUrl = firebaseAuth.currentUser!!.photoUrl.toString()
                    login(emailid, AUTH_TYPE_GMAIL)
                    mGoogleSignInClient.signOut()
                }
            } else {
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun login(email: String?, authType: String) {
        if (!MyUtils.isConnectedWithInternet(this@LoginScreenActivity)) {
            MyUtils.showToast(this@LoginScreenActivity, "No Internet connection found")
            return
        }

        Log.e(TAG, "notificationToken ========> "+ notificationToken)

        customeProgressDialog.show()
        val request = RequestModel()
        request.name = name
        request.email = email!!
        request.device_id = notificationToken
        request.user_type = authType
        request.provider_id = firebaseAuth.uid!!
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
                        (applicationContext as My11HerosApplication).saveUserInformations(
                            responseb.infomodel
                        )
                        if (TextUtils.isEmpty(infoModels.mobileNumber) || TextUtils.isEmpty(
                                infoModels.userEmail
                            ) || TextUtils.isEmpty(
                                infoModels.fullName
                            )
                        ) {
                            registerUsers(firebaseAuth.uid)
                        } else
                            if (infoModels.isOtpVerified) {
                                MyPreferences.setLoginStatus(this@LoginScreenActivity, true)
                                val intent =
                                    Intent(this@LoginScreenActivity, MainActivity::class.java)
                                setResult(Activity.RESULT_OK)
                                startActivity(intent)
                                finish()
                            } else {
                                sendOTP(firebaseAuth.uid)
                            }
                    } else {
                        MyUtils.showToast(
                            this@LoginScreenActivity,
                            "Something went wrong, please contact admin"
                        )
                    }
                } else {
                    if (responseb.statusCode == BindingUtils.REUEST_STATUS_CODE_FRAUD) {
                        showDeadLineAlert(responseb.message)
                    } else {
                        val infomodel = UserInfo()
                        infomodel.userEmail = emailid
                        (applicationContext as My11HerosApplication).saveUserInformations(
                            responseb.infomodel
                        )
                        registerUsers(firebaseAuth.uid)
                    }
                }

            } else {
                MyUtils.showToast(this@LoginScreenActivity, "Invalid Email or Password")
            }
        }

    }

    private fun registerUsers(uid: String?) {
        val intent = Intent(this@LoginScreenActivity, RegisterScreenActivity::class.java)
        intent.putExtra(OtpVerifyActivity.EXTRA_KEY_PROVIDER_ID, firebaseAuth.uid)
        startActivity(intent)
    }

    private fun sendOTP(uid: String?) {
        val intent = Intent(this, OtpVerifyActivity::class.java)
        intent.putExtra(OtpVerifyActivity.EXTRA_KEY_EDIT_MOBILE_NUMBER, true)
        intent.putExtra(OtpVerifyActivity.EXTRA_KEY_PROVIDER_ID, uid)
        startActivityForResult(intent, RegisterScreenActivity.REQUESTCODE_LOGIN)
    }


    private fun processStep1() {

        binding!!.txtTnc.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(this@LoginScreenActivity, WebActivity::class.java)
                intent.putExtra(WebActivity.KEY_TITLE, BindingUtils.WEB_TITLE_TERMS_CONDITION)
                intent.putExtra(WebActivity.KEY_URL, BindingUtils.WEBVIEW_TNC)
                if (Build.VERSION.SDK_INT > 20) {
                    val options =
                        ActivityOptions.makeSceneTransitionAnimation(this@LoginScreenActivity)
                    startActivity(intent, options.toBundle())
                } else {
                    startActivity(intent)
                }
            }

        })

        binding!!.txtPrivacy.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(this@LoginScreenActivity, WebActivity::class.java)
                intent.putExtra(WebActivity.KEY_TITLE, BindingUtils.WEB_TITLE_PRIVACY_POLICY)
                intent.putExtra(WebActivity.KEY_URL, BindingUtils.WEBVIEW_PRIVACY)
                if (Build.VERSION.SDK_INT > 20) {
                    val options =
                        ActivityOptions.makeSceneTransitionAnimation(this@LoginScreenActivity)
                    startActivity(intent, options.toBundle())
                } else {
                    startActivity(intent)
                }
            }

        })
    }

//    private fun processStep2(infomodel: UserInfo?) {
//        binding!!.linearStep1.visibility = View.GONE
//        binding!!.linearStep2.visibility = View.VISIBLE
//
//        binding!!.editName.setText(infomodel!!.fullName)
//        binding!!.afterLoginEmail.setText(infomodel!!.userEmail)
//        binding!!.editMobile.setText(infomodel!!.mobileNumber)
//
//
//        binding!!.registerButton.setOnClickListener(object: View.OnClickListener{
//            override fun onClick(v: View?) {
//
//                var emailAddress = infomodel.userEmail
//                var mobileNumber = binding!!.editMobile.text.toString()
//                var fullName = binding!!.editName.text.toString()
//
//                if(TextUtils.isEmpty(fullName)){
//                    Toast.makeText(this@LoginScreenActivity
//                        , "Warning , Please enter your Full Name", Toast.LENGTH_LONG).show()
//                    return
//                }
//                if(TextUtils.isEmpty(mobileNumber)){
//                    Toast.makeText(this@LoginScreenActivity
//                        , "Warning , Please enter your Mobile Number", Toast.LENGTH_LONG).show()
//                    return
//                }
//
//                if(TextUtils.isEmpty(emailAddress)){
//                    Toast.makeText(this@LoginScreenActivity
//                        , "Warning , Email Address Required", Toast.LENGTH_LONG).show()
//                    return
//                }
//
//                if(!MyUtils.isConnectedWithInternet(this@LoginScreenActivity)) {
//                    MyUtils.showToast(this@LoginScreenActivity,"No Internet connection found")
//                    return
//                }
//                customeProgressDialog!!.show()
//                var request = RequestModel()
//                request.user_id =infomodel!!.userId.toString()
//                request.name =binding!!.beforeEditName.text.toString()
//                request.image_url =photoUrl
//                request.referral_code =binding!!.editInvitecode.text.toString()
//                request.email = infomodel!!.userEmail
//                request.mobile_number = mobileNumber
//                request.device_id = notificationToken
//
//                infomodel!!.mobileNumber =mobileNumber
//                infomodel!!.fullName =name
//                infomodel!!.referalCode =binding!!.editInvitecode.text.toString()
//
//
//
//                request.deviceDetails = HardwareInfoManager(this@LoginScreenActivity).collectData()
//                WebServiceClient(this@LoginScreenActivity).client.create(BackEndApi::class.java).updateAfterLogin(request)
//                    .enqueue(object : Callback<ResponseModel?>{
//                        override fun onFailure(call: Call<ResponseModel?>?, t: Throwable?) {
//
//                        }
//
//                        override fun onResponse(
//                            call: Call<ResponseModel?>?,
//                            response: Response<ResponseModel?>?
//                        ) {
//
//                            if(!isFinishing) {
//                                customeProgressDialog!!.dismiss()
//
//                                var responseb = response!!.body()
//                                if (responseb != null && responseb.status) {
//                                    (applicationContext as PlugSportsApplication).saveUserInformations(infomodel)
//                                    MyPreferences.setLoginStatus(this@LoginScreenActivity, true)
//                                    MyPreferences.setUserID(this@LoginScreenActivity, ""+infomodel.userId)
//                                    var intent = Intent(this@LoginScreenActivity, MainActivity::class.java)
//                                    if(isActivityRequiredResult!!){
//                                        setResult(Activity.RESULT_OK)
//                                    }else {
//                                        startActivity(intent)
//                                    }
//                                    finish()
//                                }else {
//                                    MyUtils.showToast(
//                                        this@LoginScreenActivity,
//                                        responseb!!.message
//                                    )
//                                }
//
//
//                            }
//
//                        }
//
//                    })
//            }
//
//
//        })


    //   }


    override fun onFailure(call: Call<ResponseModel>?, t: Throwable?) {
//        customeProgressDialog!!.dismiss()
//        Toast.makeText(this
//            , "Warning , ${t?.message}", Toast.LENGTH_LONG).show()


    }


//    fun facebookInit(){
//        FacebookSdk.sdkInitialize(this);
////        AppEventsLogger.activateApp(this);
//        callbackManager = CallbackManager.Factory.create()
//        //buttonFacebookLogin.setReadPermissions("email", "public_profile")
//        LoginManager.getInstance().registerCallback(callbackManager,
//            object : FacebookCallback<LoginResult?> {
//                override fun onSuccess(loginResult: LoginResult?) {
//                    Log.d("Success", "Login")
//                    val accessToken: AccessToken = AccessToken.getCurrentAccessToken()
//                    val isLoggedIn = accessToken != null && !accessToken.isExpired()
//                    //Toast.makeText(this@LoginScreenActivity, "Login Success "+accessToken+" isLogin"+isLoggedIn, Toast.LENGTH_LONG).show()
//
//                    var photoUrl = ""
//                    val request = GraphRequest.newMeRequest(
//                        accessToken,
//                        object : GraphRequest.GraphJSONObjectCallback {
//                            override fun onCompleted(
//                                jsonObjects: JSONObject?,
//                                response: GraphResponse?
//                            ){
//                                Log.d("fbres",jsonObjects.toString())
//                                emailid = jsonObjects!!.getString("email")
//                                name = jsonObjects!!.getString("name")
//                                MyPreferences.setProfilePicture(this@LoginScreenActivity,photoUrl);
//                                login(emailid,"", AUTH_TYPE_FACEBOOK)
//                            }
//                        })
//                    val parameters = Bundle()
//                    parameters.putString("fields", "id,name,link")
//                    request.parameters = parameters
//                    request.executeAsync()
//
//
//
//
//
//
//                }
//
//                override fun onCancel() {
//                    Toast.makeText(this@LoginScreenActivity, "Login Cancel", Toast.LENGTH_LONG).show()
//                }
//
//                override fun onError(exception: FacebookException) {
//                    Toast.makeText(this@LoginScreenActivity, exception.message, Toast.LENGTH_LONG).show()
//                }
//            })
//    }


}
