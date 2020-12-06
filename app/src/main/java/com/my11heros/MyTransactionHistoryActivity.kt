package com.my11heros

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.my11heros.models.TransactionModel
import com.my11heros.network.IApiMethod
import com.my11heros.network.RequestModel
import com.my11heros.network.WebServiceClient
import com.my11heros.ui.BaseActivity
import com.my11heros.ui.home.models.UsersPostDBResponse
import com.my11heros.utils.CustomeProgressDialog
import com.my11heros.utils.MyPreferences
import com.my11heros.utils.MyUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.my11heros.databinding.ActivityMyTransactionBinding


class MyTransactionHistoryActivity : BaseActivity() {

    private lateinit var adapter: MoreOptionsAdaptor
    private var mBinding: ActivityMyTransactionBinding? = null

    var transactionList = ArrayList<TransactionModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_my_transaction
        )
        mBinding!!.toolbar.title = "Recent Transaction"
        mBinding!!.toolbar.setTitleTextColor(resources.getColor(R.color.white))
        mBinding!!.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        setSupportActionBar(mBinding!!.toolbar)
        mBinding!!.toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })

        customeProgressDialog = CustomeProgressDialog(this)

        mBinding!!.transactionHistoryRecycler.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        var itemDecoration = DividerItemDecoration(this, LinearLayout.VERTICAL)
        mBinding!!.transactionHistoryRecycler.addItemDecoration(itemDecoration)
        var transactionHistory =
            (applicationContext as SportsFightApplication).getTransactionHistory
        if(transactionHistory!=null && transactionHistory.size>0){
            transactionList.clear()
            transactionList.addAll(transactionHistory)
        }

        adapter = MoreOptionsAdaptor(this, transactionList)
        mBinding!!.transactionHistoryRecycler.adapter = adapter
        if(!MyUtils.isConnectedWithInternet(this)) {
            MyUtils.showToast(this,"No Internet connection found")
            return
        }
        getMyTransaction()
    }

    override fun onBitmapSelected(bitmap: Bitmap) {
        TODO("Not yet implemented")
    }

    override fun onUploadedImageUrl(url: String) {

    }

    fun getMyTransaction() {
        //var userInfo = (activity as PlugSportsApplication).userInformations
        mBinding!!.emptyview.visibility=View.GONE
        mBinding!!.progressBar.visibility = View.VISIBLE
        var models = RequestModel()
        models.user_id = MyPreferences.getUserID(this)!!

        WebServiceClient(this).client.create(IApiMethod::class.java).getTransactionHistory(models)
            .enqueue(object : Callback<UsersPostDBResponse?> {
                override fun onFailure(call: Call<UsersPostDBResponse?>?, t: Throwable?) {
                    mBinding!!.emptyview.visibility=View.VISIBLE
                    mBinding!!.progressBar.visibility = View.GONE
                }

                override fun onResponse(
                    call: Call<UsersPostDBResponse?>?,
                    response: Response<UsersPostDBResponse?>?
                ) {
                    mBinding!!.progressBar.visibility = View.GONE
                    var res = response!!.body()
                    if(res!=null) {
                        var responseModel = res.transactionHistory
                        if(responseModel!=null) {
                            if (responseModel.transactionList != null && responseModel.transactionList!!.size > 0) {
                                (applicationContext as SportsFightApplication).saveTransactionHistory(
                                    responseModel.transactionList!!)
                                transactionList.addAll(responseModel.transactionList!!)
                                adapter.notifyDataSetChanged()
                                mBinding!!.emptyview.visibility=View.GONE

                            }else {
                                mBinding!!.emptyview.visibility=View.VISIBLE
                            }
                        }else {
                            mBinding!!.emptyview.visibility=View.VISIBLE
                        }

                    }

                }

            })

    }


    inner class MoreOptionsAdaptor(
        val context: Context,
        val tradeinfoModels: ArrayList<TransactionModel>
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var onItemClick: ((TransactionModel) -> Unit)? = null
        private var optionListObject = tradeinfoModels


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_recent_transactions, parent, false)
            return DataViewHolder(view)

        }

        override fun onBindViewHolder(parent: RecyclerView.ViewHolder, viewType: Int) {
            var objectVal = optionListObject[viewType]
            val viewHolder: DataViewHolder = parent as DataViewHolder
            viewHolder.transactionNote?.text = objectVal.paymentType
            viewHolder.transactionId?.text = objectVal.transactionId
            viewHolder.transactionDate?.text = objectVal.createdDate

            if(objectVal.debitCreditStatus.equals("+")){
                viewHolder.transactionAmount.setTextColor(resources.getColor(R.color.colorAccent))
                viewHolder.transactionAmount?.text = objectVal.debitCreditStatus+"₹"+objectVal.depositAmount
            }else {
                viewHolder.transactionAmount.setTextColor(resources.getColor(R.color.red))
                viewHolder.transactionAmount?.text = objectVal.debitCreditStatus+"₹"+objectVal.depositAmount
            }
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

            val transactionNote = itemView.findViewById<TextView>(R.id.transaction_note)
            val transactionId = itemView.findViewById<TextView>(R.id.transaction_id)
            val transactionDate = itemView.findViewById<TextView>(R.id.transaction_date)
            val transactionAmount = itemView.findViewById<TextView>(R.id.transaction_amount)
        }


    }
}
