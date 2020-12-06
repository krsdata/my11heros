package com.my11heros

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.TextUtils
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.andrognito.flashbar.Flashbar
import com.deliverdas.customers.utils.HardwareInfoManager
import com.google.gson.JsonObject
import com.my11heros.databinding.ActivityWithdrawAmountBinding
import com.my11heros.models.WalletInfo
import com.my11heros.network.IApiMethod
import com.my11heros.network.RequestModel
import com.my11heros.network.WebServiceClient
import com.my11heros.ui.BaseActivity
import com.my11heros.ui.home.models.UsersPostDBResponse
import com.my11heros.utils.CustomeProgressDialog
import com.my11heros.utils.MyPreferences
import com.my11heros.utils.MyUtils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WithdrawAmountsActivity : BaseActivity() {

    private var walletInfo: WalletInfo? = null
    private var mBinding: ActivityWithdrawAmountBinding? = null
    private var pageType: String = ""
    private var mContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customeProgressDialog = CustomeProgressDialog(this)
        userInfo = (application as SportsFightApplication).userInformations
        walletInfo = (application as SportsFightApplication).walletInfo
        mBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_withdraw_amount
        )

        mContext = this

        mBinding!!.toolbar.title = "Withdraw Money"
        mBinding!!.toolbar.setTitleTextColor(resources.getColor(R.color.white))
        mBinding!!.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        setSupportActionBar(mBinding!!.toolbar)
        mBinding!!.toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })

        mBinding!!.winningAmount.text = String.format("₹ %s", walletInfo!!.prizeAmount)

        //mBinding!!.viewAmount.visibility = View.GONE

        mBinding!!.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val rb = group.findViewById<RadioButton>(checkedId)
            ////Log.e(TAG, "checkedId =====> " + checkedId);
            if (rb != null) {
                if (rb.text.toString().equals("paytm", true)) {
                    pageType = "paytm"
                } else {
                    pageType = "bank account"
                }
                //mBinding!!.viewAmount.visibility = View.VISIBLE
                ////Log.e(TAG, "pageType ======> " + pageType);
            }
        }

        mBinding!!.submitBtnWithdrawal.setOnClickListener(View.OnClickListener {
            var value = mBinding!!.editWithdrawalAmount.text.toString().trim()
            if (TextUtils.isEmpty(value)) {
                value = "0"
            }
            val amount = value.toInt()
            if (!pageType.equals("")) {
                if (amount >= 200) {
                    showWithdrawalAlert(amount, pageType)
                } else {
                    MyUtils.showMessage(
                        this@WithdrawAmountsActivity,
                        "You cannot withdraw amount less than 200 INR"
                    )
                }
            } else {
                MyUtils.showMessage(
                    this@WithdrawAmountsActivity,
                    "Select amount withdraw type"
                )
            }
        })

        mBinding!!.contactUs.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@WithdrawAmountsActivity, SupportActivity::class.java)
            startActivity(intent)
        })

        getMessage()
    }

    private fun showWithdrawalAlert(amount: Int, type: String) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Confirmation")
        //set message for alert dialog
        if (type.equals("")) {
            builder.setMessage(
                String.format(
                    "%d will be transferred to your verified bank accounts",
                    amount
                )
            )
        } else {
            builder.setMessage(
                String.format(
                    "%d will be transferred to your verified Paytm account",
                    amount
                )
            )
        }
        builder.setIcon(android.R.drawable.ic_btn_speak_now)

        //performing positive action
        builder.setPositiveButton("Proceed") { dialogInterface, which ->
            withdrawalRequest(amount, type)
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

    private fun withdrawalRequest(amount: Int, type: String) {
        if (!MyUtils.isConnectedWithInternet(this)) {
            MyUtils.showToast(this, "No Internet connection found")
            return
        }
        customeProgressDialog.show()
        val models = RequestModel()
        models.user_id = MyPreferences.getUserID(this)!!
        models.token = MyPreferences.getToken(this)!!
        models.withdraw_amount = amount
        models.payment_taken_in = type

        WebServiceClient(this).client.create(IApiMethod::class.java).withdrawAmount(models)
            .enqueue(object : Callback<UsersPostDBResponse?> {
                override fun onFailure(call: Call<UsersPostDBResponse?>?, t: Throwable?) {
                    customeProgressDialog.dismiss()
                }

                override fun onResponse(
                    call: Call<UsersPostDBResponse?>?,
                    response: Response<UsersPostDBResponse?>?
                ) {
                    customeProgressDialog.dismiss()
                    val res = response!!.body()
                    if (res != null && res.status) {
                        successAlert(res.message, true)
                    } else {
                        if (res != null) {
                            successAlert(res.message, false)
                        } else {
                            successAlert("Please try again! Something went wrong", false)
                        }
                    }
                }
            })
    }

    private fun successAlert(message: String, isClose: Boolean) {
        val flashbar = Flashbar.Builder(this@WithdrawAmountsActivity)
            .gravity(Flashbar.Gravity.TOP)
            .title(getString(R.string.app_name))
            .message(message)
            .backgroundDrawable(R.color.green)
            /*.showIcon()
            .icon(R.drawable.ic_photo_camera_black_24dp)
            .iconAnimation(
                FlashAnim.with(this@WithdrawAmountsActivity)
                    .animateIcon()
                    .pulse()
                    .alpha()
                    .duration(750)
                    .accelerate()
            )*/
            .build()
        flashbar.show()
        Handler().postDelayed(Runnable {
            if (isClose) {
                setResult(Activity.RESULT_OK)
                finish()
            }

        }, 2000L)
    }

    override fun onBitmapSelected(bitmap: Bitmap) {

    }

    override fun onUploadedImageUrl(url: String) {

    }

    private fun getMessage() {
        if (!MyUtils.isConnectedWithInternet(this)) {
            return
        }
        val models = RequestModel()
        models.user_id = MyPreferences.getUserID(mContext!!)!!
        models.token = MyPreferences.getToken(mContext!!)!!
        val deviceToken: String? = MyPreferences.getDeviceToken(mContext!!)
        models.deviceDetails = HardwareInfoManager(mContext).collectData(deviceToken!!)

        WebServiceClient(mContext!!).client.create(IApiMethod::class.java).getMessages(models)
            .enqueue(object : Callback<JsonObject?> {
                override fun onFailure(call: Call<JsonObject?>?, t: Throwable?) {
                    // customeProgressDialog.dismiss()
                }

                override fun onResponse(
                    call: Call<JsonObject?>?,
                    response: Response<JsonObject?>?
                ) {

                    val resObje = response!!.body().toString()
                    val jsonObject = JSONObject(resObje)
                    if (jsonObject.optBoolean("status")) {
                        val array = jsonObject.getJSONArray("data")
                        val data = array.getJSONObject(1)
                        if (data.optInt("message_status") == 0) {
                            mBinding!!.withdrawMessage.visibility = View.GONE
                        } else {
                            if (data.getString("message_type") == "HTML") {
                                //mBinding!!.withdrawMessage.linksClickable = true
                                //mBinding!!.withdrawMessage.movementMethod = LinkMovementMethod.getInstance()
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    mBinding!!.withdrawMessage.text =
                                        Html.fromHtml(
                                            data.getString("message"), Html.FROM_HTML_MODE_COMPACT
                                        )
                                } else {
                                    mBinding!!.withdrawMessage.text = Html.fromHtml(
                                        data.getString("message")
                                    )
                                }
                            } else {
                                mBinding!!.withdrawMessage.text = data.getString("message")
                            }
                            mBinding!!.withdrawMessage.visibility = View.VISIBLE
                        }

                        val withdrawData = array.getJSONObject(2)

                        if (withdrawData.optInt("message_status") == 0) {
                            mBinding!!.walletCard.visibility = View.GONE
                            mBinding!!.viewAmount.visibility = View.GONE
                            mBinding!!.showAlert.visibility = View.VISIBLE
                            mBinding!!.alertMessage.text = withdrawData.getString("message")
                        } else {
                            mBinding!!.walletCard.visibility = View.VISIBLE
                            mBinding!!.viewAmount.visibility = View.VISIBLE
                            mBinding!!.showAlert.visibility = View.GONE
                        }
                    }
                }
            })
    }
}