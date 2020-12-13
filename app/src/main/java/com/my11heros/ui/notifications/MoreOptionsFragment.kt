package com.my11heros.ui.notifications

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout.VERTICAL
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.my11heros.*
import com.my11heros.databinding.FragmentMoreoptionsBinding
import com.my11heros.models.MoreOptionsModel
import com.my11heros.ui.BaseFragment
import com.my11heros.utils.BindingUtils
import com.my11heros.utils.MyPreferences
import com.my11heros.utils.MyUtils

class MoreOptionsFragment : BaseFragment() {

    private var mBinding: FragmentMoreoptionsBinding? = null
    lateinit var adapter: MoreOptionsAdaptor
    var allOptionsList = ArrayList<MoreOptionsModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_moreoptions, container, false
        )
        return mBinding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).showToolbar()
        mBinding!!.appVersion.text = "App Version ${MyUtils.getAppVersionName(requireActivity())}"
        mBinding!!.recyclerMoreoptions.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        initContent()

        mBinding!!.progressBar.visibility = View.VISIBLE
        BackgroundLoading().execute()
    }


    private fun initContent() {
        allOptionsList.clear()

        /*val upcomingMModle11 = MoreOptionsModel()
        upcomingMModle11.drawable = R.drawable.more_terms_conditions
        upcomingMModle11.id = 11
        upcomingMModle11.title = "Top Referral Users"
        allOptionsList.add(upcomingMModle11)

        val upcomingMModle10 = MoreOptionsModel()
        upcomingMModle10.drawable = R.drawable.more_terms_conditions
        upcomingMModle10.id = 10
        upcomingMModle10.title = "Offers"
        allOptionsList.add(upcomingMModle10)*/

        val upcomingMModle1 = MoreOptionsModel()
        upcomingMModle1.drawable = R.drawable.more_refern_earn
        upcomingMModle1.id = 0
        upcomingMModle1.title = "Refer & Earn"
        allOptionsList.add(upcomingMModle1)

        val upcomingMModle7 = MoreOptionsModel()
        upcomingMModle7.drawable = R.drawable.more_support
        upcomingMModle7.id = 6
        upcomingMModle7.title = getString(R.string.label_supportteam)
        allOptionsList.add(upcomingMModle7)


        val upcomingMModle2 = MoreOptionsModel()
        upcomingMModle2.drawable = R.drawable.more_point_system
        upcomingMModle2.id = 1
        upcomingMModle2.title = "Fantasy Points System"
        allOptionsList.add(upcomingMModle2)


        /*val upcomingMModle3 = MoreOptionsModel()
        upcomingMModle3.drawable = R.drawable.more_terms_conditions
        upcomingMModle3.id = 2
        upcomingMModle3.title = "How to Play"
        allOptionsList.add(upcomingMModle3)*/

        val upcomingMModle9 = MoreOptionsModel()
        upcomingMModle9.drawable = R.drawable.more_terms_conditions
        upcomingMModle9.id = 9
        upcomingMModle9.title = "FAQs"
        allOptionsList.add(upcomingMModle9)

        val upcomingMModle4 = MoreOptionsModel()
        upcomingMModle4.drawable = R.drawable.more_about_us
        upcomingMModle4.id = 3
        upcomingMModle4.title = "About Us"
        allOptionsList.add(upcomingMModle4)

        /*val upcomingMModle5 = MoreOptionsModel()
        upcomingMModle5.drawable = R.drawable.more_legality
        upcomingMModle5.id = 4
        upcomingMModle5.title = "Legality"
        allOptionsList.add(upcomingMModle5)*/

        val upcomingMModle6 = MoreOptionsModel()
        upcomingMModle6.drawable = R.drawable.more_terms_conditions
        upcomingMModle6.id = 5
        upcomingMModle6.title = "Terms and Conditions"
        allOptionsList.add(upcomingMModle6)

//        var upcomingMModle9 = MoreOptionsModel()
//        upcomingMModle9.drawable = R.drawable.ic_chat_black_24dp
//        upcomingMModle9.id = 9
//        upcomingMModle9.title = "Chat with Sports Fight"
//        allOptionsList.add(upcomingMModle9)

        val upcomingMModle8 = MoreOptionsModel()
        upcomingMModle8.drawable = R.drawable.more_logout
        upcomingMModle8.id = 8
        val userId = MyPreferences.getUserID(requireActivity())!!
        if (!TextUtils.isEmpty(userId)) {
            upcomingMModle8.title = "Logout"
        } else {
            upcomingMModle8.title = "Login"
        }

        allOptionsList.add(upcomingMModle8)
    }

    inner class MoreOptionsAdaptor(
        val context: Context,
        val tradeinfoModels: ArrayList<MoreOptionsModel>
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var onItemClick: ((MoreOptionsModel) -> Unit)? = null
        private var optionListObject = tradeinfoModels


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_more_options, parent, false)
            return DataViewHolder(view)

        }

        override fun onBindViewHolder(parent: RecyclerView.ViewHolder, viewType: Int) {
            val objectVal = optionListObject[viewType]
            val viewHolder: DataViewHolder = parent as DataViewHolder
            viewHolder.optionsTitle?.text = objectVal.title
            viewHolder.optionIcon.setImageResource(objectVal.drawable)
        }

        override fun getItemCount(): Int {
            return optionListObject.size
        }

        inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            init {
                itemView.setOnClickListener {
                    onItemClick?.invoke(optionListObject[adapterPosition])
                }
            }

            val optionsTitle = itemView.findViewById<TextView>(R.id.options_title)
            val optionIcon = itemView.findViewById<ImageView>(R.id.option_icon)
        }
    }

    inner class BackgroundLoading : AsyncTask<Unit, Unit, String>() {

        override fun doInBackground(vararg params: Unit): String {
            Thread.sleep(200)
            return ""
        }

        override fun onPostExecute(result: String) {
            mBinding!!.progressBar.visibility = View.INVISIBLE

            val itemDecoration = DividerItemDecoration(activity!!, VERTICAL)
            mBinding!!.recyclerMoreoptions.addItemDecoration(itemDecoration)
            adapter = MoreOptionsAdaptor(activity!!, allOptionsList)
            mBinding!!.recyclerMoreoptions.adapter = adapter
            adapter.onItemClick = { objects ->

                when (objects.id) {
                    0 -> {
                        val intent = Intent(activity!!, InviteFriendsActivity::class.java)
                        val options =
                            ActivityOptions.makeSceneTransitionAnimation(activity)
                        startActivity(intent, options.toBundle())
                    }
                    1 -> {
                        val intent = Intent(activity!!, WebActivity::class.java)
                        intent.putExtra(
                            WebActivity.KEY_TITLE,
                            BindingUtils.WEB_TITLE_FANTASY_POINTS
                        )
                        intent.putExtra(WebActivity.KEY_URL, BindingUtils.WEBVIEW_FANTASY_POINTS)
                        val options =
                            ActivityOptions.makeSceneTransitionAnimation(activity)
                        startActivity(intent, options.toBundle())
                    }
                    2 -> {
                        val intent = Intent(activity!!, WebActivity::class.java)
                        intent.putExtra(WebActivity.KEY_TITLE, BindingUtils.WEB_TITLE_HOW_TO_PLAY)
                        intent.putExtra(
                            WebActivity.KEY_URL,
                            BindingUtils.WEBVIEW_FANTASY_HOW_TO_PLAY
                        )
                        val options =
                            ActivityOptions.makeSceneTransitionAnimation(activity)
                        startActivity(intent, options.toBundle())
                    }
                    3 -> {
                        val intent = Intent(activity!!, WebActivity::class.java)
                        intent.putExtra(WebActivity.KEY_TITLE, BindingUtils.WEB_TITLE_ABOUT_US)
                        intent.putExtra(WebActivity.KEY_URL, BindingUtils.WEBVIEW_ABOUT_US)
                        val options =
                            ActivityOptions.makeSceneTransitionAnimation(activity)
                        startActivity(intent, options.toBundle())
                    }
                    4 -> {
                        val intent = Intent(activity!!, WebActivity::class.java)
                        intent.putExtra(WebActivity.KEY_TITLE, BindingUtils.WEB_TITLE_LEGALITY)
                        intent.putExtra(WebActivity.KEY_URL, BindingUtils.WEBVIEW_LEGALITY)
                        val options =
                            ActivityOptions.makeSceneTransitionAnimation(activity)
                        startActivity(intent, options.toBundle())
                    }
                    5 -> {
                        val intent = Intent(activity!!, WebActivity::class.java)
                        intent.putExtra(
                            WebActivity.KEY_TITLE,
                            BindingUtils.WEB_TITLE_TERMS_CONDITION
                        )
                        intent.putExtra(WebActivity.KEY_URL, BindingUtils.WEBVIEW_TNC)
                        val options =
                            ActivityOptions.makeSceneTransitionAnimation(activity)
                        startActivity(intent, options.toBundle())
                    }

                    6 -> {
                        val intent = Intent(activity!!, SupportActivity::class.java)
                        val options =
                            ActivityOptions.makeSceneTransitionAnimation(activity)
                        startActivity(intent, options.toBundle())
                    }

                    8 -> {
                        logoutApp("Are you sure you want to logout", true)
                    }

                    9 -> {
                        val intent = Intent(activity!!, WebActivity::class.java)
                        intent.putExtra(WebActivity.KEY_TITLE, BindingUtils.WEB_TITLE_FAQ)
                        intent.putExtra(WebActivity.KEY_URL, BindingUtils.WEBVIEW_FAQ)
                        val options =
                            ActivityOptions.makeSceneTransitionAnimation(activity)
                        startActivity(intent, options.toBundle())
                    }

                    10 -> {
                        val intent = Intent(activity!!, WebActivity::class.java)
                        intent.putExtra(WebActivity.KEY_TITLE, BindingUtils.WEB_TITLE_OFFERS)
                        intent.putExtra(WebActivity.KEY_URL, BindingUtils.WEBVIEW_OFFERS)
                        val options =
                            ActivityOptions.makeSceneTransitionAnimation(activity)
                        startActivity(intent, options.toBundle())
                    }

                    11 -> {
                        val intent = Intent(activity!!, WebActivity::class.java)
                        intent.putExtra(
                            WebActivity.KEY_TITLE,
                            BindingUtils.WEB_TITLE_TOP_REFERRAL_USER
                        )
                        intent.putExtra(WebActivity.KEY_URL, BindingUtils.WEBVIEW_TOP_REFERRAL_USER)
                        val options =
                            ActivityOptions.makeSceneTransitionAnimation(activity)
                        startActivity(intent, options.toBundle())
                    }
                }
            }
        }
    }
}