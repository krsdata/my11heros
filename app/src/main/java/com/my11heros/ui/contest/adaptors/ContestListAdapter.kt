package com.my11heros.ui.contest.adaptors

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edify.atrist.listener.OnContestEvents
import com.my11heros.LeadersBoardActivity
import com.my11heros.R
import com.my11heros.models.UpcomingMatchesModel
import com.my11heros.ui.contest.models.ContestModelLists
import com.my11heros.utils.BindingUtils


class ContestListAdapter(
    val context: Activity,
    val contestModelList: ArrayList<ContestModelLists>,
    matchObject: UpcomingMatchesModel,
    val listener: OnContestEvents?,
    val colorCode: Int
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var onItemClick: ((ContestModelLists) -> Unit)? = null
    private var matchesListObject = contestModelList

    var matchObject = matchObject
    val TYPE_IPL_FINAL: Int = 1
    val TYPE_NORMAL: Int = 100

    override fun getItemViewType(position: Int): Int {
        /*if (matchesListObject[position].totalWinningPrize.toInt() % 20000 == 0) {
            return TYPE_IPL_FINAL
        } else {
            return TYPE_NORMAL
        }*/
        return TYPE_NORMAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_NORMAL) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.contest_rows_inner, parent, false)
            return DataViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.contest_rows_ipl_final, parent, false)
            return DataViewHolder(view)
        }
    }

    override fun onBindViewHolder(parent: RecyclerView.ViewHolder, position: Int) {
        val objectVal = matchesListObject[position]
        val viewHolder: DataViewHolder = parent as DataViewHolder
        viewHolder.contestPrizePool?.text = String.format("%s%s", "₹", objectVal.totalWinningPrize)

        if (objectVal.entryFees.toInt() == 0 && objectVal.winnerCounts!!.toInt() > 0) {
            viewHolder.contestEntryPrize?.text = "Free"
            viewHolder.winningPercentage?.text = "" + objectVal.winnerCounts
        } else if (objectVal.entryFees.toInt() == 0 && objectVal.winnerCounts!!.toInt() == 0) {
            viewHolder.contestEntryPrize?.text = "Join"
            viewHolder.winningPercentage?.text = "Practice"
        } else {
            viewHolder.contestEntryPrize?.text = String.format("%s%s", "₹", objectVal.entryFees)
            viewHolder.winningPercentage?.text = "" + objectVal.winnerCounts
        }
        viewHolder.firstPrize?.text = String.format("%s%s", "₹", objectVal.firstPrice)
        //String.format("%d%s",objectVal.winnerPercentage,"%")
        //viewHolder.maxAllowedTeam?.text = String.format("%s %d %s","Upto",objectVal.maxAllowedTeam,"teams")


        if (objectVal.maxAllowedTeam > 1) {
            viewHolder.allowedTeamType.text = "M"
            viewHolder.contestMultiplayer.text = "" + objectVal.maxAllowedTeam
            viewHolder.linearMulti.visibility = View.VISIBLE
        } else {
            viewHolder.allowedTeamType.text = "S"
            viewHolder.contestMultiplayer.text = "" + objectVal.maxAllowedTeam
            viewHolder.linearMulti.visibility = View.VISIBLE
            // viewHolder.contestMultiplayer.text =  ""+objectVal.maxAllowedTeam
            // viewHolder.linearMulti.visibility = View.GONE
        }
        if (objectVal.usableBonus.toInt() == 0) {
            viewHolder.linearBonus?.visibility = View.GONE
        } else {
            viewHolder.linearBonus?.visibility = View.VISIBLE
            viewHolder.contestBonus?.text = String.format("%s%s", objectVal.usableBonus, "%")
        }

        if (getItemViewType(position) == TYPE_NORMAL) {
            if (objectVal.totalSpots == 0) {
                viewHolder.contestProgress.max =
                    objectVal.filledSpots + BindingUtils.UNLIMITED_SPOT_MARGIN
                viewHolder.contestProgress.progress = objectVal.filledSpots
                viewHolder.totalSpot?.text = String.format("unlimited spots")
                viewHolder.totalSpotLeft?.text =
                    String.format("%d spot filled", objectVal.filledSpots)
            } else {
                viewHolder.contestProgress.max = objectVal.totalSpots
                viewHolder.contestProgress.progress = objectVal.filledSpots
                if (objectVal.totalSpots == objectVal.filledSpots) {
                    viewHolder.totalSpot?.text = "Contest full"
                    viewHolder.totalSpot?.textSize = 18.0f
                    viewHolder.totalSpotLeft?.text = ""
                } else {
                    viewHolder.totalSpot?.text = String.format("%d spots", objectVal.totalSpots)
                    viewHolder.totalSpotLeft?.text =
                        String.format("%d  spot left", objectVal.totalSpots - objectVal.filledSpots)
                }
            }
        } else {
            if (objectVal.totalSpots == 0) {
                viewHolder.contestProgress.max =
                    objectVal.filledSpots + BindingUtils.UNLIMITED_SPOT_MARGIN
                viewHolder.contestProgress.progress = objectVal.filledSpots
                viewHolder.totalSpot?.text = String.format("unlimited spots")
                viewHolder.totalSpotLeft?.text = String.format("%d", objectVal.filledSpots)
            } else {
                viewHolder.contestProgress.max = objectVal.totalSpots
                viewHolder.contestProgress.progress = objectVal.filledSpots
                if (objectVal.totalSpots == objectVal.filledSpots) {
                    viewHolder.totalSpot?.text = "Contest full"
                    viewHolder.totalSpot?.textSize = 18.0f
                    viewHolder.totalSpotLeft?.text = ""
                } else {
                    viewHolder.totalSpot?.text = String.format("%d", objectVal.totalSpots)
                    viewHolder.totalSpotLeft?.text =
                        String.format("%d", objectVal.filledSpots)
                }
            }
        }

        // viewHolder?.cardBackround?.setBackgroundColor(colorCode)
        viewHolder.linearContestViews?.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, LeadersBoardActivity::class.java)
            intent.putExtra(LeadersBoardActivity.SERIALIZABLE_MATCH_KEY, matchObject)
            intent.putExtra(LeadersBoardActivity.SERIALIZABLE_CONTEST_KEY, objectVal)
            context.startActivityForResult(intent, LeadersBoardActivity.CREATETEAM_REQUESTCODE)
        })

        if (objectVal.cancellation) {
            viewHolder.contestCancellation?.visibility = View.INVISIBLE
        } else {
            viewHolder.contestCancellation?.visibility = View.VISIBLE
        }
//        viewHolder.contestCancellation?.setOnClickListener(View.OnClickListener {
//            //MyUtils.showToast(viewHolder?.contestCancellation,objectVal.cancellation)
//        })

        //if(matchObject.status==1) {
        viewHolder.contestEntryPrize?.setOnClickListener(View.OnClickListener {
            listener!!.onContestJoinning(objectVal, position)

        })
//        }else {
//            viewHolder?.contestEntryPrize?.setBackgroundResource(R.drawable.button_selector_grey)
//        }
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

        // val cardBackround = itemView.findViewById<CardView>(R.id.card_backround)
        val linearContestViews = itemView.findViewById<LinearLayout>(R.id.linear_trades_status)
        val contestPrizePool = itemView.findViewById<TextView>(R.id.contest_prize_pool)
        val contestEntryPrize = itemView.findViewById<TextView>(R.id.contest_entry_prize)
        val firstPrize = itemView.findViewById<TextView>(R.id.first_prize)
        val winningPercentage = itemView.findViewById<TextView>(R.id.winning_percentage)

        //val maxAllowedTeam = itemView.findViewById<TextView>(R.id.max_allowed_team)
        val contestCancellation = itemView.findViewById<TextView>(R.id.contest_cancellation)
        val allowedTeamType = itemView.findViewById<TextView>(R.id.allowedTeamType)
        val contestMultiplayer = itemView.findViewById<TextView>(R.id.contest_multiplayer)
        val linearMulti = itemView.findViewById<LinearLayout>(R.id.linear_Multi)
        val linearBonus = itemView.findViewById<LinearLayout>(R.id.linear_bonues)
        val contestBonus = itemView.findViewById<TextView>(R.id.contest_bonus)

        val totalSpotLeft = itemView.findViewById<TextView>(R.id.total_spot_left)
        val totalSpot = itemView.findViewById<TextView>(R.id.total_spot)
        val contestProgress = itemView.findViewById<ProgressBar>(R.id.contest_progress)
    }
}