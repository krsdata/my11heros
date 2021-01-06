package com.my11heros

import android.content.Context
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.my11heros.databinding.WebviewBinding
import com.my11heros.utils.CustomeProgressDialog


class WebActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    private var mBinding: WebviewBinding? = null
    lateinit var customProgressDialog: CustomeProgressDialog
    var mContext: Context? = null

    companion object {
        val KEY_TITLE: String = "web.title"
        val KEY_URL: String = "url.web"
    }

    var URL: String? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        setEnterAnimations()
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(
            this,
            R.layout.webview
        )

        mContext = this

        mBinding!!.toolbar.title = intent.getStringExtra(KEY_TITLE)
        mBinding!!.toolbar.setTitleTextColor(resources.getColor(R.color.white))
        mBinding!!.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        setSupportActionBar(mBinding!!.toolbar)
        mBinding!!.toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            mBinding!!.refreshLayout.setColorSchemeColors(
                mContext!!.resources.getColor(
                    R.color.colorPrimary,
                    null
                )
            )
        } else {
            mBinding!!.refreshLayout.setColorSchemeColors(mContext!!.resources.getColor(R.color.colorPrimary))
        }
        mBinding!!.refreshLayout.setOnRefreshListener(this)

        customProgressDialog = CustomeProgressDialog(mContext)
        customProgressDialog.show()

        URL = intent.getStringExtra(KEY_URL)
        loadURL()
    }

    fun setEnterAnimations() {
        val slide = Slide()
        slide.slideEdge = Gravity.BOTTOM
        slide.duration = 400
        slide.interpolator = DecelerateInterpolator()
        window.exitTransition = slide
        window.enterTransition = slide
    }

    fun loadURL() {
        mBinding!!.webBody.webViewClient = MyWebViewClient()
        mBinding!!.webBody.settings.javaScriptEnabled = true
        mBinding!!.webBody.loadUrl(URL)
    }

    private inner class MyWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(
            view: WebView,
            url: String
        ): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            if (mBinding!!.refreshLayout.isRefreshing) {
                mBinding!!.refreshLayout.isRefreshing = false
            }
            customProgressDialog.dismiss()
        }
    }

    override fun onRefresh() {
        loadURL()
    }
}