package com.my11heros.ui.login.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.my11heros.models.ResponseModel
import com.my11heros.network.IApiMethod
import com.my11heros.network.RequestModel
import com.my11heros.network.WebServiceClient
import com.my11heros.utils.HardwareInfo
import com.my11heros.utils.MyUtils
import com.my11heros.utils.SingleLiveEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(application: Application) : AndroidViewModel(application),
    Callback<ResponseModel> {
    var app:Application?=application

    var btnSelected: ObservableBoolean? = null
    var email: ObservableField<String>? = null
    var password: ObservableField<String>? = null
    var progressDialog: SingleLiveEvent<Boolean>? = null
    var userLogin: MutableLiveData<ResponseModel>? = null
    var hrdinfo: HardwareInfo?=null
    init {
        btnSelected = ObservableBoolean(false)
        progressDialog = SingleLiveEvent<Boolean>()
        email = ObservableField("")
        password = ObservableField("")
        userLogin = MutableLiveData<ResponseModel>()

    }

    fun onEmailChanged(s: CharSequence, start: Int, befor: Int, count: Int) {
        btnSelected?.set(MyUtils.isMobileValid(s.toString()) && password?.get()!!.length >= 8)


    }

    fun onPasswordChanged(s: CharSequence, start: Int, befor: Int, count: Int) {
        btnSelected?.set(MyUtils.isMobileValid(email?.get()!!) && s.toString().length >= 8)


    }

    fun login() {
        progressDialog?.value = true

        var request = RequestModel()
        request.username = email?.get()!!
        request.password = password?.get()!!
        request.deviceDetails = hrdinfo
        WebServiceClient(getApplication()).client.create(IApiMethod::class.java).customerLogin(request)
                .enqueue(this)

    }

    override fun onResponse(call: Call<ResponseModel>?, response: Response<ResponseModel>?) {
        progressDialog?.value = false
        userLogin?.value = response?.body()

    }

    override fun onFailure(call: Call<ResponseModel>?, t: Throwable?) {
        progressDialog?.value = false
        Toast.makeText(app
            , "Warning , ${t?.message}", Toast.LENGTH_LONG).show()


    }

}

