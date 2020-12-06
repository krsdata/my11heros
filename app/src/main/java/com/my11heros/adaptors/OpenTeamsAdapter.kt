package com.my11heros.adaptors

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.my11heros.CreateTeamActivity
import com.my11heros.SelectTeamActivity
import com.my11heros.R
import com.my11heros.models.MyTeamModels
import com.my11heros.models.UpcomingMatchesModel
import com.my11heros.network.IApiMethod
import com.my11heros.network.RequestModel
import com.my11heros.network.WebServiceClient
import com.my11heros.ui.contest.MyTeamFragment
import com.my11heros.ui.home.models.UsersPostDBResponse
import com.my11heros.utils.CustomeProgressDialog
import com.my11heros.utils.MyPreferences
import com.my11heros.utils.MyUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList


class OpenTeamsAdapter(
    val context: SelectTeamActivity,
    val matchObject: UpcomingMatchesModel?,
    val customeProgressDialog: CustomeProgressDialog,
    val myopenTeamsList: ArrayList<MyTeamModels>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var onClickListener: View.OnClickListener
    var onItemClick: ((MyTeamModels) -> Unit)? = null
    private var myopenTeamList = myopenTeamsList


    override fun getItemCount(): Int {
        return myopenTeamList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.myopen_team_row, parent, false)
        return MyMatchViewHolder(view)
    }

    override fun onBindViewHolder(parent: RecyclerView.ViewHolder, viewType: Int) {
        var objectVal = myopenTeamList[viewType]
        val viewHolder: MyMatchViewHolder =
            parent as MyMatchViewHolder
        viewHolder.userTeamName.text = objectVal.teamName
        viewHolder.teamaName.text = objectVal.teamsInfo!!.get(0).teamName
        viewHolder.teambName.text = objectVal.teamsInfo!!.get(1).teamName

        viewHolder.teamaCount.text = "" + objectVal.teamsInfo!!.get(0).count
        viewHolder.teambCount.text = "" + objectVal.teamsInfo!!.get(1).count


        viewHolder.captainPlayerName.text = objectVal.captain!!.playerName
        viewHolder.vcPlayerName.text = objectVal.viceCaptain!!.playerName

        viewHolder.countWicketkeeper.text = String.format("%d", objectVal.wicketKeepers!!.size)
        viewHolder.countBatsman.text = String.format("%d", objectVal.batsmen!!.size)
        viewHolder.countAllRounder.text = String.format("%d", objectVal.allRounders!!.size)
        viewHolder.countBowler.text = String.format("%d", objectVal.bowlers!!.size)

        viewHolder.checkBoxSelected.isChecked = objectVal.isSelected!!



        viewHolder.checkBoxSelected.setOnClickListener(object: View.OnClickListener
            {
                override fun onClick(v: View?) {
                    objectVal.isSelected = !objectVal.isSelected!!
                    notifyDataSetChanged()

                    //checkforAllSelections(objectVal.openTeamList, viewholderOpenTeam.checkAll)
                }


            })

        Glide.with(context)
            .load("https://")
            .placeholder(R.drawable.player_blue)
            .into(viewHolder.captainImageView)

        Glide.with(context)
            .load("https://")
            .placeholder(R.drawable.player_blue)
            .into(viewHolder.vcImageView)

        viewHolder.teamEdit.visibility = View.VISIBLE
        viewHolder.teamCopy.visibility = View.VISIBLE
        viewHolder.teamEdit.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, CreateTeamActivity::class.java)
            intent.putExtra(CreateTeamActivity.SERIALIZABLE_MATCH_KEY, matchObject)
            intent.putExtra(MyTeamFragment.SERIALIZABLE_EDIT_TEAM, objectVal)
            context.startActivity(intent)
        })

        viewHolder.teamCopy.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                //copyTeam(objectVal.teamId)
                val intent = Intent(context, CreateTeamActivity::class.java)
                intent.putExtra(CreateTeamActivity.SERIALIZABLE_MATCH_KEY, matchObject)
                intent.putExtra(MyTeamFragment.SERIALIZABLE_EDIT_TEAM, objectVal)
                context.startActivity(intent)
            }
        })
    }

    fun copyTeam(teamid: MyTeamModels.MyTeamId?) {
        //var userInfo = (activity as PlugSportsApplication).userInformations
        if(!MyUtils.isConnectedWithInternet(context)) {
            MyUtils.showToast(context,"No Internet connection found")
            return
        }
        customeProgressDialog.show()
        var models = RequestModel()
        models.user_id = MyPreferences.getUserID(context)!!
        models.match_id =""+matchObject!!.matchId
        models.team_id = teamid!!.teamId

        WebServiceClient(context).client.create(IApiMethod::class.java).copyTeam(models)
            .enqueue(object : Callback<UsersPostDBResponse?> {
                override fun onFailure(call: Call<UsersPostDBResponse?>?, t: Throwable?) {
                        MyUtils.showToast(context as AppCompatActivity,t!!.localizedMessage)
                }

                override fun onResponse(
                    call: Call<UsersPostDBResponse?>?,
                    response: Response<UsersPostDBResponse?>?
                ) {
                    customeProgressDialog.dismiss()
                    context.refreshContents()

                }

            })

    }

    fun setOnCheckChangedListeners(onClickListener: View.OnClickListener) {
        this.onClickListener = onClickListener
    }


    inner  class MyMatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(myopenTeamList[adapterPosition])
            }
        }
        val checkBoxSelected = itemView.findViewById<CheckBox>(R.id.selected)

        val userTeamName = itemView.findViewById<TextView>(R.id.user_team_name)
        val teamEdit = itemView.findViewById<ImageView>(R.id.team_edit)
        val teamCopy = itemView.findViewById<ImageView>(R.id.team_copy)
        val teamShare = itemView.findViewById<ImageView>(R.id.team_share)

        val teamaName = itemView.findViewById<TextView>(R.id.teama_name)
        val teamaCount = itemView.findViewById<TextView>(R.id.teama_count)
        val teambName = itemView.findViewById<TextView>(R.id.teamb_name)
        val teambCount = itemView.findViewById<TextView>(R.id.teamb_count)

        val captainImageView = itemView.findViewById<ImageView>(R.id.captain_imageView)
        val captainPlayerName = itemView.findViewById<TextView>(R.id.captain_player_name)

        val vcImageView = itemView.findViewById<ImageView>(R.id.vc_imageView)
        val vcPlayerName = itemView.findViewById<TextView>(R.id.vc_player_name)

        val countWicketkeeper = itemView.findViewById<TextView>(R.id.count_wicketkeeper)
        val countBatsman = itemView.findViewById<TextView>(R.id.count_batsman)
        val countAllRounder = itemView.findViewById<TextView>(R.id.count_allrounder)
        val countBowler = itemView.findViewById<TextView>(R.id.count_bowler)


    }


}

