package com.my11heros

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.edify.atrist.listener.OnMatchTimerStarted
import com.edify.atrist.listener.OnTeamCreateListener
import com.my11heros.databinding.ActivityCreateTeamBinding
import com.my11heros.models.MyTeamModels
import com.my11heros.models.PlayerModels
import com.my11heros.models.UpcomingMatchesModel
import com.my11heros.network.IApiMethod
import com.my11heros.network.RequestModel
import com.my11heros.network.WebServiceClient
import com.my11heros.ui.BaseActivity
import com.my11heros.ui.contest.MyTeamFragment
import com.my11heros.ui.contest.MyTeamFragment.Companion.SERIALIZABLE_COPY_TEAM
import com.my11heros.ui.contest.MyTeamFragment.Companion.SERIALIZABLE_EDIT_TEAM
import com.my11heros.ui.createteam.models.PlayersInfoModel
import com.my11heros.ui.home.models.UsersPostDBResponse
import com.my11heros.utils.BindingUtils
import com.my11heros.utils.MyPreferences
import com.my11heros.utils.MyUtils
import plug.cricket.ui.createteam.AllRounder
import plug.cricket.ui.createteam.Batsman
import plug.cricket.ui.createteam.Bowlers
import plug.cricket.ui.createteam.WicketKeepers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CreateTeamActivity : BaseActivity(), OnTeamCreateListener {


    private var playersList: PlayerModels? = null

    // private var isMatchLive: Boolean = false
    var myTeamModel: MyTeamModels? = null

    // private var playersList: PlayerModels? = null
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    var matchObject: UpcomingMatchesModel? = null
    var crateTeamHashMap: HashMap<String, ArrayList<PlayersInfoModel>> =
        HashMap<String, ArrayList<PlayersInfoModel>>()
    private var mBinding: ActivityCreateTeamBinding? = null

    companion object {

        var CREATETEAM_REQUESTCODE: Int = 2001
        val SERIALIZABLE_MATCH_KEY: String = "matchObject"
        val SERIALIZABLE_CONTEST_KEY: String = "contest"
        val SERIALIZABLE_SELECTED_TEAMS: String = "selected_teams"
        val SERIALIZABLE_KEY_PLAYERS: String = "playerslist"
        val CREATE_TEAM_WICKET_KEEPER: String = "wk"
        val CREATE_TEAM_BATSMAN: String = "bat"
        val CREATE_TEAM_ALLROUNDER: String = "all"
        val CREATE_TEAM_BOWLER: String = "bow"

        var isEditMode: Boolean = false
        var isCopyTeam: Boolean = false
        val MAX_WICKET_KEEPER: IntArray = intArrayOf(1, 4)
        val MAX_BATSMAN: IntArray = intArrayOf(1, 6)
        val MAX_ALL_ROUNDER: IntArray = intArrayOf(1, 6)
        val MAX_BOWLER: IntArray = intArrayOf(1, 6)
        val MAX_PLAYERS_CRICKET: Int = 11
        val MAX_PLAYERS_FROM_TEAM: Int = 7

        var TEAMA: Int = 0
        var TEAMB: Int = 0
        var teamAId = 0
        var teamBId = 0

        var COUNT_WICKET_KEEPER: Int = 0
        var COUNT_BATS_MAN: Int = 0
        var COUNT_ALL_ROUNDER: Int = 0
        var COUNT_BOWLER: Int = 0
        var isAllPlayersSelected: Boolean? = false
        var totalPlayers: Int = 0

        var WANT_WK: Int = 1
        var WANT_BAT: Int = 2
        var WANT_ALL: Int = 3
        var WANT_BOWL: Int = 4

        var isSortBySelectionActive: Boolean? = false
        var isSortBySelectionActiveDecending: Boolean? = false

        var isSortByPointsActive: Boolean? = false
        var isSortByPointsActiveDecending: Boolean? = false

        var isSortByCreditsActive: Boolean? = false
        var isSortByCreditsActiveDecending: Boolean? = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_create_team
        )
        isEditMode = false
        isCopyTeam = false
        ressetPlayers()
        updatePlayersCountBar(0)
        matchObject = intent.getSerializableExtra(SERIALIZABLE_MATCH_KEY) as UpcomingMatchesModel
        if (intent.hasExtra(SERIALIZABLE_EDIT_TEAM)) {
            isEditMode = true
            isCopyTeam = false
            myTeamModel = intent.getSerializableExtra(SERIALIZABLE_EDIT_TEAM) as MyTeamModels
        } else if (intent.hasExtra(SERIALIZABLE_COPY_TEAM)) {
            isEditMode = true
            isCopyTeam = true
            myTeamModel = intent.getSerializableExtra(SERIALIZABLE_COPY_TEAM) as MyTeamModels
        }
        teamAId = matchObject!!.teamAInfo!!.teamId
        teamBId = matchObject!!.teamBInfo!!.teamId

        mBinding!!.imageBack.setOnClickListener(View.OnClickListener {
            finish()
        })

        mBinding!!.teamPreview.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@CreateTeamActivity, TeamPreviewActivity::class.java)
            intent.putExtra(SERIALIZABLE_MATCH_KEY, matchObject)
            intent.putExtra(TeamPreviewActivity.SERIALIZABLE_TEAM_PREVIEW_KEY, crateTeamHashMap)
            startActivity(intent)
        })

        mBinding!!.teamContinue.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@CreateTeamActivity, SaveTeamActivity::class.java)
            intent.putExtra(SERIALIZABLE_MATCH_KEY, matchObject)
            intent.putExtra(TeamPreviewActivity.SERIALIZABLE_TEAM_PREVIEW_KEY, crateTeamHashMap)

            if (myTeamModel != null) {
                if (isCopyTeam) {
                    myTeamModel!!.teamId!!.teamId = 0
                }
                intent.putExtra(MyTeamFragment.SERIALIZABLE_EDIT_TEAM, myTeamModel)
            }
            startActivityForResult(intent, CREATETEAM_REQUESTCODE)
        })

        mBinding!!.imgWallet.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@CreateTeamActivity, MyBalanceActivity::class.java)
            startActivity(intent)
        })

        mBinding!!.fantasyPoints.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@CreateTeamActivity, WebActivity::class.java)
            intent.putExtra(WebActivity.KEY_TITLE, BindingUtils.WEB_TITLE_PRIVACY_POLICY)
            intent.putExtra(WebActivity.KEY_URL, BindingUtils.WEBVIEW_PRIVACY)
            val options =
                ActivityOptions.makeSceneTransitionAnimation(this)
            startActivity(intent, options.toBundle())
        })

        mBinding!!.clearAllPlayer.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this@CreateTeamActivity)
            //set title for alert dialog
            // builder.setTitle("Warning")
            //set message for alert dialog
            builder.setMessage("Do You want to clear all selected players ?")
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            //performing positive action
            builder.setPositiveButton("OK") { dialogInterface, which ->
                ressetPlayers()
                initViewPager()
            }
            builder.setNegativeButton("Cancel") { dialogInterface, which ->

            }
            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false)
            alertDialog.setCanceledOnTouchOutside(false)
            alertDialog.show()
        })

        mBinding!!.teamaName.text = matchObject!!.teamAInfo!!.teamShortName
        mBinding!!.teambName.text = matchObject!!.teamBInfo!!.teamShortName
        Glide.with(this)
            .load(matchObject!!.teamAInfo!!.logoUrl)
            .placeholder(R.drawable.placeholder_player_teama)
            .into(mBinding!!.teamaLogo)


        Glide.with(this)
            .load(matchObject!!.teamBInfo!!.logoUrl)
            .placeholder(R.drawable.placeholder_player_teama)
            .into(mBinding!!.teambLogo)

        if (MyUtils.isConnectedWithInternet(this)) {
            showLoading()
            getAllPlayers()
        } else {
            MyUtils.showToast(this, "No Internet connection found")
        }

    }

    private fun updatePlayersCountBar(count: Int) {
        when (count) {
            0 -> {
                mBinding!!.clearAllPlayer.isEnabled = false
                mBinding!!.playerSelected1.text = ""
                mBinding!!.playerSelected1.setBackgroundResource(R.drawable.rectangle_left_top_curve)

                mBinding!!.playerSelected2.text = ""
                mBinding!!.playerSelected2.setBackgroundResource(R.drawable.rectangle_no_curve)

                mBinding!!.playerSelected3.text = ""
                mBinding!!.playerSelected3.setBackgroundResource(R.drawable.rectangle_no_curve)

                mBinding!!.playerSelected4.text = ""
                mBinding!!.playerSelected4.setBackgroundResource(R.drawable.rectangle_no_curve)

                mBinding!!.playerSelected5.text = ""
                mBinding!!.playerSelected5.setBackgroundResource(R.drawable.rectangle_no_curve)

                mBinding!!.playerSelected6.text = ""
                mBinding!!.playerSelected6.setBackgroundResource(R.drawable.rectangle_no_curve)

                mBinding!!.playerSelected7.text = ""
                mBinding!!.playerSelected7.setBackgroundResource(R.drawable.rectangle_no_curve)

                mBinding!!.playerSelected8.text = ""
                mBinding!!.playerSelected8.setBackgroundResource(R.drawable.rectangle_no_curve)

                mBinding!!.playerSelected9.text = ""
                mBinding!!.playerSelected9.setBackgroundResource(R.drawable.rectangle_no_curve)

                mBinding!!.playerSelected10.text = ""
                mBinding!!.playerSelected10.setBackgroundResource(R.drawable.rectangle_no_curve)

                mBinding!!.playerSelected11.text = "11"
                mBinding!!.playerSelected11.setBackgroundResource(R.drawable.rectangle_right_top_curve)
                mBinding!!.playerSelected11.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )
            }
            1 -> {
                mBinding!!.clearAllPlayer.isEnabled = true
                mBinding!!.playerSelected1.text = "1"
                mBinding!!.playerSelected1.setBackgroundResource(R.drawable.rectangle_left_top_curve_filled)
                mBinding!!.playerSelected1.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected2.text = ""
                mBinding!!.playerSelected2.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected2.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected3.text = ""
                mBinding!!.playerSelected3.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected3.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected4.text = ""
                mBinding!!.playerSelected4.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected4.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected5.text = ""
                mBinding!!.playerSelected5.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected5.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected6.text = ""
                mBinding!!.playerSelected6.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected6.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected7.text = ""
                mBinding!!.playerSelected7.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected7.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected8.text = ""
                mBinding!!.playerSelected8.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected8.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected9.text = ""
                mBinding!!.playerSelected9.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected9.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected10.text = ""
                mBinding!!.playerSelected10.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected10.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )

                mBinding!!.playerSelected11.text = "11"
                mBinding!!.playerSelected11.setBackgroundResource(R.drawable.rectangle_right_top_curve)
                mBinding!!.playerSelected11.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )
            }
            2 -> {
                mBinding!!.playerSelected1.text = "1"
                mBinding!!.playerSelected1.setBackgroundResource(R.drawable.rectangle_left_top_curve_filled)
                mBinding!!.playerSelected1.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected2.text = "2"
                mBinding!!.playerSelected2.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected2.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected3.text = ""
                mBinding!!.playerSelected3.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected3.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected4.text = ""
                mBinding!!.playerSelected4.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected4.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected5.text = ""
                mBinding!!.playerSelected5.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected5.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected6.text = ""
                mBinding!!.playerSelected6.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected6.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected7.text = ""
                mBinding!!.playerSelected7.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected7.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected8.text = ""
                mBinding!!.playerSelected8.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected8.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected9.text = ""
                mBinding!!.playerSelected9.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected9.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected10.text = ""
                mBinding!!.playerSelected10.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected10.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )

                mBinding!!.playerSelected11.text = "11"
                mBinding!!.playerSelected11.setBackgroundResource(R.drawable.rectangle_right_top_curve)
                mBinding!!.playerSelected11.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )

            }
            3 -> {
                mBinding!!.playerSelected1.text = "1"
                mBinding!!.playerSelected1.setBackgroundResource(R.drawable.rectangle_left_top_curve_filled)
                mBinding!!.playerSelected1.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected2.text = "2"
                mBinding!!.playerSelected2.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected2.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected3.text = "3"
                mBinding!!.playerSelected3.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected3.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected4.text = ""
                mBinding!!.playerSelected4.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected4.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected5.text = ""
                mBinding!!.playerSelected5.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected5.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected6.text = ""
                mBinding!!.playerSelected6.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected6.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected7.text = ""
                mBinding!!.playerSelected7.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected7.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected8.text = ""
                mBinding!!.playerSelected8.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected8.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected9.text = ""
                mBinding!!.playerSelected9.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected9.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected10.text = ""
                mBinding!!.playerSelected10.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected10.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )

                mBinding!!.playerSelected11.text = "11"
                mBinding!!.playerSelected11.setBackgroundResource(R.drawable.rectangle_right_top_curve)
                mBinding!!.playerSelected11.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )
            }
            4 -> {
                mBinding!!.playerSelected1.text = "1"
                mBinding!!.playerSelected1.setBackgroundResource(R.drawable.rectangle_left_top_curve_filled)
                mBinding!!.playerSelected1.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected2.text = "2"
                mBinding!!.playerSelected2.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected2.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected3.text = "3"
                mBinding!!.playerSelected3.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected3.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected4.text = "4"
                mBinding!!.playerSelected4.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected4.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected5.text = ""
                mBinding!!.playerSelected5.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected5.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected6.text = ""
                mBinding!!.playerSelected6.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected6.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected7.text = ""
                mBinding!!.playerSelected7.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected7.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected8.text = ""
                mBinding!!.playerSelected8.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected8.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected9.text = ""
                mBinding!!.playerSelected9.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected9.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected10.text = ""
                mBinding!!.playerSelected10.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected10.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )

                mBinding!!.playerSelected11.text = "11"
                mBinding!!.playerSelected11.setBackgroundResource(R.drawable.rectangle_right_top_curve)
                mBinding!!.playerSelected11.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )

            }
            5 -> {
                mBinding!!.playerSelected1.text = "1"
                mBinding!!.playerSelected1.setBackgroundResource(R.drawable.rectangle_left_top_curve_filled)
                mBinding!!.playerSelected1.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected2.text = "2"
                mBinding!!.playerSelected2.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected2.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected3.text = "3"
                mBinding!!.playerSelected3.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected3.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected4.text = "4"
                mBinding!!.playerSelected4.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected4.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected5.text = "5"
                mBinding!!.playerSelected5.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected5.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected6.text = ""
                mBinding!!.playerSelected6.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected6.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected7.text = ""
                mBinding!!.playerSelected7.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected7.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected8.text = ""
                mBinding!!.playerSelected8.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected8.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected9.text = ""
                mBinding!!.playerSelected9.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected9.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected10.text = ""
                mBinding!!.playerSelected10.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected10.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )

                mBinding!!.playerSelected11.text = "11"
                mBinding!!.playerSelected11.setBackgroundResource(R.drawable.rectangle_right_top_curve)
                mBinding!!.playerSelected11.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )
            }
            6 -> {
                mBinding!!.playerSelected1.text = "1"
                mBinding!!.playerSelected1.setBackgroundResource(R.drawable.rectangle_left_top_curve_filled)
                mBinding!!.playerSelected1.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected2.text = "2"
                mBinding!!.playerSelected2.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected2.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected3.text = "3"
                mBinding!!.playerSelected3.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected3.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected4.text = "4"
                mBinding!!.playerSelected4.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected4.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected5.text = "5"
                mBinding!!.playerSelected5.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected5.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected6.text = "6"
                mBinding!!.playerSelected6.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected6.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected7.text = ""
                mBinding!!.playerSelected7.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected7.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected8.text = ""
                mBinding!!.playerSelected8.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected8.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected9.text = ""
                mBinding!!.playerSelected9.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected9.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected10.text = ""
                mBinding!!.playerSelected10.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected10.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )

                mBinding!!.playerSelected11.text = "11"
                mBinding!!.playerSelected11.setBackgroundResource(R.drawable.rectangle_right_top_curve)
                mBinding!!.playerSelected11.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )
            }
            7 -> {
                mBinding!!.playerSelected1.text = "1"
                mBinding!!.playerSelected1.setBackgroundResource(R.drawable.rectangle_left_top_curve_filled)
                mBinding!!.playerSelected1.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected2.text = "2"
                mBinding!!.playerSelected2.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected2.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected3.text = "3"
                mBinding!!.playerSelected3.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected3.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected4.text = "4"
                mBinding!!.playerSelected4.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected4.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected5.text = "5"
                mBinding!!.playerSelected5.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected5.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected6.text = "6"
                mBinding!!.playerSelected6.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected6.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected7.text = "7"
                mBinding!!.playerSelected7.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected7.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected8.text = ""
                mBinding!!.playerSelected8.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected8.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected9.text = ""
                mBinding!!.playerSelected9.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected9.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected10.text = ""
                mBinding!!.playerSelected10.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected10.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )

                mBinding!!.playerSelected11.text = "11"
                mBinding!!.playerSelected11.setBackgroundResource(R.drawable.rectangle_right_top_curve)
                mBinding!!.playerSelected11.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )

            }
            8 -> {
                mBinding!!.playerSelected1.text = "1"
                mBinding!!.playerSelected1.setBackgroundResource(R.drawable.rectangle_left_top_curve_filled)
                mBinding!!.playerSelected1.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected2.text = "2"
                mBinding!!.playerSelected2.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected2.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected3.text = "3"
                mBinding!!.playerSelected3.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected3.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected4.text = "4"
                mBinding!!.playerSelected4.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected4.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected5.text = "5"
                mBinding!!.playerSelected5.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected5.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected6.text = "6"
                mBinding!!.playerSelected6.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected6.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected7.text = "7"
                mBinding!!.playerSelected7.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected7.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected8.text = "8"
                mBinding!!.playerSelected8.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected8.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected9.text = ""
                mBinding!!.playerSelected9.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected9.setTextColor(ContextCompat.getColor(this, R.color.black))

                mBinding!!.playerSelected10.text = ""
                mBinding!!.playerSelected10.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected10.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )

                mBinding!!.playerSelected11.text = "11"
                mBinding!!.playerSelected11.setBackgroundResource(R.drawable.rectangle_right_top_curve)
                mBinding!!.playerSelected11.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )
            }
            9 -> {
                mBinding!!.playerSelected1.text = "1"
                mBinding!!.playerSelected1.setBackgroundResource(R.drawable.rectangle_left_top_curve_filled)
                mBinding!!.playerSelected1.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected2.text = "2"
                mBinding!!.playerSelected2.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected2.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected3.text = "3"
                mBinding!!.playerSelected3.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected3.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected4.text = "4"
                mBinding!!.playerSelected4.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected4.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected5.text = "5"
                mBinding!!.playerSelected5.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected5.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected6.text = "6"
                mBinding!!.playerSelected6.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected6.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected7.text = "7"
                mBinding!!.playerSelected7.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected7.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected8.text = "8"
                mBinding!!.playerSelected8.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected8.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected9.text = "9"
                mBinding!!.playerSelected9.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected9.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected10.text = ""
                mBinding!!.playerSelected10.setBackgroundResource(R.drawable.rectangle_no_curve)
                mBinding!!.playerSelected10.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )

                mBinding!!.playerSelected11.text = "11"
                mBinding!!.playerSelected11.setBackgroundResource(R.drawable.rectangle_right_top_curve)
                mBinding!!.playerSelected11.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )
            }
            10 -> {
                mBinding!!.playerSelected1.text = "1"
                mBinding!!.playerSelected1.setBackgroundResource(R.drawable.rectangle_left_top_curve_filled)
                mBinding!!.playerSelected1.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected2.text = "2"
                mBinding!!.playerSelected2.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected2.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected3.text = "3"
                mBinding!!.playerSelected3.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected3.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected4.text = "4"
                mBinding!!.playerSelected4.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected4.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected5.text = "5"
                mBinding!!.playerSelected5.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected5.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected6.text = "6"
                mBinding!!.playerSelected6.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected6.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected7.text = "7"
                mBinding!!.playerSelected7.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected7.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected8.text = "8"
                mBinding!!.playerSelected8.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected8.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected9.text = "9"
                mBinding!!.playerSelected9.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected9.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected10.text = "10"
                mBinding!!.playerSelected10.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected10.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.white
                    )
                )

                mBinding!!.playerSelected11.text = "11"
                mBinding!!.playerSelected11.setBackgroundResource(R.drawable.rectangle_right_top_curve)
                mBinding!!.playerSelected11.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.black
                    )
                )

            }
            11 -> {
                mBinding!!.playerSelected1.text = "1"
                mBinding!!.playerSelected1.setBackgroundResource(R.drawable.rectangle_left_top_curve_filled)
                mBinding!!.playerSelected1.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected2.text = "2"
                mBinding!!.playerSelected2.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected2.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected3.text = "3"
                mBinding!!.playerSelected3.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected3.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected4.text = "4"
                mBinding!!.playerSelected4.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected4.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected5.text = "5"
                mBinding!!.playerSelected5.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected5.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected6.text = "6"
                mBinding!!.playerSelected6.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected6.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected7.text = "7"
                mBinding!!.playerSelected7.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected7.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected8.text = "8"
                mBinding!!.playerSelected8.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected8.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected9.text = "9"
                mBinding!!.playerSelected9.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected9.setTextColor(ContextCompat.getColor(this, R.color.white))

                mBinding!!.playerSelected10.text = "10"
                mBinding!!.playerSelected10.setBackgroundResource(R.drawable.rectangle_no_curve_filled)
                mBinding!!.playerSelected10.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.white
                    )
                )

                mBinding!!.playerSelected11.text = "11"
                mBinding!!.playerSelected11.setBackgroundResource(R.drawable.rectangle_right_top_curve_filled)
                mBinding!!.playerSelected11.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.white
                    )
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (matchObject!!.status == BindingUtils.MATCH_STATUS_UPCOMING) {
            startCountDown()
        } else {
            updateTimerHeader()
        }
    }

    private fun updateTimerHeader() {
        mBinding!!.matchTimer.text = matchObject!!.statusString.toUpperCase()
        mBinding!!.matchTimer.setTextColor(resources.getColor(R.color.green))
    }

    private fun startCountDown() {
        BindingUtils.logD("TimerLogs", "initViewUpcomingMatches() called in ContestActivity")
        //matchObject!!.timestampStart = 1591158573 + 300
        BindingUtils.countDownStart(matchObject!!.timestampStart, object : OnMatchTimerStarted {

            override fun onTimeFinished() {
                updateTimerHeader()
//                if(matchObject!!.status.equals(BindingUtils.MATCH_STATUS_UPCOMING)){
//                    showMatchTimeUpDialog()
//                }
            }

            override fun onTicks(time: String) {
                mBinding!!.matchTimer.text = time
                mBinding!!.matchTimer.setTextColor(resources.getColor(R.color.white))
                //         mBinding!!.watchTimerImg.visibility =View.VISIBLE
                BindingUtils.logD("TimerLogs", "ContestScreen: " + time)
            }
        })
    }

    fun pauseCountDown() {
        BindingUtils.stopTimer()
    }

    override fun onPause() {
        super.onPause()
        pauseCountDown()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (CREATETEAM_REQUESTCODE == requestCode && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun onBitmapSelected(bitmap: Bitmap) {

    }

    override fun onUploadedImageUrl(url: String) {

    }

    private fun ressetPlayers() {
        isSortBySelectionActive = false
        isSortBySelectionActiveDecending = false
        isSortByPointsActive = false
        isSortByPointsActiveDecending = false
        isSortByCreditsActive = false
        isSortByCreditsActiveDecending = false

        totalPlayers = 0
        TEAMA = 0
        TEAMB = 0
        COUNT_WICKET_KEEPER = 0
        COUNT_BATS_MAN = 0
        COUNT_ALL_ROUNDER = 0
        COUNT_BOWLER = 0
        isAllPlayersSelected = false
        crateTeamHashMap.clear()
//        viewPagerAdapter.notifyDataSetChanged()
    }

    private fun showLoading() {
        mBinding!!.relativeViewpager.visibility = View.GONE
        mBinding!!.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        mBinding!!.relativeViewpager.visibility = View.VISIBLE
        mBinding!!.progressBar.visibility = View.GONE
    }

    private fun getAllPlayers() {

        val models = RequestModel()
        models.user_id = MyPreferences.getUserID(this)!!
        models.token = MyPreferences.getToken(this)!!
        models.match_id = "" + matchObject!!.matchId

        if (!MyUtils.isConnectedWithInternet(this)) {
            MyUtils.showToast(this, "No Internet connection found")
            return
        }
        WebServiceClient(this).client.create(IApiMethod::class.java).getPlayer(models)
            .enqueue(object : Callback<UsersPostDBResponse?> {
                override fun onFailure(call: Call<UsersPostDBResponse?>?, t: Throwable?) {
                    MyUtils.showToast(this@CreateTeamActivity, t!!.localizedMessage)
                }

                override fun onResponse(
                    call: Call<UsersPostDBResponse?>?,
                    response: Response<UsersPostDBResponse?>?
                ) {
                    hideLoading()
                    var res = response!!.body()
                    if (res != null) {
                        var responseModel = res.responseObject
                        if (responseModel!!.playersList != null) {
                            this@CreateTeamActivity.playersList = responseModel.playersList!!
                            initViewPager()
                            updateEditTEam()
                        }
                    }
                }

            })

    }

    private fun initViewPager() {
        setupViewPager(mBinding!!.viewpager)
        mBinding!!.tabs.setupWithViewPager(mBinding!!.viewpager)
        mBinding!!.viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {

                val fragment = viewPagerAdapter.getItem(position)
                when (position) {
                    0 -> {
                        val objectsWicketKeeper = fragment as WicketKeepers
                        objectsWicketKeeper.setFilterIfActive()
                    }
                    1 -> {
                        val objectsBatsMan = fragment as Batsman
                        objectsBatsMan.setFilterIfActive()
                    }
                    2 -> {
                        val objectsAllRounder = fragment as AllRounder
                        objectsAllRounder.setFilterIfActive()
                    }
                    3 -> {
                        val objectsBowler = fragment as Bowlers
                        objectsBowler.setFilterIfActive()
                    }
                }
            }
        })
    }

    private fun setupViewPager(
        viewPager: ViewPager
    ) {

        val wkList = parseEditTeamModel(playersList!!.wicketKeepers!!, 1)

        var titleTabs = ""
        if (isEditMode || isCopyTeam) {
            titleTabs = String.format("WK(%d)", myTeamModel!!.wicketKeepers!!.size)
        } else {
            titleTabs = getString(R.string.createteam_type_wk)
        }
        val bundleWicketKeepers = Bundle()
        bundleWicketKeepers.putSerializable(SERIALIZABLE_KEY_PLAYERS, wkList)
        bundleWicketKeepers.putSerializable(
            ContestActivity.SERIALIZABLE_KEY_MATCH_OBJECT,
            matchObject
        )

        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(WicketKeepers.newInstance(bundleWicketKeepers), titleTabs)


        val batsManList = parseEditTeamModel(playersList!!.batsmen!!, 2)

        val bundleBatsMatchesModel = Bundle()
        bundleBatsMatchesModel.putSerializable(SERIALIZABLE_KEY_PLAYERS, batsManList)
        bundleBatsMatchesModel.putSerializable(
            ContestActivity.SERIALIZABLE_KEY_MATCH_OBJECT,
            matchObject
        )

        if (isEditMode || isCopyTeam) {
            titleTabs = String.format("BAT(%d)", myTeamModel!!.batsmen!!.size)
        } else {
            titleTabs = getString(R.string.createteam_type_bat)
        }

        viewPagerAdapter.addFragment(Batsman.newInstance(bundleBatsMatchesModel), titleTabs)

        val allList = parseEditTeamModel(playersList!!.allRounders!!, 3)
        if (isEditMode || isCopyTeam) {
            titleTabs = String.format("AR(%d)", myTeamModel!!.allRounders!!.size)
        } else {
            titleTabs = getString(
                R.string.createteam_type_ar
            )
        }
        val bundleAllRounder = Bundle()
        bundleAllRounder.putSerializable(SERIALIZABLE_KEY_PLAYERS, allList)
        bundleAllRounder.putSerializable(ContestActivity.SERIALIZABLE_KEY_MATCH_OBJECT, matchObject)
        viewPagerAdapter.addFragment(AllRounder.newInstance(bundleAllRounder), titleTabs)

        val bowlerList = parseEditTeamModel(playersList!!.bowlers!!, 4)

        if (isEditMode || isCopyTeam) {
            titleTabs = String.format("BOWL(%d)", myTeamModel!!.bowlers!!.size)
        } else {
            titleTabs = getString(
                R.string.createteam_type_bowl
            )
        }
        val bundleBowlers = Bundle()
        bundleBowlers.putSerializable(SERIALIZABLE_KEY_PLAYERS, bowlerList)
        bundleBowlers.putSerializable(ContestActivity.SERIALIZABLE_KEY_MATCH_OBJECT, matchObject)
        viewPagerAdapter.addFragment(Bowlers.newInstance(bundleBowlers), titleTabs)

        viewPager.adapter = viewPagerAdapter


    }

    private fun updateEditTEam() {
        if (myTeamModel != null) {
            val wkSelected = myTeamModel!!.wicketKeepers
            val playersListWK = playersList!!.wicketKeepers!!
            for (y in 0..wkSelected!!.size - 1) {
                val robj = wkSelected.get(y)

                for (x in 0..playersListWK.size - 1) {
                    val dkkd = playersListWK.get(x)
                    if (robj == dkkd.playerId) {
                        val obj = playersListWK.get(x)
                        obj.isSelected = true
                        onWicketKeeperSelected(obj)
                    }
                }
            }
            //BatsManSelected
            val batsSelected = myTeamModel!!.batsmen
            val playersListBats = playersList!!.batsmen!!
            for (y in 0..batsSelected!!.size - 1) {
                val robj = batsSelected.get(y)
                for (x in 0..playersListBats.size - 1) {
                    if (robj == playersListBats.get(x).playerId) {
                        val obj = playersListBats.get(x)
                        obj.isSelected = true
                        onBatsManSelected(obj)
                    }
                }
            }

            //allRounderSelected
            val allSelected = myTeamModel!!.allRounders
            val playersListAll = playersList!!.allRounders!!
            for (y in 0..allSelected!!.size - 1) {
                val robj = allSelected.get(y)
                for (x in 0..playersListAll.size - 1) {
                    if (robj == playersListAll.get(x).playerId) {
                        val obj = playersListAll.get(x)
                        obj.isSelected = true
                        onAllRounderSelected(obj)
                    }
                }
            }

            //allBowlerSelected
            val bowlerSelected = myTeamModel!!.bowlers
            val playersListbowl = playersList!!.bowlers!!
            for (y in 0..bowlerSelected!!.size - 1) {
                val robj = bowlerSelected.get(y)
                for (x in 0..playersListbowl.size - 1) {
                    if (robj == playersListbowl.get(x).playerId) {
                        val obj = playersListbowl.get(x)
                        obj.isSelected = true
                        onBowlerSelected(obj)
                    }
                }
            }
        }
    }

    private fun parseEditTeamModel(
        realList: java.util.ArrayList<PlayersInfoModel>,
        position: Int
    ): java.util.ArrayList<PlayersInfoModel> {
        return realList
    }


    override fun onWicketKeeperSelected(objects: PlayersInfoModel) {
        COUNT_WICKET_KEEPER++
        var playerListObject: ArrayList<PlayersInfoModel>? = null
        if (crateTeamHashMap.containsKey(CREATE_TEAM_WICKET_KEEPER)) {
            playerListObject =
                crateTeamHashMap.get(CREATE_TEAM_WICKET_KEEPER) as ArrayList<PlayersInfoModel>
        } else {
            playerListObject = ArrayList<PlayersInfoModel>()
        }
        playerListObject.add(objects)
        crateTeamHashMap.put(CREATE_TEAM_WICKET_KEEPER, playerListObject)
        countPlayers(1)
        addTeamPlayers(objects)
        // if (!isEditMode && !isCopyTeam) {
        mBinding!!.tabs.getTabAt(0)!!.text = String.format("WK(%d)", playerListObject.size)
        // }
    }

    override fun onWicketKeeperDeSelected(objects: PlayersInfoModel) {
        COUNT_WICKET_KEEPER--
        var playerListObject: ArrayList<PlayersInfoModel>? = null
        if (crateTeamHashMap.containsKey(CREATE_TEAM_WICKET_KEEPER)) {
            playerListObject =
                crateTeamHashMap.get(CREATE_TEAM_WICKET_KEEPER) as ArrayList<PlayersInfoModel>
        } else {
            playerListObject = ArrayList<PlayersInfoModel>()
        }
        playerListObject.remove(objects)
        crateTeamHashMap.put(CREATE_TEAM_WICKET_KEEPER, playerListObject)
        countPlayers(-1)
        removeTeamPlayers(objects)
        //if (!isEditMode && !isCopyTeam) {
        mBinding!!.tabs.getTabAt(0)!!.text = String.format("WK(%d)", playerListObject.size)
        //}
    }

    override fun onBatsManSelected(objects: PlayersInfoModel) {
        COUNT_BATS_MAN++
        var playerListObject: ArrayList<PlayersInfoModel>? = null
        if (crateTeamHashMap.containsKey(CREATE_TEAM_BATSMAN)) {
            playerListObject =
                crateTeamHashMap.get(CREATE_TEAM_BATSMAN) as ArrayList<PlayersInfoModel>
        } else {
            playerListObject = ArrayList<PlayersInfoModel>()
        }
        playerListObject.add(objects)
        crateTeamHashMap.put(CREATE_TEAM_BATSMAN, playerListObject)
        countPlayers(1)
        addTeamPlayers(objects)
        //if (!isEditMode && !isCopyTeam) {
        mBinding!!.tabs.getTabAt(1)!!.text = String.format("BAT(%d)", playerListObject.size)
        //}
    }

    override fun onBatsManDeSelected(objects: PlayersInfoModel) {
        COUNT_BATS_MAN--
        var playerListObject: ArrayList<PlayersInfoModel>? = null
        if (crateTeamHashMap.containsKey(CREATE_TEAM_BATSMAN)) {
            playerListObject =
                crateTeamHashMap.get(CREATE_TEAM_BATSMAN) as ArrayList<PlayersInfoModel>
        } else {
            playerListObject = ArrayList<PlayersInfoModel>()
        }
        playerListObject.remove(objects)
        crateTeamHashMap.put(CREATE_TEAM_BATSMAN, playerListObject)
        countPlayers(-1)
        removeTeamPlayers(objects)
        // if (!isEditMode && !isCopyTeam) {
        mBinding!!.tabs.getTabAt(1)!!.text = String.format("BAT(%d)", playerListObject.size)
        //}
    }

    override fun onAllRounderSelected(objects: PlayersInfoModel) {
        COUNT_ALL_ROUNDER++
        var playerListObject: ArrayList<PlayersInfoModel>? = null
        if (crateTeamHashMap.containsKey(CREATE_TEAM_ALLROUNDER)) {
            playerListObject =
                crateTeamHashMap.get(CREATE_TEAM_ALLROUNDER) as ArrayList<PlayersInfoModel>
        } else {
            playerListObject = ArrayList<PlayersInfoModel>()
        }
        playerListObject.add(objects)
        crateTeamHashMap.put(CREATE_TEAM_ALLROUNDER, playerListObject)
        countPlayers(1)
        addTeamPlayers(objects)
        //if (!isEditMode && !isCopyTeam) {
        mBinding!!.tabs.getTabAt(2)!!.text = String.format("AR(%d)", playerListObject.size)
        //}
    }

    override fun onAllRounderDeSelected(objects: PlayersInfoModel) {
        COUNT_ALL_ROUNDER--
        var playerListObject: ArrayList<PlayersInfoModel>? = null
        if (crateTeamHashMap.containsKey(CREATE_TEAM_ALLROUNDER)) {
            playerListObject =
                crateTeamHashMap.get(CREATE_TEAM_ALLROUNDER) as ArrayList<PlayersInfoModel>
        } else {
            playerListObject = ArrayList<PlayersInfoModel>()
        }
        playerListObject.remove(objects)
        crateTeamHashMap.put(CREATE_TEAM_ALLROUNDER, playerListObject)
        countPlayers(-1)
        removeTeamPlayers(objects)
        // if (!isEditMode && !isCopyTeam) {
        mBinding!!.tabs.getTabAt(2)!!.text = String.format("AR(%d)", playerListObject.size)
        ///}
    }

    override fun onBowlerSelected(objects: PlayersInfoModel) {
        COUNT_BOWLER++
        var playerListObject: ArrayList<PlayersInfoModel>? = null
        if (crateTeamHashMap.containsKey(CREATE_TEAM_BOWLER)) {
            playerListObject =
                crateTeamHashMap.get(CREATE_TEAM_BOWLER) as ArrayList<PlayersInfoModel>
        } else {
            playerListObject = ArrayList<PlayersInfoModel>()
        }
        playerListObject.add(objects)
        crateTeamHashMap.put(CREATE_TEAM_BOWLER, playerListObject)
        countPlayers(1)
        addTeamPlayers(objects)
        // if (!isEditMode && !isCopyTeam) {
        mBinding!!.tabs.getTabAt(3)!!.text = String.format("BOWL(%d)", playerListObject.size)
        // }
    }

    override fun onBowlerDeSelected(objects: PlayersInfoModel) {
        COUNT_BOWLER--
        var playerListObject: ArrayList<PlayersInfoModel>? = null
        if (crateTeamHashMap.containsKey(CREATE_TEAM_BOWLER)) {
            playerListObject =
                crateTeamHashMap.get(CREATE_TEAM_BOWLER) as ArrayList<PlayersInfoModel>
        } else {
            playerListObject = ArrayList<PlayersInfoModel>()
        }
        playerListObject.remove(objects)
        crateTeamHashMap.put(CREATE_TEAM_BOWLER, playerListObject)
        countPlayers(-1)
        removeTeamPlayers(objects)
        //  if (!isEditMode && !isCopyTeam) {
        mBinding!!.tabs.getTabAt(3)!!.text = String.format("BOWL(%d)", playerListObject.size)
        //  }
    }

    override fun countPlayers(obj: Int) {
        totalPlayers += obj
        mBinding!!.totalplayerSelected.text = String.format("%d", totalPlayers)
        updatePlayersCountBar(totalPlayers)
        if (totalPlayers == MAX_PLAYERS_CRICKET) {
            isAllPlayersSelected = true
            mBinding!!.teamContinue.isEnabled = true
            mBinding!!.teamContinue.setBackgroundResource(R.drawable.default_rounded_button_sportsfight)
        } else {
            isAllPlayersSelected = false
            mBinding!!.teamContinue.isEnabled = false
            mBinding!!.teamContinue.setBackgroundResource(R.drawable.button_selector_grey)
        }
    }

    override fun addTeamPlayers(objects: PlayersInfoModel) {

        if (objects.teamId == teamAId) {
            TEAMA++
        } else if (objects.teamId == teamBId) {
            TEAMB++
        }
        mBinding!!.teamaCounts.text = String.format("%d", TEAMA)
        mBinding!!.teambCounts.text = String.format("%d", TEAMB)
    }

    override fun removeTeamPlayers(objects: PlayersInfoModel) {
        //if(objects.teamId==matchObject!!.teamAInfo!!.teamId){
        if (objects.teamId == teamAId) {
            TEAMA--
        } else if (objects.teamId == teamBId) {
            TEAMB--
        }

        mBinding!!.teamaCounts.text = String.format("%d", TEAMA)
        mBinding!!.teambCounts.text = String.format("%d", TEAMB)
    }

    fun isSpotAvailable(wantPlayerFrom: Int): Boolean {
        val remainingSpots = MAX_PLAYERS_CRICKET - totalPlayers
        if (remainingSpots <= (COUNT_BATS_MAN + COUNT_BOWLER + COUNT_ALL_ROUNDER + COUNT_WICKET_KEEPER)) {
            val totalWKRemaining = MAX_WICKET_KEEPER[0] - COUNT_WICKET_KEEPER
            val totalBatsRemaining = MAX_BATSMAN[0] - COUNT_BATS_MAN
            val totalAllRounderRemaining = MAX_ALL_ROUNDER[0] - COUNT_ALL_ROUNDER
            val totalBowlerRemainig = MAX_BOWLER[0] - COUNT_BOWLER

            if (WANT_WK == wantPlayerFrom) {
                var countnow = 0
//                if(totalWKRemaining>0){
//                    countnow +=totalWKRemaining
//                }
                if (totalBatsRemaining > 0) {
                    countnow += totalBatsRemaining
                }
                if (totalAllRounderRemaining > 0) {
                    countnow += totalAllRounderRemaining
                }
                if (totalBowlerRemainig > 0) {
                    countnow += totalBowlerRemainig
                }
                return remainingSpots <= countnow
            } else if (WANT_BAT == wantPlayerFrom) {
                var countnow = 0
                if (totalWKRemaining > 0) {
                    countnow += totalWKRemaining
                }
//                if(totalBatsRemaining>0){
//                    countnow +=totalBatsRemaining
//                }
                if (totalAllRounderRemaining > 0) {
                    countnow += totalAllRounderRemaining
                }
                if (totalBowlerRemainig > 0) {
                    countnow += totalBowlerRemainig
                }
                return remainingSpots <= countnow
            } else if (WANT_ALL == wantPlayerFrom) {
                var countnow = 0
                if (totalWKRemaining > 0) {
                    countnow += totalWKRemaining
                }
                if (totalBatsRemaining > 0) {
                    countnow += totalBatsRemaining
                }
//                if(totalAllRounderRemaining>0){
//                    countnow +=totalAllRounderRemaining
//                }
                if (totalBowlerRemainig > 0) {
                    countnow += totalBowlerRemainig
                }
                return remainingSpots <= countnow
            } else if (WANT_BOWL == wantPlayerFrom) {
                var countnow = 0
                if (totalWKRemaining > 0) {
                    countnow += totalWKRemaining
                }
                if (totalBatsRemaining > 0) {
                    countnow += totalBatsRemaining
                }
                if (totalAllRounderRemaining > 0) {
                    countnow += totalAllRounderRemaining
                }
//                  if(totalBowlerRemainig>0){
//                    countnow +=totalBowlerRemainig
//                  }
                return remainingSpots <= countnow
            }
        }
        return false
    }

    fun sortBySelections() {
        isSortBySelectionActive = true
        isSortByPointsActive = false
        isSortByCreditsActive = false
        isSortBySelectionActiveDecending = !isSortBySelectionActiveDecending!!
    }

    fun sortByPoints() {
        isSortBySelectionActive = false
        isSortByPointsActive = true
        isSortByCreditsActive = false
        isSortByPointsActiveDecending = !isSortByPointsActiveDecending!!
    }

    fun sortByCredits() {
        isSortBySelectionActive = false
        isSortByPointsActive = false
        isSortByCreditsActive = true
        isSortByCreditsActiveDecending = !isSortByCreditsActiveDecending!!
    }

    internal inner class ViewPagerAdapter(manager: FragmentManager) :
        FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
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