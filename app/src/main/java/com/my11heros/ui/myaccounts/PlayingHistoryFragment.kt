package com.my11heros.ui.myaccounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.my11heros.network.IApiMethod
import com.my11heros.network.RequestModel
import com.my11heros.network.WebServiceClient
import com.my11heros.R
import com.my11heros.databinding.FragmentPlayingHistoryBinding
import com.my11heros.ui.home.models.UsersPostDBResponse
import com.my11heros.utils.MyPreferences
import com.my11heros.utils.MyUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PlayingHistoryFragment : Fragment() {

    private var mBinding: FragmentPlayingHistoryBinding? = null
   // var myAccountFragment: MyAccountFragment?=null
    companion object{
        fun newInstance(bundle : Bundle) : PlayingHistoryFragment {
            val fragment = PlayingHistoryFragment()
            fragment.arguments=bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  myAccountFragment = arguments!!.get(SERIALIZABLE_ACCOUNT_BAL) as MyAccountFragment

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        mBinding  = DataBindingUtil.inflate(inflater,
            R.layout.fragment_playing_history, container, false)

        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding!!.progressBarMatches.visibility = View.GONE
        getPlayingMatchHistory()
    }

    fun getPlayingMatchHistory() {
        if(!MyUtils.isConnectedWithInternet(activity as AppCompatActivity)) {
            MyUtils.showToast(activity as AppCompatActivity,"No Internet connection found")
            return
        }
        var models = RequestModel()
        models.user_id = MyPreferences.getUserID(activity!!)!!
        WebServiceClient(activity!!).client.create(IApiMethod::class.java).getPlayingMatchHistory(models)
            .enqueue(object : Callback<UsersPostDBResponse?> {
                override fun onFailure(call: Call<UsersPostDBResponse?>?, t: Throwable?) {

                }

                override fun onResponse(
                    call: Call<UsersPostDBResponse?>?,
                    response: Response<UsersPostDBResponse?>?
                ) {
                    if(isAdded) {
                        var res = response!!.body()
                        if(res!=null) {
                            var responseModel = res.responseObject
                            if(responseModel!=null) {
                                //var totalTeamJoined = responseModel.totalTeamJoined
                                var totalContestJoined = responseModel.totalContestJoined
                                var totalMatchPlayed = responseModel.totalMatchPlayed
                                var totalMatchWin = responseModel.totalMatchWin
                                mBinding!!.txtMatchPlayed.text = String.format("%d",totalMatchPlayed)
                                mBinding!!.txtContestPlayed.text =
                                    String.format("%d",totalContestJoined)
                                mBinding!!.txtContestWin.text = String.format("%d",totalMatchWin)
                                mBinding!!.totalBalance.setText(String.format("%s",responseModel.totalWinningAmount))

                            }
                        }
                    }


                }

            })

    }



}
