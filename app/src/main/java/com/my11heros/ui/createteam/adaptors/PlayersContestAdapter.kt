package com.my11heros.ui.createteam.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.my11heros.R
import com.my11heros.models.UpcomingMatchesModel
import com.my11heros.ui.createteam.models.PlayersInfoModel


class PlayersContestAdapter(
    val context: Context,
    val playerList: ArrayList<PlayersInfoModel>,
    matchObject: UpcomingMatchesModel
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var onItemClick: ((PlayersInfoModel) -> Unit)? = null
    private var playerListObject = playerList
    private var matchObject = matchObject

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.createteam_row_players, parent, false)
        return DataViewHolder(view)
    }

    override fun onBindViewHolder(parent: RecyclerView.ViewHolder, viewType: Int) {
        val objectVal = playerListObject[viewType]
        val viewHolder: DataViewHolder = parent as DataViewHolder
        if (objectVal.analyticsModel != null) {
            viewHolder.playerSelectionPercentage?.text = "Sel by ${objectVal.analyticsModel!!.selectionPc}%"
        } else {
            viewHolder.playerSelectionPercentage?.text = ""
        }
        viewHolder.playerName?.text = objectVal.shortName
        viewHolder.teamName?.text = objectVal.teamShortName
        if (matchObject.teamAInfo!!.teamId == objectVal.teamId) {
            viewHolder.teamName?.setBackgroundColor(context.resources.getColor(R.color.player_bg_dark_red))
            viewHolder.teamName?.setTextColor(context.resources.getColor(R.color.white))
        } else {
            viewHolder.teamName?.setBackgroundColor(context.resources.getColor(R.color.player_bg_dark_yellow))
            viewHolder.teamName?.setTextColor(context.resources.getColor(R.color.white))
        }
        viewHolder.fantasyPoints?.text = "${objectVal.fantasyPlayerRating}"
        viewHolder.playerPoints?.text = "${objectVal.playerSeriesPoints}"

        if (objectVal.isPlaying11 /*&& matchObject.isLineup*/) {
            viewHolder.anouncedIndicatorCircle?.setBackgroundResource(R.drawable.circle_green)
            viewHolder.anouncedIndicatorText?.text = "Announced"
            viewHolder.anouncedIndicatorText?.setTextColor(context.resources.getColor(R.color.colorPrimary))
        } else {
            viewHolder.anouncedIndicatorText?.text = ""
            viewHolder.anouncedIndicatorCircle?.setBackgroundResource(R.drawable.circle_red)
        }

        Glide.with(context)
            .load(objectVal.playerImage)
            .placeholder(R.drawable.player_blue)
            .into(viewHolder.playerImage)

        if (objectVal.isSelected) {
            viewHolder.addImage.setImageResource(R.drawable.ic_do_not_disturb_on_black_24dp)
            viewHolder.linearTradesStatus.setBackgroundColor(context.resources.getColor(R.color.highlighted_text_material_dark))
        } else {
            viewHolder.addImage.setImageResource(R.drawable.ic_add_circle_outline_black_24dp)
            viewHolder.linearTradesStatus.setBackgroundColor(context.resources.getColor(R.color.white))
        }
    }

    override fun getItemCount(): Int {
        return playerListObject.size
    }

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(playerListObject[adapterPosition])
            }
        }

        val linearTradesStatus = itemView.findViewById<LinearLayout>(R.id.linear_trades_status)
        val playerSelectionPercentage =
            itemView.findViewById<TextView>(R.id.player_selection_percentage)
        val playerName = itemView.findViewById<TextView>(R.id.player_name)
        val playerImage = itemView.findViewById<ImageView>(R.id.player_image)
        val teamName = itemView.findViewById<TextView>(R.id.team_name)
        val fantasyPoints = itemView.findViewById<TextView>(R.id.fantasy_points)
        val playerPoints = itemView.findViewById<TextView>(R.id.player_points)
        val anouncedIndicatorCircle =
            itemView.findViewById<TextView>(R.id.anounced_indicator_circle)
        val anouncedIndicatorText = itemView.findViewById<TextView>(R.id.anounced_indicator_text)
        val addImage = itemView.findViewById<ImageView>(R.id.add_image)
    }
}

