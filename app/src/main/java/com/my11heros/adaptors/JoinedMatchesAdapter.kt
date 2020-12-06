package com.my11heros.adaptors

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.edify.atrist.listener.OnMatchTimerStarted
import com.my11heros.R
import com.my11heros.models.JoinedMatchModel
import com.my11heros.utils.BindingUtils
import java.util.*


class JoinedMatchesAdapter(val context: Context, val tradeinfoModels: ArrayList<JoinedMatchModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var onItemClick: ((JoinedMatchModel) -> Unit)? = null
    private var matchesListObject = tradeinfoModels


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.matches_row_joined_inner, parent, false)
        return DataViewHolder(view)
    }

    override fun onBindViewHolder(parent: RecyclerView.ViewHolder, viewType: Int) {
        val objectVal = matchesListObject[viewType]
        val viewHolder: DataViewHolder = parent as DataViewHolder
        viewHolder.matchTitle?.text = objectVal.matchTitle
        viewHolder.matchStatus?.text = objectVal.statusString
        viewHolder.matchProgress?.text = ""
        viewHolder.opponent1?.text = objectVal.teamAInfo!!.teamShortName
        viewHolder.opponent2?.text = objectVal.teamBInfo!!.teamShortName
        viewHolder.totalTeamCreated?.text = String.format("%d", objectVal.totalTeams)
        viewHolder.totalContestJoined?.text = String.format("%d", objectVal.totalJoinContests)

        viewHolder.teamAColorView?.setBackgroundColor(getRandomColor())
        viewHolder.teamBColorView?.setBackgroundColor(getRandomColor())

        if (!TextUtils.isEmpty(objectVal.prizeAmount)) {
            val prize = objectVal.prizeAmount.toDouble()
            if (prize > 0) {
                if (objectVal.status == BindingUtils.MATCH_STATUS_LIVE) {
                    viewHolder.winningPrice.text = String.format(
                        "Winning ₹%s",
                        objectVal.prizeAmount
                    )
                } else {
                    viewHolder.winningPrice.text = String.format(
                        "Won ₹%s",
                        objectVal.prizeAmount
                    )
                }
            } else {
                viewHolder.winningPrice.visibility = View.GONE
            }
        } else {
            viewHolder.winningPrice.visibility = View.GONE
        }

        BindingUtils.countDownStartForAdaptors(objectVal.timestampStart, object :
            OnMatchTimerStarted {

            override fun onTimeFinished() {
                viewHolder.matchProgress.text = objectVal.dateStart
            }

            override fun onTicks(time: String) {
                viewHolder.matchProgress.text = time
            }
        })

        Glide.with(context)
            .load(objectVal.teamAInfo!!.logoUrl)
            .placeholder(R.drawable.placeholder_player_teama)
            .disallowHardwareConfig()
            .into(viewHolder.teamALogo)

        Glide.with(context)
            .load(objectVal.teamBInfo!!.logoUrl)
            .placeholder(R.drawable.placeholder_player_teama)
            .disallowHardwareConfig()
            .into(viewHolder.teamBLogo)

    }

    override fun getItemCount(): Int {
        return matchesListObject.size
    }

    fun getRandomColor(): Int {
        val rnd = Random()
        val color: Int = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))

        return color
    }

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(matchesListObject[adapterPosition])
            }
        }

        val matchTitle = itemView.findViewById<TextView>(R.id.completed_match_title)
        val matchStatus = itemView.findViewById<TextView>(R.id.completed_match_status)
        val opponent1 = itemView.findViewById<TextView>(R.id.upcoming_opponent1)

        //val matchProgress = itemView.findViewById<TextView>(R.id.upcoming_match_progress)
        val opponent2 = itemView.findViewById<TextView>(R.id.upcoming_opponent2)
        val totalTeamCreated = itemView.findViewById<TextView>(R.id.total_team_created)
        val totalContestJoined = itemView.findViewById<TextView>(R.id.total_contest_joined)
        val teamALogo = itemView.findViewById<ImageView>(R.id.teama_logo)
        val teamBLogo = itemView.findViewById<ImageView>(R.id.teamb_logo)
        val winningPrice = itemView.findViewById<TextView>(R.id.winning_price)

        val matchProgress = itemView.findViewById<TextView>(R.id.completed_match_date)

        val teamAColorView = itemView.findViewById<View>(R.id.countrycolorview)
        val teamBColorView = itemView.findViewById<View>(R.id.countrybcolorview)
    }
}