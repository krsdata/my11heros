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
        mBinding!!.appVersion.text = String.format("App Version %s", MyUtils.getAppVersionName(requireActivity()))
        mBinding!!.recyclerMoreoptions.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        initContent()

        mBinding!!.progressBar.visibility = View.VISIBLE
        BackgroundLoading().execute()
    }

    private fun initContent() {
        allOptionsList.clear()

        val moreOptionModel1 = MoreOptionsModel()
        moreOptionModel1.drawable = R.drawable.more_refern_earn
        moreOptionModel1.id = 1
        moreOptionModel1.title = "Refer & Earn"
        allOptionsList.add(moreOptionModel1)

        val moreOptionModel2 = MoreOptionsModel()
        moreOptionModel2.drawable = R.drawable.more_point_system
        moreOptionModel2.id = 2
        moreOptionModel2.title = "Fantasy Points System"
        allOptionsList.add(moreOptionModel2)

        val moreOptionModel5 = MoreOptionsModel()
        moreOptionModel5.drawable = R.drawable.more_support
        moreOptionModel5.id = 5
        moreOptionModel5.title = getString(R.string.label_supportteam)
        allOptionsList.add(moreOptionModel5)

        val moreOptionModel3 = MoreOptionsModel()
        moreOptionModel3.drawable = R.drawable.more_about_us
        moreOptionModel3.id = 3
        moreOptionModel3.title = "About Us"
        allOptionsList.add(moreOptionModel3)

        val moreOptionModel4 = MoreOptionsModel()
        moreOptionModel4.drawable = R.drawable.more_terms_conditions
        moreOptionModel4.id = 4
        moreOptionModel4.title = "Terms and Conditions"
        allOptionsList.add(moreOptionModel4)

        val moreOptionModel6 = MoreOptionsModel()
        moreOptionModel6.drawable = R.drawable.more_terms_conditions
        moreOptionModel6.id = 6
        moreOptionModel6.title = "FAQs"
        allOptionsList.add(moreOptionModel6)

        val moreOptionModel7 = MoreOptionsModel()
        moreOptionModel7.drawable = R.drawable.more_logout
        moreOptionModel7.id = 6
        val userId = MyPreferences.getUserID(requireActivity())!!
        if (!TextUtils.isEmpty(userId)) {
            moreOptionModel7.title = "Logout"
        } else {
            moreOptionModel7.title = "Login"
        }
        allOptionsList.add(moreOptionModel7)
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
                    1 -> {
                        val intent = Intent(activity!!, InviteFriendsActivity::class.java)
                        val options =
                            ActivityOptions.makeSceneTransitionAnimation(activity)
                        startActivity(intent, options.toBundle())
                    }
                    2 -> {
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
                        intent.putExtra(
                            WebActivity.KEY_TITLE,
                            BindingUtils.WEB_TITLE_TERMS_CONDITION
                        )
                        intent.putExtra(WebActivity.KEY_URL, BindingUtils.WEBVIEW_TNC)
                        val options =
                            ActivityOptions.makeSceneTransitionAnimation(activity)
                        startActivity(intent, options.toBundle())
                    }
                    5 -> {
                        val intent = Intent(activity!!, SupportActivity::class.java)
                        val options =
                            ActivityOptions.makeSceneTransitionAnimation(activity)
                        startActivity(intent, options.toBundle())
                    }
                    6 -> {
                        val intent = Intent(activity!!, WebActivity::class.java)
                        intent.putExtra(WebActivity.KEY_TITLE, BindingUtils.WEB_TITLE_FAQ)
                        intent.putExtra(WebActivity.KEY_URL, BindingUtils.WEBVIEW_FAQ)
                        val options =
                            ActivityOptions.makeSceneTransitionAnimation(activity)
                        startActivity(intent, options.toBundle())
                    }
                    7 -> {
                        logoutApp("Are you sure you want to logout", true)
                    }
                }
            }
        }
    }
}