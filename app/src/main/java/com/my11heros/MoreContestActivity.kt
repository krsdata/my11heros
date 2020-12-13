package com.my11heros

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.deliverdas.customers.utils.HardwareInfoManager
import com.edify.atrist.listener.OnContestEvents
import com.edify.atrist.listener.OnContestLoadedListener
import com.edify.atrist.listener.OnMatchTimerStarted
import com.my11heros.databinding.ActivityMoreContestBinding
import com.my11heros.models.ContestsParentModels
import com.my11heros.models.MyTeamModels
import com.my11heros.models.UpcomingMatchesModel
import com.my11heros.network.IApiMethod
import com.my11heros.network.RequestModel
import com.my11heros.network.WebServiceClient
import com.my11heros.ui.BaseActivity
import com.my11heros.ui.contest.MoreContestFragment
import com.my11heros.ui.contest.models.ContestModelLists
import com.my11heros.ui.home.models.UsersPostDBResponse
import com.my11heros.utils.BindingUtils
import com.my11heros.utils.MyPreferences
import com.my11heros.utils.MyUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MoreContestActivity : BaseActivity(), OnContestLoadedListener, OnContestEvents,
    ViewPager.OnPageChangeListener {

    private var allContestList: ArrayList<ContestsParentModels>? = null
    private lateinit var selectedObject: ContestsParentModels
    var matchObject: UpcomingMatchesModel? = null
    var isTimeUp: Boolean = false
    var joinedTeamList: java.util.ArrayList<MyTeamModels>? = null
    var contestObjects: ArrayList<ContestModelLists>? = null
    private var mBinding: ActivityMoreContestBinding? = null
    var selected_position = 0

    companion object {
        val SERIALIZABLE_KEY_LIST_POSTIION: String = "contestposition"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_more_contest)
        matchObject =
            intent.getSerializableExtra(ContestActivity.SERIALIZABLE_KEY_UPCOMING_MATCHES) as UpcomingMatchesModel
        allContestList =
            intent.getSerializableExtra(ContestActivity.SERIALIZABLE_KEY_JOINED_CONTEST) as ArrayList<ContestsParentModels>
        selectedObject =
            intent.getSerializableExtra(SERIALIZABLE_KEY_LIST_POSTIION) as ContestsParentModels

        if (matchObject != null) {
            initViewUpcomingMatches()
        }

        mBinding!!.imageBack.setOnClickListener(View.OnClickListener {
            finish()
        })

        mBinding!!.imgWallet.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MoreContestActivity, MyBalanceActivity::class.java)
            startActivity(intent)
        })
        setupViewPager(mBinding!!.viewpagerContest)
        mBinding!!.tabs.setupWithViewPager(mBinding!!.viewpagerContest)
        mBinding!!.viewpagerContest.addOnPageChangeListener(this)

//        mBinding!!.mycontestRefresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
//            getAllContest()
//        })
    }


    private fun initViewUpcomingMatches() {

        mBinding!!.teamsa.text = matchObject!!.teamAInfo!!.teamShortName
        mBinding!!.teamsb.text = matchObject!!.teamBInfo!!.teamShortName

    }

    override fun onResume() {
        super.onResume()
        if (matchObject!!.status == BindingUtils.MATCH_STATUS_UPCOMING) {
            pauseCountDown()
            startCountDown()
        } else {
            updateTimerHeader()
        }
    }

    private fun startCountDown() {
        BindingUtils.logD("TimerLogs", "initViewUpcomingMatches() called in ContestActivity")
        //matchObject!!.timestampStart = 1591371412 + 300
        BindingUtils.countDownStart(matchObject!!.timestampStart, object : OnMatchTimerStarted {

            override fun onTimeFinished() {
                if (!isTimeUp) {
                    isTimeUp = true
                    updateTimerHeader()
                    if (matchObject!!.status.equals(BindingUtils.MATCH_STATUS_UPCOMING)) {
                        showMatchTimeUpDialog()
                    }
                }
            }

            override fun onTicks(time: String) {
                mBinding!!.matchTimer.text = time
                mBinding!!.matchTimer.setTextColor(resources.getColor(R.color.red))
                mBinding!!.watchTimerImg.visibility = View.VISIBLE
                BindingUtils.logD("TimerLogs", "ContestScreen: " + time)
            }
        })
    }


    private fun updateTimerHeader() {
        mBinding!!.matchTimer.text = matchObject!!.statusString.toUpperCase()
        mBinding!!.matchTimer.setTextColor(resources.getColor(R.color.colorPrimary))
        mBinding!!.watchTimerImg.visibility = View.GONE
    }

    fun pauseCountDown() {
        BindingUtils.stopTimer()
    }

    override fun onPause() {
        super.onPause()
        pauseCountDown()
    }

    fun changeTabsPositions(postion: Int) {
        mBinding!!.viewpagerContest.currentItem = postion
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mBinding!!.viewpagerContest.currentItem = selected_position
        //getAllContest()
    }


    fun getAllContest() {
        //var userInfo = (activity as PlugSportsApplication).userInformations
        mBinding!!.mycontestRefresh.isRefreshing = true
        val models = RequestModel()
        models.user_id = MyPreferences.getUserID(this@MoreContestActivity)!!
        // models.token =MyPreferences.getToken(requireActivity())!!
        models.match_id = "" + matchObject!!.matchId
        models.token = MyPreferences.getToken(this@MoreContestActivity)!!
        val deviceToken: String? = MyPreferences.getDeviceToken(this@MoreContestActivity)
        models.deviceDetails = HardwareInfoManager(this@MoreContestActivity).collectData(deviceToken!!)

        WebServiceClient(this@MoreContestActivity).client.create(IApiMethod::class.java)
            .getContestByMatch(models)
            .enqueue(object : Callback<UsersPostDBResponse?> {
                override fun onFailure(call: Call<UsersPostDBResponse?>?, t: Throwable?) {

                }

                override fun onResponse(
                    call: Call<UsersPostDBResponse?>?,
                    response: Response<UsersPostDBResponse?>?
                ) {

                    mBinding!!.mycontestRefresh.isRefreshing = false
                    val res = response!!.body()
                    if (res != null) {
                        BindingUtils.currentTimeStamp = res.systemTime
                        val responseModel = res.responseObject
                        if (responseModel!!.matchContestlist != null && responseModel.matchContestlist!!.size > 0) {
//                            checkinArrayList.clear()
//                            checkinArrayList.addAll(responseModel.matchContestlist!!)
//                            adapter.setMatchesList(checkinArrayList)
//                            mListener.onMyTeam(responseModel.myjoinedTeams!!)
//                            mListener.onMyContest(responseModel.joinedContestDetails!!)
                            allContestList =
                                responseModel.matchContestlist as ArrayList<ContestsParentModels>?
                            setupViewPager(mBinding!!.viewpagerContest)
                        } else {
                            MyUtils.showToast(
                                this@MoreContestActivity,
                                "No Contest available for this match " + res.toString()
                            )
                        }
                    }
                }
            })
    }

    override fun onBitmapSelected(bitmap: Bitmap) {

    }

    override fun onUploadedImageUrl(url: String) {


    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    private fun setupViewPager(viewPager: ViewPager) {


        val adapter = ViewPagerAdapter(supportFragmentManager)
        viewPager.removeAllViews()
        for (i in 0..allContestList!!.size - 1) {
            val clobject = allContestList!!.get(i)
            val bundle = Bundle()
            bundle.putSerializable(ContestActivity.SERIALIZABLE_KEY_MATCH_OBJECT, matchObject)
            bundle.putSerializable(MoreContestFragment.CONTEST_LIST, clobject.allContestsRunning)
            adapter.addFragment(
                MoreContestFragment.newInstance(bundle),
                clobject.contestTitle + "(" + clobject.allContestsRunning!!.size + ")"
            )

            if (selectedObject.contestTitle.equals(clobject.contestTitle)) {
                selected_position = i
            }
        }
        viewPager.adapter = adapter
        viewPager.currentItem = selected_position

    }

    internal inner class ViewPagerAdapter(manager: FragmentManager) :
        FragmentPagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }
    }

    override fun onMyContest(contestObjects: ArrayList<ContestModelLists>) {
        this.contestObjects = contestObjects
        if (matchObject!!.status == BindingUtils.MATCH_STATUS_UPCOMING) {
            mBinding!!.tabs.getTabAt(1)!!.text =
                String.format("My Contest(%d)", contestObjects.size)
        } else {
            mBinding!!.tabs.getTabAt(0)!!.text =
                String.format("My Contest(%d)", contestObjects.size)
        }
    }

    override fun onMyTeam(objects: ArrayList<MyTeamModels>) {
        this.joinedTeamList = objects
        if (matchObject!!.status == BindingUtils.MATCH_STATUS_UPCOMING) {
            mBinding!!.tabs.getTabAt(2)!!.text =
                String.format("MyTeam(%d)", this.joinedTeamList!!.size)
        } else {
            mBinding!!.tabs.getTabAt(1)!!.text =
                String.format("MyTeam(%d)", this.joinedTeamList!!.size)
        }
    }

    override fun onContestJoinning(objects: ContestModelLists, position: Int) {
        customeProgressDialog.show()
        val models = RequestModel()
        models.user_id = MyPreferences.getUserID(this)!!
        //  models.token =MyPreferences.getToken(this)!!
        models.match_id = "" + matchObject!!.matchId
        models.contest_id = "" + objects.id
        if (!MyUtils.isConnectedWithInternet(this)) {
            MyUtils.showToast(this, "No Internet connection found")
            return
        }
        WebServiceClient(this).client.create(IApiMethod::class.java).joinNewContestStatus(models)
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
                    if (res != null) {
                        if (!res.status) {
                            if (res.code == 401) {
                                MyUtils.showToast(
                                    this@MoreContestActivity,
                                    res.message
                                )
                            } else {
                                MyUtils.showMessage(
                                    this@MoreContestActivity,
                                    res.message
                                )
                            }
                        } else {
                            if (res.actionForTeam == 1) {
                                val intent =
                                    Intent(this@MoreContestActivity, CreateTeamActivity::class.java)
                                intent.putExtra(
                                    CreateTeamActivity.SERIALIZABLE_MATCH_KEY,
                                    matchObject
                                )
                                startActivityForResult(
                                    intent,
                                    CreateTeamActivity.CREATETEAM_REQUESTCODE
                                )
                            } else if (res.actionForTeam == 2) {
                                val intent =
                                    Intent(this@MoreContestActivity, SelectTeamActivity::class.java)
                                intent.putExtra(
                                    CreateTeamActivity.SERIALIZABLE_MATCH_KEY,
                                    matchObject
                                )
                                intent.putExtra(
                                    CreateTeamActivity.SERIALIZABLE_CONTEST_KEY,
                                    objects
                                )
                                intent.putExtra(
                                    CreateTeamActivity.SERIALIZABLE_SELECTED_TEAMS,
                                    res.selectedTeamModel
                                )
                                startActivityForResult(
                                    intent,
                                    CreateTeamActivity.CREATETEAM_REQUESTCODE
                                )
                            } else {
                                Toast.makeText(
                                    this@MoreContestActivity,
                                    res.message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            })
    }

    override fun onShareContest(objects: ContestModelLists) {

    }

    override fun onPageScrollStateChanged(state: Int) {


    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        selected_position = position
    }


}
