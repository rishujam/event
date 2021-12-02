package ind.ev.events.db

import ind.ev.events.models.OtpResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface OtpApi {


    @GET("{api_key}/BAL/SMS")
    suspend fun getBalance(
        @Path("api_key") key: String
    ):OtpResponse

    @GET("{api_key}/SMS/{phone_number}/AUTOGEN")
    suspend fun getOtp(
        @Path("api_key") key: String,
        @Path("phone_number") number:String
    ):OtpResponse

    @GET("{api_key}/SMS/VERIFY/{session_id}/{otp_input}")
    suspend fun verifyOtp(
        @Path("api_key") key:String,
        @Path("session_id")id:String,
        @Path("otp_input") otp:String
    ):OtpResponse
}