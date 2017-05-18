package com.wakarkhan.deliverydrop.network;

import com.wakarkhan.deliverydrop.model.Auth;
import com.wakarkhan.deliverydrop.model.User;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by wakar on 18/5/17.
 */

public interface UserRequestInterface {

    @POST("users/register/")
    Observable<User> register(@Body User user);

    @FormUrlEncoded
    @POST("api-token-auth/")
    Observable<Auth> login(@Field("username") String username, @Field("password") String password);

    @GET("currentuser/")
    Observable<User> getCurrentUser(@Header("Authorization") String token);

}
