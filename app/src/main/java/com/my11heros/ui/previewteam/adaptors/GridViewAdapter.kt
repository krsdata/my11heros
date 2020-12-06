package com.my11heros.ui.previewteam.adaptors

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.my11heros.R
import com.my11heros.models.UpcomingMatchesModel
import com.my11heros.ui.createteam.models.PlayersInfoModel
import com.my11heros.utils.BindingUtils

class GridViewAdapter(
    val context: Context,
    val listImageURLs: List<PlayersInfoModel>,
    matchObject: UpcomingMatchesModel
) : BaseAdapter() {

    var matchObject = matchObject
    override fun getItem(position: Int): Any {
        return listImageURLs[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return listImageURLs.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        val viewHolder: ViewHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.preview_player_info, parent, false)
            viewHolder =
                ViewHolder()
            viewHolder.imageView = convertView!!.findViewById(R.id.imageView)
            viewHolder.playerName = convertView.findViewById(R.id.player_name)
            viewHolder.playerFantasyPoints = convertView.findViewById(R.id.player_points)
            viewHolder.playerRole = convertView.findViewById(R.id.player_role)
            viewHolder.playing11 = convertView.findViewById(R.id.playing11)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }
        val objects = listImageURLs.get(position)
        viewHolder.playerName.text = objects.shortName
        if (matchObject.status == BindingUtils.MATCH_STATUS_UPCOMING) {
            viewHolder.playerFantasyPoints.text = "" + objects.fantasyPlayerRating + " Cr"
        } else {
            viewHolder.playerFantasyPoints.text = objects.playerPoints + " Pt"
        }

        Glide.with(context)
            .load(objects.playerImage)
            .placeholder(R.drawable.player_blue)
            .into(viewHolder.imageView)

        if (matchObject.teamAInfo!!.teamId == objects.teamId) {
            viewHolder.playerName.background = context.resources.getDrawable(R.drawable.ract_white_background)
            viewHolder.playerName.setTextColor(context.resources.getColor(R.color.black))
        } else {
            viewHolder.playerName.background = context.resources.getDrawable(R.drawable.ract_black_background)
            viewHolder.playerName.setTextColor(context.resources.getColor(R.color.white))
        }

        if (objects.isPlaying11) {
            viewHolder.playing11.visibility = View.GONE
        } else {
            viewHolder.playing11.visibility = View.VISIBLE
        }
        if (objects.isCaptain) {
            viewHolder.playerRole.visibility = View.VISIBLE
            viewHolder.playerRole.text = "C"
        } else if (objects.isViceCaptain) {
            viewHolder.playerRole.visibility = View.VISIBLE
            viewHolder.playerRole.text = "VC"
        } else if (objects.isTrump) {
            viewHolder.playerRole.visibility = View.VISIBLE
            viewHolder.playerRole.text = "T"
        } else {
            viewHolder.playerRole.visibility = View.GONE
        }
        return convertView
    }

    class ViewHolder {
        lateinit var imageView: ImageView
        lateinit var playerName: TextView
        lateinit var playerFantasyPoints: TextView
        lateinit var playerRole: TextView
        lateinit var playing11: TextView
    }
}