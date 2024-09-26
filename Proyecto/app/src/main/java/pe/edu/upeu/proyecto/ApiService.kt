package pe.edu.upeu.proyecto

import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("muebles")
    fun getAllMuebles(): Call<List<Muebles>>

    @GET("muebles/{id}")
    fun getMueblesById(@Path("id") id: Long): Call<Muebles>

    @POST("muebles")
    fun createMuebles(@Body oferta: Muebles): Call<Muebles>

    @PUT("muebles/{id}")
    fun updateMuebles(@Path("id") id: Long, @Body muebles: Muebles): Call<Muebles>

    @DELETE("muebles/{id}")
    fun deleteMuebles(@Path("id") id: Long): Call<Void>
}
