package com.my11heros.utils

import android.content.Context
import com.my11heros.CreateTeamActivity.Companion.CREATE_TEAM_ALLROUNDER
import com.my11heros.CreateTeamActivity.Companion.CREATE_TEAM_BATSMAN
import com.my11heros.CreateTeamActivity.Companion.CREATE_TEAM_BOWLER
import com.my11heros.CreateTeamActivity.Companion.CREATE_TEAM_WICKET_KEEPER
import com.my11heros.models.UpcomingMatchesModel
import com.my11heros.ui.createteam.models.PlayersInfoModel
import com.my11heros.R

class CricketPlayersFilters(context: Context) {

    companion object {

        fun getPlayersbyOddEvenPositions(
            filterPlayersLists: ArrayList<PlayersInfoModel>,
            matchObject: UpcomingMatchesModel,
            playersType: String
        ): ArrayList<PlayersInfoModel> {

            var finalobjects: ArrayList<PlayersInfoModel> = ArrayList<PlayersInfoModel>()
            var teamAPlayerList: ArrayList<PlayersInfoModel> = ArrayList<PlayersInfoModel>()
            var teamBPlayerList: ArrayList<PlayersInfoModel> = ArrayList<PlayersInfoModel>()



            for (x in 0..filterPlayersLists.size - 1) {
                var playerInfoObject = filterPlayersLists.get(x)
                if (matchObject.teamAInfo!!.teamId == playerInfoObject.teamId) {
                    if (playersType.equals(CREATE_TEAM_WICKET_KEEPER)) {
                        playerInfoObject.setPlayerIcon(R.drawable.ic_player_wk_teama)
                    } else if (playersType.equals(CREATE_TEAM_BATSMAN)) {
                        playerInfoObject.setPlayerIcon(R.drawable.ic_player_bat_teama)
                    } else if (playersType.equals(CREATE_TEAM_ALLROUNDER)) {
                        playerInfoObject.setPlayerIcon(R.drawable.ic_player_all_teama)
                    } else if (playersType.equals(CREATE_TEAM_BOWLER)) {
                        playerInfoObject.setPlayerIcon(R.drawable.ic_player_bowler_teama)
                    }
                    teamAPlayerList.add(playerInfoObject)
                }
                if (matchObject.teamBInfo!!.teamId == playerInfoObject.teamId) {
                    if (playersType.equals(CREATE_TEAM_WICKET_KEEPER)) {
                        playerInfoObject.setPlayerIcon(R.drawable.ic_player_wk_teamb)
                    } else if (playersType.equals(CREATE_TEAM_BATSMAN)) {
                        playerInfoObject.setPlayerIcon(R.drawable.ic_player_bat_teamb)
                    } else if (playersType.equals(CREATE_TEAM_ALLROUNDER)) {
                        playerInfoObject.setPlayerIcon(R.drawable.ic_player_all_teamb)
                    } else if (playersType.equals(CREATE_TEAM_BOWLER)) {
                        playerInfoObject.setPlayerIcon(R.drawable.ic_player_bowler_teamb)
                    }
                    teamBPlayerList.add(playerInfoObject)
                }
            }
            val t1 = teamAPlayerList.size
            val t2 = teamBPlayerList.size
            if (t1 > t2) {
                for (x in 0..t1 - 1) {
                    finalobjects.add(teamAPlayerList.get(x))
                    if (x < t2) {
                        finalobjects.add(teamBPlayerList.get(x))
                    }
                }
            } else {
                for (x in 0..t2 - 1) {
                    finalobjects.add(teamBPlayerList.get(x))
                    if (x < t1) {
                        finalobjects.add(teamAPlayerList.get(x))
                    }
                }
            }

            return finalobjects
        }

        fun getPlayersbyMaxSelection(filteredWicketKeepers: java.util.ArrayList<PlayersInfoModel>, isEntryAscending:Boolean): List<PlayersInfoModel> {
            if(isEntryAscending) {
                return filteredWicketKeepers.sortedBy { it -> it.analyticsModel!!.selectionPc }
            }else {
                return filteredWicketKeepers.sortedByDescending { it -> it.analyticsModel!!.selectionPc }
            }
        }

        fun getPlayersbyMaxPoints(filteredWicketKeepers: java.util.ArrayList<PlayersInfoModel>, isEntryAscending:Boolean): List<PlayersInfoModel> {
            if(isEntryAscending) {
                return filteredWicketKeepers.sortedBy { it -> it.playerSeriesPoints }
            }else {
                return filteredWicketKeepers.sortedByDescending { it -> it.playerSeriesPoints }
            }
        }

        fun getPlayersbyMaxCredits(filteredWicketKeepers: java.util.ArrayList<PlayersInfoModel>, isEntryAscending:Boolean): List<PlayersInfoModel> {
            if(isEntryAscending) {
                return filteredWicketKeepers.sortedBy { it -> it.fantasyPlayerRating}
            }else {
                return filteredWicketKeepers.sortedByDescending { it -> it.fantasyPlayerRating }
            }
        }



    }




}