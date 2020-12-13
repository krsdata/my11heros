package com.my11heros.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.deliverdas.customers.utils.HardwareInfoManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.my11heros.MainActivity
import com.my11heros.MaintainanceActivity
import com.my11heros.R
import com.my11heros.SportsFightApplication
import com.my11heros.adaptors.MatchesAdapter
import com.my11heros.databinding.FragmentAllGamesBinding
import com.my11heros.listener.RecyclerViewLoadMoreScroll
import com.my11heros.models.MatchesModels
import com.my11heros.network.IApiMethod
import com.my11heros.network.RequestModel
import com.my11heros.network.WebServiceClient
import com.my11heros.ui.BaseFragment
import com.my11heros.ui.home.models.UsersPostDBResponse
import com.my11heros.utils.BindingUtils
import com.my11heros.utils.MyPreferences
import com.my11heros.utils.MyUtils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FixtureCricketFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {
    //var listener : OnPageRefreshedListener? =null
    companion object {
        fun newInstance() = FixtureCricketFragment()
        var pageNo = 1
    }

    //   private lateinit var mainViewModel: MatchesViewModel
    private var mBinding: FragmentAllGamesBinding? = null
    lateinit var adapter: MatchesAdapter
    var allmatchesArrayList = ArrayList<MatchesModels>()
    var scrollListener: RecyclerViewLoadMoreScroll? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_all_games, container, false
        )
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).showToolbar()
        // mainViewModel = ViewModelProviders.of(this).get(MatchesViewModel::class.java)
        //mainViewModel = ViewModelProviders.of(this).get(MatchesViewModel::class.java)
        mBinding!!.allGameViewRecycler.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        mBinding!!.linearEmptyContest.visibility = View.GONE

        mBinding!!.swipeRefresh.setColorScheme(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )

        mBinding!!.swipeRefresh.setOnRefreshListener(this)

        // initDummyContent();
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        scrollListener = RecyclerViewLoadMoreScroll(linearLayoutManager)

        mBinding!!.allGameViewRecycler.layoutManager = linearLayoutManager

        val upcomingmatchlist =
            (requireActivity().applicationContext as SportsFightApplication).getUpcomingMatches
        if (upcomingmatchlist != null && upcomingmatchlist.size > 0) {
            allmatchesArrayList.clear()
            allmatchesArrayList.addAll(upcomingmatchlist)
        }
        adapter = MatchesAdapter(requireActivity(), allmatchesArrayList)
        mBinding!!.allGameViewRecycler.adapter = adapter
        getAllMatches()
        getMessage()
    }

    private fun isValidRequest(): Boolean {
        val offset = 10
        val cal = (pageNo * offset) + 1
        if (adapter.itemCount <= cal) {
            return true
        } else {
            return true
        }
    }

    fun updateEmptyViews() {
        if (allmatchesArrayList.size == 0) {
            mBinding!!.linearEmptyContest.visibility = View.VISIBLE
            mBinding!!.btnEmptyView.setOnClickListener(View.OnClickListener {
                val openURL = Intent(Intent.ACTION_VIEW)
                openURL.data = Uri.parse(BindingUtils.WEBVIEW_TNC)
                startActivity(openURL)
            })

        } else {
            mBinding!!.linearEmptyContest.visibility = View.GONE
        }
    }

    private fun getAllMatches() {
        if (!MyUtils.isConnectedWithInternet(activity as AppCompatActivity)) {
            mBinding!!.swipeRefresh.isRefreshing = false

            Snackbar.make(
                requireActivity().findViewById(android.R.id.content),
                "NO Internet Connection found!!",
                Snackbar.LENGTH_INDEFINITE
            ).setAction("Retry") {
                // Call action functions here
                getAllMatches()
            }.setActionTextColor(resources.getColor(R.color.red)).show()
            return
        }
        mBinding!!.swipeRefresh.isRefreshing = true
        val models = RequestModel()
        models.user_id = MyPreferences.getUserID(requireActivity())!!
        models.token = MyPreferences.getToken(requireActivity())!!
        val deviceToken: String? = MyPreferences.getDeviceToken(requireActivity())
        models.deviceDetails = HardwareInfoManager(context).collectData(deviceToken!!)

        WebServiceClient(requireActivity()).client.create(IApiMethod::class.java).getAllMatches(
            models
        )
            .enqueue(object : Callback<UsersPostDBResponse?> {
                override fun onFailure(call: Call<UsersPostDBResponse?>?, t: Throwable?) {
                    // customeProgressDialog.dismiss()
                }

                override fun onResponse(
                    call: Call<UsersPostDBResponse?>?,
                    response: Response<UsersPostDBResponse?>?
                ) {
                    if (isVisible) {
                        mBinding!!.swipeRefresh.isRefreshing = false
                        val resObje = response!!.body()

                        if (resObje != null && resObje.appMaintainance) {
                            val intent = Intent(activity, MaintainanceActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            activity!!.finish()
                        } else
                            if (resObje != null && resObje.status) {
                                if (resObje.sessionExpired) {
                                    logoutApp("Session Expired Please login again!!", false)
                                } else {
                                    BindingUtils.currentTimeStamp = resObje.systemTime
                                    val responseObject = resObje.responseObject
                                    val listofData =
                                        responseObject!!.matchdatalist as ArrayList<MatchesModels>?
                                    (activity!!.applicationContext as SportsFightApplication).saveUpcomingMatches(
                                        listofData
                                    )
                                    if (listofData!!.size > 0) {
                                        addAllList(listofData)
                                        adapter.setMatchesList(allmatchesArrayList)
                                    }
                                }
                            }
                        updateEmptyViews()
                    }
                }
            })
    }

    private fun addAllList(userPostData: java.util.ArrayList<MatchesModels>) {
        if (isValidRequest()) {
            allmatchesArrayList.clear()
            allmatchesArrayList.addAll(userPostData)
        }
    }

    override fun onRefresh() {
        getAllMatches()
        getMessage()
    }

    private fun getMessage() {
        if (!MyUtils.isConnectedWithInternet(activity as AppCompatActivity)) {
            return
        }
        mBinding!!.swipeRefresh.isRefreshing = true
        val models = RequestModel()
        models.user_id = MyPreferences.getUserID(requireActivity())!!
        models.token = MyPreferences.getToken(requireActivity())!!
        val deviceToken: String? = MyPreferences.getDeviceToken(requireActivity())
        models.deviceDetails = HardwareInfoManager(context).collectData(deviceToken!!)

        WebServiceClient(requireActivity()).client.create(IApiMethod::class.java)
            .getMessages(models)
            .enqueue(object : Callback<JsonObject?> {
                override fun onFailure(call: Call<JsonObject?>?, t: Throwable?) {
                    // customeProgressDialog.dismiss()
                }

                override fun onResponse(
                    call: Call<JsonObject?>?,
                    response: Response<JsonObject?>?
                ) {
                    if (isVisible) {
                        mBinding!!.swipeRefresh.isRefreshing = false
                        val resObje = response!!.body().toString()
                        val jsonObject = JSONObject(resObje)
                        if (jsonObject.optBoolean("status")) {
                            val array = jsonObject.getJSONArray("data")
                            val data = array.getJSONObject(0)
                            if (data.optInt("message_status") == 0) {
                                mBinding!!.messageCard.visibility = View.GONE
                            } else {
                                if (data.getString("message_type") == "HTML") {
                                    mBinding!!.labelMessage.linksClickable = true
                                    mBinding!!.labelMessage.movementMethod =
                                        LinkMovementMethod.getInstance()
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        mBinding!!.labelMessage.text =
                                            Html.fromHtml(
                                                data.getString("message"),
                                                Html.FROM_HTML_MODE_COMPACT
                                            )
                                    } else {
                                        mBinding!!.labelMessage.text = Html.fromHtml(
                                            data.getString("message")
                                        )
                                    }
                                } else {
                                    mBinding!!.labelMessage.text = data.getString("message")
                                }
                                mBinding!!.messageCard.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            })
    }
}