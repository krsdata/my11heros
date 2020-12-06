package com.edify.atrist.listener

import com.my11heros.ui.contest.models.ContestModelLists

interface OnContestEvents {
    fun onContestJoinning(objects: ContestModelLists, position: Int)
    fun onShareContest(objects: ContestModelLists)
}