package com.my11heros.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.my11heros.UpdateApplicationActivity
import com.my11heros.utils.CustomeProgressDialog
import com.my11heros.utils.DownloadController
//import com.paytm.pgsdk.PaytmOrder
//import com.paytm.pgsdk.PaytmPGService
//import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.my11heros.R
import com.my11heros.databinding.FragmentUpdateApkBinding

class UpdateAppDialogFragment(
    private val updateApkUrl: String,
    private val releaseNote: String
) : DialogFragment() {

    private var mBinding: FragmentUpdateApkBinding? = null
    private lateinit var downloadController: DownloadController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_update_apk, container, false
        )
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val customProgressDialog = CustomeProgressDialog(activity)
        dialog!!.requestWindowFeature(STYLE_NO_TITLE)
        downloadController = DownloadController(
            requireActivity(),
            updateApkUrl,
            customProgressDialog
        )
        mBinding!!.imgClose.visibility = View.GONE
        mBinding!!.imgClose.setOnClickListener {
            dismiss()
        }
        mBinding!!.updateApk.setOnClickListener {

            checkStoragePermission()
        }

        mBinding!!.releaseNote.text = releaseNote
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == UpdateApplicationActivity.PERMISSION_REQUEST_STORAGE) {
            // Request for camera permission.
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // start downloading
                downloadController.enqueueDownload()
            }
        }
    }

    private fun checkStoragePermission() {
        // Check if the storage permission has been granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (requireActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                downloadController.enqueueDownload()
            } else {
                requestStoragePermission()
            }
        }
    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    UpdateApplicationActivity.PERMISSION_REQUEST_STORAGE
                )
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    UpdateApplicationActivity.PERMISSION_REQUEST_STORAGE
                )
            }
        }
    }
}