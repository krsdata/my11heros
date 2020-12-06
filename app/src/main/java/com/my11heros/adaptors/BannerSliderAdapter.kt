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
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.my11heros.AddMoneyActivity
import com.my11heros.InviteFriendsActivity
import com.my11heros.R
import com.my11heros.SupportActivity
import com.my11heros.models.MatchBannersModel
import com.my11heros.utils.BindingUtils

class BannerSliderAdapter(val context: Context, val tradeinfoModels: ArrayList<MatchBannersModel>) :
    PagerAdapter() {
    var mContext: Context = context
    private var arrayList = tradeinfoModels
    var inflater: LayoutInflater = LayoutInflater.from(mContext)

    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var itemView: View? = null
        val viewHolder: ViewHolder
        if (itemView == null) {
            itemView = inflater.inflate(R.layout.matches_row_banners_inner, container, false)
            viewHolder = ViewHolder(itemView!!)
            itemView.tag = viewHolder
        } else {
            viewHolder = itemView.tag as ViewHolder
        }
        try {
            val objectVal = arrayList[position]
            Glide.with(context)
                .load(objectVal.bannerUrl)
                .placeholder(R.drawable.rectangle_left_top_curve)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.target)

            viewHolder.target.setOnClickListener(View.OnClickListener {
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
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
        container.addView(itemView)
        return itemView
    }

    internal class ViewHolder(view: View) {
        var target: ImageView = view.findViewById(R.id.image_banner)

    }
}