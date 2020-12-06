package com.deliverdas.customers.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import android.telephony.TelephonyManager
import com.my11heros.utils.HardwareInfo
import com.my11heros.utils.MyUtils
import com.my11heros.BuildConfig
import java.io.Serializable
import java.util.*


@TargetApi(Build.VERSION_CODES.DONUT)
class HardwareInfoManager(internal var hardwareContext: Context?) : Serializable {
    internal var rows = 0

    /*try {
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                DualSimManagerLolipop simManager = new DualSimManagerLolipop(hardwareContext);
                simCount = simManager.getSimSupportedCount();
            } else {
                simCount = MeterPreferences.getInt(hardwareContext, LocalConstants.SIM_SUPPORTED_COUNT);
                if (simCount == -2) {
                    dualSim = new DualSimManager(hardwareContext);
                    simCount = dualSim.getSupportedSimCount();
                }
            }
        } catch (Exception e) {
        }*/ val simSupportedCount: Int
        get() = -1

    val os: Int
        get() = Build.VERSION.SDK_INT

    val osName: String
        get() {
            var osName = ""
            var osCode = 0
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                osCode = Build.VERSION.SDK_INT
            } else {
                osCode = Build.VERSION.SDK_INT
            }
            when (osCode) {

                11 -> osName = "Honeycomb"

                12 -> osName = "Honeycomb"

                13 -> osName = "Honeycomb"

                14 -> osName = "Ice Cream Sandwich"

                15 -> osName = "Ice Cream Sandwich"

                16 -> osName = "Jelly Bean"

                17 -> osName = "Jelly Bean"

                18 -> osName = "Jelly Bean"

                19 -> osName = "KitKat"

                21 -> osName = "Lollipop"

                22 -> osName = "Lollipop"

                23 -> osName = "Marshmallow"

                24 -> osName = "Nougat"

                25 -> osName = "Nougat"

                26 -> osName = "Oreo"
                27 -> osName = "Oreo"
                28 -> osName = "Pie"
                29 -> osName = "Q"
                else -> osName = "N/A"
            }


            return osName

        }

    val ratsVersion: String
        get() = BuildConfig.VERSION_NAME

    val dataDiaryVersion: String?
        get() = androidId

    val manufacturesName: String
        get() {
            var manufaturer = ""
            manufaturer = Build.MANUFACTURER
            return manufaturer
        }

    val model: String
        get() {
            var model = ""
            model = Build.MODEL
            return model
        }

    val productInfo: String
        get() {
            var productinfo = ""
            productinfo = Build.PRODUCT
            return productinfo
        }

    val brand: String
        get() {
            var brand = ""
            brand = Build.BRAND
            return brand
        }

    val androidId: String?
        get() = MyUtils.getDeviceID(hardwareContext!!)

    val osVersion: String
        get() = Build.VERSION.RELEASE

    val timeZone: String
        get() = TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT, Locale.US).trim { it <= ' ' }

    val defaultCarrierName: String
        get() {
            var carrierName = "NA"
            try {
                val manager = hardwareContext!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                carrierName = manager.networkOperatorName
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return carrierName
        }

    fun collectData(notificationToken: String): HardwareInfo {
        val hardware = HardwareInfo()
        try {
            hardware.brand = brand
            hardware.noOfSIM = simSupportedCount
            hardware.device_id = notificationToken
            hardware.model = model
            hardware.manufacturer = manufacturesName
            hardware.setOs(osName)
            hardware.os_version = Build.VERSION.RELEASE
            hardware.country =""
            hardware.package_name =hardwareContext!!.packageName
            val sig: Signature = hardwareContext!!.getPackageManager().getPackageInfo(
                hardwareContext!!.getPackageName(),
                PackageManager.GET_SIGNATURES
            ).signatures.get(0)
            hardware.signature = sig.hashCode()
            hardware.adId = "N/A"
            hardware.appVersion = ratsVersion
            hardware.versionCode = BuildConfig.VERSION_CODE
            hardware.timeZone = TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT, Locale.US).trim { it <= ' ' }

        } catch (ee: Exception) {
            ee.printStackTrace()
        }

        return hardware
    }

    companion object {

        val deviceName: String
            get() {
                val manufacturer = Build.MANUFACTURER
                val model = Build.MODEL
                val s: String
                if (model.contains(manufacturer)) {
                    s = model
                } else {
                    s = StringBuilder(manufacturer.toString()).append(" ").append(model).toString()
                }
                return s
            }
    }
}
