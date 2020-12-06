package com.my11heros.ui.createteam.adaptors

//
//class PlayerSelectedLabelAdapter(val context: Context, val tradeinfoModels: ArrayList<MatchesModels>) :
//    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//    var onItemClick: ((MatchesModels) -> Unit)? = null
//    var mContext:Context ? =context
//    private var matchesListObject =  tradeinfoModels
//
//    companion object {
//        const val TYPE_LABEL = 1
//        const val TYPE_DATA = 2
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        val comparable = matchesListObject.get(position)
//            return comparable.viewType
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        if(viewType== TYPE_LABEL){
//            var view = LayoutInflater.from(parent.context)
//                .inflate(R.layout.matches_row_joined_matches, parent, false)
//            return ViewLabelsHolders(view)
//        }else if(viewType== TYPE_DATA){
//            var view = LayoutInflater.from(parent.context)
//                .inflate(R.layout.matches_row_banners_matches, parent, false)
//            return BannersViewHolder(view)
//        }
//        return null!!
//    }
//
//    override fun onBindViewHolder(parent: RecyclerView.ViewHolder, viewType: Int) {
//        var objectVal = matchesListObject[viewType]
//
//        if(objectVal.viewType== TYPE_LABEL){
//
//        } else
//        if(viewType== TYPE_DATA){
//
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return matchesListObject.size
//    }
//
//    inner  class ViewLabelsHolders(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val roleType = itemView.findViewById<TextView>(R.id.player_role_label)
//    }
//
//    inner  class PlayerSelectedModels(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val recyclerView = itemView.findViewById<RecyclerView>(R.id.recycler_banners)
//    }
//
//}
//
