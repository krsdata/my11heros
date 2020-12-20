package com.my11heros.ui.createteam.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.edify.atrist.listener.OnRolesSelected
import com.my11heros.R
import com.my11heros.ui.createteam.models.PlayersInfoModel


class PlayersSelectedAdapter(
    val context: Context,
    contestModelList: ArrayList<PlayersInfoModel>,
    listener: OnRolesSelected
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var onItemClick: ((PlayersInfoModel) -> Unit)? = null
    private var matchesListObject = contestModelList
    var listeners = listener

    companion object {
        const val TYPE_LABEL = 1
        const val TYPE_DATA = 0
    }

    override fun getItemViewType(position: Int): Int {
        val comparable = matchesListObject.get(position)
        return comparable.viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_LABEL) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.savematch_row_label, parent, false)
            return ViewLabelsHolders(view)
        } else if (viewType == TYPE_DATA) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_players_selected, parent, false)
            return DataViewHolder(view)
        }
        return null!!
    }

    override fun onBindViewHolder(parent: RecyclerView.ViewHolder, position: Int) {

        val objectVal = matchesListObject[position]
        if(objectVal.viewType== TYPE_LABEL){
            val viewHolder: ViewLabelsHolders = parent as ViewLabelsHolders
            viewHolder.roleType.text = objectVal.playerRole
        } else {
            val viewHolder: DataViewHolder = parent as DataViewHolder
            viewHolder.selectedPlayerName.text = objectVal.shortName

            viewHolder.selectedPlayerCountry.text = objectVal.teamShortName
            viewHolder.selectedPlayingStyle.text = objectVal.playerRole

            if (objectVal.analyticsModel != null) {
                viewHolder.selectedCaptainPercentage.text =
                    String.format("%.1f%%", objectVal.analyticsModel!!.captainPc)
                viewHolder.selectedvcPercentage.text =
                    String.format("%.1f%%", objectVal.analyticsModel!!.viceCaptainPc)
                viewHolder.selectedTrumpPercentage.text =
                    String.format("%.1f%%", objectVal.analyticsModel!!.trumpPc)
                viewHolder.playerSelectionPercentage.text = String.format("Sel by %.1f%", objectVal.analyticsModel!!.selectionPc)
                viewHolder.selectedPlayerPoints.text = String.format("%d", objectVal.playerSeriesPoints)

            } else {
                viewHolder.selectedCaptainPercentage.text = "0%"
                viewHolder.selectedvcPercentage.text = "0%"
                viewHolder.selectedTrumpPercentage.text = "0%"
                viewHolder.playerSelectionPercentage.text = ""
            }

            Glide.with(context)
                .load(objectVal.playerImage)
                .placeholder(R.drawable.player_blue)
                .into(viewHolder.selectedPlayerImage)
            setSelections(objectVal, viewHolder, position)
        }
    }

    private fun setSelections(
        objectVal: PlayersInfoModel,
        viewHolder: DataViewHolder,
        position: Int
    ) {

        if (objectVal.isTrump) {
            viewHolder.roleTypeTrump.text = "3X"
            viewHolder.roleTypeTrump.setBackgroundResource(R.drawable.circle_green)
            viewHolder.roleTypeTrump.setTextColor(context.resources.getColor(R.color.white))

        } else {
            viewHolder.roleTypeTrump.text = "T"
            viewHolder.roleTypeTrump.setBackgroundResource(R.drawable.circle_save_match)
            viewHolder.roleTypeTrump.setTextColor(context.resources.getColor(R.color.black))
        }

        if (objectVal.isCaptain) {
            viewHolder.roleTypeCaptain.text = "2X"
            viewHolder.roleTypeCaptain.setBackgroundResource(R.drawable.circle_green)
            viewHolder.roleTypeCaptain.setTextColor(context.resources.getColor(R.color.white))
        } else {
            viewHolder.roleTypeCaptain.text = "C"
            viewHolder.roleTypeCaptain.setBackgroundResource(R.drawable.circle_save_match)
            viewHolder.roleTypeCaptain.setTextColor(context.resources.getColor(R.color.black))
        }


        if (objectVal.isViceCaptain) {
            viewHolder.roleTypeViceCaptain.text = "1.5X"
            viewHolder.roleTypeViceCaptain.setBackgroundResource(R.drawable.circle_green)
            viewHolder.roleTypeViceCaptain.setTextColor(context.resources.getColor(R.color.white))
        } else {
            viewHolder.roleTypeViceCaptain.text = "VC"
            viewHolder.roleTypeViceCaptain.setBackgroundResource(R.drawable.circle_save_match)
            viewHolder.roleTypeViceCaptain.setTextColor(context.resources.getColor(R.color.black))
        }


        viewHolder.roleTypeTrump.setOnClickListener(View.OnClickListener {
            listeners.onTrumpSelected(objectVal, position)

        })

        viewHolder.roleTypeCaptain.setOnClickListener(View.OnClickListener {
            listeners.onCaptainSelected(objectVal, position)

        })

        viewHolder.roleTypeViceCaptain.setOnClickListener(View.OnClickListener {
            listeners.onViceCaptainSelected(objectVal, position)

        })
    }


    override fun getItemCount(): Int {
        return matchesListObject.size
    }

    inner class ViewLabelsHolders(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val roleType = itemView.findViewById<TextView>(R.id.player_role_label)
    }

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(matchesListObject[adapterPosition])
            }
        }

        val selectedPlayerName: TextView = itemView.findViewById(R.id.player_name)
        val playerSelectionPercentage: TextView =
            itemView.findViewById(R.id.player_selection_percentage)
        val selectedPlayerPoints: TextView = itemView.findViewById(R.id.player_points)
        val selectedPlayerCountry: TextView = itemView.findViewById(R.id.selected_player_country)
        val selectedPlayingStyle: TextView = itemView.findViewById(R.id.selected_player_playing_style)
        val selectedPlayerImage: ImageView = itemView.findViewById(R.id.player_image)

        val roleTypeTrump: TextView = itemView.findViewById(R.id.role_type_trump)
        val selectedTrumpPercentage: TextView =
            itemView.findViewById(R.id.selected_trump_percentage)

        val roleTypeCaptain : TextView = itemView.findViewById(R.id.role_type_captain)
        val selectedCaptainPercentage : TextView =
            itemView.findViewById(R.id.selected_captain_percentage)

        val roleTypeViceCaptain: TextView = itemView.findViewById(R.id.role_type_vicecaptain)
        val selectedvcPercentage: TextView = itemView.findViewById(R.id.selected_vc_percentage)
    }
}