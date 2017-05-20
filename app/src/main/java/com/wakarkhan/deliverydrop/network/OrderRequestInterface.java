package com.wakarkhan.deliverydrop.network;

import com.wakarkhan.deliverydrop.model.Order;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by wakar on 19/5/17.
 */

public interface OrderRequestInterface {

    @GET("userorders/")
    Observable<List<Order>> getUserOrders(@Header("Authorization") String token);

    @GET("orders/{id}/")
    Observable<Order> getOrder(@Path("id") int orderId,@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("orders/delete/")
    Observable<Response<Object>> deleteOrder(@Field("id") int orderId,@Header("Authorization") String token);
}
