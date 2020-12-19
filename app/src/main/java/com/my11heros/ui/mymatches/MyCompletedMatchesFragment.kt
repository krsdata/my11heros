package com.my11heros.ui.mymatches

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.my11heros.ContestActivity
import com.my11heros.MainActivity
import com.my11heros.R
import com.my11heros.databinding.FragmentMyCompletedBinding
import com.my11heros.models.JoinedMatchModel
import com.my11heros.network.IApiMethod
import com.my11heros.network.RequestModel
import com.my11heros.network.WebServiceClient
import com.my11heros.ui.home.models.UsersPostDBResponse
import com.my11heros.utils.MyPreferences
import com.my11heros.utils.MyUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class MyCompletedMatchesFragment : Fragment() {

    private var mBinding: FragmentMyCompletedBinding? = null
    lateinit var adapter: MyMatchesAdapter
    var checkinArrayList = ArrayList<JoinedMatchModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_my_completed, container, false
        )
        mBinding!!.recyclerMyUpcoming.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        adapter = MyMatchesAdapter(requireActivity(), checkinArrayList)
        mBinding!!.recyclerMyUpcoming.adapter = adapter
        adapter.onItemClick = { objects ->
            val intent = Intent(requireActivity(), ContestActivity::class.java)
            intent.putExtra(ContestActivity.SERIALIZABLE_KEY_JOINED_CONTEST, objects)
            requireActivity().startActivity(intent)
        }
        if (checkinArrayList.size > 0) {
            mBinding!!.linearEmptyContest.visibility = View.GONE
        } else {
            mBinding!!.linearEmptyContest.visibility = View.VISIBLE
        }

        mBinding!!.btnEmptyView.setOnClickListener {
            (activity as MainActivity).viewUpcomingMatches()
        }
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        if (!MyUtils.isConnectedWithInternet(activity as AppCompatActivity)) {
            MyUtils.showToast(activity as AppCompatActivity, "No Internet connection found")
            return
        }
        getMatchHistory()
    }

    private fun getMatchHistory() {
        if (!MyUtils.isConnectedWithInternet(activity as AppCompatActivity)) {
            MyUtils.showToast(activity as AppCompatActivity, "No Internet connection found")
            return
        }
        if (checkinArrayList.size == 0) {
            mBinding!!.progressBar.visibility = View.VISIBLE
        }
        mBinding!!.linearEmptyContest.visibility = View.GONE
        val models = RequestModel()
        models.user_id = MyPreferences.getUserID(requireActivity())!!
        models.action_type = "completed"

        WebServiceClient(requireActivity()).client.create(IApiMethod::class.java)
            .getMatchHistory(models)
            .enqueue(object : Callback<UsersPostDBResponse?> {
                override fun onFailure(call: Call<UsersPostDBResponse?>?, t: Throwable?) {
                    if(mBinding!!.progressBar.visibility == View.VISIBLE){
                        mBinding!!.progressBar.visibility = View.GONE
                    }
                    updateEmptyViews()
                }

                override fun onResponse(
                    call: Call<UsersPostDBResponse?>?,
                    response: Response<UsersPostDBResponse?>?
                ) {
                    mBinding!!.progressBar.visibility = View.GONE
                    val res = response!!.body()
                    if (res != null) {
                        val responseModel = res.responseObject
                        if (responseModel != null) {
                            if (responseModel.matchdatalist != null && responseModel.matchdatalist!!.size > 0) {
                                checkinArrayList.clear()
                                checkinArrayList.addAll(responseModel.matchdatalist!!.get(0).completedMatchHistory!!)
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
                    updateEmptyViews()
                }
            })
    }

    private fun updateEmptyViews() {
        if (checkinArrayList.size > 0) {
            mBinding!!.linearEmptyContest.visibility = View.GONE
        } else {
            mBinding!!.linearEmptyContest.visibility = View.VISIBLE
        }
    }

    inner class MyMatchesAdapter(
        val context: Context,
        val tradeinfoModels: ArrayList<JoinedMatchModel>
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var onItemClick: ((JoinedMatchModel) -> Unit)? = null
        private var matchesListObject = tradeinfoModels

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.matches_row_completed, parent, false)
            return DataViewHolder(view)
        }

        private fun getRandomColor(): Int {
            val rnd = Random()
            return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        }

        override fun onBindViewHolder(parent: RecyclerView.ViewHolder, viewType: Int) {
            val objectVal = matchesListObject[viewType]
            val viewHolder: MyMatchesAdapter.DataViewHolder =
                parent as MyMatchesAdapter.DataViewHolder
            viewHolder.matchTitle?.text = objectVal.matchTitle
            viewHolder.matchStatus?.text = objectVal.statusString
            viewHolder.matchProgress.text = objectVal.dateStart
            // viewHolder?.matchProgress?.text = ""+objectVal.timestampEnd
            viewHolder.opponent1?.text = objectVal.teamAInfo!!.teamShortName
            viewHolder.opponent2?.text = objectVal.teamBInfo!!.teamShortName
            viewHolder.winningPrice?.text = String.format("You Won ₹%s", objectVal.prizeAmount)

            viewHolder.totalTeamCreated?.text = String.format("%d", objectVal.totalTeams)
            viewHolder.totalContestJoined?.text = String.format("%d", objectVal.totalJoinContests)

            viewHolder.teamAColorView?.setBackgroundColor(getRandomColor())
            viewHolder.teamBColorView?.setBackgroundColor(getRandomColor())

//            BindingUtils.countDownStart(objectVal.timestampStart,object : OnMatchTimerStarted {
//                override fun onTimeFinished() {
//                    viewHolder?.matchProgress.setText(objectVal.statusString)
//                }
//
//                override fun onTicks(time:String) {
//                    viewHolder?.matchProgress.setText(time)
//                }
//
//            })

            Glide.with(context)
                .load(objectVal.teamAInfo!!.logoUrl)
                .placeholder(R.drawable.placeholder_player_teama)
                .into(viewHolder.teamALogo)

            Glide.with(context)
                .load(objectVal.teamBInfo!!.logoUrl)
                .placeholder(R.drawable.placeholder_player_teama)
                .into(viewHolder.teamBLogo)
        }

        override fun getItemCount(): Int {
            return matchesListObject.size
        }

        inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            init {
                itemView.setOnClickListener {
                    onItemClick?.invoke(matchesListObject[adapterPosition])
                }
            }

            val teamALogo = itemView.findViewById<ImageView>(R.id.teama_logo)
            val teamBLogo = itemView.findViewById<ImageView>(R.id.teamb_logo)
            val matchTitle = itemView.findViewById<TextView>(R.id.completed_match_title)
            val matchStatus = itemView.findViewById<TextView>(R.id.completed_match_status)
            val teamAColorView = itemView.findViewById<View>(R.id.countrycolorview)
            val teamBColorView = itemView.findViewById<View>(R.id.countrybcolorview)
            val opponent1 = itemView.findViewById<TextView>(R.id.upcoming_opponent1)
            val opponent2 = itemView.findViewById<TextView>(R.id.upcoming_opponent2)
            val matchProgress = itemView.findViewById<TextView>(R.id.completed_match_date)
            val winningPrice = itemView.findViewById<TextView>(R.id.winning_price)
            val totalTeamCreated = itemView.findViewById<TextView>(R.id.total_team_created)
            val totalContestJoined = itemView.findViewById<TextView>(R.id.total_contest_joined)
        }
    }
}