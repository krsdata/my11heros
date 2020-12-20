package com.my11heros

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edify.atrist.listener.OnMatchTimerStarted
import com.edify.atrist.listener.OnRolesSelected
import com.my11heros.CreateTeamActivity.Companion.CREATE_TEAM_ALLROUNDER
import com.my11heros.CreateTeamActivity.Companion.CREATE_TEAM_BATSMAN
import com.my11heros.CreateTeamActivity.Companion.CREATE_TEAM_BOWLER
import com.my11heros.CreateTeamActivity.Companion.CREATE_TEAM_WICKET_KEEPER
import com.my11heros.databinding.ActivitySaveTeamBinding
import com.my11heros.models.MyTeamModels
import com.my11heros.models.UpcomingMatchesModel
import com.my11heros.network.IApiMethod
import com.my11heros.network.RequestCreateTeamModel
import com.my11heros.network.WebServiceClient
import com.my11heros.ui.BaseActivity
import com.my11heros.ui.contest.MyTeamFragment
import com.my11heros.ui.createteam.adaptors.PlayersSelectedAdapter
import com.my11heros.ui.createteam.models.PlayersInfoModel
import com.my11heros.ui.home.models.UsersPostDBResponse
import com.my11heros.ui.login.RegisterScreenActivity
import com.my11heros.utils.BindingUtils
import com.my11heros.utils.CustomeProgressDialog
import com.my11heros.utils.MyPreferences
import com.my11heros.utils.MyUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class SaveTeamActivity : BaseActivity(), OnRolesSelected {

    private var mCreateTeamRequest: RequestCreateTeamModel? = null
    var myTeamModel: MyTeamModels? = null
    private var isTrumpSelected: Boolean = false
    private var isCaptainSelected: Boolean = false
    private var isViceCaptainSelected: Boolean = false
    private lateinit var matchObject: UpcomingMatchesModel
    private lateinit var hasmapPlayers: HashMap<String, ArrayList<PlayersInfoModel>>
    private var mBinding: ActivitySaveTeamBinding? = null
    var savedTeamList = ArrayList<PlayersInfoModel>()
    lateinit var adapter: PlayersSelectedAdapter

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RegisterScreenActivity.REQUESTCODE_LOGIN && resultCode == Activity.RESULT_OK) {
            mCreateTeamRequest = getRequest()
            createTeam(mCreateTeamRequest!!)
        }
    }

    override fun onBitmapSelected(bitmap: Bitmap) {

    }

    override fun onUploadedImageUrl(url: String) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_save_team
        )
        customeProgressDialog = CustomeProgressDialog(this)
        matchObject =
            intent.getSerializableExtra(CreateTeamActivity.SERIALIZABLE_MATCH_KEY) as UpcomingMatchesModel
        hasmapPlayers =
            intent.getSerializableExtra(TeamPreviewActivity.SERIALIZABLE_TEAM_PREVIEW_KEY) as HashMap<String, ArrayList<PlayersInfoModel>>

        if (intent.hasExtra(MyTeamFragment.SERIALIZABLE_EDIT_TEAM)) {
            CreateTeamActivity.isEditMode = true
            myTeamModel =
                intent.getSerializableExtra(MyTeamFragment.SERIALIZABLE_EDIT_TEAM) as MyTeamModels
        }

        mBinding!!.imageBack.setOnClickListener(View.OnClickListener {
            finish()
        })
        mBinding!!.imgWallet.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@SaveTeamActivity, MyBalanceActivity::class.java)
            startActivity(intent)
        })

        mBinding!!.howToplay.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@SaveTeamActivity, WebActivity::class.java)
            intent.putExtra(WebActivity.KEY_TITLE, BindingUtils.WEB_TITLE_HOW_TO_PLAY)
            intent.putExtra(WebActivity.KEY_URL, BindingUtils.WEBVIEW_FANTASY_HOW_TO_PLAY)
            val options =
                ActivityOptions.makeSceneTransitionAnimation(this)
            startActivity(intent, options.toBundle())
        })
        initPlayers()

        mBinding!!.recyclerSaveTeam.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        adapter = PlayersSelectedAdapter(
            this,
            savedTeamList,
            this
        )
        mBinding!!.recyclerSaveTeam.adapter = adapter
        onReady()

        mBinding!!.teamPreview.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@SaveTeamActivity, TeamPreviewActivity::class.java)
            intent.putExtra(CreateTeamActivity.SERIALIZABLE_MATCH_KEY, matchObject)
            intent.putExtra(TeamPreviewActivity.SERIALIZABLE_TEAM_PREVIEW_KEY, hasmapPlayers)
            startActivity(intent)
        })

        mBinding!!.teamContinue.setOnClickListener(View.OnClickListener {
            mCreateTeamRequest = getRequest()
            if (MyPreferences.getLoginStatus(this@SaveTeamActivity)!!) {
                if (mCreateTeamRequest != null) {
                    createTeam(mCreateTeamRequest!!)
                }
            } else {
                val intent = Intent(this@SaveTeamActivity, RegisterScreenActivity::class.java)
                intent.putExtra(RegisterScreenActivity.ISACTIVITYRESULT, true)
                startActivityForResult(intent, RegisterScreenActivity.REQUESTCODE_LOGIN)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (matchObject.status == BindingUtils.MATCH_STATUS_UPCOMING) {
            startCountDown()
        } else {
            updateTimerHeader()
        }
    }

    private fun updateTimerHeader() {
        mBinding!!.matchTimer.text = matchObject.statusString.toUpperCase(Locale.ENGLISH)
        mBinding!!.matchTimer.setTextColor(resources.getColor(R.color.white))
    }

    private fun startCountDown() {
        BindingUtils.logD("TimerLogs", "initViewUpcomingMatches() called in ContestActivity")
        //matchObject!!.timestampStart = 1591158573 + 300
        BindingUtils.countDownStart(matchObject.timestampStart, object : OnMatchTimerStarted {

            override fun onTimeFinished() {
                updateTimerHeader()
                //           mBinding!!.watchTimerImg.visibility =View.GONE
                if (matchObject.status.equals(BindingUtils.MATCH_STATUS_UPCOMING)) {
                    showMatchTimeUpDialog()
                }
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

    private fun getRequest(): RequestCreateTeamModel? {
        var trump = 0
        var captain = 0
        var viceCaptain = 0
        val teams: ArrayList<Int> = ArrayList()
        var isTrump = false
        var isCaptain = false
        var isViceCaptain = false
        var team_id = false

        for (x in 0 until savedTeamList.size) {
            val obj = savedTeamList[x]
            teams.add(obj.playerId)
            if (obj.isTrump) {
                isTrump = true
                trump = obj.playerId
            } else if (obj.isCaptain) {
                isCaptain = true
                captain = obj.playerId
            } else if (obj.isViceCaptain) {
                isViceCaptain = true
                viceCaptain = obj.playerId
            }
        }

//        if(!isTrump){
//            MyUtils.showToast(this@SaveTeamActivity,"Please select Trump")
//            return null
//        }
        if (!isCaptain) {
            MyUtils.showToast(this@SaveTeamActivity, "Please select Captain")
            return null
        }
        if (!isViceCaptain) {
            MyUtils.showToast(this@SaveTeamActivity, "Please select Vice Captain")
            return null
        }
        val teamIds = ArrayList<Int>()
        teamIds.add(matchObject.teamAInfo!!.teamId)
        teamIds.add(matchObject.teamBInfo!!.teamId)
        val models = RequestCreateTeamModel()
        models.user_id = MyPreferences.getUserID(this)!!
        // models.token = MyPreferences.getToken(this)!!
        models.match_id = "" + matchObject.matchId
        models.trump = 0//trump @passing value as 0 for trump
        models.captain = captain
        models.vice_captain = viceCaptain
        if (myTeamModel != null) {
            models.create_team_id = myTeamModel!!.teamId!!.teamId
        }
        models.teams = teams
        models.team_id = teamIds
        return models
    }

    private fun initPlayers() {
        var captainId = 0
        // var trumpId = 0
        var viceCaptainId = 0
        if (myTeamModel != null) {
            isCaptainSelected = true
            isTrumpSelected = true
            isViceCaptainSelected = true
            captainId = myTeamModel!!.captain!!.playerId
            //  trumpId = myTeamModel!!.trump!!.playerId
            viceCaptainId = myTeamModel!!.viceCaptain!!.playerId
        }
        if (hasmapPlayers.containsKey(CREATE_TEAM_WICKET_KEEPER)) {
            val objList =
                hasmapPlayers.get(CREATE_TEAM_WICKET_KEEPER) as ArrayList<PlayersInfoModel>
            val playersLabel = PlayersInfoModel()
            playersLabel.viewType = PlayersSelectedAdapter.TYPE_LABEL
            playersLabel.playerRole = "Wicket Keeper"
            savedTeamList.add(playersLabel)

            for (x in 0 until objList.size) {
                val listPlayers = objList[x]
                listPlayers.playerRole = CREATE_TEAM_WICKET_KEEPER
                if (captainId == listPlayers.playerId) {
                    listPlayers.isCaptain = true
                } else
                    if (viceCaptainId == listPlayers.playerId) {
                        listPlayers.isViceCaptain = true
                    }

                savedTeamList.add(listPlayers)
            }
        }
        if (hasmapPlayers.containsKey(CREATE_TEAM_BATSMAN)) {
            val objList =
                hasmapPlayers.get(CREATE_TEAM_BATSMAN) as ArrayList<PlayersInfoModel>
            val playersLabel = PlayersInfoModel()
            playersLabel.viewType = PlayersSelectedAdapter.TYPE_LABEL
            playersLabel.playerRole = "BATSMAN"
            savedTeamList.add(playersLabel)
            for (x in 0 until objList.size) {
                val listPlayers = objList[x]
                listPlayers.playerRole = CREATE_TEAM_BATSMAN
                if (captainId == listPlayers.playerId) {
                    listPlayers.isCaptain = true
                } else
                    if (viceCaptainId == listPlayers.playerId) {
                        listPlayers.isViceCaptain = true
                    }
                savedTeamList.add(listPlayers)

            }
        }
        if (hasmapPlayers.containsKey(CREATE_TEAM_ALLROUNDER)) {
            val objList =
                hasmapPlayers.get(CREATE_TEAM_ALLROUNDER) as ArrayList<PlayersInfoModel>
            val playersLabel = PlayersInfoModel()
            playersLabel.viewType = PlayersSelectedAdapter.TYPE_LABEL
            playersLabel.playerRole = "ALL ROUNDER"
            savedTeamList.add(playersLabel)
            for (x in 0 until objList.size) {
                val listPlayers = objList[x]
                listPlayers.playerRole = CREATE_TEAM_ALLROUNDER
                if (captainId == listPlayers.playerId) {
                    listPlayers.isCaptain = true
                } else
                    if (viceCaptainId == listPlayers.playerId) {
                        listPlayers.isViceCaptain = true
                    }
                savedTeamList.add(listPlayers)
            }
        }
        if (hasmapPlayers.containsKey(CREATE_TEAM_BOWLER)) {
            val objList =
                hasmapPlayers.get(CREATE_TEAM_BOWLER) as ArrayList<PlayersInfoModel>
            val playersLabel = PlayersInfoModel()
            playersLabel.viewType = PlayersSelectedAdapter.TYPE_LABEL
            playersLabel.playerRole = "BOWLER"
            savedTeamList.add(playersLabel)
            for (x in 0 until objList.size) {
                val listPlayers = objList[x]
                listPlayers.playerRole = CREATE_TEAM_BOWLER
                if (captainId == listPlayers.playerId) {
                    listPlayers.isCaptain = true
                } else
                    if (viceCaptainId == listPlayers.playerId) {
                        listPlayers.isViceCaptain = true
                    }
                savedTeamList.add(listPlayers)
            }
        }
        onReady()
    }

    override fun onTrumpSelected(objects: PlayersInfoModel, position: Int) {
//          for(X in 0..savedTeamList.size-1){
//              var values = savedTeamList.get(X)
//              if(position==X) {
//                  values.isTrump = true
//                  values.isCaptain = false
//                  values.isViceCaptain = false
//                  savedTeamList.set(X, values)
//              }else {
//                  values.isTrump = false
//                  savedTeamList.set(X, values)
//              }
//          }
//        adapter.notifyDataSetChanged()
//        onReady()
    }

    override fun onCaptainSelected(objects: PlayersInfoModel, position: Int) {
        isCaptainSelected = true
        for (X in 0 until savedTeamList.size) {
            val values = savedTeamList[X]
            if (position == X) {
                values.isCaptain = true
                values.isTrump = false
                values.isViceCaptain = false
                savedTeamList.set(X, values)
            } else {
                values.isCaptain = false
                savedTeamList.set(X, values)
            }
        }
        adapter.notifyDataSetChanged()
        onReady()
    }

    override fun onViceCaptainSelected(objects: PlayersInfoModel, position: Int) {
        isViceCaptainSelected = true
        for (X in 0 until savedTeamList.size) {
            val values = savedTeamList[X]
            if (position == X) {
                values.isViceCaptain = true
                values.isCaptain = false
                values.isTrump = false
                savedTeamList.set(X, values)
            } else {
                values.isViceCaptain = false
                savedTeamList.set(X, values)
            }
        }
        adapter.notifyDataSetChanged()
        onReady()
    }


    override fun onReady() {
        if (isCaptainSelected && isViceCaptainSelected) {
            mBinding!!.teamContinue.isEnabled = true
            mBinding!!.teamContinue.setBackgroundResource(R.drawable.default_rounded_button_sportsfight)
        } else {
            mBinding!!.teamContinue.isEnabled = false
            mBinding!!.teamContinue.setBackgroundResource(R.drawable.button_selector_grey)
        }
    }


    fun createTeam(
        request: RequestCreateTeamModel
    ) {
        if (!MyUtils.isConnectedWithInternet(this)) {
            MyUtils.showToast(this, "No Internet connection found")
            return
        }
        request.token = MyPreferences.getToken(this)!!
        customeProgressDialog.show()
        WebServiceClient(this).client.create(IApiMethod::class.java).createTeam(request)
            .enqueue(object : Callback<UsersPostDBResponse?> {
                override fun onFailure(call: Call<UsersPostDBResponse?>?, t: Throwable?) {
                    MyUtils.showToast(this@SaveTeamActivity, t!!.localizedMessage)
                }

                override fun onResponse(
                    call: Call<UsersPostDBResponse?>?,
                    response: Response<UsersPostDBResponse?>?
                ) {
                    customeProgressDialog.dismiss()
                    val res = response!!.body()
                    if (res != null) {
                        if (res.status) {
//                            BindingUtils.sendEventLogs(
//                                this@SaveTeamActivity,
//                                matchObject!!.matchId,0,
//                                userInfo!!,
//                                BindingUtils.FIREBASE_EVENT_ITEM_ID_SAVE_TEAM_ACTIVITY
//                            )
                            Toast.makeText(
                                this@SaveTeamActivity,
                                "Team Created Successfully",
                                Toast.LENGTH_LONG
                            ).show()
                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            Toast.makeText(this@SaveTeamActivity, res.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            })
    }
}