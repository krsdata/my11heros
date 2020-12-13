package com.my11heros.ui.contest

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.deliverdas.customers.utils.HardwareInfoManager
import com.edify.atrist.listener.OnContestEvents
import com.edify.atrist.listener.OnContestLoadedListener
import com.my11heros.*
import com.my11heros.databinding.FragmentAllContestBinding
import com.my11heros.models.ContestsParentModels
import com.my11heros.models.UpcomingMatchesModel
import com.my11heros.network.IApiMethod
import com.my11heros.network.RequestModel
import com.my11heros.network.WebServiceClient
import com.my11heros.ui.contest.adaptors.ContestAdapter
import com.my11heros.ui.contest.adaptors.ContestListAdapter
import com.my11heros.ui.contest.models.ContestModelLists
import com.my11heros.ui.home.models.UsersPostDBResponse
import com.my11heros.utils.BindingUtils
import com.my11heros.utils.MyPreferences
import com.my11heros.utils.MyUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContestFragment : Fragment() {


    private var objectMatches: UpcomingMatchesModel? = null
    var matchObject: UpcomingMatchesModel? = null
    var mListenerContestEvents: OnContestEvents? = null
    private lateinit var mListener: OnContestLoadedListener
    private var mBinding: FragmentAllContestBinding? = null
    lateinit var adapter: ContestAdapter
    private lateinit var spotSizeFilterAdaptor: ContestListAdapter
    var allContestListData = ArrayList<ContestsParentModels>()
    var filterSpotsListData = ArrayList<ContestModelLists>()
    var isEntryAscending = false
    private var isVisibleToUser: Boolean = false

    companion object {
        fun newInstance(bundle: Bundle): ContestFragment {
            val fragment = ContestFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        objectMatches =
            requireArguments().get(ContestActivity.SERIALIZABLE_KEY_MATCH_OBJECT) as UpcomingMatchesModel

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_all_contest, container, false
        )
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        matchObject = objectMatches
        mBinding!!.contestViewRecycler.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        adapter = ContestAdapter(
            requireActivity(),
            allContestListData,
            matchObject,
            mListenerContestEvents!!
        )
        mBinding!!.linearEmptyContest.visibility = View.GONE
        mBinding!!.contestViewRecycler.adapter = adapter


        registerSpotSizeSelection()

        mBinding!!.recyclerBySpotsize.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        spotSizeFilterAdaptor = ContestListAdapter(
            requireActivity(),
            filterSpotsListData,
            matchObject!!,
            mListenerContestEvents,
            0
        )
        mBinding!!.recyclerBySpotsize.adapter = spotSizeFilterAdaptor


        mBinding!!.linearEmptyContest.visibility = View.GONE
        mBinding!!.contestViewRecycler.adapter = adapter

        mBinding!!.btnCreateTeam.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, CreateTeamActivity::class.java)
            intent.putExtra(CreateTeamActivity.SERIALIZABLE_MATCH_KEY, matchObject)
            requireActivity().startActivityForResult(intent, CreateTeamActivity.CREATETEAM_REQUESTCODE)
        })

        mBinding!!.btnEmptyView.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, WebActivity::class.java)
            intent.putExtra(WebActivity.KEY_TITLE, BindingUtils.WEB_TITLE_PRIVACY_POLICY)
            intent.putExtra(WebActivity.KEY_URL, BindingUtils.WEBVIEW_PRIVACY)
            requireActivity().startActivity(intent)
        })
        mBinding!!.contestRefresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            getAllContest()
        })
    }

    private fun registerSpotSizeSelection() {

        mBinding!!.sortBy2spots.setOnClickListener(View.OnClickListener {

            mBinding!!.sortBy2spots.setBackgroundResource(R.drawable.circle_app_color)
            mBinding!!.sortBy2spots.setTextColor(resources.getColor(R.color.white))

            mBinding!!.sortBy3spots.setBackgroundResource(R.drawable.circle_grey)
            mBinding!!.sortBy3spots.setTextColor(resources.getColor(R.color.black))

            mBinding!!.sortBy4spots.setBackgroundResource(R.drawable.circle_grey)
            mBinding!!.sortBy4spots.setTextColor(resources.getColor(R.color.black))

            mBinding!!.filterByAll.setBackgroundResource(R.drawable.circle_grey)
            mBinding!!.filterByAll.setTextColor(resources.getColor(R.color.black))

            mBinding!!.linearEntryPrizeSort.setBackgroundResource(R.drawable.circle_grey)
            mBinding!!.prizeArrow.visibility = View.GONE
            mBinding!!.rupees.setTextColor(resources.getColor(R.color.black))

            showRecyclerListBySpotSize(2)

        })
        mBinding!!.sortBy3spots.setOnClickListener(View.OnClickListener {

            mBinding!!.sortBy2spots.setBackgroundResource(R.drawable.circle_grey)
            mBinding!!.sortBy2spots.setTextColor(resources.getColor(R.color.black))

            mBinding!!.sortBy3spots.setBackgroundResource(R.drawable.circle_app_color)
            mBinding!!.sortBy3spots.setTextColor(resources.getColor(R.color.white))

            mBinding!!.sortBy4spots.setBackgroundResource(R.drawable.circle_grey)
            mBinding!!.sortBy4spots.setTextColor(resources.getColor(R.color.black))

            mBinding!!.filterByAll.setBackgroundResource(R.drawable.circle_grey)
            mBinding!!.filterByAll.setTextColor(resources.getColor(R.color.black))

            mBinding!!.linearEntryPrizeSort.setBackgroundResource(R.drawable.circle_grey)
            mBinding!!.prizeArrow.visibility = View.GONE
            mBinding!!.rupees.setTextColor(resources.getColor(R.color.black))

            showRecyclerListBySpotSize(3)


        })
        mBinding!!.sortBy4spots.setOnClickListener(View.OnClickListener {

            mBinding!!.sortBy2spots.setBackgroundResource(R.drawable.circle_grey)
            mBinding!!.sortBy2spots.setTextColor(resources.getColor(R.color.black))

            mBinding!!.sortBy3spots.setBackgroundResource(R.drawable.circle_grey)
            mBinding!!.sortBy3spots.setTextColor(resources.getColor(R.color.black))

            mBinding!!.sortBy4spots.setBackgroundResource(R.drawable.circle_app_color)
            mBinding!!.sortBy4spots.setTextColor(resources.getColor(R.color.white))

            mBinding!!.filterByAll.setBackgroundResource(R.drawable.circle_grey)
            mBinding!!.filterByAll.setTextColor(resources.getColor(R.color.black))

            mBinding!!.linearEntryPrizeSort.setBackgroundResource(R.drawable.circle_grey)
            mBinding!!.prizeArrow.visibility = View.GONE
            mBinding!!.rupees.setTextColor(resources.getColor(R.color.black))

            showRecyclerListBySpotSize(4)
        })

        mBinding!!.linearEntryPrizeSort.setOnClickListener(View.OnClickListener {

            mBinding!!.sortBy2spots.setBackgroundResource(R.drawable.circle_grey)
            mBinding!!.sortBy2spots.setTextColor(resources.getColor(R.color.black))

            mBinding!!.sortBy3spots.setBackgroundResource(R.drawable.circle_grey)
            mBinding!!.sortBy3spots.setTextColor(resources.getColor(R.color.black))

            mBinding!!.sortBy4spots.setBackgroundResource(R.drawable.circle_grey)
            mBinding!!.sortBy4spots.setTextColor(resources.getColor(R.color.black))

            mBinding!!.filterByAll.setBackgroundResource(R.drawable.circle_grey)
            mBinding!!.filterByAll.setTextColor(resources.getColor(R.color.black))



            mBinding!!.linearEntryPrizeSort.setBackgroundResource(R.drawable.circle_app_color)
            mBinding!!.prizeArrow.visibility = View.VISIBLE

            mBinding!!.rupees.setTextColor(resources.getColor(R.color.white))

            filterByEntryPrize()
        })


        mBinding!!.filterByAll.setOnClickListener(View.OnClickListener {

            selectAllContest()

        })
    }

    private fun selectAllContest() {
        showAllContestRecycler()

        mBinding!!.linearEntryPrizeSort.setBackgroundResource(R.drawable.circle_grey)
        mBinding!!.rupees.setTextColor(resources.getColor(R.color.black))
        mBinding!!.prizeArrow.visibility = View.GONE

        mBinding!!.sortBy2spots.setBackgroundResource(R.drawable.circle_grey)
        mBinding!!.sortBy2spots.setTextColor(resources.getColor(R.color.black))

        mBinding!!.sortBy3spots.setBackgroundResource(R.drawable.circle_grey)
        mBinding!!.sortBy3spots.setTextColor(resources.getColor(R.color.black))

        mBinding!!.sortBy4spots.setBackgroundResource(R.drawable.circle_grey)
        mBinding!!.sortBy4spots.setTextColor(resources.getColor(R.color.black))

        mBinding!!.filterByAll.setBackgroundResource(R.drawable.circle_app_color)
        mBinding!!.filterByAll.setTextColor(resources.getColor(R.color.white))
    }

    private fun showAllContestRecycler() {
        mBinding!!.contestViewRecycler.visibility = View.VISIBLE
        mBinding!!.recyclerBySpotsize.visibility = View.GONE
    }

    private fun filterByEntryPrize() {

        isEntryAscending = !isEntryAscending
        if (isEntryAscending) {
            mBinding!!.prizeArrow.setImageResource(R.drawable.ic_baseline_arrow_upward_24)
        } else {
            mBinding!!.prizeArrow.setImageResource(R.drawable.ic_baseline_arrow_downward_24)
        }
        mBinding!!.contestViewRecycler.visibility = View.GONE
        mBinding!!.recyclerBySpotsize.visibility = View.VISIBLE
        filterSpotsListData.clear()
        val filterValues = ArrayList<ContestModelLists>()
        for (i in 0..allContestListData.size - 1) {
            val values = allContestListData.get(i).allContestsRunning
            if (values != null && values.size > 0) {

                if (isEntryAscending) {
                    val sortedEntryPrizes = values.sortedBy { it -> it.entryFees }
                    sortedEntryPrizes.forEach { s -> filterValues.add(s) }
                } else {
                    val sortedEntryPrizes = values.sortedByDescending { it -> it.entryFees }
                    sortedEntryPrizes.forEach { s -> filterValues.add(s) }
                }


//                println("Sorting by ascending : ")
//                val sortedStudents = students.sortedBy { student -> student.name }
//                sortedStudents.forEach { s -> println(s.name) }
//
//                println("Sorting by descending : ")
//                val dSortedStudents = sortedStudents.sortedByDescending { student -> student.name }
//                dSortedStudents.forEach { s -> println(s.name) }
            }
        }
        //mBinding!!.recyclerBySpotsize.scrollToPosition(filterSpotsListData!!.size - 1)
        if (isEntryAscending) {
            val objectPrize = filterValues.sortedBy { it -> it.entryFees }
            objectPrize.forEach { s -> filterSpotsListData.add(s) }
        } else {
            val objectPrize = filterValues.sortedByDescending { it -> it.entryFees }
            objectPrize.forEach { s -> filterSpotsListData.add(s) }
        }
        spotSizeFilterAdaptor.notifyDataSetChanged()
        mBinding!!.recyclerBySpotsize.scheduleLayoutAnimation()
        // mBinding!!.recyclerBySpotsize.smoothScrollToPosition(spotSizeFilterAdaptor.getItemCount() - 1);
    }

    private fun showRecyclerListBySpotSize(spotSize: Int) {
        mBinding!!.contestViewRecycler.visibility = View.GONE
        mBinding!!.recyclerBySpotsize.visibility = View.VISIBLE
        filterSpotsListData.clear()
        for (i in 0..allContestListData.size - 1) {
            val values = allContestListData.get(i).allContestsRunning
            if (values != null && values.size > 0) {
                for (j in 0..values.size - 1) {
                    val spotObject = values.get(j)
                    if (4 == spotSize && spotObject.totalSpots >= 4) {
                        filterSpotsListData.add(spotObject)
                    } else {
                        if (spotObject.totalSpots == spotSize) {
                            filterSpotsListData.add(spotObject)
                        }
                    }
                }
            }
        }
        //mBinding!!.recyclerBySpotsize.scrollToPosition(filterSpotsListData!!.size - 1)

        spotSizeFilterAdaptor.notifyDataSetChanged()
        //mBinding!!.recyclerBySpotsize.scheduleLayoutAnimation()
        mBinding!!.recyclerBySpotsize.smoothScrollToPosition(spotSizeFilterAdaptor.itemCount - 1)
    }

    override fun onResume() {
        super.onResume()
        if (!MyUtils.isConnectedWithInternet(activity as AppCompatActivity)) {
            MyUtils.showToast(activity as AppCompatActivity, "No Internet connection found")
            return
        }
        //if (isVisibleToUser) {
            getAllContest()
        //}
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        this.isVisibleToUser = isVisibleToUser
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnContestLoadedListener) {
            mListener = context
        } else {
            throw RuntimeException(
                "$context must implement OnContestLoadedListener"
            )
        }

        if (context is OnContestEvents) {
            mListenerContestEvents = context
        } else {
            throw RuntimeException(
                "$context must implement OnContestLoadedListener"
            )
        }

    }

    private fun getAllContest() {
        //var userInfo = (activity as PlugSportsApplication).userInformations
        selectAllContest()
        mBinding!!.contestRefresh.isRefreshing = true
        //mBinding!!.filterBar.visibility = View.GONE
        val models = RequestModel()
        models.user_id = MyPreferences.getUserID(requireActivity())!!
        // models.token =MyPreferences.getToken(activity!!)!!
        models.match_id = "" + matchObject!!.matchId
        models.token = MyPreferences.getToken(requireActivity())!!
        val deviceToken: String? = MyPreferences.getDeviceToken(requireActivity())
        models.deviceDetails = HardwareInfoManager(activity).collectData(deviceToken!!)

        WebServiceClient(requireActivity()).client.create(IApiMethod::class.java).getContestByMatch(models)
            .enqueue(object : Callback<UsersPostDBResponse?> {
                override fun onFailure(call: Call<UsersPostDBResponse?>?, t: Throwable?) {
                    if (isVisible) {
                        MyUtils.showToast(activity!! as AppCompatActivity, "Something went wrong!!")
                        mBinding!!.contestRefresh.isRefreshing = false
                    }
                }

                override fun onResponse(
                    call: Call<UsersPostDBResponse?>?,
                    response: Response<UsersPostDBResponse?>?
                ) {
                    if (!isVisible) {
                        return
                    }
                    mBinding!!.contestRefresh.isRefreshing = false
                    //mBinding!!.filterBar.visibility = View.VISIBLE
                    val res = response!!.body()
                    if (res != null && res.appMaintainance) {
                        val intent = Intent(activity, MaintainanceActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        activity!!.finish()
                    } else
                        if (res != null) {
                            BindingUtils.currentTimeStamp = res.systemTime
                            val responseModel = res.responseObject
                            if (responseModel!!.matchContestlist != null && responseModel.matchContestlist!!.size > 0) {
                                allContestListData.clear()
                                allContestListData.addAll(responseModel.matchContestlist!!)
                                adapter.setMatchesList(allContestListData)
                                mListener.onMyTeam(responseModel.myjoinedTeams!!)
                                mListener.onMyContest(responseModel.joinedContestDetails!!)
                            } else {
                                MyUtils.showToast(
                                    activity!! as AppCompatActivity,
                                    "No Contest available for this match $res"
                                )
                            }
                        }
                    updateEmptyViews()
                }
            })
    }

    fun updateEmptyViews() {
        if (allContestListData.size == 0) {
            mBinding!!.linearEmptyContest.visibility = View.VISIBLE
        } else {
            mBinding!!.linearEmptyContest.visibility = View.GONE
        }
    }
}