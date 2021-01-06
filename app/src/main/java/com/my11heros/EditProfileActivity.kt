package com.my11heros

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.my11heros.databinding.ActivityEditProfileBinding
import com.my11heros.models.ResponseModel
import com.my11heros.network.IApiMethod
import com.my11heros.network.RequestModel
import com.my11heros.network.WebServiceClient
import com.my11heros.ui.BaseActivity
import com.my11heros.ui.home.models.UsersPostDBResponse
import com.my11heros.utils.CustomeProgressDialog
import com.my11heros.utils.MyPreferences
import com.my11heros.utils.MyUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.util.*


class EditProfileActivity : BaseActivity() {

    private var mBinding: ActivityEditProfileBinding? = null
    private var photoUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userInfo = (application as My11HerosApplication).userInformations
        customeProgressDialog = CustomeProgressDialog(this)
        mBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_edit_profile
        )
        mBinding!!.toolbar.title = "Update Profile"
        mBinding!!.toolbar.setTitleTextColor(resources.getColor(R.color.white))
        mBinding!!.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        setSupportActionBar(mBinding!!.toolbar)
        mBinding!!.toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })

        Glide.with(this)
            .load(userInfo!!.profileImage)
            .placeholder(R.drawable.ic_profile)
            .into(mBinding!!.profileImage)

        updateUserOtherInfo()


        mBinding!!.profileImage.setOnClickListener {
            if (!TextUtils.isEmpty(photoUrl)) {
                val intent =
                    Intent(this@EditProfileActivity, FullScreenImageViewActivity::class.java)
                intent.putExtra(FullScreenImageViewActivity.KEY_IMAGE_URL, photoUrl)
                startActivity(intent)
            } else {
                if (checkAndRequestPermissions()) {
                    selectImage(BaseActivity.DOCUMENTS_TYPE_PROFILES)
                } else {
                    Toast.makeText(
                        this@EditProfileActivity,
                        "Permission required ",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        mBinding!!.imageEdit.setOnClickListener {
            if (checkAndRequestPermissions()) {
                selectImage(DOCUMENTS_TYPE_PROFILES)
            } else {
                Toast.makeText(this@EditProfileActivity, "Permission required ", Toast.LENGTH_LONG)
                    .show()
            }
        }

        mBinding!!.dateOfBirth.setOnClickListener(View.OnClickListener {
            val c = Calendar.getInstance()
            val mYear = c[Calendar.YEAR]
            val mMonth = c[Calendar.MONTH]
            val mDay = c[Calendar.DAY_OF_MONTH]

            val datePickerDialog = DatePickerDialog(
                this,
                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    val a = monthOfYear + 1
                    val formatter = DecimalFormat("00")
                    val month = formatter.format(a.toLong())

                    val formatter2 = DecimalFormat("00")
                    val date = formatter2.format(dayOfMonth.toLong())

                    mBinding!!.dateOfBirth.setText(
                        String.format(
                            Locale.ENGLISH,
                            "%s-%s-%d",
                            date,
                            month,
                            year
                        )
                    )
                }, mYear, mMonth, mDay
            )
            datePickerDialog.show()
        })

        mBinding!!.btnUpdateProfile.setOnClickListener(View.OnClickListener {

            updateProfile()
        })
        initProfile()
        getProfile()

    }

    private fun updateUserOtherInfo() {
        if (!TextUtils.isEmpty(userInfo!!.teamName)) {
            mBinding!!.editTeamName.setText(userInfo!!.teamName)

            mBinding!!.editTeamName.setSelection(userInfo!!.teamName.length)
        }

        if (!TextUtils.isEmpty(userInfo!!.dateOfBirth)) {
            mBinding!!.dateOfBirth.setText(userInfo!!.dateOfBirth)
        }

        if (!TextUtils.isEmpty(userInfo!!.city)) {
            mBinding!!.editCity.setText(userInfo!!.city)
        }
        /*if(!TextUtils.isEmpty(userInfo!!.pinCode)) {
            mBinding!!.editPicode.setText(userInfo!!.pinCode)
            mBinding!!.spinnerStates.prompt = userInfo!!.state

        }*/
    }

    override fun onBitmapSelected(bitmap: Bitmap) {
        //mBinding!!.profileImage.setImageBitmap(bitmap)
        if (!bitmap.equals("")) {
            Glide.with(this).asBitmap().load(bitmap).placeholder(R.drawable.player_blue)
                .into(mBinding!!.profileImage)
        }
    }

    override fun onUploadedImageUrl(url: String) {
        this.photoUrl = url
        if (url.isNotEmpty())
            Glide.with(this).load(url).placeholder(R.drawable.player_blue)
                .into(mBinding!!.profileImage)
        //MyPreferences.setProfilePicture(this@EditProfileActivity,photoUrl)
    }

    private fun initProfile() {
        photoUrl = userInfo!!.profileImage
        // MyPreferences.setProfilePicture(this,photoUrl)
        mBinding!!.editTeamName.setText(userInfo!!.teamName)
        mBinding!!.updateProfileName.setText(userInfo!!.fullName)
        mBinding!!.updateEmail.setText(userInfo!!.userEmail)
        mBinding!!.updateEditMobile.setText(userInfo!!.mobileNumber)

        if (userInfo!!.gender.equals("male")) {
            mBinding!!.genderMale.isChecked = true
            mBinding!!.genderFemale.isChecked = false
        } else {
            mBinding!!.genderMale.isChecked = false
            mBinding!!.genderFemale.isChecked = true
        }

        if (userInfo!!.profileImage.isNotEmpty())
            Glide.with(this).load(userInfo!!.profileImage).placeholder(R.drawable.player_blue)
                .into(mBinding!!.profileImage)
    }

    private fun updateProfile() {
        if (!MyUtils.isConnectedWithInternet(this)) {
            MyUtils.showToast(this, "No Internet connection found")
            return
        }
        val editTeamName = mBinding!!.editTeamName.text.toString()
        val editName = mBinding!!.updateProfileName.text.toString()
        val mobileNumber = mBinding!!.updateEditMobile.text.toString()
        val emailAddress = mBinding!!.updateEmail.text.toString()
        val cityName = mBinding!!.editCity.text.toString()
        var gender = "male"
        if (!mBinding!!.genderMale.isChecked) {
            gender = "female"
        }
        val dateOfBirth = mBinding!!.dateOfBirth.text.toString()
        //val state = mBinding!!.spinnerStates.selectedItem.toString()

        if (TextUtils.isEmpty(editName)) {
            MyUtils.showToast(this@EditProfileActivity, "Please enter your real name")
            return
        } else if (TextUtils.isEmpty(mobileNumber)) {
            MyUtils.showToast(this@EditProfileActivity, "Please enter valid mobile number")
            return
        } else if (mobileNumber.length < 10) {
            MyUtils.showToast(this@EditProfileActivity, "Please enter valid mobile number")
            return
        } else if (TextUtils.isEmpty(emailAddress) || !MyUtils.isEmailValid(emailAddress)) {
            MyUtils.showToast(this@EditProfileActivity, "Please enter valid email address")
            return
        } else if (TextUtils.isEmpty(cityName)) {
            MyUtils.showToast(this@EditProfileActivity, "Please enter city Name")
            return
        } else if (TextUtils.isEmpty(dateOfBirth)) {
            MyUtils.showToast(this@EditProfileActivity, "Please enter your Date of Birth")
            return
        }

        mBinding!!.progressBar.visibility = View.VISIBLE
        val models = RequestModel()
        models.user_id = MyPreferences.getUserID(this)!!
        models.image_url = photoUrl
        models.team_name = mBinding!!.editTeamName.text.toString()
        models.name = editName
        models.email = emailAddress
        models.mobile_number = mobileNumber
        models.city = cityName
        models.gender = gender
        models.dateOfBirth = dateOfBirth
        //models.pinCode = pinCode
        //models.state = state

        WebServiceClient(this).client.create(IApiMethod::class.java).updateProfile(models)
            .enqueue(object : Callback<UsersPostDBResponse?> {
                override fun onFailure(call: Call<UsersPostDBResponse?>?, t: Throwable?) {

                }

                override fun onResponse(
                    call: Call<UsersPostDBResponse?>?,
                    response: Response<UsersPostDBResponse?>?
                ) {
                    mBinding!!.progressBar.visibility = View.GONE
                    val res = response!!.body()
                    if (res != null && res.status) {
//                        userInfo = (application as PlugSportsApplication).userInformations
                        userInfo!!.profileImage = photoUrl
                        userInfo!!.teamName = editTeamName
                        userInfo!!.fullName = editName
                        userInfo!!.city = cityName
                        userInfo!!.gender = gender
                        userInfo!!.dateOfBirth = dateOfBirth
                       // userInfo.pinCode = pinCode
//                        userInfo.state = state

                        (application as My11HerosApplication).saveUserInformations(userInfo)

                        Toast.makeText(
                            this@EditProfileActivity,
                            "Profile updated successfully",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                }
            })
    }

    private fun getProfile() {
        if (!MyUtils.isConnectedWithInternet(this)) {
            MyUtils.showToast(this, "No Internet connection found")
            return
        }
        customeProgressDialog.show()
        val models = RequestModel()
        models.user_id = MyPreferences.getUserID(this)!!

        WebServiceClient(this).client.create(IApiMethod::class.java).getProfile(models)
            .enqueue(object : Callback<ResponseModel?> {
                override fun onFailure(call: Call<ResponseModel?>?, t: Throwable?) {
                    customeProgressDialog.dismiss()
                }

                override fun onResponse(
                    call: Call<ResponseModel?>?,
                    response: Response<ResponseModel?>?
                ) {
                    customeProgressDialog.dismiss()
                    val res = response!!.body()
                    if (res != null && res.status) {

                        val infoModels = res.infomodel
                        if (infoModels != null) {
                            (application as My11HerosApplication).saveUserInformations(infoModels)
                            userInfo = (application as My11HerosApplication).userInformations
                            initProfile()
                            updateUserOtherInfo()
                        } else {
                            MyUtils.showToast(
                                this@EditProfileActivity,
                                "Something went wrong, please contact admin"
                            )
                        }
                    }
                }
            })
    }
}