package com.edify.atrist.listener

import com.my11heros.models.MyTeamModels
import com.my11heros.ui.contest.models.ContestModelLists

interface OnContestLoadedListener {
    fun onMyContest(contestModel: ArrayList<ContestModelLists>)
    fun onMyTeam(count: ArrayList<MyTeamModels>)
}