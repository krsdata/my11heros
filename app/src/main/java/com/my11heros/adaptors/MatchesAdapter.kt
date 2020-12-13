package com.my11heros.adaptors

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.my11heros.ContestActivity
import com.my11heros.MainActivity
import com.my11heros.R
import com.my11heros.models.MatchesModels
import com.my11heros.utils.BindingUtils
import com.my11heros.utils.MyUtils
import pl.pzienowicz.autoscrollviewpager.AutoScrollViewPager


class MatchesAdapter(val context: Context, val tradeinfoModels: ArrayList<MatchesModels>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var onItemClick: ((MatchesModels) -> Unit)? = null
    var mContext: Context? = context
    private var matchesListObject = tradeinfoModels

    companion object {
        const val TYPE_JOINED = 1
        const val TYPE_BANNERS = 2
        const val TYPE_UPCOMING_MATCHES = 3
    }

    override fun getItemViewType(position: Int): Int {
        val comparable = matchesListObject.get(position)
        return comparable.viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_BANNERS) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.matches_row_banners_matches, parent, false)
            return BannersViewHolder(view)
        } else if (viewType == TYPE_JOINED) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.matches_row_joined_matches, parent, false)
            return ViewHolderJoinedMatches(view)
        } else if (viewType == TYPE_UPCOMING_MATCHES) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.matches_row_upcoming_matches, parent, false)
            return UpcomingMatchesViewHolder(view)
        }
        return null!!
    }

    override fun onBindViewHolder(parent: RecyclerView.ViewHolder, position: Int) {
        val objectVal = matchesListObject[position]
        if (objectVal.viewType == TYPE_JOINED) {
            val viewJoinedMatches: ViewHolderJoinedMatches = parent as ViewHolderJoinedMatches
            viewJoinedMatches.recyclerView.layoutManager =
                LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false)
            if (objectVal.joinedMatchModel != null) {
                val adapter = JoinedMatchesAdapter(
                    mContext!!,
                    objectVal.joinedMatchModel!!
                )
                viewJoinedMatches.recyclerView.adapter = adapter
                adapter.onItemClick = { objects ->
                    //MyUtils.logd("MatchesAdapter","Joined Contest"+objects.country1Name+" Vs "+objects.country1Name)
                    val intent = Intent(mContext, ContestActivity::class.java)
                    intent.putExtra(ContestActivity.SERIALIZABLE_KEY_JOINED_CONTEST, objects)
                    mContext!!.startActivity(intent)
                    // MyUtils.showToast(parent.recyclerView,"Open New Joined Contest Activity")
                }
            }

            Glide.with(context)
                .load(BindingUtils.BASE_URL_MAIN + "banners/joined_contest_bg.jpg")
                .placeholder(R.drawable.placeholder_player_teama)
                .into(viewJoinedMatches.backgroundImage)

            viewJoinedMatches.txtViewAll.setOnClickListener {
                (mContext as MainActivity).viewAllMatches()
            }
        } else if (objectVal.viewType == TYPE_BANNERS) {
            val objectVal = matchesListObject[position]
            val viewBanners: BannersViewHolder = parent as BannersViewHolder
            /*viewBanners.recyclerView.layoutManager =
                LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false)
            val adapter = BannersMatchesAdapter(
                mContext!!,
                objectVal.matchBanners!!
            )
            viewBanners.recyclerView.adapter = adapter*/

            val scrollViewAdapter = BannerSliderAdapter(mContext!!, objectVal.matchBanners!!)
            viewBanners.recyclerView.adapter = scrollViewAdapter
            scrollViewAdapter.notifyDataSetChanged()

            if (objectVal.matchBanners!!.size == 1) {
                viewBanners.recyclerView.stopAutoScroll()
            } else {
                viewBanners.recyclerView.startAutoScroll()
            }

            viewBanners.recyclerView.setInterval(3000)
            viewBanners.recyclerView.setDirection(AutoScrollViewPager.Direction.RIGHT)
            viewBanners.recyclerView.setCycle(true)
            viewBanners.recyclerView.setBorderAnimation(true)
            viewBanners.recyclerView.visibility = View.VISIBLE

        } else if (objectVal.viewType == TYPE_UPCOMING_MATCHES) {
            val objectVal = matchesListObject[position]
            val viewUpcomingMatches: UpcomingMatchesViewHolder = parent as UpcomingMatchesViewHolder
            viewUpcomingMatches.recyclerView.layoutManager =
                LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)

            if (objectVal.upcomingMatches != null && objectVal.upcomingMatches!!.size > 0) {
                viewUpcomingMatches.linearEmptyView.visibility = GONE
                val adapter = UpcomingMatchesAdapter(
                    mContext!!,
                    objectVal.upcomingMatches!!
                )
                viewUpcomingMatches.recyclerView.setHasFixedSize(true)
                // viewUpcomingMatches.recyclerView.setDrawingCacheEnabled(true);
                // viewUpcomingMatches.recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                viewUpcomingMatches.recyclerView.adapter = adapter
                //(viewUpcomingMatches.recyclerView.adapter as UpcomingMatchesAdapter).setHasStableIds(true)
                adapter.onItemClick = { objects ->

                    //MyUtils.logd("MatchesAdapter",objects.country1Name+" Vs "+objects.country1Name)
                    val intent = Intent(mContext, ContestActivity::class.java)
                    intent.putExtra(ContestActivity.SERIALIZABLE_KEY_UPCOMING_MATCHES, objects)
                    mContext!!.startActivity(intent)
                }
            } else {
                MyUtils.logd("ADaptor", "Draw Empty View Here")
                viewUpcomingMatches.linearEmptyView.visibility = VISIBLE
            }
        }
    }

    fun setMatchesList(matchesList: java.util.ArrayList<MatchesModels>?) {
        this.matchesListObject = matchesList!!
        // this.mContext = mContext
        notifyDataSetChanged()
    }

    fun addLoadingView() {
        Handler().post {
            matchesListObject.add(MatchesModels())
            notifyItemInserted(matchesListObject.size - 1)
        }
    }

    fun removeLoadingView() {
        matchesListObject.removeAt(matchesListObject.size - 1)
        notifyItemRemoved(matchesListObject.size)
    }

    override fun getItemCount(): Int {
        return matchesListObject.size
    }

    inner class ViewHolderJoinedMatches(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView = itemView.findViewById<RecyclerView>(R.id.recycler_joined_matches)
        val txtViewAll = itemView.findViewById<TextView>(R.id.txtViewAll)
        val backgroundImage = itemView.findViewById<ImageView>(R.id.imageView4)
    }

    inner class BannersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //val recyclerView = itemView.findViewById<RecyclerView>(R.id.recycler_banners)
        val recyclerView: AutoScrollViewPager = itemView.findViewById(R.id.recycler_banners)
    }

    inner class UpcomingMatchesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView = itemView.findViewById<RecyclerView>(R.id.recycler_upcoming_matches)
        val linearEmptyView = itemView.findViewById<LinearLayout>(R.id.linear_empty_view)

    }
}

