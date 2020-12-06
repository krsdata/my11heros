package com.my11heros.ui.mymatches

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import com.my11heros.ContestActivity
import com.my11heros.MainActivity
import com.my11heros.R
import com.my11heros.databinding.FragmentMyLiveBinding
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


class MyLiveMatchesFragment : Fragment() {

    private var mBinding: FragmentMyLiveBinding? = null
    lateinit var adapter: MyMatchesAdapter
    var checkinArrayList = ArrayList<JoinedMatchModel>()
    private var TAG: String = MyLiveMatchesFragment::class.java.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_my_live, container, false
        )

        mBinding!!.recyclerMyUpcoming.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        //initDummyContent()

        adapter = MyMatchesAdapter(requireActivity(), checkinArrayList)
        mBinding!!.recyclerMyUpcoming.adapter = adapter

        adapter.onItemClick = { objects ->
            val intent = Intent(requireActivity(), ContestActivity::class.java)
            //intent.putExtra(ContestActivity.SERIALIZABLE_KEY_UPCOMING_MATCHES,objects)
            intent.putExtra(ContestActivity.SERIALIZABLE_KEY_JOINED_CONTEST, objects)
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

    /*override fun onPause() {
        super.onPause()
        BindingUtils.stopTimer()
    }*/

    override fun onResume() {
        super.onResume()
        if (!MyUtils.isConnectedWithInternet(activity as AppCompatActivity)) {
            MyUtils.showToast(activity as AppCompatActivity, "No Internet connection found")
            return
        }
        Log.e(TAG, "onResume")
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
        models.action_type = "live"

        WebServiceClient(requireActivity()).client.create(IApiMethod::class.java)
            .getMatchHistory(models)
            .enqueue(object : Callback<UsersPostDBResponse?> {
                override fun onFailure(call: Call<UsersPostDBResponse?>?, t: Throwable?) {
                    if (mBinding!!.progressBar.visibility == View.VISIBLE) {
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
                                checkinArrayList.addAll(responseModel.matchdatalist!!.get(0).liveMatchHistory!!)
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
                .inflate(R.layout.matches_row_upcoming_inner, parent, false)
            return DataViewHolder(view)

        }

        fun getRandomColor(): Int {
            val rnd = Random()
            val color: Int = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))

            return color
        }

        override fun onBindViewHolder(parent: RecyclerView.ViewHolder, viewType: Int) {
            val objectVal = matchesListObject[viewType]
            val viewHolder: DataViewHolder = parent as DataViewHolder
            viewHolder.matchTitle?.visibility = View.GONE
            viewHolder.tournamentTitle?.visibility = View.VISIBLE
            // viewHolder?.matchProgress?.text = ""+objectVal.timestampEnd
            viewHolder.tournamentTitle?.text = objectVal.matchTitle
            viewHolder.opponent1?.text = objectVal.teamAInfo!!.teamShortName
            viewHolder.opponent2?.text = objectVal.teamBInfo!!.teamShortName
            viewHolder.freeView?.visibility = View.GONE
            viewHolder.matchtime?.visibility = View.GONE

            viewHolder.teamAColorView?.setBackgroundColor(getRandomColor())
            viewHolder.teamBColorView?.setBackgroundColor(getRandomColor())

            viewHolder.matchProgress.text = objectVal.statusString
//            BindingUtils.countDownStartForAdaptors(objectVal.timestampStart,object : OnMatchTimerStarted{
//                override fun onTimeFinished() {
//                    viewHolder?.matchProgress.setText(objectVal.statusString)
//                }
//
//                override fun onTicks(time:String) {
//                    viewHolder?.matchProgress.setText(time)
//                }
//
//            })
//            if (!TextUtils.isEmpty(objectVal.contestName)) {
//                viewHolder?.upcomingLinearContestView.visibility = View.VISIBLE;
//                viewHolder?.contestName?.text = "" + objectVal.contestName
//                viewHolder?.contestPrice?.text = "" + objectVal.contestPrize
//            } else {
            viewHolder.upcomingLinearContestView.visibility = View.INVISIBLE
            //  }

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
            val freeView = itemView.findViewById<TextView>(R.id.free_view)
            val matchProgress = itemView.findViewById<TextView>(R.id.upcoming_match_progress)
            val upcomingLinearContestView =
                itemView.findViewById<LinearLayout>(R.id.upcoming_linear_contest_view)
            val matchtime = itemView.findViewById<TextView>(R.id.match_time)
        }
    }
}