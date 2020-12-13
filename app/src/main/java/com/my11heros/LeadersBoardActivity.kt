package com.my11heros

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.edify.atrist.listener.OnMatchTimerStarted
import com.my11heros.databinding.ActivityLeadersBoardBinding
import com.my11heros.models.PlayerModels
import com.my11heros.models.UpcomingMatchesModel
import com.my11heros.network.IApiMethod
import com.my11heros.network.RequestModel
import com.my11heros.network.WebServiceClient
import com.my11heros.ui.BaseActivity
import com.my11heros.ui.contest.models.ContestModelLists
import com.my11heros.ui.home.models.UsersPostDBResponse
import com.my11heros.ui.leadersboard.LeadersBoardFragment
import com.my11heros.ui.leadersboard.PrizeBreakupFragment
import com.my11heros.utils.BindingUtils
import com.my11heros.utils.MyPreferences
import com.my11heros.utils.MyUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class LeadersBoardActivity : BaseActivity() {

    var mainHandler: Handler? = Handler()
    private var playersList: PlayerModels? = null
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    var contestObject: ContestModelLists? = null
    var matchObject: UpcomingMatchesModel? = null
    private var mBinding: ActivityLeadersBoardBinding? = null
    private val updateScoresHandler = object : Runnable {
        override fun run() {
            Log.d("leadersboard", "hitting to server")
            if (!isFinishing) {
                updateScores()
                mainHandler!!.postDelayed(this, 60000)
            }
        }
    }


    companion object {
        var CREATETEAM_REQUESTCODE: Int = 2001
        var REFRESH_TIME: Int = 60000

        val SERIALIZABLE_MATCH_KEY: String = "matchObject"
        val SERIALIZABLE_CONTEST_KEY: String = "contest"

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_leaders_board
        )
        contestObject = intent.getSerializableExtra(SERIALIZABLE_CONTEST_KEY) as ContestModelLists
        matchObject = intent.getSerializableExtra(SERIALIZABLE_MATCH_KEY) as UpcomingMatchesModel

        mBinding!!.imgFantasyPoints.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@LeadersBoardActivity, WebActivity::class.java)
            intent.putExtra(WebActivity.KEY_TITLE, BindingUtils.WEB_TITLE_FANTASY_POINTS)
            intent.putExtra(WebActivity.KEY_URL, BindingUtils.WEBVIEW_FANTASY_POINTS)
            if (Build.VERSION.SDK_INT > 20) {
                val options = ActivityOptions.makeSceneTransitionAnimation(this)
                startActivity(intent, options.toBundle())
            } else {
                startActivity(intent)
            }
        })

        mBinding!!.imageBack.setOnClickListener(View.OnClickListener {
            finish()
        })

        if (matchObject!!.status == BindingUtils.MATCH_STATUS_UPCOMING) {
            mBinding!!.includeContestRow.linearTradesStatus.visibility = View.VISIBLE
            mBinding!!.includeLiveMatchRow.liveMatchesRow.visibility = View.GONE
            initUpcomingMatchData()
        } else {
            mBinding!!.includeContestRow.linearTradesStatus.visibility = View.GONE
            mBinding!!.includeLiveMatchRow.liveMatchesRow.visibility = View.VISIBLE
            customeProgressDialog.show()
            initScoreCard()
        }

        setupViewPager(mBinding!!.viewpager)
        mBinding!!.tabs.setupWithViewPager(mBinding!!.viewpager)
        initContestDetails()
    }

    override fun onResume() {
        super.onResume()
        if (matchObject!!.status == BindingUtils.MATCH_STATUS_UPCOMING) {
            startCountDown()
        } else {
            updateTimerHeader()
        }
        if (matchObject!!.status.equals(BindingUtils.MATCH_STATUS_LIVE)) {
            mainHandler!!.post(updateScoresHandler)
        }
    }

    private fun updateTimerHeader() {
        mBinding!!.matchTimer.text = matchObject!!.statusString.toUpperCase(Locale.ENGLISH)
        if (matchObject!!.status == BindingUtils.MATCH_STATUS_UPCOMING) {
            mBinding!!.tournamentTitle.text = matchObject!!.leagueTitle
        } else {
            mBinding!!.tournamentTitle.text = matchObject!!.matchTitle
        }

        Glide.with(this)
            .load(matchObject!!.teamAInfo!!.logoUrl)
            .placeholder(R.drawable.placeholder_player_teama)
            .disallowHardwareConfig()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(mBinding!!.teamaLogo)

        Glide.with(this)
            .load(matchObject!!.teamBInfo!!.logoUrl)
            .placeholder(R.drawable.placeholder_player_teama)
            .disallowHardwareConfig()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(mBinding!!.teambLogo)
    }

    private fun startCountDown() {
        BindingUtils.logD("TimerLogs", "initViewUpcomingMatches() called in ContestActivity")
        // matchObject!!.timestampStart = 1591158573 + 300
        BindingUtils.countDownStart(matchObject!!.timestampStart, object : OnMatchTimerStarted {
            override fun onTimeFinished() {
                updateTimerHeader()
                if (matchObject!!.status.equals(BindingUtils.MATCH_STATUS_UPCOMING)) {
                    showMatchTimeUpDialog()
                }
            }

            override fun onTicks(time: String) {
                mBinding!!.matchTimer.text = time
            }

        })

    }

    fun pauseCountDown() {
        BindingUtils.stopTimer()
    }


    private fun initContestDetails() {
        mBinding!!.includeLiveMatchRow.contestPrizePool.text =
            String.format("%s%s", "₹", contestObject!!.totalWinningPrize)
        mBinding!!.includeLiveMatchRow.contestSpots.text =
            String.format("%d", contestObject!!.totalSpots)
        mBinding!!.includeLiveMatchRow.contestEntryPrize.text =
            String.format("%s%s", "₹", contestObject!!.entryFees)
    }

    private fun initScoreCard() {
        mBinding!!.teamsa.text = matchObject!!.teamAInfo!!.teamShortName
        mBinding!!.teamsb.text = matchObject!!.teamBInfo!!.teamShortName
        Glide.with(this)
            .load(matchObject!!.teamAInfo!!.logoUrl)
            .placeholder(R.drawable.placeholder_player_teama)
            .into(mBinding!!.includeLiveMatchRow.imgTeamaLogo)

        Glide.with(this)
            .load(matchObject!!.teamBInfo!!.logoUrl)
            .placeholder(R.drawable.placeholder_player_teama)
            .into(mBinding!!.includeLiveMatchRow.imgTeambLogo)

        mBinding!!.matchTimer.text = matchObject!!.statusString.toUpperCase(Locale.ENGLISH)
        mBinding!!.matchTimer.setTextColor(resources.getColor(R.color.white))

        mBinding!!.includeLiveMatchRow.teamAName.text = matchObject!!.teamAInfo!!.teamShortName
        mBinding!!.includeLiveMatchRow.teamBName.text = matchObject!!.teamBInfo!!.teamShortName

        mBinding!!.includeLiveMatchRow.teamAScore.text = "0-0"
        mBinding!!.includeLiveMatchRow.teamAOver.text = "(0)"

        mBinding!!.includeLiveMatchRow.teamBScore.text = "0-0"
        mBinding!!.includeLiveMatchRow.teamBOver.text = "0-0"

        if (matchObject!!.status == BindingUtils.MATCH_STATUS_UPCOMING) {
            mBinding!!.tournamentTitle.text = matchObject!!.leagueTitle
        } else {
            mBinding!!.tournamentTitle.text = matchObject!!.matchTitle
        }

        Glide.with(this)
            .load(matchObject!!.teamAInfo!!.logoUrl)
            .placeholder(R.drawable.placeholder_player_teama)
            .disallowHardwareConfig()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(mBinding!!.teamaLogo)

        Glide.with(this)
            .load(matchObject!!.teamBInfo!!.logoUrl)
            .placeholder(R.drawable.placeholder_player_teama)
            .disallowHardwareConfig()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(mBinding!!.teambLogo)

        updateScores()
    }


    override fun onPause() {
        super.onPause()
        pauseCountDown()
        if (matchObject!!.status.equals(BindingUtils.MATCH_STATUS_LIVE)) {
            mainHandler!!.removeCallbacks(updateScoresHandler)
        }
    }


    fun updateScores() {
        val models = RequestModel()
        models.user_id = MyPreferences.getUserID(this)!!
        // models.token = MyPreferences.getToken(this)!!
        models.contest_id = "" + contestObject!!.id
        models.match_id = "" + matchObject!!.matchId

        WebServiceClient(this).client.create(IApiMethod::class.java).getScore(models)
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
                        if (res.scoresModel != null) {
                            if (res.sessionExpired) {
                                logoutApp("Session Expired Please login again!!", false)
                            } else {
                                mBinding!!.includeLiveMatchRow.statusNote.text =
                                    res.scoresModel!!.statusNote
                                if (res.scoresModel!!.teama!!.scores != null) {
                                    mBinding!!.includeLiveMatchRow.teamAScore.text =
                                        res.scoresModel!!.teama!!.scores
                                } else {
                                    mBinding!!.includeLiveMatchRow.teamAScore.text = ""
                                }

                                if (res.scoresModel!!.teama!!.overs != null) {
                                    mBinding!!.includeLiveMatchRow.teamAOver.text =
                                        String.format("(%s)", res.scoresModel!!.teama!!.overs)
                                } else {
                                    mBinding!!.includeLiveMatchRow.teamAOver.text =
                                        String.format("(%s)", "")
                                }


                                mBinding!!.includeLiveMatchRow.teamBScore.text =
                                    res.scoresModel!!.teamb!!.scores
                                mBinding!!.includeLiveMatchRow.teamBOver.text =
                                    String.format("(%s)", res.scoresModel!!.teamb!!.overs)

                                val fragment: Fragment =
                                    viewPagerAdapter.getItem(mBinding!!.viewpager.currentItem)
                                if (fragment != null) {
                                    if (fragment is LeadersBoardFragment) {
                                        fragment.getLeadersBoards()
                                    }
                                }
                            }
                        }
                    }
                }
            })
    }


    private fun initUpcomingMatchData() {
        mBinding!!.teamsa.text = matchObject!!.teamAInfo!!.teamShortName
        mBinding!!.teamsb.text = matchObject!!.teamBInfo!!.teamShortName

        if (matchObject!!.status == BindingUtils.MATCH_STATUS_UPCOMING) {
            mBinding!!.tournamentTitle.text = matchObject!!.leagueTitle
        } else {
            mBinding!!.tournamentTitle.text = matchObject!!.matchTitle
        }

        Glide.with(this)
            .load(matchObject!!.teamAInfo!!.logoUrl)
            .placeholder(R.drawable.placeholder_player_teama)
            .disallowHardwareConfig()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(mBinding!!.teamaLogo)

        Glide.with(this)
            .load(matchObject!!.teamBInfo!!.logoUrl)
            .placeholder(R.drawable.placeholder_player_teama)
            .disallowHardwareConfig()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(mBinding!!.teambLogo)

        val totalSpots = contestObject!!.totalSpots
        val filledSpots = contestObject!!.filledSpots
        mBinding!!.includeContestRow.contestPrizePool.text =
            String.format("%s%s", "₹ ", contestObject!!.totalWinningPrize)

        if (contestObject!!.entryFees.toInt() == 0 && contestObject!!.winnerCounts!!.toInt() > 0) {
            mBinding!!.includeContestRow.contestEntryPrize.text = "Free"
        } else if (contestObject!!.entryFees.toInt() == 0 && contestObject!!.winnerCounts!!.toInt() == 0) {
            mBinding!!.includeContestRow.contestEntryPrize.text = "Join"
        } else {
            mBinding!!.includeContestRow.contestEntryPrize.text =
                String.format("%s%s", "₹", contestObject!!.entryFees)
        }

        if (totalSpots == 0) {
            mBinding!!.includeContestRow.contestProgress.max =
                filledSpots + BindingUtils.UNLIMITED_SPOT_MARGIN
            mBinding!!.includeContestRow.contestProgress.progress = filledSpots
            mBinding!!.includeContestRow.totalSpot.text = String.format("Unlimited spots")
            mBinding!!.includeContestRow.totalSpotLeft.text =
                String.format("%d spots filled", filledSpots)

        } else {
            mBinding!!.includeContestRow.contestProgress.max = totalSpots
            mBinding!!.includeContestRow.contestProgress.progress = filledSpots
            mBinding!!.includeContestRow.totalSpot.text = String.format("%d spots", totalSpots)
            if (contestObject!!.totalSpots == contestObject!!.filledSpots) {
                mBinding!!.includeContestRow.totalSpotLeft.text = "Contest Full"
                mBinding!!.includeContestRow.totalSpotLeft.setTextColor(Color.RED)
            } else {
                mBinding!!.includeContestRow.totalSpotLeft.text =
                    String.format("%d spots left", (totalSpots - filledSpots))
            }
        }

        if (contestObject!!.usableBonus.toInt() == 0) {
            mBinding!!.includeContestRow.linearBonues.visibility = View.GONE
        } else {
            mBinding!!.includeContestRow.linearBonues.visibility = View.VISIBLE
            mBinding!!.includeContestRow.contestBonus.text =
                String.format("%s%s", contestObject!!.usableBonus, "%")
        }
        mBinding!!.includeContestRow.contestEntryPrize.text =
            String.format("%s%s", "₹", contestObject!!.entryFees)
        mBinding!!.includeContestRow.firstPrize.text =
            String.format("%s%s", "₹", contestObject!!.firstPrice)
        mBinding!!.includeContestRow.winningPercentage.text =
            "" + contestObject!!.winnerCounts//String.format("%d%s",contestObject!!.winnerPercentage,"%")
        // mBinding!!.includeContestRow.maxAllowedTeam.text = String.format("%s %d %s","Upto",contestObject!!.maxAllowedTeam,"teams")
        if (contestObject!!.cancellation) {
            mBinding!!.includeContestRow.contestCancellation.visibility = View.INVISIBLE
        } else {
            mBinding!!.includeContestRow.contestCancellation.visibility = View.VISIBLE
        }
//        if(contestObject!!.maxAllowedTeam>1){
//            mBinding!!.includeContestRow.contestMultiplayer.text = "M"
//            mBinding!!.includeContestRow.contestMultiplayer.visibility = View.VISIBLE
//        }else {
//            mBinding!!.includeContestRow.contestMultiplayer.text = "S"
//            mBinding!!.includeContestRow.contestMultiplayer.visibility = View.GONE
//        }
        if (contestObject!!.maxAllowedTeam > 1) {
            mBinding!!.includeContestRow.allowedTeamType.text = "M"
            mBinding!!.includeContestRow.contestMultiplayer.text =
                "" + contestObject!!.maxAllowedTeam
        } else {
            mBinding!!.includeContestRow.allowedTeamType.text = "S"
            mBinding!!.includeContestRow.contestMultiplayer.text =
                "" + contestObject!!.maxAllowedTeam
        }
        mBinding!!.includeContestRow.contestBonus.text =
            String.format("%s%s", contestObject!!.usableBonus, "%")

        mBinding!!.includeContestRow.contestEntryPrize.setOnClickListener(View.OnClickListener {
            if (!MyUtils.isConnectedWithInternet(this)) {
                MyUtils.showToast(this, "No Internet connection found")
            } else {
                customeProgressDialog.show()
                val models = RequestModel()
                models.user_id = MyPreferences.getUserID(this)!!
                // models.token =MyPreferences.getToken(this)!!
                models.match_id = "" + matchObject!!.matchId
                models.contest_id = "" + contestObject!!.id

                WebServiceClient(this).client.create(IApiMethod::class.java)
                    .joinNewContestStatus(models)
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
                                            this@LeadersBoardActivity,
                                            res.message
                                        )
                                    } else {
                                        MyUtils.showMessage(
                                            this@LeadersBoardActivity,
                                            res.message
                                        )
                                    }
                                } else {
                                    if (res.actionForTeam == 1) {
                                        val intent = Intent(
                                            this@LeadersBoardActivity,
                                            CreateTeamActivity::class.java
                                        )
                                        intent.putExtra(
                                            CreateTeamActivity.SERIALIZABLE_MATCH_KEY,
                                            matchObject
                                        )
                                        startActivityForResult(
                                            intent,
                                            CreateTeamActivity.CREATETEAM_REQUESTCODE
                                        )
                                    } else if (res.actionForTeam == 2) {
                                        val intent = Intent(
                                            this@LeadersBoardActivity,
                                            SelectTeamActivity::class.java
                                        )
                                        intent.putExtra(
                                            CreateTeamActivity.SERIALIZABLE_MATCH_KEY,
                                            matchObject
                                        )
                                        intent.putExtra(
                                            CreateTeamActivity.SERIALIZABLE_CONTEST_KEY,
                                            contestObject
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
                                            this@LeadersBoardActivity,
                                            res.message,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }
                        }
                    })
            }
        })
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (CREATETEAM_REQUESTCODE == requestCode && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun onBitmapSelected(bitmap: Bitmap) {
        //TODO("Not yet implemented")
    }

    override fun onUploadedImageUrl(url: String) {

    }

    private fun setupViewPager(
        viewPager: ViewPager
    ) {
        this.playersList = playersList

        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        val bundle = Bundle()
        bundle.putSerializable(ContestActivity.SERIALIZABLE_KEY_CONTEST_OBJECT, contestObject)
        bundle.putSerializable(ContestActivity.SERIALIZABLE_KEY_MATCH_OBJECT, matchObject)

        viewPagerAdapter.addFragment(PrizeBreakupFragment.newInstance(bundle), "Prize Breakup")
        viewPagerAdapter.addFragment(LeadersBoardFragment.newInstance(bundle), "Leaderboard")
        // viewPagerAdapter.addFragment(ContestStatsFragment(matchObject!!), "Leaderboard")
        viewPager.adapter = viewPagerAdapter
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
}