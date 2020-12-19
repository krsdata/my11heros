package com.my11heros.ui.mymatches

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.edify.atrist.listener.OnMatchTimerStarted
import com.my11heros.ContestActivity
import com.my11heros.MainActivity
import com.my11heros.R
import com.my11heros.databinding.FragmentMyUpcomingBinding
import com.my11heros.models.UpcomingMatchesModel
import com.my11heros.network.IApiMethod
import com.my11heros.network.RequestModel
import com.my11heros.network.WebServiceClient
import com.my11heros.ui.home.models.UsersPostDBResponse
import com.my11heros.utils.BindingUtils
import com.my11heros.utils.MyPreferences
import com.my11heros.utils.MyUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class MyUpcomingMatchesFragment : Fragment() {

    private var mBinding: FragmentMyUpcomingBinding? = null
    lateinit var adapter: MyMatchesAdapter
    var checkinArrayList = ArrayList<UpcomingMatchesModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_my_upcoming, container, false
        )
        mBinding!!.recyclerMyUpcoming.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)


        adapter = MyMatchesAdapter(requireActivity(), checkinArrayList)
        mBinding!!.recyclerMyUpcoming.adapter = adapter
        adapter.onItemClick = { objects ->
            val intent = Intent(requireActivity(), ContestActivity::class.java)
            intent.putExtra(ContestActivity.SERIALIZABLE_KEY_UPCOMING_MATCHES, objects)
            startActivity(intent)
        }
        if (checkinArrayList.size > 0) {
            mBinding!!.linearEmptyContest.visibility = View.GONE
        } else {
            mBinding!!.linearEmptyContest.visibility = View.VISIBLE
        }

        mBinding!!.btnEmptyView.setOnClickListener(View.OnClickListener {
            (activity as MainActivity).viewUpcomingMatches()
        })
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
        //if (checkinArrayList.size == 0) {
            mBinding!!.progressBar.visibility = View.VISIBLE
        //}
        mBinding!!.linearEmptyContest.visibility = View.GONE
        val models = RequestModel()
        models.user_id = MyPreferences.getUserID(requireActivity())!!
        models.action_type = "upcoming"


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
                                checkinArrayList.addAll(responseModel.matchdatalist!!.get(0).upcomingMatchHistory!!)
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
        val tradeinfoModels: ArrayList<UpcomingMatchesModel>
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var onItemClick: ((UpcomingMatchesModel) -> Unit)? = null
        private var matchesListObject = tradeinfoModels


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.matches_row_upcoming_inner, parent, false)
            return DataViewHolder(view)

        }

        fun getRandomColor(): Int {
            val rnd = Random()
            return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        }

        override fun onBindViewHolder(parent: RecyclerView.ViewHolder, viewType: Int) {
            val objectVal = matchesListObject[viewType]
            val viewHolder: DataViewHolder = parent as DataViewHolder
            if (objectVal.isLineup) {
                viewHolder.matchTitle?.visibility = View.VISIBLE
            } else {
                viewHolder.matchTitle?.visibility = View.INVISIBLE
            }
            viewHolder.tournamentTitle?.text = objectVal.leagueTitle
            // viewHolder?.matchProgress?.text = ""+objectVal.timestampEnd
            viewHolder.opponent1?.text = objectVal.teamAInfo!!.teamShortName
            viewHolder.opponent2?.text = objectVal.teamBInfo!!.teamShortName

            if (!TextUtils.isEmpty(objectVal.dateStart)) {
                viewHolder.matchtime.visibility = View.VISIBLE
                viewHolder.matchtime.text = objectVal.dateStart
            } else {
                viewHolder.matchtime.visibility = View.GONE
            }

            if (objectVal.freeContest) {
                viewHolder.freeView?.visibility = View.VISIBLE
            } else {
                viewHolder.freeView?.visibility = View.GONE
            }
            viewHolder.teamAColorView?.setBackgroundColor(getRandomColor())
            viewHolder.teamBColorView?.setBackgroundColor(getRandomColor())

            BindingUtils.countDownStartForAdaptors(objectVal.timestampStart,
                object : OnMatchTimerStarted {
                    override fun onTimeFinished() {
                        viewHolder.matchProgress.text = objectVal.statusString
                    }

                    override fun onTicks(time: String) {
                        viewHolder.matchProgress.text = time
                    }

                })
            if (!TextUtils.isEmpty(objectVal.contestName)) {
                viewHolder.upcomingLinearContestView.visibility = View.VISIBLE
                viewHolder.contestName?.text = "" + objectVal.contestName
                viewHolder.contestPrice?.text = "" + objectVal.contestPrize
            } else {
                viewHolder.upcomingLinearContestView.visibility = View.INVISIBLE
            }

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
            val matchTitle = itemView.findViewById<TextView>(R.id.upcoming_match_title)
            val tournamentTitle = itemView.findViewById<TextView>(R.id.tournament_title)
            val teamAColorView = itemView.findViewById<View>(R.id.countrycolorview)
            val teamBColorView = itemView.findViewById<View>(R.id.countrybcolorview)
            val opponent1 = itemView.findViewById<TextView>(R.id.upcoming_opponent1)
            val opponent2 = itemView.findViewById<TextView>(R.id.upcoming_opponent2)
            val matchtime = itemView.findViewById<TextView>(R.id.match_time)
            val freeView = itemView.findViewById<TextView>(R.id.free_view)
            val matchProgress = itemView.findViewById<TextView>(R.id.upcoming_match_progress)
            val upcomingLinearContestView =
                itemView.findViewById<LinearLayout>(R.id.upcoming_linear_contest_view)
            val contestName = itemView.findViewById<TextView>(R.id.upcoming_contest_name)
            val contestPrice = itemView.findViewById<TextView>(R.id.upcoming_contest_price)
        }
    }
}