package com.my11heros.adaptors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.my11heros.R
import com.my11heros.SelectTeamActivity
import com.my11heros.models.MyTeamModels
import com.my11heros.models.SelectedTeamModels
import com.my11heros.models.UpcomingMatchesModel
import com.my11heros.utils.CustomeProgressDialog


class SelectedTeamAdapter(
    val context: SelectTeamActivity,
    val matchObject: UpcomingMatchesModel,
    val customeProgressDialog: CustomeProgressDialog,
    val tradeinfoModels: ArrayList<SelectedTeamModels>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var onItemClick: ((SelectedTeamModels) -> Unit)? = null
    var mContext: SelectTeamActivity? = context
    private var matchesListObject = tradeinfoModels

    companion object {
        const val TYPE_CLOSED = 1
        const val TYPE_OPENED = 2
    }

    override fun getItemViewType(position: Int): Int {
        val comparable = matchesListObject.get(position)
        if (comparable.closeTeamList != null && comparable.closeTeamList!!.size > 0) {
            return TYPE_CLOSED
        } else {
            return TYPE_OPENED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_CLOSED) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.selected_team_created_label, parent, false)
            return ViewHolderJoinedMatches(view)
        } else if (viewType == TYPE_OPENED) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.unselectedselected_team_created_labe, parent, false)
            return UpcomingMatchesViewHolder(view)
        }
        return null!!
    }

    override fun onBindViewHolder(parent: RecyclerView.ViewHolder, viewType: Int) {


        if (parent.itemViewType == TYPE_CLOSED) {
            val objectVal = matchesListObject[viewType]
            val viewJoinedMatches: ViewHolderJoinedMatches = parent as ViewHolderJoinedMatches
            viewJoinedMatches.recyclerView.layoutManager =
                LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
            val adapter = ClosedTeamsAdapter(
                mContext!!,
                matchObject,
                customeProgressDialog,
                objectVal.closeTeamList!!
            )
            viewJoinedMatches.recyclerView.adapter = adapter
        } else if (parent.itemViewType == TYPE_OPENED) {
            val objectVal = matchesListObject[viewType]
            val viewholderOpenTeam: UpcomingMatchesViewHolder = parent as UpcomingMatchesViewHolder
            viewholderOpenTeam.recyclerView.layoutManager =
                LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
            if (objectVal.openTeamList != null && objectVal.openTeamList!!.size > 0) {
                val adapter = OpenTeamsAdapter(
                    mContext!!,
                    matchObject,
                    customeProgressDialog,
                    objectVal.openTeamList!!
                )
                adapter.setOnCheckChangedListeners(View.OnClickListener {
                    checkforAllSelections(objectVal.openTeamList, viewholderOpenTeam.checkAll)

                })
                viewholderOpenTeam.checkAll.setOnClickListener(View.OnClickListener {
                    if (viewholderOpenTeam.checkAll.isChecked) {
                        for (x in 0..objectVal.openTeamList!!.size - 1) {
                            val values = objectVal.openTeamList!!.get(x)
                            values.isSelected = true
                            objectVal.openTeamList!!.set(x, values)

                        }
                    } else {
                        for (x in 0..objectVal.openTeamList!!.size - 1) {
                            val values = objectVal.openTeamList!!.get(x)
                            values.isSelected = false
                            objectVal.openTeamList!!.set(x, values)
                        }
                    }

                    adapter.notifyDataSetChanged()
                })


                viewholderOpenTeam.recyclerView.adapter = adapter
                adapter.onItemClick = { objects ->

                    objects.isSelected = !objects.isSelected!!
                    adapter.notifyDataSetChanged()

                    checkforAllSelections(objectVal.openTeamList, viewholderOpenTeam.checkAll)
                }

            }
        }
    }

    private fun checkforAllSelections(
        openlist: ArrayList<MyTeamModels>?,
        checkAll: CheckBox
    ) {
        var isAllChecked = false
        for (x in 0..openlist!!.size - 1) {
            val values = openlist.get(x)
            if (values.isSelected!!) {
                isAllChecked = true
            } else {
                isAllChecked = false
                break
            }

        }

        checkAll.isChecked = isAllChecked
    }

    override fun getItemCount(): Int {
        return matchesListObject.size
    }

    inner class ViewHolderJoinedMatches(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView = itemView.findViewById<RecyclerView>(R.id.recycler_joined_team)
    }

    inner class UpcomingMatchesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView = itemView.findViewById<RecyclerView>(R.id.recycler_joined_team)
        val checkAll = itemView.findViewById<CheckBox>(R.id.checkbox_selected_team)

    }


}

