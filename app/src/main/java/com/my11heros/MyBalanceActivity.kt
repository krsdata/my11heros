package com.my11heros

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.my11heros.databinding.ActivityMyBallanceBinding
import com.my11heros.models.AccountDocumentStatus
import com.my11heros.models.WalletInfo
import com.my11heros.network.IApiMethod
import com.my11heros.network.RequestModel
import com.my11heros.network.WebServiceClient
import com.my11heros.ui.home.models.UsersPostDBResponse
import com.my11heros.ui.login.RegisterScreenActivity
import com.my11heros.utils.BindingUtils
import com.my11heros.utils.CustomeProgressDialog
import com.my11heros.utils.MyPreferences
import com.my11heros.utils.MyUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyBalanceActivity : AppCompatActivity() {

    private lateinit var walletInfo: WalletInfo
    private var customeProgressDialog: CustomeProgressDialog? = null
    private var mBinding: ActivityMyBallanceBinding? = null

    companion object {

        val REQUEST_CODE_ADD_MONEY: Int = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_my_ballance
        )
        walletInfo = (application as SportsFightApplication).walletInfo
        mBinding!!.toolbar.title = "My Balance"
        mBinding!!.toolbar.setTitleTextColor(resources.getColor(R.color.white))
        mBinding!!.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        setSupportActionBar(mBinding!!.toolbar)
        mBinding!!.toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })

        customeProgressDialog = CustomeProgressDialog(this)


        getWalletBalances()
        initWalletInfo()

        mBinding!!.addCash.setOnClickListener(View.OnClickListener {
            if (MyPreferences.getLoginStatus(this@MyBalanceActivity)!!) {
                val intent = Intent(this@MyBalanceActivity, AddMoneyActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE_ADD_MONEY)
            } else {
                val intent = Intent(this@MyBalanceActivity, RegisterScreenActivity::class.java)
                intent.putExtra(RegisterScreenActivity.ISACTIVITYRESULT, true)
                startActivityForResult(intent, RegisterScreenActivity.REQUESTCODE_LOGIN)
            }
        })

        mBinding!!.btnWithdraw.setOnClickListener(View.OnClickListener {
            if (walletInfo.bankAccountVerified == BindingUtils.BANK_DOCUMENTS_STATUS_VERIFIED) {
                val value = walletInfo.walletAmount
                val amount = value.toDouble()
                if (amount >= 200) {
                    val intent = Intent(this@MyBalanceActivity, WithdrawAmountsActivity::class.java)
                    startActivityForResult(intent, VerifyDocumentsActivity.REQUESTCODE_VERIFY_DOC)
                } else {
                    MyUtils.showToast(this@MyBalanceActivity, "Amount is less than 200 INR")
                }

            } else {
                var message = "Please Verify your account"
                if (walletInfo.bankAccountVerified == BindingUtils.BANK_DOCUMENTS_STATUS_APPROVAL_PENDING) {
                    message = "Documents Approvals Pending"
                } else if (walletInfo.bankAccountVerified == BindingUtils.BANK_DOCUMENTS_STATUS_REJECTED) {
                    message = "Your Document Rejected Please contact admin"
                }
                MyUtils.showToast(this@MyBalanceActivity, message)
            }

        })

        mBinding!!.txtRecentTransaction.setOnClickListener(View.OnClickListener {

            val intent = Intent(this@MyBalanceActivity, MyTransactionHistoryActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_MONEY)
        })
    }

    private fun updateAccountVerification(accountStatus: AccountDocumentStatus?) {
        if (accountStatus != null) {
            if (accountStatus.documentsVerified == BindingUtils.BANK_DOCUMENTS_STATUS_REJECTED) {
                mBinding!!.verifyAccountMessage.visibility = View.GONE
                mBinding!!.verifyAccount.text = "REJECTED"
                mBinding!!.verifyAccount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.0f)
                mBinding!!.verifyAccount.setBackgroundResource(R.drawable.button_selector_red)
                mBinding!!.verifyAccount.setTextColor(Color.WHITE)
                mBinding!!.verifyAccount.setOnClickListener(View.OnClickListener {
                    gotoDocumentsListActivity()
                })
            } else
                if (accountStatus.documentsVerified == BindingUtils.BANK_DOCUMENTS_STATUS_VERIFIED) {
                    mBinding!!.verifyAccountMessage.visibility = View.GONE
                    mBinding!!.verifyAccount.text = "Account Verified"
                    mBinding!!.verifyAccount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.0f)
                    mBinding!!.verifyAccount.setBackgroundResource(R.drawable.button_selector_green)
                    mBinding!!.verifyAccount.setTextColor(Color.WHITE)
                    mBinding!!.verifyAccount.setOnClickListener(View.OnClickListener {
                        gotoDocumentsListActivity()
                    })

                } else if (accountStatus.documentsVerified == BindingUtils.BANK_DOCUMENTS_STATUS_APPROVAL_PENDING) {
                    mBinding!!.verifyAccountMessage.visibility = View.GONE
                    mBinding!!.verifyAccount.text = "Approval Pending"
                    mBinding!!.verifyAccount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.0f)
                    mBinding!!.verifyAccount.setBackgroundResource(R.drawable.button_selector_white)
                    mBinding!!.verifyAccount.setTextColor(Color.BLACK)
                    mBinding!!.verifyAccount.setOnClickListener(View.OnClickListener {
                        //gotoDocumentsListActivity()
                    })
                } else {
                    mBinding!!.verifyAccountMessage.visibility = View.VISIBLE
                    mBinding!!.verifyAccount.setOnClickListener(View.OnClickListener {
                        val intent =
                            Intent(this@MyBalanceActivity, VerifyDocumentsActivity::class.java)
                        startActivityForResult(
                            intent,
                            VerifyDocumentsActivity.REQUESTCODE_VERIFY_DOC
                        )
                    })
                }
        }
    }

    private fun gotoDocumentsListActivity() {
        val intent = Intent(this, DocumentsListActivity::class.java)
        startActivityForResult(intent, VerifyDocumentsActivity.REQUESTCODE_VERIFY_DOC)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        getWalletBalances()
    }

    private fun initWalletInfo() {
        val walletInfo = (application as SportsFightApplication).walletInfo
        val totalBalance =
            walletInfo.depositAmount + walletInfo.prizeAmount + walletInfo.bonusAmount
        mBinding!!.walletTotalAmount.text = String.format("₹%.2f", totalBalance)
        mBinding!!.amountAdded.text = String.format("₹%.2f", walletInfo.prizeAmount)
        mBinding!!.depositAmount.text = String.format("₹%.2f", walletInfo.depositAmount)
        mBinding!!.bonusAmount.text = String.format("₹%.2f", walletInfo.bonusAmount)

        if (walletInfo != null) {
            val accountStatus = walletInfo.accountStatus
            updateAccountVerification(accountStatus)
        } else {
            mBinding!!.verifyAccountMessage.visibility = View.VISIBLE
            mBinding!!.verifyAccount.setOnClickListener(View.OnClickListener {
                val intent = Intent(this@MyBalanceActivity, VerifyDocumentsActivity::class.java)
                startActivityForResult(intent, VerifyDocumentsActivity.REQUESTCODE_VERIFY_DOC)
            })
        }
    }

    fun getWalletBalances() {
        if (!MyUtils.isConnectedWithInternet(this)) {
            MyUtils.showToast(this, "No Internet connection found")
            return
        }
        customeProgressDialog!!.show()
        val models = RequestModel()
        models.user_id = MyPreferences.getUserID(this)!!
        models.token = MyPreferences.getToken(this)!!

        WebServiceClient(this).client.create(IApiMethod::class.java).getWallet(models)
            .enqueue(object : Callback<UsersPostDBResponse?> {
                override fun onFailure(call: Call<UsersPostDBResponse?>?, t: Throwable?) {
                    customeProgressDialog!!.dismiss()
                }

                override fun onResponse(
                    call: Call<UsersPostDBResponse?>?,
                    response: Response<UsersPostDBResponse?>?
                ) {
                    customeProgressDialog!!.dismiss()
                    val res = response!!.body()
                    if (res != null) {
                        val responseModel = res.walletObjects
                        if (responseModel != null) {
                            MyPreferences.setRazorPayId(this@MyBalanceActivity, responseModel.razorPay)
                            MyPreferences.setShowPaytm(this@MyBalanceActivity, responseModel.paytm_show)
                            MyPreferences.setShowGpay(this@MyBalanceActivity, responseModel.gpay_show)
                            MyPreferences.setShowRazorPay(this@MyBalanceActivity, responseModel.rozarpay_show)
                            (application as SportsFightApplication).saveWalletInformation(
                                responseModel
                            )
                            initWalletInfo()
                        }
                    }
                }
            })
    }
}
