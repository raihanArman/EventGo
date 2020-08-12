package com.example.eventgoapps.data.remote;

import com.example.eventgoapps.data.remote.model.Event;
import com.example.eventgoapps.data.remote.model.Lampiran;
import com.example.eventgoapps.data.remote.model.User;
import com.example.eventgoapps.data.remote.model.Value;
import com.example.eventgoapps.data.remote.model.push_notif.DataMessage;
import com.example.eventgoapps.data.remote.model.push_notif.FCMResponse;
import com.example.eventgoapps.data.remote.model.response.EventResponse;
import com.example.eventgoapps.data.remote.model.response.KategoriResponse;
import com.example.eventgoapps.data.remote.model.response.LikeEventResponse;
import com.example.eventgoapps.data.remote.model.response.LoginResponse;
import com.example.eventgoapps.data.remote.model.response.PesanResponse;
import com.example.eventgoapps.data.remote.model.response.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiRequest {
    @FormUrlEncoded
    @POST("login_user.php")
    Call<LoginResponse> loginUserRequest(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("registrasi_user.php")
    Call<LoginResponse> registerUserRequest(
            @Field("nama") String nama,
            @Field("email") String email,
            @Field("password") String password
    );

    @GET("tampil_kategori.php")
    Call<KategoriResponse> kategoriAllRequest();

    @GET("tampil_user.php")
    Call<UserResponse> userRequest(
            @Query("id_user") String idUser
    );

    @GET("tampil_event.php")
    Call<EventResponse> eventAllRequest();

    @GET("tampil_event.php")
    Call<EventResponse> eventByIdRequest(
            @Query("id_event") String idEvent
    );

    @GET("tampil_event.php")
    Call<EventResponse> eventByWeekRequest(
            @Query("tanggal_1") String tanggal1,
            @Query("tanggal_2") String tanggal2
    );

    @GET("tampil_event.php")
    Call<EventResponse> eventSearchRequest(
            @Query("cari") String cari
    );

    @GET("tampil_event.php")
    Call<EventResponse> eventByUserRequest(
            @Query("id_user") String idUser
    );

    @GET("tampil_event.php")
    Call<EventResponse> eventByJenisRequest(
            @Query("jenis") String jenis
    );

    @GET("tampil_favorite.php")
    Call<EventResponse> eventFavoriteRequest();

    @GET("tampil_like_event.php")
    Call<LikeEventResponse> eventLikeRequest(
            @Query("id_event") String idEvent
    );

    @FormUrlEncoded
    @POST("tambah_like.php")
    Call<Value> addLike(
            @Field("id_event") String idEvent,
            @Field("id_user") String idUser
    );

    @GET("cek_like.php")
    Call<Value> cekLike(
            @Query("id_event") String idEvent,
            @Query("id_user") String idUser
    );

//    Favorite User
    @GET("tampil_favorite_user.php")
    Call<EventResponse> favoriteUserRequest(
            @Query("id_user") String idUser
    );

//    EditProfil
    @POST("edit_user.php")
    Call<Value> editUserRequest(
            @Body User user
    );

//    Cek Event User
    @GET("cek_event_user.php")
    Call<Value> cekEventUserRequest(
            @Query("id_user") String idUser
    );

    @FormUrlEncoded
    @POST("logout_user.php")
    Call<Value> logoutRequest(
            @Field("id_user") String idUser
    );

//    Input Event
    @POST("input_event.php")
    Call<Value> inputEvent(
            @Body Event event
    );

    @FormUrlEncoded
    @POST("input_lampiran.php")
    Call<Value> inputLampiranRequest(
            @Field("id_event") String idEvent,
            @Field("keterangan") String keterangan,
            @Field("gambar") String gambar
    );
//    Input Event

    //    Input Event
    @POST("edit_event.php")
    Call<Value> editEventRequest(
            @Body Event event
    );

    @FormUrlEncoded
    @POST("edit_lampiran.php")
    Call<Value> editLampiranRequest(
            @Field("id_event") String idEvent,
            @Field("keterangan") String keterangan,
            @Field("gambar") String gambar
    );
//    Input Event

//    Aktivitas

    @GET("tampil_aktivitas.php")
    Call<EventResponse> aktivitasByUserRequest(
            @Query("id_user") String idUser
    );

    @GET("tampil_aktivitas.php")
    Call<EventResponse> aktivitasByIdRequest(
            @Query("id_event") String idEvent
    );
//    Aktivitas

//    Google Maps
    @GET
    Call<String> getPath(@Url String url);

//    Pesan
    @GET("tampil_pesan.php")
    Call<PesanResponse> pesanAllRequest(
            @Query("id_user") String idUser
    );

    @GET("tampil_pesan.php")
    Call<PesanResponse> pesanByIdRequest(
            @Query("id_pesan_user") String id_pesan_user
    );

    @FormUrlEncoded
    @POST("update_pesan.php")
    Call<Value> updatePesanRequest(
            @Field("id_pesan_user") String idPesanUser
    );

    @FormUrlEncoded
    @POST("hapus_pesan.php")
    Call<Value> hapusPesanRequest(
            @Field("id_pesan_user") String idPesanUser
    );

    @GET("tampil_event_kategori.php")
    Call<EventResponse> eventKategoriRequest(
            @Query("id_kategori") String idKategori
    );

    @GET("tampil_event_kategori.php")
    Call<EventResponse> eventKategoriCariRequest(
            @Query("id_kategori") String idKategori,
            @Query("cari") String cari
    );


    @GET("tampil_lampiran.php")
    Call<List<Lampiran>> allLampiranRequest(
            @Query("id_event") String id_event
    );

//    Riwayat
    @GET("tampil_riwayat_user.php")
    Call<EventResponse> riwayatUserRequest(
            @Query("id_user") String idUser
    );

    @FormUrlEncoded
    @POST("hapus_riwayat_user.php")
    Call<Value> hapusRiwayatRequest(
            @Field("id_riwayat_user") String idRiwayat
    );
//    Riwayat


//    Push Notif
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAG8R6Kz4:APA91bGIZvF0CqetyOEasapb7_1aWlIcJQBcBBp4AbW8mxUu7kFLjSMauZ1WzAHKtXI4nHnjjCtLhWXfbHwD4-NRVPqng6L7kHFuEEcQydxkYHsspytGdfWElFhYt7vkCqruvoZamkQ6"
    })
    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body DataMessage body);

}
