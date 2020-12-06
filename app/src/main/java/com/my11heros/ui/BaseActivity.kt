package com.my11heros.ui

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.andrognito.flashbar.Flashbar
import com.andrognito.flashbar.anim.FlashAnim
import com.deliverdas.customers.utils.HardwareInfoManager
import com.google.firebase.iid.FirebaseInstanceId
import com.my11heros.*
import com.my11heros.models.ResponseModel
import com.my11heros.models.UserInfo
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


abstract class BaseActivity : AppCompatActivity() {

    private lateinit var options: Array<CharSequence>
    private var imageUri: Uri? = null

    private var sizeofImage: Int = 0
    var userInfo: UserInfo? = null
    var notificationToken: String = ""
    lateinit var customeProgressDialog: CustomeProgressDialog
    private var image_uri: Uri? = null
    var mBitmap: Bitmap? = null
    var mDocumentType = ""

    companion object {
        val DOCUMENTS_TYPE_PROFILES = "profile"
        var DOCUMENT_TYPE_PANCARD = "pancard"
        var DOCUMENT_TYPE_ADHARCARD = "adharcard"
        var DOCUMENT_TYPE_BANK_PASSBOOK = "passbook"
        var DOCUMENT_TYPE_PAYTM = "paytm"
        val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
        val PICK_IMAGE_REQUEST_CAMERA = 1001
        val PICK_IMAGE_REQUEST_GALLERY = 1002
        private val PERMISSION_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customeProgressDialog = CustomeProgressDialog(this)
        //uploadReceiver = SingleUploadBroadcastReceiver()
        userInfo = (application as SportsFightApplication).userInformations
    }

    fun showDeadLineAlert(message: String) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        // builder.setTitle("Warning")
        //set message for alert dialog
        builder.setMessage(message)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("OK") { dialogInterface, which ->
            finish()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

    fun showMatchTimeUpDialog() {
        val flashbar = Flashbar.Builder(this)
            .gravity(Flashbar.Gravity.BOTTOM)
            .title(getString(R.string.app_name))
            .message("Time Up Editing your team, match went to live.")
            .backgroundDrawable(R.color.secondery_color)
            /*.showIcon()
            .icon(R.mipmap.ic_launcher)
            .iconAnimation(
                FlashAnim.with(this)
                    .animateIcon()
                    .pulse()
                    .alpha()
                    .duration(750)
                    .accelerate()
            )*/
            .build()
        flashbar.show()
        Handler().postDelayed(Runnable {
            val intent = Intent(this@BaseActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()

        }, 2000L)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST_CAMERA) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    mBitmap = data!!.extras!!.get("data") as Bitmap
                } else {
                    mBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, image_uri)
                }
                onBitmapSelected(mBitmap!!)
                uploadBase64ImageToServer(mBitmap!!)

            } else if (requestCode == PICK_IMAGE_REQUEST_GALLERY) {

                val selectedImage = data!!.data
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val source = ImageDecoder.createSource(this.contentResolver, selectedImage!!)
                    mBitmap = ImageDecoder.decodeBitmap(source)
                    uploadBase64ImageToServer(mBitmap!!)
                } else {

                    val mImageCaptureUri = data.data
                    var path = getRealPathFromURI(mImageCaptureUri) //from Gallery
                    if (path == null) {
                        path = mImageCaptureUri!!.path //from File Manager
                    }
                    if (path != null) {
                        mBitmap = modifyOrientation(BitmapFactory.decodeFile(path), path)
                        onBitmapSelected(mBitmap!!)
                        uploadBase64ImageToServer(mBitmap!!)
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    open fun modifyOrientation(bitmap: Bitmap, image_absolute_path: String?): Bitmap? {
        val ei = ExifInterface(image_absolute_path)
        val orientation: Int =
            ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotate(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotate(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotate(bitmap, 270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flip(bitmap, true, false)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> flip(bitmap, false, true)
            else -> bitmap
        }
    }

    open fun rotate(bitmap: Bitmap, degrees: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }

    open fun flip(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap? {
        val matrix = Matrix()
        var orientationH = 0.0f
        var orientationV = 0.0f
        if (horizontal) {
            orientationH = -1.0f
        } else {
            orientationH = 1.0f
        }

        if (vertical) {
            orientationV = -1.0f
        } else {
            orientationV = 1.0f
        }

        matrix.preScale(orientationH, orientationV)
        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }

    private fun getRealPathFromURI(contentUri: Uri?): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = managedQuery(contentUri, proj, null, null, null) ?: return null
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    private fun uploadBase64ImageToServer(bitmap: Bitmap) {
        val base64Images = getImageUrl(bitmap)
        if (sizeofImage > 2) {
            showCommonAlert("Image size cannot be greater than 2MB,  Current is $sizeofImage MB")
            return
        }

        customeProgressDialog.show()
        WebServiceClient(this).client.create(IApiMethod::class.java)
            .uploadImage(base64Images, MyPreferences.getUserID(this)!!, mDocumentType)
            .enqueue(object : Callback<ResponseModel?> {
                override fun onFailure(call: Call<ResponseModel?>?, t: Throwable?) {
                    customeProgressDialog.dismiss()
                    Toast.makeText(this@BaseActivity, "" + t!!.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<ResponseModel?>?,
                    response: Response<ResponseModel?>?
                ) {
                    if (!isFinishing) {
                        customeProgressDialog.dismiss()
                        val res = response!!.body()
                        if (res != null && res.status) {
                            val photoUrl = res.image_url
                            onBitmapSelected(mBitmap!!)
                            onUploadedImageUrl(photoUrl)
                            //Toast.makeText(this@BaseActivity, "" + res.message, Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this@BaseActivity, "" + res!!.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            })
    }

    /**
     * Android Q Implementations
     */
    private fun setImageUri(): Uri {
        val folder = File("${getExternalFilesDir(Environment.DIRECTORY_DCIM)}")
        folder.mkdirs()

        val file = File(folder, "Image_Tmp.jpg")
        if (file.exists())
            file.delete()
        file.createNewFile()
        imageUri = FileProvider.getUriForFile(
            this,
            BuildConfig.APPLICATION_ID + getString(R.string.file_provider),
            file
        )
        // imgPath = file.absolutePath
        return imageUri!!
    }

    /**
     * End of android Q impelemntations
     */
    fun showCommonAlert(messge: String) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Warning")
        //set message for alert dialog
        builder.setMessage(messge)
        builder.setIcon(android.R.drawable.ic_btn_speak_now)

        //performing positive action
        builder.setPositiveButton("Ok") { dialogInterface, which ->

        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

    private fun getImageUrl(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()
        val sizeofIamge = bytes.size
        setSizeOfImage(sizeofIamge)
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private fun setSizeOfImage(sizeofIamge: Int) {
        this.sizeofImage = (sizeofIamge / 1024) / 1024
    }

    fun checkAndRequestPermissions(): Boolean {
        val camerapermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val writepermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)


        val listPermissionsNeeded = ArrayList<String>()

        if (camerapermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    fun selectImage(mDocumentType: String) {
        if (!checkAndRequestPermissions()) {
            return
        }

        this.mDocumentType = mDocumentType
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            options =
                arrayOf<CharSequence>("Choose from Gallery", "Cancel")
        } else {
            options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        }

        val builder: android.app.AlertDialog.Builder =
            android.app.AlertDialog.Builder(this@BaseActivity)
        builder.setTitle("Add Photo!")
        builder.setItems(options, object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, items: Int) {
                if (options[items] == "Take Photo") {
                    openCamera(PICK_IMAGE_REQUEST_CAMERA)
                } else if (options[items] == "Choose from Gallery") {
                    selectGalleryImage()
                } else if (options[items] == "Cancel") {
                    dialog!!.dismiss()
                }
            }

        })
        builder.show()
    }

    private fun openCamera(requestCode: Int) {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, requestCode)

    }

    private fun selectGalleryImage() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED
            ) {
                //permission denied
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                //permission already granted
                pickImageFromGallery()
            }
        } else {
            //system OS is < Marshmallow
            pickImageFromGallery()
        }
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST_GALLERY)
    }

    fun updateFireBase() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener { instanceIdResult ->
                val deviceToken = instanceIdResult.token
                if (!TextUtils.isEmpty(deviceToken)) {
                    notificationToken = deviceToken
                    MyPreferences.setDeviceToken(this@BaseActivity, deviceToken)
                }
//                    var notid =  FirebaseInstanceId.getInstance()
//                        .getToken(getString(R.string.gcm_default_sender_id), "FCM")
                val userId = MyPreferences.getUserID(this@BaseActivity)!!
                if (!TextUtils.isEmpty(deviceToken) && !TextUtils.isEmpty(userId)) {
                    val request = RequestModel()
                    request.user_id = userId
                    request.device_id = deviceToken
                    request.token = MyPreferences.getToken(this@BaseActivity)!!
                    request.deviceDetails = HardwareInfoManager(this@BaseActivity).collectData(deviceToken)
                    WebServiceClient(this@BaseActivity).client.create(IApiMethod::class.java)
                        .deviceNotification(request)
                        .enqueue(object : Callback<UsersPostDBResponse?> {
                            override fun onFailure(
                                call: Call<UsersPostDBResponse?>?,
                                t: Throwable?
                            ) {
                            }

                            override fun onResponse(
                                call: Call<UsersPostDBResponse?>?,
                                response: Response<UsersPostDBResponse?>?
                            ) {
                                MyUtils.logd("deviceId", "Posted successfully")
                            }
                        })
                }
            }
    }

    fun logoutApp(message: String, boolean: Boolean) {
        if (!MyUtils.isConnectedWithInternet(this@BaseActivity)) {
            MyUtils.showToast(this@BaseActivity, "No Internet connection found")
            return
        }
        genericAlertDialog(message, boolean)
    }

    private fun genericAlertDialog(message: String, boolean: Boolean) {
        val builder = AlertDialog.Builder(this@BaseActivity)
        //set title for alert dialog
        // builder.setTitle("Warning")
        //set message for alert dialog

        builder.setMessage(message)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        if (boolean) {
            builder.setNegativeButton("Cancel", null)
        }
        builder.setPositiveButton("OK") { dialogInterface, which ->

            customeProgressDialog.show()
            val request = RequestModel()
            request.user_id = MyPreferences.getUserID(this@BaseActivity)!!
            request.token = MyPreferences.getToken(this@BaseActivity)!!
            WebServiceClient(this@BaseActivity).client.create(IApiMethod::class.java)
                .logout(request)
                .enqueue(object : Callback<UsersPostDBResponse?> {
                    override fun onFailure(call: Call<UsersPostDBResponse?>?, t: Throwable?) {

                    }

                    override fun onResponse(
                        call: Call<UsersPostDBResponse?>?,
                        response: Response<UsersPostDBResponse?>?
                    ) {
                        customeProgressDialog.dismiss()
                        MyPreferences.clear(this@BaseActivity)
                        val intent = Intent(
                            this@BaseActivity,
                            SplashScreenActivity::class.java
                        )
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                })
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

    protected open fun sizeOf(data: Bitmap): Int {
        return (data.byteCount / 1024) / 1024
    }

    abstract fun onBitmapSelected(bitmap: Bitmap)
    abstract fun onUploadedImageUrl(url: String)

}