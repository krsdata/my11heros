package com.my11heros

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.my11heros.databinding.ActivityMainBinding
import com.my11heros.network.IApiMethod
import com.my11heros.network.RequestModel
import com.my11heros.network.WebServiceClient
import com.my11heros.ui.BaseActivity
import com.my11heros.ui.UpdateAppDialogFragment
import com.my11heros.ui.dashboard.MyAccountFragment
import com.my11heros.ui.dashboard.MyMatchesFragment
import com.my11heros.ui.home.FixtureCricketFragment
import com.my11heros.ui.home.models.UsersPostDBResponse
import com.my11heros.ui.notifications.MoreOptionsFragment
import com.my11heros.utils.MyPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener,
    BottomNavigationView.OnNavigationItemSelectedListener {

    var fragment: Fragment? = null
    private var mBinding: ActivityMainBinding? = null

    companion object {
        var CHECK_WALLET_ONCE: Boolean? = false
        var updatedApkUrl: String = ""
        var releaseNote: String = ""
        var CHECK_APK_UPDATE_API: Boolean = false
        var CHECK_FORCE_UPDATE: Boolean = true
        var telegramLink: String = "https://t.me/my11heroes"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
        userInfo = (application as My11HerosApplication).userInformations
        setSupportActionBar(mBinding!!.toolbar)
        // setUpDrawerLayout()
        updateFireBase()

        mBinding!!.imgWalletAmount.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MainActivity, MyBalanceActivity::class.java)
            startActivityForResult(intent, MyBalanceActivity.REQUEST_CODE_ADD_MONEY)
        })
        mBinding!!.notificationId.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MainActivity, NotificationListActivity::class.java)
            startActivityForResult(intent, MyBalanceActivity.REQUEST_CODE_ADD_MONEY)
        })

        getWalletBalances()

        Glide.with(this).load(userInfo!!.profileImage)
            .placeholder(R.drawable.player_blue).into(mBinding!!.profileImage)

        mBinding!!.profileImage.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val intent = Intent(this@MainActivity, EditProfileActivity::class.java)
                intent.putExtra(FullScreenImageViewActivity.KEY_IMAGE_URL, userInfo!!.profileImage)
                startActivity(intent)
            }
        })

        mBinding!!.telegramId.setOnClickListener{
            if(telegramLink != "") {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(telegramLink))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                //intent.setPackage("com.android.chrome")
                startActivity(intent)
            }
        }

        mBinding!!.navigation.setOnNavigationItemSelectedListener(this)

        fragment = FixtureCricketFragment()
        loadFragment()
    }

    override fun onResume() {
        super.onResume()
        if (userInfo != null) {
            Glide.with(this)
                .load(userInfo!!.profileImage)
                .placeholder(R.drawable.player_blue)
                .into(mBinding!!.profileImage)
        }
    }

    fun viewUpcomingMatches() {
        mBinding!!.navigation.selectedItemId = R.id.navigation_home
    }

    fun viewAllMatches() {
        mBinding!!.navigation.selectedItemId = R.id.navigation_home
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MyBalanceActivity.REQUEST_CODE_ADD_MONEY) {
            getWalletBalances()
            setWalletBalanceValue()
        }
    }

    private fun setWalletBalanceValue() {
//        var walletInfo = (application as My11HerosApplication).walletInfo
//        if(walletInfo!=null){
//            var totalBalance =
//                walletInfo.depositAmount + walletInfo.prizeAmount + walletInfo.bonusAmount
//            mBinding!!.walletAmount.setText(String.format("₹%s",totalBalance.toString()))
//        }
    }

    override fun onBitmapSelected(bitmap: Bitmap) {
    }

    override fun onUploadedImageUrl(url: String) {

    }

    override fun onStart() {
        super.onStart()
        setWalletBalanceValue()
        if (CHECK_APK_UPDATE_API) {
            CHECK_APK_UPDATE_API = false
            val fm = supportFragmentManager
            val pioneersFragment =
                UpdateAppDialogFragment(updatedApkUrl, releaseNote)
            pioneersFragment.isCancelable = false
            pioneersFragment.show(fm, "updateapp_tag")
        }
    }

    private fun getWalletBalances() {
        //var userInfo = (activity as PlugSportsApplication).userInformations
        val models = RequestModel()
        models.user_id = MyPreferences.getUserID(this)!!
        // models.token = MyPreferences.getToken(this)!!

        WebServiceClient(this).client.create(IApiMethod::class.java).getWallet(models)
            .enqueue(object : Callback<UsersPostDBResponse?> {
                override fun onFailure(call: Call<UsersPostDBResponse?>?, t: Throwable?) {
                    CHECK_APK_UPDATE_API = false
                }

                override fun onResponse(
                    call: Call<UsersPostDBResponse?>?,
                    response: Response<UsersPostDBResponse?>?
                ) {
                    CHECK_APK_UPDATE_API = false
                    val res = response!!.body()
                    if (res != null) {
                        val responseModel = res.walletObjects
                        if (responseModel != null) {
                            MyPreferences.setRazorPayId(this@MainActivity, responseModel.razorPay)
                            MyPreferences.setShowPaytm(this@MainActivity, responseModel.paytm_show)
                            MyPreferences.setShowGpay(this@MainActivity, responseModel.gpay_show)
                            MyPreferences.setShowRazorPay(
                                this@MainActivity,
                                responseModel.rozarpay_show
                            )
                            (application as My11HerosApplication).saveWalletInformation(
                                responseModel
                            )
                            setWalletBalanceValue()
                        }
                    }
                }
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun showToolbar() {
        mBinding!!.toolbar.visibility = View.VISIBLE
        mBinding!!.toolLayout.visibility = View.VISIBLE
    }

    fun hideToolbar() {
        mBinding!!.toolbar.visibility = View.GONE
        mBinding!!.toolLayout.visibility = View.GONE
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.getItemId()) {
            R.id.navigation_home -> {
                fragment = FixtureCricketFragment()
                loadFragment()
                return true
            }
            R.id.navigation_dashboard -> {
                fragment = MyMatchesFragment()
                loadFragment()
                return true
            }
            R.id.navigation_myaccount -> {
                fragment = MyAccountFragment()
                loadFragment()
                return true
            }
            R.id.navigation_notifications -> {
                fragment = MoreOptionsFragment()
                loadFragment()
                return true
            }
        }
        return false
    }

    private fun loadFragment() {
        if (fragment != null) {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container_body, fragment!!)
            fragmentTransaction.commit()
        }
    }
}