package com.my11heros

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.my11heros.CreateTeamActivity.Companion.CREATE_TEAM_ALLROUNDER
import com.my11heros.CreateTeamActivity.Companion.CREATE_TEAM_BATSMAN
import com.my11heros.CreateTeamActivity.Companion.CREATE_TEAM_BOWLER
import com.my11heros.CreateTeamActivity.Companion.CREATE_TEAM_WICKET_KEEPER
import com.my11heros.databinding.ActivityTeamPreviewBinding
import com.my11heros.models.UpcomingMatchesModel
import com.my11heros.network.IApiMethod
import com.my11heros.network.RequestModel
import com.my11heros.network.WebServiceClient
import com.my11heros.ui.createteam.models.PlayersInfoModel
import com.my11heros.ui.home.models.UsersPostDBResponse
import com.my11heros.ui.previewteam.adaptors.GridViewAdapter
import com.my11heros.utils.BindingUtils
import com.my11heros.utils.CustomeProgressDialog
import com.my11heros.utils.MyPreferences
import com.my11heros.utils.MyUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TeamPreviewActivity : AppCompatActivity() {
    private lateinit var customeProgressDialog: CustomeProgressDialog
    private var teamId: Int = 0
    private var teamName: String = ""
    private lateinit var matchObject: UpcomingMatchesModel
    private lateinit var hasmapPlayers: HashMap<String, java.util.ArrayList<PlayersInfoModel>>
    private var mBinding: ActivityTeamPreviewBinding? = null
    private val listWicketKeeper = ArrayList<PlayersInfoModel>()
    private val listBatsMan = ArrayList<PlayersInfoModel>()
    private val listAllRounder = ArrayList<PlayersInfoModel>()
    private val listBowler = ArrayList<PlayersInfoModel>()

    companion object {
        val SERIALIZABLE_TEAM_PREVIEW_KEY: String = "teampreview"
        val KEY_TEAM_NAME: String = "team_name"
        val KEY_TEAM_ID: String = "team_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_team_preview
        )
        customeProgressDialog = CustomeProgressDialog(this)
        if (intent.hasExtra(KEY_TEAM_NAME)) {
            teamName = intent.getStringExtra(KEY_TEAM_NAME)
        }
        if (intent.hasExtra(KEY_TEAM_ID)) {
            teamId = intent.getIntExtra(KEY_TEAM_ID, 0)
        }
        matchObject =
            intent.getSerializableExtra(CreateTeamActivity.SERIALIZABLE_MATCH_KEY) as UpcomingMatchesModel
        hasmapPlayers =
            intent.getSerializableExtra(SERIALIZABLE_TEAM_PREVIEW_KEY) as HashMap<String, ArrayList<PlayersInfoModel>>
        mBinding!!.imgRefresh.setOnClickListener(View.OnClickListener {
            getPoints(teamId)
        })

        mBinding!!.imgClose.setOnClickListener(View.OnClickListener {
            finish()
        })

        mBinding!!.fantasyPointsWebsview.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@TeamPreviewActivity, WebActivity::class.java)
            intent.putExtra(WebActivity.KEY_TITLE, BindingUtils.WEB_TITLE_FANTASY_POINTS)
            intent.putExtra(WebActivity.KEY_URL, BindingUtils.WEBVIEW_FANTASY_POINTS)
            startActivity(intent)
        })

        mBinding!!.teamName.text = teamName
        if (matchObject.status == BindingUtils.MATCH_STATUS_UPCOMING) {
            mBinding!!.pointsBar.visibility = View.GONE
            mBinding!!.imgRefresh.visibility = View.GONE
        } else {
            mBinding!!.pointsBar.visibility = View.VISIBLE
            mBinding!!.imgRefresh.visibility = View.VISIBLE
        }

        setupPlayersOnGrounds()
    }

    private fun setupPlayersOnGrounds() {
        mBinding!!.totalPointsValue.text = calculatePoints()
        addWicketKeeper()
        addBatsman()
        addAllRounder()
        addBowler()

        val gridViewAdapterWicket =
            GridViewAdapter(
                this@TeamPreviewActivity,
                listWicketKeeper,
                matchObject
            )
        mBinding!!.gridWicketKeeper.numColumns = listWicketKeeper.size
        mBinding!!.gridWicketKeeper.adapter = gridViewAdapterWicket

        var sizeofColumn = 0
        val gridViewAdapterBatsMan =
            GridViewAdapter(
                this@TeamPreviewActivity,
                listBatsMan,
                matchObject
            )

        if (listBatsMan.size > 4) {
            sizeofColumn = 3
        } else {
            sizeofColumn = listBatsMan.size
        }
        mBinding!!.gridBatsman.numColumns = sizeofColumn
        mBinding!!.gridBatsman.adapter = gridViewAdapterBatsMan

        val gridViewAdapterAllRounder =
            GridViewAdapter(
                this@TeamPreviewActivity,
                listAllRounder,
                matchObject
            )
        if (listAllRounder.size > 4) {
            sizeofColumn = 3
        } else {
            sizeofColumn = listAllRounder.size
        }
        mBinding!!.gridAllRounders.numColumns = sizeofColumn
        mBinding!!.gridAllRounders.adapter = gridViewAdapterAllRounder

        val gridViewAdapterBowler =
            GridViewAdapter(
                this@TeamPreviewActivity,
                listBowler,
                matchObject
            )
        if (listBowler.size > 4) {
            sizeofColumn = 3
        } else {
            sizeofColumn = listBowler.size
        }
        mBinding!!.gridBowlers.numColumns = sizeofColumn
        mBinding!!.gridBowlers.adapter = gridViewAdapterBowler
        setGridViewOnItemClickListener()
    }

    private fun getPoints(teamId: Int) {
        if (!MyUtils.isConnectedWithInternet(this)) {
            MyUtils.showToast(this, "No Internet connection found")
            return
        }
        customeProgressDialog.show()
        val models = RequestModel()
        models.user_id = MyPreferences.getUserID(this)!!
        models.team_id = teamId

        WebServiceClient(this).client.create(IApiMethod::class.java).getPoints(models)
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
                        var totalPoints = res.totalPoints
                        val responseModel = res.responseObject
                        if (responseModel != null) {
                            val playerPointsList = responseModel.playerPointsList
                            val hasmapPlayers: HashMap<String, ArrayList<PlayersInfoModel>> =
                                HashMap<String, ArrayList<PlayersInfoModel>>()

                            val wktKeeperList: ArrayList<PlayersInfoModel> =
                                ArrayList<PlayersInfoModel>()
                            val batsManList: ArrayList<PlayersInfoModel> =
                                ArrayList<PlayersInfoModel>()
                            val allRounderList: ArrayList<PlayersInfoModel> =
                                ArrayList<PlayersInfoModel>()
                            val allbowlerList: ArrayList<PlayersInfoModel> =
                                ArrayList<PlayersInfoModel>()

                            for (x in 0..playerPointsList!!.size - 1) {
                                val plyObj = playerPointsList.get(x)
                                if (plyObj.playerRole.equals("wk")) {
                                    wktKeeperList.add(plyObj)
                                } else if (plyObj.playerRole.equals("bat")) {
                                    batsManList.add(plyObj)
                                } else if (plyObj.playerRole.equals("all")) {
                                    allRounderList.add(plyObj)
                                } else if (plyObj.playerRole.equals("bowl")) {
                                    allbowlerList.add(plyObj)
                                }
                            }
                            hasmapPlayers.put(CREATE_TEAM_WICKET_KEEPER, wktKeeperList)
                            hasmapPlayers.put(CREATE_TEAM_BATSMAN, batsManList)
                            hasmapPlayers.put(CREATE_TEAM_ALLROUNDER, allRounderList)
                            hasmapPlayers.put(CREATE_TEAM_BOWLER, allbowlerList)

                            updatePlayersPoints(hasmapPlayers)
                        }
                    }
                }
            })
    }

    private fun updatePlayersPoints(hasmapPlayers: HashMap<String, ArrayList<PlayersInfoModel>>) {
        this.hasmapPlayers.clear()
        this.hasmapPlayers = hasmapPlayers
        setupPlayersOnGrounds()
    }

    private fun calculatePoints(): String {
        var totalPoints: Double = 0.0
        if (hasmapPlayers.containsKey(CREATE_TEAM_WICKET_KEEPER)) {
            val lkeeper = hasmapPlayers.get(CREATE_TEAM_WICKET_KEEPER)
            for (x in 0..lkeeper!!.size - 1) {
                val obj = lkeeper.get(x)
                totalPoints = totalPoints + obj.playerPoints.toDouble()
            }
        }

        if (hasmapPlayers.containsKey(CREATE_TEAM_BATSMAN)) {
            val btslist = hasmapPlayers.get(CREATE_TEAM_BATSMAN)
            for (x in 0..btslist!!.size - 1) {
                val obj = btslist.get(x)
                totalPoints = totalPoints + obj.playerPoints.toDouble()
            }
        }

        if (hasmapPlayers.containsKey(CREATE_TEAM_ALLROUNDER)) {
            val alllist = hasmapPlayers.get(CREATE_TEAM_ALLROUNDER)
            for (x in 0..alllist!!.size - 1) {
                val obj = alllist.get(x)
                totalPoints = totalPoints + obj.playerPoints.toDouble()
            }
        }
        if (hasmapPlayers.containsKey(CREATE_TEAM_BOWLER)) {
            val bwllist = hasmapPlayers.get(CREATE_TEAM_BOWLER)
            for (x in 0..bwllist!!.size - 1) {
                val obj = bwllist.get(x)
                totalPoints = totalPoints + obj.playerPoints.toDouble()
            }
        }
        return totalPoints.toString()
    }

    private fun setGridViewOnItemClickListener() {
        mBinding!!.gridWicketKeeper.setOnItemClickListener { parent, view, position, id ->

        }
        mBinding!!.gridBatsman.setOnItemClickListener { parent, view, position, id ->

        }
        mBinding!!.gridAllRounders.setOnItemClickListener { parent, view, position, id ->

        }
        mBinding!!.gridBowlers.setOnItemClickListener { parent, view, position, id ->

        }
    }

    private fun addWicketKeeper() {
        listWicketKeeper.clear()
        if (hasmapPlayers.containsKey(CREATE_TEAM_WICKET_KEEPER)) {
            val listofPlayers = hasmapPlayers.get(CREATE_TEAM_WICKET_KEEPER)!!
            for (i in 0..listofPlayers.size - 1) {
                val playerObject = listofPlayers.get(i)
                if (playerObject.teamId == matchObject.teamAInfo!!.teamId) {
                    playerObject.setPlayerIcon(R.drawable.ic_player_wk_teama)
                } else {
                    playerObject.setPlayerIcon(R.drawable.ic_player_wk_teamb)
                }
            }
            listWicketKeeper.addAll(listofPlayers)
        }
    }

    private fun addBatsman() {
        listBatsMan.clear()
        if (hasmapPlayers.containsKey(CREATE_TEAM_BATSMAN)) {
            val listofPlayers = hasmapPlayers.get(CREATE_TEAM_BATSMAN)!!
            for (i in 0..listofPlayers.size - 1) {
                val playerObject = listofPlayers.get(i)
                if (playerObject.teamId == matchObject.teamAInfo!!.teamId) {
                    playerObject.setPlayerIcon(R.drawable.ic_player_bat_teama)
                } else {
                    playerObject.setPlayerIcon(R.drawable.ic_player_bat_teamb)
                }
            }
            listBatsMan.addAll(listofPlayers)
        }
    }

    private fun addAllRounder() {
        listAllRounder.clear()
        if (hasmapPlayers.containsKey(CREATE_TEAM_ALLROUNDER)) {
            val listofPlayers = hasmapPlayers.get(CREATE_TEAM_ALLROUNDER)!!
            for (i in 0..listofPlayers.size - 1) {
                val playerObject = listofPlayers.get(i)
                if (playerObject.teamId == matchObject.teamAInfo!!.teamId) {
                    playerObject.setPlayerIcon(R.drawable.ic_player_all_teama)
                } else {
                    playerObject.setPlayerIcon(R.drawable.ic_player_all_teamb)
                }
            }
            listAllRounder.addAll(listofPlayers)
        }
    }

    private fun addBowler() {
        listBowler.clear()
        if (hasmapPlayers.containsKey(CREATE_TEAM_BOWLER)) {
            val listofPlayers = hasmapPlayers.get(CREATE_TEAM_BOWLER)!!
            for (i in 0..listofPlayers.size - 1) {
                val playerObject = listofPlayers.get(i)
                if (playerObject.teamId == matchObject.teamAInfo!!.teamId) {
                    playerObject.setPlayerIcon(R.drawable.ic_player_bowler_teama)
                } else {
                    playerObject.setPlayerIcon(R.drawable.ic_player_bowler_teamb)
                }
            }
            listBowler.addAll(listofPlayers)
        }
    }
}