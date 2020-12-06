package com.my11heros.ui.createteam.adaptors

//
//class SelectedPlayersCategoryAdapter(val context: Context, contestModelList: ArrayList<PlayersInfoModel>, listeners: OnRolesSelected) :
//    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//    var onItemClick: ((PlayersInfoModel) -> Unit)? = null
//    private var matchesListObject =  contestModelList
//    var listeners = listeners
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        var view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.row_players_selected, parent, false)
//        return DataViewHolder(view)
//    }
//
//    override fun onBindViewHolder(parent: RecyclerView.ViewHolder, position: Int) {
//        var objectVal = matchesListObject[position]
//
//        val viewHolder: DataViewHolder = parent as DataViewHolder
//        viewHolder.selectedPlayerName?.text = objectVal.shortName
//        viewHolder.selectedPlayerPoints?.text = ""+objectVal.fantasyPlayerRating
//        viewHolder.selectedPlayerCountry?.text = objectVal.teamShortName
//
//        viewHolder.selectedPlayingStyle?.text = objectVal.playerRole
//
//        if(objectVal.analyticsModel!=null){
//            viewHolder.selectedCaptainPercentage.text = String.format("%.1f%%",objectVal.analyticsModel!!.captainPc)
//            viewHolder.selectedvcPercentage.text = String.format("%.1f%%",objectVal.analyticsModel!!.viceCaptainPc)
//            viewHolder.selectedTrumpPercentage.text = String.format("%.1f%%",objectVal.analyticsModel!!.trumpPc)
//        }else {
//            viewHolder.selectedCaptainPercentage.text = "0%"
//            viewHolder.selectedvcPercentage.text = "0%"
//            viewHolder.selectedTrumpPercentage.text = "0%"
//        }
//
//
//        Glide.with(context)
//            .load(objectVal.playerImage)
//            .placeholder(getPlaceHolder())
//            .into(viewHolder.selectedPlayerImage)
//
//        setSelections(objectVal,viewHolder,position)
//
//    }
//
//    private fun getPlaceHolder(): Int {
//
//        return R.drawable.placeholder_player_teamb
//    }
//
//    private fun setSelections(
//        objectVal: PlayersInfoModel,
//        viewHolder: DataViewHolder,
//        position:Int
//    ) {
//
//        if(objectVal.isTrump){
//            viewHolder.roleTypeTrump.text = "3X"
//            viewHolder.roleTypeTrump.setBackgroundResource(R.drawable.circle_green)
//            viewHolder.roleTypeTrump.setTextColor(context.resources.getColor(R.color.white))
//
//        }else {
//            viewHolder.roleTypeTrump.text = "T"
//            viewHolder.roleTypeTrump.setBackgroundResource(R.drawable.circle_save_match)
//            viewHolder.roleTypeTrump.setTextColor(context.resources.getColor(R.color.black))
//        }
//
//        if(objectVal.isCaptain){
//            viewHolder.roleTypeCaptain.text = "2X"
//            viewHolder.roleTypeCaptain.setBackgroundResource(R.drawable.circle_green)
//            viewHolder.roleTypeCaptain.setTextColor(context.resources.getColor(R.color.white))
//        }else {
//            viewHolder.roleTypeCaptain.text = "C"
//            viewHolder.roleTypeCaptain.setBackgroundResource(R.drawable.circle_save_match)
//            viewHolder.roleTypeCaptain.setTextColor(context.resources.getColor(R.color.black))
//        }
//
//
//        if(objectVal.isViceCaptain){
//            viewHolder.roleTypeViceCaptain.text = "1.5X"
//            viewHolder.roleTypeViceCaptain.setBackgroundResource(R.drawable.circle_green)
//            viewHolder.roleTypeViceCaptain.setTextColor(context.resources.getColor(R.color.white))
//        }else {
//            viewHolder.roleTypeViceCaptain.text = "VC"
//            viewHolder.roleTypeViceCaptain.setBackgroundResource(R.drawable.circle_save_match)
//            viewHolder.roleTypeViceCaptain.setTextColor(context.resources.getColor(R.color.black))
//        }
//
//
//        viewHolder.roleTypeTrump?.setOnClickListener(View.OnClickListener {
//            listeners.onTrumpSelected(objectVal,position)
//
//        })
//
//        viewHolder.roleTypeCaptain?.setOnClickListener(View.OnClickListener {
//            listeners.onCaptainSelected(objectVal,position)
//
//        })
//
//        viewHolder.roleTypeViceCaptain?.setOnClickListener(View.OnClickListener {
//            listeners.onViceCaptainSelected(objectVal,position)
//
//        })
//    }
//
//
//    override fun getItemCount(): Int {
//        return matchesListObject.size
//    }
//
//    inner  class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        init {
//            itemView.setOnClickListener {
//                onItemClick?.invoke(matchesListObject[adapterPosition])
//            }
//        }
//        val selectedPlayerName = itemView.findViewById<TextView>(R.id.selected_player_name)
//        val selectedPlayerPoints = itemView.findViewById<TextView>(R.id.selected_player_points)
//        val selectedPlayerCountry = itemView.findViewById<TextView>(R.id.selected_player_country)
//        val selectedPlayingStyle = itemView.findViewById<TextView>(R.id.selected_player_playing_style)
//        val selectedPlayerImage = itemView.findViewById<ImageView>(R.id.selected_player_image)
//
//        val roleTypeTrump = itemView.findViewById<TextView>(R.id.role_type_trump)
//        val selectedTrumpPercentage = itemView.findViewById<TextView>(R.id.selected_trump_percentage)
//
//        val roleTypeCaptain = itemView.findViewById<TextView>(R.id.role_type_captain)
//        val selectedCaptainPercentage = itemView.findViewById<TextView>(R.id.selected_captain_percentage)
//
//        val roleTypeViceCaptain = itemView.findViewById<TextView>(R.id.role_type_vicecaptain)
//        val selectedvcPercentage = itemView.findViewById<TextView>(R.id.selected_vc_percentage)
//
//
//
//
//    }
//
//
//}

