package com.my11heros.ui.leadersboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.my11heros.*
import com.my11heros.databinding.FragmentLeadersBoardBinding
import com.my11heros.models.UpcomingMatchesModel
import com.my11heros.models.UserInfo
import com.my11heros.network.IApiMethod
import com.my11heros.network.RequestModel
import com.my11heros.network.WebServiceClient
import com.my11heros.ui.contest.models.ContestModelLists
import com.my11heros.ui.createteam.models.PlayersInfoModel
import com.my11heros.ui.home.models.UsersPostDBResponse
import com.my11heros.ui.leadersboard.models.LeadersBoardModels
import com.my11heros.utils.BindingUtils
import com.my11heros.utils.CustomeProgressDialog
import com.my11heros.utils.MyPreferences
import com.my11heros.utils.MyUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LeadersBoardFragment : Fragment() {
    private lateinit var userInfo: UserInfo

    //private var isMatchStarted: Boolean?=false
    private lateinit var customeProgressDialog: CustomeProgressDialog
    private var teamName: String? = ""
    private var mBinding: FragmentLeadersBoardBinding? = null
    var adapter: LeadersBoardAdapter? = null
    var leadersBoardList = ArrayList<LeadersBoardModels>()

    var matchObject: UpcomingMatchesModel? = null
    var contestObject: ContestModelLists? = null

    companion object {
        fun newInstance(bundle: Bundle): LeadersBoardFragment {
            val fragment = LeadersBoardFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contestObject =
            requireArguments().get(ContestActivity.SERIALIZABLE_KEY_CONTEST_OBJECT) as ContestModelLists
        matchObject =
            requireArguments().get(ContestActivity.SERIALIZABLE_KEY_MATCH_OBJECT) as UpcomingMatchesModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_leaders_board, container, false
        )
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        customeProgressDialog = CustomeProgressDialog(activity)
        mBinding!!.progressBar.visibility = View.GONE
        mBinding!!.prizeLeadersboardRecycler.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        userInfo = (requireActivity().applicationContext as My11HerosApplication).userInformations
        val dividerItemDecoration = DividerItemDecoration(
            mBinding!!.prizeLeadersboardRecycler.context,
            RecyclerView.VERTICAL
        )
        mBinding!!.prizeLeadersboardRecycler.addItemDecoration(dividerItemDecoration)

        adapter = LeadersBoardAdapter(requireActivity(), leadersBoardList)
        mBinding!!.prizeLeadersboardRecycler.adapter = adapter
        adapter!!.onItemClick = { objects ->
            teamName = String.format("%s(%s)", objects.userInfo!!.fullName, objects.teamName)
            if (TextUtils.isEmpty(objects.userId)) {
                MyUtils.showToast(
                    requireActivity() as AppCompatActivity,
                    "System issue please contact Admin."
                )
            } else {
                if (objects.userId.equals(MyPreferences.getUserID(requireActivity()))) {
                    getPoints(objects.teamId, objects.userId)
                } else
                    if (matchObject!!.status != BindingUtils.MATCH_STATUS_UPCOMING) {
                        getPoints(objects.teamId, objects.userId)
                    } else {
                        MyUtils.showToast(
                            requireActivity() as AppCompatActivity,
                            "You cannot see other players teams, until match started"
                        )
                    }
            }


        }
        mBinding!!.swipeRefreshLeaderboard.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            getLeadersBoards()
        })
        setTotalTeamCounts(0)
        getLeadersBoards()

    }


    private fun setTotalTeamCounts(value: Int) {
        mBinding!!.totalTeamCounts.text = String.format("ALL TEAMS (%d)", value)
    }

    fun getPoints(teamId: Int, user_id: String) {
        customeProgressDialog.show()
        val models = RequestModel()
        models.user_id = MyPreferences.getUserID(requireActivity())!!
        //models.token =MyPreferences.getToken(activity!!)!!
        models.contest_id = "" + contestObject!!.id
        models.match_id = "" + matchObject!!.matchId
        models.team_id = teamId

        WebServiceClient(requireActivity()).client.create(IApiMethod::class.java).getPoints(models)
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
                        //var totalPoints = res.totalPoints
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
                            hasmapPlayers.put(
                                CreateTeamActivity.CREATE_TEAM_WICKET_KEEPER,
                                wktKeeperList
                            )
                            hasmapPlayers.put(CreateTeamActivity.CREATE_TEAM_BATSMAN, batsManList)
                            hasmapPlayers.put(
                                CreateTeamActivity.CREATE_TEAM_ALLROUNDER,
                                allRounderList
                            )
                            hasmapPlayers.put(CreateTeamActivity.CREATE_TEAM_BOWLER, allbowlerList)

                            BindingUtils.sendEventLogs(
                                activity!!,
                                "" + matchObject!!.matchId,
                                "" + contestObject!!.id,
                                user_id,
                                teamId,
                                (requireActivity().applicationContext as My11HerosApplication).userInformations,
                                "Last Seen"
                            )

                            val intent = Intent(activity, TeamPreviewActivity::class.java)
                            intent.putExtra(TeamPreviewActivity.KEY_TEAM_NAME, teamName)
                            intent.putExtra(TeamPreviewActivity.KEY_TEAM_ID, teamId)
                            intent.putExtra(CreateTeamActivity.SERIALIZABLE_MATCH_KEY, matchObject)
                            intent.putExtra(
                                TeamPreviewActivity.SERIALIZABLE_TEAM_PREVIEW_KEY,
                                hasmapPlayers
                            )
                            startActivity(intent)
                        }
                    }
                }
            })
    }

    fun getLeadersBoards() {
        // (activity as LeadersBoardActivity).updateScores()
        // customeProgressDialog.show()
        if (!isVisible) {
            return
        }
        mBinding!!.progressBar.visibility = View.VISIBLE
        val models = RequestModel()
        models.user_id = MyPreferences.getUserID(requireActivity())!!
        models.token = MyPreferences.getToken(requireActivity())!!
        models.contest_id = "" + contestObject!!.id
        models.match_id = "" + matchObject!!.matchId
        WebServiceClient(requireActivity()).client.create(IApiMethod::class.java)
            .getLeaderBoard(models)
            .enqueue(object : Callback<UsersPostDBResponse?> {
                override fun onFailure(call: Call<UsersPostDBResponse?>?, t: Throwable?) {
                    // MyUtils.showToast(activity!!.getWindow().getDecorView().getRootView(),t!!.localizedMessage)
                    //customeProgressDialog.dismiss()
                    if (!isVisible) {
                        return
                    }
                    mBinding!!.swipeRefreshLeaderboard.isRefreshing = false
                }

                override fun onResponse(
                    call: Call<UsersPostDBResponse?>?,
                    response: Response<UsersPostDBResponse?>?
                ) {
                    if (!isVisible) {
                        return
                    }
                    mBinding!!.progressBar.visibility = View.GONE
                    mBinding!!.swipeRefreshLeaderboard.isRefreshing = false
                    //customeProgressDialog.dismiss()
                    val res = response!!.body()
                    if (res != null) {
                        val responseModel = res.leaderBoardList
                        if (responseModel != null) {
                            if (responseModel != null) {
                                if (responseModel.size > 0) {
                                    leadersBoardList.clear()
                                    leadersBoardList.addAll(responseModel)
                                    adapter!!.notifyDataSetChanged()
                                    setTotalTeamCounts(responseModel.size)
                                }
                            }
                        }
//                        BindingUtils.sendEventLogs(
//                            activity!!,
//                            matchObject!!.matchId,contestObject!!.id,
//                            userInfo,
//                            BindingUtils.FIREBASE_EVENT_ITEM_ID_LEADERS_BOARD_REFRESH
//                        )
                    }
                }
            })
    }

    inner class LeadersBoardAdapter(
        val context: Context,
        rangeModels: ArrayList<LeadersBoardModels>
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var onItemClick: ((LeadersBoardModels) -> Unit)? = null
        private var matchesListObject = rangeModels


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.leaders_board_rows, parent, false)
            return MyMatchViewHolder(view)
        }

        override fun onBindViewHolder(parent: RecyclerView.ViewHolder, viewType: Int) {
            val objectVal = matchesListObject[viewType]
            val viewHolder: MyMatchViewHolder = parent as MyMatchViewHolder
            viewHolder.teamName.text =
                String.format("%s(%s)", objectVal.userInfo!!.teamName, objectVal.teamName)
            viewHolder.userPoints.text = objectVal.teamPoints
            viewHolder.playeRanks.text = objectVal.teamRanks
            if (matchObject!!.status == BindingUtils.MATCH_STATUS_LIVE) {
                viewHolder.teamWonStatus.text = "Winning Zone"
            } else {
                viewHolder.teamWonStatus.text = String.format(
                    "Won ₹%s",
                    objectVal.teamWonStatus
                )
            }
            if (!TextUtils.isEmpty(objectVal.teamWonStatus)) {
                if (objectVal.teamWonStatus.toDouble() > 0) {
                    viewHolder.teamWonStatus.visibility = View.VISIBLE
                } else {
                    viewHolder.teamWonStatus.visibility = View.INVISIBLE
                }
            }


            if (!TextUtils.isEmpty(objectVal.userInfo.profileImage)) {
                Glide.with(context)
                    .load(objectVal.userInfo.profileImage)
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .into(viewHolder.profileImage)
            } else {
                Glide.with(context)
                    .load(R.drawable.ic_profile)
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .into(viewHolder.profileImage)
            }
        }

        override fun getItemCount(): Int {
            return matchesListObject.size
        }

        inner class MyMatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            init {
                itemView.setOnClickListener {
                    onItemClick?.invoke(matchesListObject[adapterPosition])
                }
            }

            val profileImage = itemView.findViewById<ImageView>(R.id.profile_image)
            val teamName = itemView.findViewById<TextView>(R.id.team_name)
            val userPoints = itemView.findViewById<TextView>(R.id.points)
            val playeRanks = itemView.findViewById<TextView>(R.id.player_rank)
            val teamWonStatus = itemView.findViewById<TextView>(R.id.team_won_status)
            val imgMatchStatus = itemView.findViewById<ImageView>(R.id.match_status)
        }
    }
}