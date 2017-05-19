package com.wakarkhan.deliverydrop.network;

import com.wakarkhan.deliverydrop.model.Order;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created by wakar on 19/5/17.
 */

public interface OrderRequestInterface {

    @GET("userorders/")
    Observable<List<Order>> getUserOrders(@Header("Authorization") String token);
}
