package com.my11heros.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.my11heros.R
import com.my11heros.adaptors.MatchesAdapter
import com.my11heros.databinding.FragmentAllGamesBinding
import com.my11heros.models.MatchesModels

class FixtureBasketballFragment : Fragment() {

    private var mBinding: FragmentAllGamesBinding? = null
    lateinit var adapter: MatchesAdapter
    var checkinArrayList = ArrayList<MatchesModels>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding  = DataBindingUtil.inflate(inflater,
            R.layout.fragment_all_games, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding!!.allGameViewRecycler.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)


        initDummyContent()

        adapter =
            MatchesAdapter(requireActivity(), checkinArrayList)
        mBinding!!.allGameViewRecycler.adapter = adapter

    }

    private fun initDummyContent() {
//        checkinArrayList.clear()
//
//        var matchModels3 = MatchesModels()
//        matchModels3.viewType = MatchesAdapter.TYPE_UPCOMING_MATCHES
//        matchModels3.upcomingMatches = ArrayList()
//        var upcomingMModle1 = UpcomingMatchesModel()
//        upcomingMModle1.matchTitle = "NBA 2019-20"
//        upcomingMModle1.country1Name = "MIN"
//        upcomingMModle1.country2Name = "LAC"
//        upcomingMModle1.contestName = "Mega"
//        upcomingMModle1.contestPrice = "5 Lakhs"
//
//        matchModels3.upcomingMatches!!.add(upcomingMModle1)
//
//        var upcomingMModle2 = UpcomingMatchesModel()
//        upcomingMModle2.matchTitle = "NBA 2019-20"
//        upcomingMModle2.country1Name = "SAC"
//        upcomingMModle2.country2Name = "SAS"
//        upcomingMModle2.contestName = "Mega"
//        upcomingMModle2.contestPrice = "5Lakh"
//        matchModels3.upcomingMatches!!.add(upcomingMModle2)
//
//        var upcomingMModle3 = UpcomingMatchesModel()
//        upcomingMModle3.matchTitle = "Laliga"
//        upcomingMModle3.country1Name = "OKC"
//        upcomingMModle3.country2Name = "BOS"
//        matchModels3.upcomingMatches!!.add(upcomingMModle3)
//
//        var upcomingMModle4 = UpcomingMatchesModel()
//        upcomingMModle4.matchTitle = "Premier League"
//        upcomingMModle4.country1Name = "WOL"
//        upcomingMModle4.country2Name = "LEI"
//        matchModels3.upcomingMatches!!.add(upcomingMModle4)
//
//
//
//        checkinArrayList.add(matchModels3)


    }
}
