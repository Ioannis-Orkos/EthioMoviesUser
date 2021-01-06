package com.ioannisnicos.ethiomoviesuser.retrofit;

import com.ioannisnicos.ethiomoviesuser.models.Movies;
import com.ioannisnicos.ethiomoviesuser.models.Tvshow;
import com.ioannisnicos.ethiomoviesuser.models.Users;
import com.ioannisnicos.ethiomoviesuser.retrofit_movie_response.MoviesResponsePaging;
import com.ioannisnicos.ethiomoviesuser.retrofit_movie_response.QuerryResponse;
import com.ioannisnicos.ethiomoviesuser.retrofit_movie_response.StoresSubscriptionResponse;
import com.ioannisnicos.ethiomoviesuser.retrofit_movie_response.TransactionsResponsePaging;
import com.ioannisnicos.ethiomoviesuser.retrofit_tvshow_response.TvshowsResponsePaging;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

    public interface APIInterface {

        //Users-------------------------------------------------------------------------------------
        @FormUrlEncoded
        @POST("users/user_register_onLogin.php")
        Call<QuerryResponse> RegisterUserOnLoginPost(@Field("google_id")     String gi,
                                                     @Field("first_name")    String fn,
                                                     @Field("last_name")     String ln,
                                                     @Field("display_name")  String dn,
                                                     @Field("email")         String e
        );

        @FormUrlEncoded
        @POST("users/user_update_bywhat.php")
        Call<QuerryResponse> UpdateToken(
                @Field("google_id")      String gId,
                @Field("what_to_update") String what,
                @Field("notif_token")    String token
        );

        @GET("users/user_read_single_byID.php")
        Call<Users> getUserInfo(@Query("id") String id);

        @Multipart
        @POST("users/user_update_profile_picture.php")
        Call<QuerryResponse> sendImage(@Part("google_id")        RequestBody gid,
                                       @Part("what_to_update")   RequestBody w,
                                       @Part MultipartBody.Part  image
        );

        @FormUrlEncoded
        @POST("users/user_update_bywhat.php")
        Call<QuerryResponse> UpdateByWhatSetting(
                @Field("google_id")      String gid,
                @Field("what_to_update") String w,
                @Field("display_name")   String dn,
                @Field("description")    String d,
                @Field("language")       String l
        );

        @FormUrlEncoded
        @POST("users/user_update_bywhat.php")
        Call<QuerryResponse> UpdateLocation(
                @Field("google_id")      String gid,
                @Field("what_to_update") String w,
                @Field("address")        String address,
                @Field("postcode")       String postcode,
                @Field("geolat")         String lat,
                @Field("geolng")         String lng
        );
        //------------------------------------------------------------------------------------------

        //Movies------------------------------------------------------------------------------------
        @GET("movies_forUser/readOne_movie_forUsers.php")
        Call<Movies> getMovieDetail(@Query("id") String id);

        @GET("movies_forUser/readPaging_movies_forUsers.php")
        Call<MoviesResponsePaging> getMoviesList(@Query("page")  Integer page,
                                                 @Query("type")  String  type,
                                                 @Query("genre") String  genre,
                                                 @Query("year")  String  year,
                                                 @Query("order") String  order
        );

        @GET("movies_forUser/readPaging_storeMovies_forUser.php")
        Call<MoviesResponsePaging> getStoreMovies(@Query("page") Integer page,
                                                  @Query("storeId") Integer storeId,
                                                  @Query("type") String type,
                                                  @Query("genre") String genre,
                                                  @Query("year") String year,
                                                  @Query("order") String order
        );

        @GET("movies_forUser/readPaging_searchMovies_forUser.php")
        Call<MoviesResponsePaging> getSearchMovies(@Query("page") Integer page,
                                                   @Query("search") String storeId,
                                                   @Query("type") String type,
                                                   @Query("genre") String genre,
                                                   @Query("year") String year,
                                                   @Query("order") String order
        );

        @FormUrlEncoded
        @POST("movies_forUser/store_listForRentMovie.php")
        Call<StoresSubscriptionResponse> getMoviesForRent(@Field("ugid") String  id,
                                                          @Field("mid") String  mid,
                                                          @Field("sid") int     sid
        );

        @FormUrlEncoded
        @POST("movies_forUser/store_RequestMovieRent.php")
        Call<QuerryResponse> requestMovieRent( @Field("mid")  String mid,
                                               @Field("ugid") String ugid,
                                               @Field("sid")  int    sid,
                                               @Field("user_msg")  String user_msg
        );

        @FormUrlEncoded
        @POST("movies_forUser/store_cancelRequestMovieRent.php")
        Call<QuerryResponse> cancelMovieRequestRent( @Field("mid")  String mid,
                                                     @Field("ugid") String ugid,
                                                     @Field("sid")  int    sid
        );

        @GET("movies_forUser/user_readMovieTransactionPaging.php")
        Call<TransactionsResponsePaging> getUserTransactionListMovie(@Query("page") Integer page,
                                                                     @Query("ugid") String ugid,
                                                                     @Query("status") String status
        );

        //------------------------------------------------------------------------------------------


       //Tvshow-------------------------------------------------------------------------------------
       @GET("tvshows_forUser/readOne_tvshow_forUsers.php")
       Call<Tvshow> getTvshowDetail(@Query("id") String id);

        @GET("tvshows_forUser/readPaging_tvshow_forUsers.php")
        Call<TvshowsResponsePaging> getTvshowList(@Query("page")  Integer page,
                                                  @Query("type")  String  type,
                                                  @Query("genre") String  genre,
                                                  @Query("year")  String  year,
                                                  @Query("order") String  order
        );

        @GET("tvshows_forUser/readPaging_storeTvshows_forUser.php")
        Call<TvshowsResponsePaging> getStoreTvshows(@Query("page") Integer page,
                                                    @Query("storeId") Integer storeId,
                                                    @Query("type") String type,
                                                    @Query("genre") String genre,
                                                    @Query("year") String year,
                                                    @Query("order") String order
        );

        @GET("tvshows_forUser/readPaging_searchTvshows_forUser.php")
        Call<TvshowsResponsePaging> getSearchTvshows(@Query("page") Integer page,
                                                    @Query("search") String search,
                                                    @Query("type") String type,
                                                    @Query("genre") String genre,
                                                    @Query("year") String year,
                                                    @Query("order") String order
        );


        @FormUrlEncoded
        @POST("tvshows_forUser/store_listForRentTvshow.php")
        Call<com.ioannisnicos.ethiomoviesuser.retrofit_tvshow_response.StoresSubscriptionResponse>
        getTvshowsForRent(@Field("ugid") String  id,
                          @Field("tid") String  mid,
                          @Field("sid") int     sid
        );



        @FormUrlEncoded
        @POST("tvshows_forUser/store_RequestTvshowRent.php")
        Call<com.ioannisnicos.ethiomoviesuser.retrofit_tvshow_response.QuerryResponse>
        requestTvshowRent( @Field("tid")  String mid,
                           @Field("ugid") String ugid,
                           @Field("sid")  int    sid,
                           @Field("user_msg")  String user_msg
        );

        @FormUrlEncoded
        @POST("tvshows_forUser/store_cancelRequestTvshowRent.php")
        Call<com.ioannisnicos.ethiomoviesuser.retrofit_tvshow_response.QuerryResponse>
        cancelTvshowRequestRent( @Field("tid")  String mid,
                                 @Field("ugid") String ugid,
                                 @Field("sid")  int    sid
        );

        @GET("tvshows_forUser/user_readTvshowTransactionPaging.php")
        Call<com.ioannisnicos.ethiomoviesuser.retrofit_tvshow_response.TransactionsResponsePaging>
        getUserTransactionListTvshow(@Query("page") Integer page,
                                     @Query("ugid") String ugid,
                                     @Query("status") String status
        );
        //------------------------------------------------------------------------------------------


        //Stores------------------------------------------------------------------------------------
        @FormUrlEncoded
        @POST("stores_forUser/store_listUserSubs.php")
        Call<StoresSubscriptionResponse> getUserSubs(@Field("ugid") String gid);

        @FormUrlEncoded
        @POST("stores_forUser/store_listNearUser.php")
        Call<StoresSubscriptionResponse> getNearStores(@Field("ugid") String id,
                                                        @Field("lat") double lat,
                                                        @Field("lng") double lng
        );

        @FormUrlEncoded
        @POST("stores_forUser/store_subscribe.php")
        Call<QuerryResponse> store_subscribe(@Field("ugid") String ugid,
                                             @Field("sid") int rid
        );

        @FormUrlEncoded
        @POST("stores_forUser/store_unsubscribe.php")
        Call<QuerryResponse> store_unsubscribe(@Field("ugid") String ugid,
                                               @Field("sid") int rid
        );

        //------------------------------------------------------------------------------------------




    }

