package com.my11heros.ui.leadersboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.my11heros.ContestActivity
import com.my11heros.R
import com.my11heros.databinding.FragmentPrizeBreakupBinding
import com.my11heros.models.UpcomingMatchesModel
import com.my11heros.network.IApiMethod
import com.my11heros.network.RequestModel
import com.my11heros.network.WebServiceClient
import com.my11heros.ui.contest.models.ContestModelLists
import com.my11heros.ui.home.models.UsersPostDBResponse
import com.my11heros.ui.leadersboard.models.PrizeBreakUpModels
import com.my11heros.utils.MyPreferences
import com.my11heros.utils.MyUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PrizeBreakupFragment : Fragment() {

    private var mBinding: FragmentPrizeBreakupBinding? = null
    lateinit var adapter: PrizeBreakUpAdapter
    var prizeBreakupList = ArrayList<PrizeBreakUpModels>()

    var matchObject: UpcomingMatchesModel? = null
    var contestObject: ContestModelLists? = null

    companion object {
        fun newInstance(bundle: Bundle): PrizeBreakupFragment {
            val fragment = PrizeBreakupFragment()
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
    ): View {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_prize_breakup, container, false
        )
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding!!.prizeViewRecycler.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        adapter = PrizeBreakUpAdapter(
            requireActivity(),
            prizeBreakupList
        )
        mBinding!!.prizeViewRecycler.adapter = adapter
        if (!MyUtils.isConnectedWithInternet(activity as AppCompatActivity)) {
            MyUtils.showToast(activity as AppCompatActivity, "No Internet connection found")
            return
        }
        if (contestObject!!.winnerCounts!!.toInt() == 0) {
            mBinding!!.winnerGlory.visibility = View.VISIBLE
        } else {
            mBinding!!.winnerGlory.visibility = View.GONE
            getPrizeBreakup()
        }
    }

    fun getPrizeBreakup() {
        mBinding!!.progressBar.visibility = View.VISIBLE
        val models = RequestModel()
        models.user_id = MyPreferences.getUserID(requireActivity())!!
        models.token = MyPreferences.getToken(requireActivity())!!
        models.match_id = "" + matchObject!!.matchId
        models.contest_id = "" + contestObject!!.id
        WebServiceClient(requireActivity()).client.create(IApiMethod::class.java)
            .getPrizeBreakUp(models)
            .enqueue(object : Callback<UsersPostDBResponse?> {
                override fun onFailure(call: Call<UsersPostDBResponse?>?, t: Throwable?) {

                }

                override fun onResponse(
                    call: Call<UsersPostDBResponse?>?,
                    response: Response<UsersPostDBResponse?>?
                ) {
                    val res = response!!.body()
                    if (res != null) {
                        if (isVisible) {
                            mBinding!!.progressBar.visibility = View.GONE
                            val responseModel = res.responseObject
                            if (responseModel!!.prizeBreakUpModelsList!!.size > 0) {
                                prizeBreakupList.clear()

                                prizeBreakupList.addAll(responseModel.prizeBreakUpModelsList!!)
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
            })
    }

    inner class PrizeBreakUpAdapter(
        val context: Context,
        rangeModels: ArrayList<PrizeBreakUpModels>
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var onItemClick: ((PrizeBreakUpModels) -> Unit)? = null
        private var matchesListObject = rangeModels


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.prize_breakup_rows, parent, false)
            return MyMatchViewHolder(view)
        }

        override fun onBindViewHolder(parent: RecyclerView.ViewHolder, viewType: Int) {
            val objectVal = matchesListObject[viewType]
            val viewHolder: MyMatchViewHolder = parent as MyMatchViewHolder
            viewHolder.rankRange.text = objectVal.rangeName
            viewHolder.winnerPrize.text = "₹" + objectVal.winnersPrice
        }

        override fun getItemCount(): Int {
            return matchesListObject.size
        }

        inner class MyMatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val rankRange = itemView.findViewById<TextView>(R.id.rank_range)
            val winnerPrize = itemView.findViewById<TextView>(R.id.winner_rpize)
        }
    }
}