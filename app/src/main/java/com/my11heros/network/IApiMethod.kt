package com.my11heros.network

import com.google.gson.JsonObject
import com.my11heros.models.DocumentsModel
import com.my11heros.models.ResponseModel
import com.my11heros.payments.RequestPaytmModel
import com.my11heros.ui.home.models.UsersPostDBResponse
import retrofit2.Call
import retrofit2.http.*

interface IApiMethod {

    @Headers("Content-Type: application/json")
    @POST("api/v2/login")
    fun customerLogin(@Body request: RequestModel): Call<ResponseModel>

    @Headers("Content-Type: application/json")
    @POST("api/v2/member/logout")
    fun logout(@Body request: RequestModel): Call<UsersPostDBResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v2/getMatch")
    fun getAllMatches(@Body request: RequestModel): Call<UsersPostDBResponse>


    @Headers("Content-Type: application/json")
    @POST("api/v2/getContestByMatch")
    fun getContestByMatch(@Body request: RequestModel): Call<UsersPostDBResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v2/getPlayer")
    fun getPlayer(@Body request: RequestModel): Call<UsersPostDBResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v2/createTeam")
    fun createTeam(@Body request: RequestCreateTeamModel): Call<UsersPostDBResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v2/getMyTeam")
    fun getMyTeam(@Body request: RequestModel): Call<UsersPostDBResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v2/getMyContest")
    fun getMyContest(@Body request: RequestModel): Call<UsersPostDBResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v2/joinContest")
    fun joinContest(@Body request: RequestModel): Call<UsersPostDBResponse>


    @Headers("Content-Type: application/json")
    @POST("api/v2/getWallet")
    fun getWallet(@Body request: RequestModel): Call<UsersPostDBResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v2/leaderBoard")
    fun getLeaderBoard(@Body request: RequestModel): Call<UsersPostDBResponse>


    @Headers("Content-Type: application/json")
    @POST("api/v2/getPrizeBreakup")
    fun getPrizeBreakUp(@Body request: RequestModel): Call<UsersPostDBResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v2/forgotPassword")
    fun forgotPassword(@Body request: RequestModel): Call<UsersPostDBResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v2/deviceNotification")
    fun deviceNotification(@Body request: RequestModel): Call<UsersPostDBResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v2/addMoney")
    fun addMoney(@Body request: RequestModel): Call<UsersPostDBResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v2/getPoints")
    fun getPoints(@Body request: RequestModel): Call<UsersPostDBResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v2/joinNewContestStatus")
    fun joinNewContestStatus(@Body request: RequestModel): Call<UsersPostDBResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v2/getScore")
    fun getScore(@Body request: RequestModel): Call<UsersPostDBResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v2/transactionHistory")
    fun getTransactionHistory(@Body request: RequestModel): Call<UsersPostDBResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v2/apkUpdate")
    fun apkUpdate(@Body request: RequestModel): Call<UsersPostDBResponse>

    @Headers("Content-Type: application/json")
    @POST("paytm/generateChecksum.php")
    fun getPaytmChecksum(@Body request: RequestPaytmModel): Call<ResponseModel>

    @Headers("Content-Type: application/json")
    @POST("api/v2/cloneMyTeam")
    fun copyTeam(@Body request: RequestModel): Call<UsersPostDBResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v2/getMatchHistory")
    fun getMatchHistory(@Body request: RequestModel): Call<UsersPostDBResponse>

    @FormUrlEncoded
    @POST("api/v2/uploadbase64Image")
    fun uploadImage(
        @Field("image_bytes") imageBytes: String,
        @Field("user_id") userid: String,
        @Field("documents_type") documentsType: String
    ): Call<ResponseModel>

    @Headers("Content-Type: application/json")
    @POST("api/v2/saveDocuments")
    fun saveBankDetails(@Body request: DocumentsModel): Call<ResponseModel>

    @Headers("Content-Type: application/json")
    @POST("api/v2/verification")
    fun verification(@Body request: DocumentsModel): Call<ResponseModel>

    @Headers("Content-Type: application/json")
    @POST("api/v2/myReferralDetails")
    fun myRefferalsList(@Body request: RequestModel): Call<UsersPostDBResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v2/updateProfile")
    fun updateProfile(@Body request: RequestModel): Call<UsersPostDBResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v2/getProfile")
    fun getProfile(@Body request: RequestModel): Call<ResponseModel>

    @Headers("Content-Type: application/json")
    @POST("api/v2/generateOtp")
    fun generateOtp(@Body request: RequestModel): Call<UsersPostDBResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v2/verifyOtp")
    fun verifyOtp(@Body request: RequestModel): Call<UsersPostDBResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v2/mChangePassword")
    fun changePassword(@Body request: RequestModel): Call<UsersPostDBResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v2/getPlayingMatchHistory")
    fun getPlayingMatchHistory(@Body request: RequestModel): Call<UsersPostDBResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v2/getNotification")
    fun getNotification(@Body request: RequestModel): Call<UsersPostDBResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v2/changeMobile")
    fun switchNumbers(@Body request: RequestModel): Call<UsersPostDBResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v2/verification")
    fun getDocumentsList(@Body request: RequestModel): Call<UsersPostDBResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v2/withdrawAmount")
    fun withdrawAmount(@Body request: RequestModel): Call<UsersPostDBResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v2/createRazorPayOrder")
    fun createRazorPayOrder(@Body request: RequestModel): Call<UsersPostDBResponse>

    @Headers("Content-Type: application/json")
    @POST("api/v2/messageApi")
    fun getMessages(@Body request: RequestModel): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("api/v2/eventLog")
    fun sendEventLogs(@Body request: RequestModel): Call<UsersPostDBResponse>


    @Headers("Content-Type: application/json")
    @POST("api/v2/saveAllDocuments")
    fun saveAllDocuments(@Body request: DocumentsModel): Call<ResponseModel>
}