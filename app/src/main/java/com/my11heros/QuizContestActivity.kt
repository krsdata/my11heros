package com.my11heros

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.my11heros.models.UpcomingMatchesModel
import com.my11heros.ui.BaseActivity
import com.my11heros.ui.contest.models.ContestModelLists
import com.my11heros.databinding.ActivityQuizContestBinding


class QuizContestActivity : BaseActivity() {

    var matchObject: UpcomingMatchesModel? = null
    private var mBinding: ActivityQuizContestBinding? = null
    var allContestsRunning: ArrayList<ContestModelLists> =ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.red)
        }
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_quiz_contest)

        for( i in 0..10){
            allContestsRunning.add(ContestModelLists())
        }


        mBinding!!.imageBack.setOnClickListener(View.OnClickListener {
            finish()
        })

        mBinding!!.imgWallet.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@QuizContestActivity, MyBalanceActivity::class.java)
            startActivity(intent)
        })

        mBinding!!.recyclerQuizContest.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        var adapter = QuizContestListAdapter(
            this,
            allContestsRunning,
            matchObject!!
        )
        mBinding!!.recyclerQuizContest.adapter = adapter

    }

    override fun onBitmapSelected(bitmap: Bitmap) {
    }

    override fun onUploadedImageUrl(url: String) {}

    class QuizContestListAdapter(
        val context: Activity,
        val contestModelList: ArrayList<ContestModelLists>,
        matchObject: UpcomingMatchesModel
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var onItemClick: ((ContestModelLists) -> Unit)? = null
        private var matchesListObject =  contestModelList

        var matchObject = matchObject

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context)
                .inflate(R.layout.contest_rows_inner, parent, false)
            return DataViewHolder(view)
        }

        override fun onBindViewHolder(parent: RecyclerView.ViewHolder, viewType: Int) {
            var objectVal = matchesListObject[viewType]
            val viewHolder: DataViewHolder = parent as DataViewHolder
//            viewHolder?.contestPrizePool?.text =String.format("%s%d","Rs ",objectVal.totalWinningPrize)
//            viewHolder?.contestEntryPrize?.text = String.format("%s%d","₹",objectVal.entryFees)
//            viewHolder?.firstPrize?.text = String.format("%s%d","₹",objectVal.firstPrice)
//            viewHolder?.winningPercentage?.text = String.format("%d%s",objectVal.winnerPercentage,"%")
//            viewHolder?.maxAllowedTeam?.text = String.format("%s %d %s","Upto",objectVal.maxAllowedTeam,"teams")
//
//
//            if(objectVal.totalSpots==0){
//                viewHolder.contestProgress.max =objectVal.totalSpots +15
//                viewHolder.contestProgress.progress =objectVal.filledSpots
//                viewHolder?.totalSpot?.text = String.format("unlimited spots")
//                viewHolder?.totalSpotLeft?.text = String.format("%d spot filled",objectVal.filledSpots)
//            }else {
//                viewHolder.contestProgress.max =objectVal.totalSpots
//                viewHolder.contestProgress.progress =objectVal.filledSpots
//                if(objectVal.totalSpots==objectVal.filledSpots){
//                    viewHolder?.totalSpot?.text = "Contest full"
//                    viewHolder?.totalSpot?.setTextSize(18.0f)
//                    viewHolder?.totalSpotLeft?.text = ""
//                }else {
//                    viewHolder?.totalSpot?.text = String.format("%d spots", objectVal.totalSpots)
//                    viewHolder?.totalSpotLeft?.text =
//                        String.format("%d spot left", objectVal.totalSpots - objectVal.filledSpots)
//                }
//            }
//
//            viewHolder?.linearContestViews?.setOnClickListener(View.OnClickListener {
//                val intent = Intent(context, LeadersBoardActivity::class.java)
//                intent.putExtra(LeadersBoardActivity.SERIALIZABLE_MATCH_KEY, matchObject)
//                intent.putExtra(LeadersBoardActivity.SERIALIZABLE_CONTEST_KEY, objectVal)
//                context!!.startActivityForResult(intent, LeadersBoardActivity.CREATETEAM_REQUESTCODE)
//            })
//
//            viewHolder?.contestCancellation?.setOnClickListener(View.OnClickListener {
//                //MyUtils.showToast(viewHolder?.contestCancellation,objectVal.cancellation)
//            })
//
//            viewHolder?.contestEntryPrize?.setOnClickListener(View.OnClickListener {
//            })
        }



        override fun getItemCount(): Int {
            return matchesListObject.size
        }

        inner  class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            init {
                itemView.setOnClickListener {
                    onItemClick?.invoke(matchesListObject[adapterPosition])
                }
            }
            val linearContestViews = itemView.findViewById<LinearLayout>(R.id.linear_trades_status)
            val contestPrizePool = itemView.findViewById<TextView>(R.id.contest_prize_pool)
            val contestEntryPrize = itemView.findViewById<TextView>(R.id.contest_entry_prize)
            val firstPrize = itemView.findViewById<TextView>(R.id.first_prize)
            val winningPercentage = itemView.findViewById<TextView>(R.id.winning_percentage)
         //   val maxAllowedTeam = itemView.findViewById<TextView>(R.id.max_allowed_team)
            val contestCancellation = itemView.findViewById<ImageView>(R.id.contest_cancellation)

            val totalSpotLeft = itemView.findViewById<TextView>(R.id.total_spot_left)
            val totalSpot = itemView.findViewById<TextView>(R.id.total_spot)
            val contestProgress = itemView.findViewById<ProgressBar>(R.id.contest_progress)
       }


    }
}
