package com.my11heros

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.my11heros.databinding.ActivityVerifyDocumentBinding
import com.my11heros.models.DocumentsModel
import com.my11heros.models.ResponseModel
import com.my11heros.network.IApiMethod
import com.my11heros.network.WebServiceClient
import com.my11heros.ui.BaseActivity
import com.my11heros.utils.CustomeProgressDialog
import com.my11heros.utils.MyUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class VerifyDocumentsActivity : BaseActivity() {

    companion object {
        var REQUESTCODE_VERIFY_DOC = 1008
    }

    private var mIsAdharFrontSelected: Boolean = false

    // private var mIsAdharFrontSelected: Boolean = false
    private var mBinding: ActivityVerifyDocumentBinding? = null
    private var isMobileNumberVerified = true
    private var isEmailVeirfied = true

    var pancardDocumentUrl: String = ""
    var adharCardDocumentUrlFront: String = ""
    var adharCardDocumentUrlBack: String = ""
    var bankPassbookUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userInfo = (application as My11HerosApplication).userInformations
        mBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_verify_document
        )
        mBinding!!.toolbar.title = "Verify Documents"
        mBinding!!.toolbar.setTitleTextColor(resources.getColor(R.color.white))
        mBinding!!.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        setSupportActionBar(mBinding!!.toolbar)
        mBinding!!.toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })

        customeProgressDialog = CustomeProgressDialog(this)

        initCommunication()
        initDocuments()
        initBankDocuments()
        initPaytm()
    }

    private fun initCommunication() {
        if (isMobileNumberVerified) {
            mBinding!!.linearMobileBorder.setBackgroundResource(R.drawable.btn_selector_verified)
            mBinding!!.verifyMobileMessage.text = "Your mobile number verified"
            mBinding!!.verifyMobileNumber.text = userInfo!!.mobileNumber
            mBinding!!.verifyMobileNumber.setTextColor(resources.getColor(R.color.colorPrimary))

        } else {
            mBinding!!.linearMobileBorder.setBackgroundResource(R.drawable.btn_selector_not_verified)
            mBinding!!.verifyMobileMessage.text = "Your mobile number not verified"
            mBinding!!.verifyMobileNumber.text = userInfo!!.mobileNumber
            mBinding!!.verifyMobileNumber.setTextColor(resources.getColor(R.color.red))
        }

        if (isEmailVeirfied) {
            mBinding!!.linearEmailBorder.setBackgroundResource(R.drawable.btn_selector_verified)
            mBinding!!.verifyEmailMessage.text = "Your Email Address verified"
            mBinding!!.verifyEmailAddress.text = userInfo!!.userEmail
            mBinding!!.verifyEmailAddress.setTextColor(resources.getColor(R.color.colorPrimary))
        } else {
            mBinding!!.linearEmailBorder.setBackgroundResource(R.drawable.btn_selector_not_verified)
            mBinding!!.verifyEmailMessage.text = "Your Email Address not verified"
            mBinding!!.verifyEmailAddress.text = userInfo!!.userEmail
            mBinding!!.verifyEmailAddress.setTextColor(resources.getColor(R.color.red))
        }
    }

    private fun initDocuments() {
        mBinding!!.imgPancard.setOnClickListener(View.OnClickListener {
            selectImage(DOCUMENT_TYPE_PANCARD)
        })

        mBinding!!.imgAdharcardFront.setOnClickListener(View.OnClickListener {
            mIsAdharFrontSelected = true
            selectImage(DOCUMENT_TYPE_ADHARCARD)
        })

        mBinding!!.imgAdharcardBack.setOnClickListener(View.OnClickListener {
            mIsAdharFrontSelected = false
            selectImage(DOCUMENT_TYPE_ADHARCARD)
        })
    }

    private fun initBankDocuments() {
        mBinding!!.imgBankPassbook.setOnClickListener(View.OnClickListener {
            selectImage(DOCUMENT_TYPE_BANK_PASSBOOK)
        })
    }

    private fun initPaytm() {

        mBinding!!.btnSubmitVerification.setOnClickListener(View.OnClickListener {
            val models = DocumentsModel()
            models.user_id = userInfo!!.userId
            models.documentType = mDocumentType

            val pancardName = mBinding!!.editPancardName.text.toString()
            val pancardNumber = mBinding!!.editPancardNumber.text.toString()
            val pancardConfirmNumber = mBinding!!.editPancardConfirmNumber.text.toString()
            val paytmNumber = mBinding!!.editPaytmNumber.text.toString()

            val bankName = mBinding!!.editBankName.text.toString()
            val accountHolderName = mBinding!!.editAccountHolderName.text.toString()
            val accountNumber = mBinding!!.editAccountNumber.text.toString()
            val ifscCode = mBinding!!.editAccountIfscCode.text.toString()
            val accountType = mBinding!!.editAccoutType.text.toString()
            val phonepe = mBinding!!.editPhonePayNumber.text.toString()
            val gpay = mBinding!!.editGpayNumber.text.toString()
            val whatsApp = mBinding!!.editWhatsAppNumber.text.toString()


            if (TextUtils.isEmpty(pancardName)) {
                MyUtils.showToast(this@VerifyDocumentsActivity, "Please enter name as on Pan card")
                return@OnClickListener
            } else if (TextUtils.isEmpty(pancardNumber)) {
                MyUtils.showToast(this@VerifyDocumentsActivity, "Please enter Pan card number")
                return@OnClickListener
            } else if (TextUtils.isEmpty(pancardConfirmNumber)) {
                MyUtils.showToast(this@VerifyDocumentsActivity, "Please confirm Pan card number")
                return@OnClickListener
            } else if (!pancardNumber.equals(pancardConfirmNumber)) {
                MyUtils.showToast(
                    this@VerifyDocumentsActivity,
                    "Both Pan card number does not matched"
                )
                return@OnClickListener
            } else if (TextUtils.isEmpty(pancardDocumentUrl)) {
                MyUtils.showToast(
                    this@VerifyDocumentsActivity,
                    "Please upload picture of pan card"
                )
                return@OnClickListener
            } else if (TextUtils.isEmpty(bankName)) {

                MyUtils.showToast(this@VerifyDocumentsActivity, "Please enter your Bank name")
                return@OnClickListener
            } else if (TextUtils.isEmpty(accountHolderName)) {
                MyUtils.showToast(this@VerifyDocumentsActivity, "Please your name on Bank card")
                return@OnClickListener
            } else if (TextUtils.isEmpty(accountNumber)) {
                MyUtils.showToast(this@VerifyDocumentsActivity, "Please enter Bank account number")
                return@OnClickListener
            } else if (TextUtils.isEmpty(ifscCode)) {
                MyUtils.showToast(this@VerifyDocumentsActivity, "Please enter IFSC code")
                return@OnClickListener
            } else if (TextUtils.isEmpty(accountType)) {
                MyUtils.showToast(this@VerifyDocumentsActivity, "Please enter account type")
                return@OnClickListener
            } else if (TextUtils.isEmpty(bankPassbookUrl)) {
                MyUtils.showToast(
                    this@VerifyDocumentsActivity,
                    "Please upload picture of passbook or cheque"
                )
                return@OnClickListener
            } else if (TextUtils.isEmpty(paytmNumber)) {
                MyUtils.showToast(this@VerifyDocumentsActivity, "Please enter your Paytm number")
                return@OnClickListener
            }

            models.panCardName = pancardName
            models.panCardNumber = pancardConfirmNumber
            models.pancardDocumentUrl = pancardDocumentUrl

            models.bankName = bankName
            models.accountHolderName = accountHolderName
            models.accountNumber = accountNumber
            models.ifscCode = ifscCode
            models.accountType = accountType
            models.bankPassbookUrl = bankPassbookUrl

            models.paytmNumber = paytmNumber
            models.phonepe = phonepe
            models.gpay = gpay
            models.whats_app = whatsApp
            models.documentType = DOCUMENT_TYPE_PANCARD

            if (MyUtils.isConnectedWithInternet(this@VerifyDocumentsActivity)) {
                submitDocuments(models, DOCUMENT_TYPE_PANCARD)
            } else {
                MyUtils.showToast(
                    this@VerifyDocumentsActivity,
                    "Please check your internet connections"
                )
            }
        })
    }

    private fun submitDocuments(models: DocumentsModel, documentType: String) {
        customeProgressDialog.show()
        models.documentType = DOCUMENT_TYPE_PANCARD

        if (MyUtils.isConnectedWithInternet(this@VerifyDocumentsActivity)) {
            WebServiceClient(this@VerifyDocumentsActivity).client.create(IApiMethod::class.java)
                .saveAllDocuments(models)
                .enqueue(object : Callback<ResponseModel?> {
                    override fun onFailure(call: Call<ResponseModel?>?, t: Throwable?) {
                        MyUtils.showToast(this@VerifyDocumentsActivity, t!!.localizedMessage)
                    }

                    override fun onResponse(
                        call: Call<ResponseModel?>?,
                        response: Response<ResponseModel?>?
                    ) {
                        customeProgressDialog.dismiss()
                        val res = response!!.body()
                        if (res != null && res.status) {
                            Toast.makeText(
                                this@VerifyDocumentsActivity,
                                res.message,
                                Toast.LENGTH_LONG
                            ).show()
                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            MyUtils.showToast(this@VerifyDocumentsActivity, res!!.message)
                        }
                    }
                })
        } else {
            MyUtils.showToast(
                this@VerifyDocumentsActivity,
                "Please check your internet connections"
            )
        }
    }

    override fun onBitmapSelected(bitmap: Bitmap) {
        if (mDocumentType.equals(DOCUMENT_TYPE_PANCARD)) {
            mBinding!!.imgPancard.setImageBitmap(bitmap)
        } else if (mDocumentType.equals(DOCUMENT_TYPE_ADHARCARD)) {
            if (mIsAdharFrontSelected) {
                mBinding!!.imgAdharcardFront.setImageBitmap(bitmap)
            } else {
                mBinding!!.imgAdharcardBack.setImageBitmap(bitmap)
            }
        } else if (mDocumentType.equals(DOCUMENT_TYPE_BANK_PASSBOOK)) {
            mBinding!!.imgBankPassbook.setImageBitmap(bitmap)
        }
    }

    override fun onUploadedImageUrl(url: String) {
        if (mDocumentType.equals(DOCUMENT_TYPE_PANCARD)) {
            pancardDocumentUrl = url
        } else if (mDocumentType.equals(DOCUMENT_TYPE_ADHARCARD)) {
            if (mIsAdharFrontSelected) {
                adharCardDocumentUrlFront = url
            } else {
                adharCardDocumentUrlBack = url
            }
        } else if (mDocumentType.equals(DOCUMENT_TYPE_BANK_PASSBOOK)) {
            bankPassbookUrl = url
        }
    }
}