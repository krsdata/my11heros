package com.my11heros

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.my11heros.ui.BaseActivity
import com.my11heros.databinding.WebviewBinding


public class WebActivity : BaseActivity() {
    private var mBinding: WebviewBinding? = null
    companion object{
        val KEY_TITLE:String = "web.title"
        val KEY_URL:String = "url.web"
    }
    var URL: String? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        setEnterAnimations()
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this,
            R.layout.webview
        )
        mBinding!!.toolbar.title =intent.getStringExtra(KEY_TITLE)
        mBinding!!.toolbar.setTitleTextColor(resources.getColor(R.color.white))
        mBinding!!.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        setSupportActionBar(mBinding!!.toolbar)
        mBinding!!.toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })

        customeProgressDialog.show()
        URL = intent.getStringExtra(KEY_URL)
        loadURL()
    }

    override fun onBitmapSelected(bitmap: Bitmap) {
        TODO("Not yet implemented")
    }

    override fun onUploadedImageUrl(url: String) {
        TODO("Not yet implemented")
    }

    fun  setEnterAnimations() {
        if (Build.VERSION.SDK_INT > 20) {
            var slide = Slide()
            slide.slideEdge = Gravity.BOTTOM
            slide.duration = 400
            slide.interpolator = DecelerateInterpolator()
            window.exitTransition = slide
            window.enterTransition = slide
        }
    }


//
//    override fun finish() {
//        super.finish()
//        overridePendingTransition(R.anim.hold, R.anim.grow_linear_animation);
//    }

    /**
     * Load url.
     */
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
            customeProgressDialog.dismiss()
        }
    }
}