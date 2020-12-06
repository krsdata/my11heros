package com.my11heros.adaptors

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.my11heros.AddMoneyActivity
import com.my11heros.InviteFriendsActivity
import com.my11heros.R
import com.my11heros.SupportActivity
import com.my11heros.models.MatchBannersModel
import com.my11heros.utils.BindingUtils


class BannersMatchesAdapter(
    val context: Context,
    val tradeinfoModels: ArrayList<MatchBannersModel>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var onItemClick: ((MatchBannersModel) -> Unit)? = null
    private var matchesListObject = tradeinfoModels


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.matches_row_banners_inner, parent, false)
        return BannerMatchesViewHolder(view)
    }

    override fun onBindViewHolder(parent: RecyclerView.ViewHolder, viewType: Int) {
        val objectVal = matchesListObject[viewType]
        val viewHolder: BannerMatchesViewHolder = parent as BannerMatchesViewHolder

        Glide.with(context)
            .load(objectVal.bannerUrl)
            .placeholder(R.drawable.rectangle_left_top_curve)
            .skipMemoryCache(true)
            .disallowHardwareConfig()
            .into(viewHolder.imageBanners)

        viewHolder.imageBanners.setOnClickListener(View.OnClickListener {
            if (objectVal.title.equals(BindingUtils.BANNERS_KEY_ADD)) {
                val intent = Intent(context, AddMoneyActivity::class.java)
                context.startActivity(intent)
            }
            if (objectVal.title.equals(BindingUtils.BANNERS_KEY_REFFER)) {
                val intent = Intent(context, InviteFriendsActivity::class.java)
                context.startActivity(intent)
            }
            if (objectVal.title.equals(BindingUtils.BANNERS_KEY_SUPPORT)) {
                val intent = Intent(context, SupportActivity::class.java)
                context.startActivity(intent)
            }

            if (objectVal.title.equals(BindingUtils.BANNERS_KEY_BROWSERS)) {

                val builder = CustomTabsIntent.Builder()
                builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))
                val customTabsIntent = builder.build()
                customTabsIntent.intent.setPackage("com.android.chrome")
                customTabsIntent.launchUrl(context, Uri.parse(objectVal.descriptions))

                // BindingUtils.sendEventLogs(context!!, 0,0,(context!!.applicationContext as SportsFightApplication).userInformations,"GAMEZOP-BANNER-FREE")

//                val urlString = objectVal.descriptions
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                intent.setPackage("com.android.chrome")
//                try {
//                    context.startActivity(intent)
//                } catch (ex: ActivityNotFoundException) {
//                    // Chrome browser presumably not installed so allow user to choose instead
//                    intent.setPackage(null)
//                    context.startActivity(intent)
//                }
            }
        })

    }


    override fun getItemCount(): Int {
        return matchesListObject.size
    }

    inner class BannerMatchesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageBanners = itemView.findViewById<ImageView>(R.id.image_banner)


    }


}

