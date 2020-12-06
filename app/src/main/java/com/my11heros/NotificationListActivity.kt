package com.my11heros

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.my11heros.models.NotifyModels
import com.my11heros.models.WalletInfo
import com.my11heros.network.IApiMethod
import com.my11heros.network.RequestModel
import com.my11heros.network.WebServiceClient
import com.my11heros.ui.home.models.UsersPostDBResponse
import com.my11heros.utils.CustomeProgressDialog
import com.my11heros.utils.MyPreferences
import com.my11heros.utils.MyUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.my11heros.databinding.ActivityNotificationListBinding


class NotificationListActivity : AppCompatActivity() {
    private lateinit var adapter: NotificationListAdaptor
    var allNotificationList = ArrayList<NotifyModels>()
    private lateinit var walletInfo: WalletInfo
    private var customeProgressDialog: CustomeProgressDialog?=null
    private var mBinding: ActivityNotificationListBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_notification_list
        )
        walletInfo = (application as SportsFightApplication).walletInfo
        mBinding!!.toolbar.title = "Notifications"
        mBinding!!.toolbar.setTitleTextColor(resources.getColor(R.color.white))
        mBinding!!.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        setSupportActionBar(mBinding!!.toolbar)
        mBinding!!.toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })
        customeProgressDialog = CustomeProgressDialog(this)
        adapter = NotificationListAdaptor(this, allNotificationList)
        mBinding!!.recyclerNotificationList.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        var itemDecoration = DividerItemDecoration(this, LinearLayout.VERTICAL)
        mBinding!!.recyclerNotificationList.addItemDecoration(itemDecoration)
        mBinding!!.recyclerNotificationList.adapter = adapter
        adapter.onItemClick = { objects ->

        }
        if(!MyUtils.isConnectedWithInternet(this)) {
            MyUtils.showToast(this,"No Internet connection found")
            return
        }
        getNotificationList()
    }

    fun getNotificationList() {
        customeProgressDialog!!.show()
        var models = RequestModel()
        models.user_id = MyPreferences.getUserID(this)!!
        //models.token = MyPreferences.getToken(this)!!

        WebServiceClient(this).client.create(IApiMethod::class.java).getNotification(models)
            .enqueue(object : Callback<UsersPostDBResponse?> {
                override fun onFailure(call: Call<UsersPostDBResponse?>?, t: Throwable?) {
                    customeProgressDialog!!.dismiss()
                }

                override fun onResponse(
                    call: Call<UsersPostDBResponse?>?,
                    response: Response<UsersPostDBResponse?>?
                ) {
                    customeProgressDialog!!.dismiss()
                    var res = response!!.body()
                    if(res!=null) {
                        var responseModel = res.notificationList
                        if(responseModel!=null &&  responseModel.size>0) {
                            allNotificationList.addAll(responseModel)
                            adapter.notifyDataSetChanged()
                        }
                    }

                }

            })

    }


    inner class NotificationListAdaptor(
        val context: Context,
        val tradeinfoModels: ArrayList<NotifyModels>
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var onItemClick: ((NotifyModels) -> Unit)? = null
        private var optionListObject = tradeinfoModels


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_notification, parent, false)
            return DataViewHolder(view)

        }

        override fun onBindViewHolder(parent: RecyclerView.ViewHolder, viewType: Int) {
            var objectVal = optionListObject[viewType]
            val viewHolder: DataViewHolder = parent as DataViewHolder
            viewHolder.notificationTitle?.text = objectVal.notificationTitle
            viewHolder.notificationMessage?.text = objectVal.notificationMessages
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

            val notificationMessage = itemView.findViewById<TextView>(R.id.notification_message)
            val notificationTitle = itemView.findViewById<TextView>(R.id.notification_title)
        }


    }

}
