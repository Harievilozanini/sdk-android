package com.mercadopago.services;

import com.mercadopago.model.Customer;
import com.mercadopago.model.Discount;
import com.mercadopago.model.MerchantPayment;
import com.mercadopago.model.Payment;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MerchantService {

    @GET("/{uri}")
    Call<Customer> getCustomer(@Path(value = "uri", encoded = false) String uri, @Query("merchant_access_token") String merchantAccessToken);

    @POST("/{uri}")
    Call<Payment> createPayment(@Path(value = "uri", encoded = false) String uri, @Body MerchantPayment body);

    @GET("/{uri}")
    Call<Discount> getDiscount(@Path(value = "uri", encoded = false) String uri, @Query("merchant_access_token") String merchantAccessToken, @Query("item.id") String itemId, @Query("item.quantity") Integer itemQuantity);
}
