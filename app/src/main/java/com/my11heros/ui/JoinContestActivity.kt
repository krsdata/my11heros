package com.my11heros.ui

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.my11heros.*
import com.my11heros.databinding.FragmentJoinContestConfirmationBinding
import com.my11heros.models.MyTeamModels
import com.my11heros.models.UpcomingMatchesModel
import com.my11heros.models.UserInfo
import com.my11heros.network.IApiMethod
import com.my11heros.network.RequestModel
import com.my11heros.network.WebServiceClient
import com.my11heros.ui.contest.models.ContestModelLists
import com.my11heros.ui.home.models.UsersPostDBResponse
import com.my11heros.utils.BindingUtils
import com.my11heros.utils.CustomeProgressDialog
import com.my11heros.utils.MyPreferences
import com.my11heros.utils.MyUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JoinContestActivity : AppCompatActivity() {

    private lateinit var userInfo: UserInfo
    private lateinit var customeProgressDialog: CustomeProgressDialog
    var walletAmount: Double = 0.0
    var bonusAmount: Double = 0.0
    var createdTeamIdList: ArrayList<Int>? = null
    private var mBinding: FragmentJoinContestConfirmationBinding? = null
    private var mContext: Context? = null
    var myTeamArrayList: ArrayList<MyTeamModels> = ArrayList<MyTeamModels>()
    var matchObject: UpcomingMatchesModel? = null
    var contestModel: ContestModelLists? = null

    companion object {
        var DISCOUNT_ON_BONUS: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(
            this,
            R.layout.fragment_join_contest_confirmation
        )
        mContext = this

        matchObject = intent.getSerializableExtra(CreateTeamActivity.SERIALIZABLE_MATCH_KEY) as UpcomingMatchesModel?
        contestModel = intent.getSerializableExtra(CreateTeamActivity.SERIALIZABLE_CONTEST_KEY) as ContestModelLists?
        myTeamArrayList = intent.getSerializableExtra(CreateTeamActivity.SERIALIZABLE_SELECTED_TEAMS) as ArrayList<MyTeamModels>

        customeProgressDialog = CustomeProgressDialog(mContext)
        mBinding!!.imgClose.setOnClickListener(View.OnClickListener {
            setResult(RESULT_OK)
            finish()
        })
        initWalletInfo()
        getWalletBalances()
    }

    override fun onBackPressed() {
        setResult(RESULT_OK)
        finish()
    }

    private fun initWalletInfo() {
        val walletInfo = (applicationContext as My11HerosApplication).walletInfo
        userInfo = (applicationContext as My11HerosApplication).userInformations
        JoinContestDialogFragment.DISCOUNT_ON_BONUS = contestModel!!.usableBonus.toInt()
        walletAmount = walletInfo.walletAmount
        bonusAmount = walletInfo.bonusAmount
        createdTeamIdList = ArrayList<Int>()
        var totalEntryFees = 0.0
        var discountFromBonusAmount = 0.0
        var totalPayable = 0.0
        if (contestModel!!.isBonusContest) {
            mBinding!!.walletTotalAmount.text = String.format("Bonus Amount =₹%.2f", bonusAmount)
        } else {
            mBinding!!.walletTotalAmount.text =
                String.format("Amount Added + Bonus =₹%.2f", walletAmount + bonusAmount)
        }

        for (x in 0..myTeamArrayList.size - 1) {
            val objects = myTeamArrayList.get(x)
            if (objects.isSelected!!) {
                createdTeamIdList!!.add(objects.teamId!!.teamId)
                totalEntryFees += contestModel!!.entryFees.toInt()
            }
        }

        var actualPayable = 0.0
        if (contestModel!!.isBonusContest) {
            actualPayable = totalEntryFees
            mBinding!!.entryFees.text = "0"
            mBinding!!.usableCashbonus.text = String.format("₹%.2f", actualPayable)
        } else {
            discountFromBonusAmount =
                ((totalEntryFees * JoinContestDialogFragment.DISCOUNT_ON_BONUS)) / 100

            if (bonusAmount >= discountFromBonusAmount) {
                totalPayable = totalEntryFees - discountFromBonusAmount
            } else {
                discountFromBonusAmount = 0.0
                totalPayable = totalEntryFees - discountFromBonusAmount
            }
//            if (totalPayable <= walletAmount) {
//                actualPayable = 0.0
//            } else {
            actualPayable = totalPayable
            // }
            mBinding!!.entryFees.text = String.format("₹%.2f", totalEntryFees)
            mBinding!!.usableCashbonus.text = String.format("₹%.2f", discountFromBonusAmount)
        }


        //var finalAmount = walletAmount - totalPayable
        mBinding!!.usableTopay.text = String.format("₹%.2f", Math.abs(actualPayable))
        if (actualPayable > walletAmount) {
            mBinding!!.joinContest.text = "Pay Now"
            mBinding!!.joinContest.setBackgroundResource(R.drawable.default_flat_button_sportsfight)
        }
        mBinding!!.joinContest.setOnClickListener(View.OnClickListener {

            if (actualPayable > walletAmount && !contestModel!!.isBonusContest) {
                val intent = Intent(mContext, AddMoneyActivity::class.java)
                intent.putExtra(AddMoneyActivity.ADD_EXTRA_AMOUNT, Math.abs(actualPayable) + 10)
                startActivityForResult(intent, MyBalanceActivity.REQUEST_CODE_ADD_MONEY)
                finish()
            } else {
                placeOrders(totalEntryFees, actualPayable, discountFromBonusAmount)
            }
        })

        mBinding!!.termsCondition.setOnClickListener(View.OnClickListener {
            val intent = Intent(mContext, WebActivity::class.java)
            intent.putExtra(WebActivity.KEY_TITLE, BindingUtils.WEB_TITLE_TERMS_CONDITION)
            intent.putExtra(WebActivity.KEY_URL, BindingUtils.WEBVIEW_TNC)
            val options =
                ActivityOptions.makeSceneTransitionAnimation(this@JoinContestActivity)
            startActivity(intent, options.toBundle())
        })
    }

    private fun placeOrders(
        totalEntryFees: Double,
        totalPayable: Double,
        discountFromBonusAmount: Double
    ) {
        if (!MyUtils.isConnectedWithInternet(this@JoinContestActivity)) {
            MyUtils.showToast(this@JoinContestActivity, "No Internet connection found")
            return
        }
        customeProgressDialog.show()
        val models = RequestModel()
        models.user_id = MyPreferences.getUserID(mContext!!)!!
        //models.token = MyPreferences.getToken(activity!!)!!
        models.match_id = "" + matchObject!!.matchId
        models.contest_id = "" + contestModel!!.id
        models.created_team_id = createdTeamIdList
        models.token = MyPreferences.getToken(mContext!!)!!
        models.entryFees = totalEntryFees.toString()
        models.totalPaidAmount = totalPayable.toString()
        models.discountOnBonusAmount = discountFromBonusAmount.toString()

        WebServiceClient(mContext!!).client.create(IApiMethod::class.java)
            .joinContest(models)
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
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        MyUtils.showMessage(mContext!!, res!!.message)
                    }
                }
            })
    }

    private fun getWalletBalances() {
        //var userInfo = (activity as PlugSportsApplication).userInformations
        if (!MyUtils.isConnectedWithInternet(this@JoinContestActivity)) {
            MyUtils.showToast(this@JoinContestActivity, "No Internet connection found")
            return
        }
        customeProgressDialog.show()
        val models = RequestModel()
        models.user_id = MyPreferences.getUserID(mContext!!)!!
        models.token = MyPreferences.getToken(mContext!!)!!

        WebServiceClient(mContext!!).client.create(IApiMethod::class.java).getWallet(models)
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
                        if (res.sessionExpired) {
                            logoutApp("Session Expired Please login again!!", false)
                        } else {
                            val responseModel = res.walletObjects
                            if (responseModel != null) {
                                (applicationContext as My11HerosApplication).saveWalletInformation(
                                    responseModel
                                )
                                initWalletInfo()
                            }
                        }
                    }
                }
            })
    }

    fun logoutApp(message: String, boolean: Boolean) {
        if (!MyUtils.isConnectedWithInternet(this@JoinContestActivity)) {
            MyUtils.showToast(this@JoinContestActivity, "No Internet connection found")
            return
        }
        genericAlertDialog(message, boolean)
    }

    private fun genericAlertDialog(message: String, boolean: Boolean) {
        val builder = AlertDialog.Builder(mContext!!)
        //set title for alert dialog
        // builder.setTitle("Warning")
        //set message for alert dialog

        builder.setMessage(message)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        if (boolean) {
            builder.setNegativeButton("Cancel", null)
        }
        builder.setPositiveButton("OK") { dialogInterface, which ->

            customeProgressDialog.show()
            val request = RequestModel()
            request.user_id = MyPreferences.getUserID(mContext!!)!!
            request.token = MyPreferences.getToken(mContext!!)!!
            WebServiceClient(mContext!!).client.create(IApiMethod::class.java)
                .logout(request)
                .enqueue(object : Callback<UsersPostDBResponse?> {
                    override fun onFailure(call: Call<UsersPostDBResponse?>?, t: Throwable?) {

                    }

                    override fun onResponse(
                        call: Call<UsersPostDBResponse?>?,
                        response: Response<UsersPostDBResponse?>?
                    ) {

                        customeProgressDialog.dismiss()
                        MyPreferences.clear(mContext!!)
                        val intent = Intent(
                            mContext!!,
                            SplashScreenActivity::class.java
                        )
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }

                })
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }
}