package com.my11heros.ui.contest.adaptors

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edify.atrist.listener.OnContestEvents
import com.my11heros.ContestActivity
import com.my11heros.LeadersBoardActivity
import com.my11heros.MoreContestActivity
import com.my11heros.R
import com.my11heros.models.ContestsParentModels
import com.my11heros.models.UpcomingMatchesModel
import com.my11heros.ui.contest.models.ContestModelLists


class ContestAdapter(
    val context: Activity,
    val contestInfoModel: ArrayList<ContestsParentModels>,
    matchObject: UpcomingMatchesModel?,
    val listener: OnContestEvents?
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var mContext: Context? = context
    var matchObject = matchObject
    var onItemClick: ((ContestsParentModels) -> Unit)? = null
    private var matchesListObject = contestInfoModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contest_row_header, parent, false)
        return ViewHolderJoinedContest(view)
    }

    override fun onBindViewHolder(parent: RecyclerView.ViewHolder, viewType: Int) {
        val objectVal = matchesListObject[viewType]
        val viewJoinedMatches: ViewHolderJoinedContest = parent as ViewHolderJoinedContest

        viewJoinedMatches.contestTitle.text = objectVal.contestTitle
        viewJoinedMatches.contestSubTitle.text = objectVal.contestSubTitle

        viewJoinedMatches.recyclerView.layoutManager =
            LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        var colorCode = context.resources.getColor(R.color.white)
        if (objectVal.contestTitle.contains("Practise")) {
            colorCode = context.resources.getColor(R.color.highlighted_text_material_dark)
        }

        /**
         * Replace this part with below part once api comes
         */
        viewJoinedMatches.viewMoreLayout?.visibility = View.GONE
        val adapter = ContestListAdapter(
            context,
            objectVal.allContestsRunning!!,
            matchObject!!,
            listener,
            colorCode
        )
        viewJoinedMatches.recyclerView.adapter = adapter

        //  Settings for more Contests
        /*val top3: ArrayList<ContestModelLists> = getFirst3Values(objectVal.allContestsRunning!!)
        val adapter = ContestListAdapter(
            context,
            top3,
            matchObject!!,
            listener,
            colorCode
        )
        viewJoinedMatches.recyclerView.adapter = adapter
        if (objectVal.allContestsRunning != null && objectVal.allContestsRunning!!.size > 3) {
            viewJoinedMatches.viewMoreLayout?.visibility = View.VISIBLE
            viewJoinedMatches.moreContestClick?.setOnClickListener(View.OnClickListener {
                val intent = Intent(context, MoreContestActivity::class.java)
                intent.putExtra(ContestActivity.SERIALIZABLE_KEY_UPCOMING_MATCHES, matchObject)
                intent.putExtra(ContestActivity.SERIALIZABLE_KEY_JOINED_CONTEST, matchesListObject)
                intent.putExtra(MoreContestActivity.SERIALIZABLE_KEY_LIST_POSTIION, objectVal)
                context.startActivityForResult(intent, LeadersBoardActivity.CREATETEAM_REQUESTCODE)
            })
        } else {
            viewJoinedMatches.viewMoreLayout?.visibility = View.GONE
        }*/

        adapter.onItemClick = { objects ->
            //MyUtils.logd("JoinedContestAdapter","Joined Contest"+objects.country1Name+" Vs "+objects.country1Name)
        }
    }

    private fun getFirst3Values(allContestsRunning: java.util.ArrayList<ContestModelLists>): java.util.ArrayList<ContestModelLists> {
        val MAX_FILTER_CONTEST_SIZE = 2
        if (allContestsRunning.size > MAX_FILTER_CONTEST_SIZE) {
            val values = ArrayList<ContestModelLists>()
            for (i in 0..MAX_FILTER_CONTEST_SIZE) {
                values.add(allContestsRunning.get(i))
            }
            return values
        } else {
            return allContestsRunning
        }
    }

    fun setMatchesList(matchesList: ArrayList<ContestsParentModels>?) {
        this.matchesListObject = matchesList!!
        // this.mContext = mContext
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return matchesListObject.size
    }

    inner class ViewHolderJoinedContest(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(matchesListObject[adapterPosition])
            }
        }

        val contestTitle: TextView = itemView.findViewById(R.id.contest_title)
        val contestSubTitle: TextView = itemView.findViewById(R.id.contest_sub_title)
        val recyclerView: RecyclerView = itemView.findViewById(R.id.recycler_all_contest)
        val moreContestClick: TextView = itemView.findViewById(R.id.more_contest_click)
        val viewMoreLayout: RelativeLayout = itemView.findViewById(R.id.view_more_layout)
    }
}