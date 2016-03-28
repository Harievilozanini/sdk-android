package com.mercadopago.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.mercadopago.R;
import com.mercadopago.model.ApiException;

import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ApiUtil {

    public static void finishWithApiException(Activity activity, Response<?> response) {

        finishWithApiException(activity, getApiException(response));
    }

    public static void finishWithApiException(Activity activity, Throwable throwable) {

        finishWithApiException(activity, getApiException(throwable));
    }

    public static void finishWithApiException(Activity activity, ApiException apiException) {

        if (!ApiUtil.checkConnection(activity)) {  // check for connection error

            // Show refresh layout
            LayoutUtil.showRefreshLayout(activity);
            Toast.makeText(activity, activity.getString(R.string.mpsdk_no_connection_message), Toast.LENGTH_LONG).show();

        } else {

            // Return with api exception
            Intent intent = new Intent();
            activity.setResult(activity.RESULT_CANCELED, intent);
            intent.putExtra("apiException", JsonUtil.getInstance().toJson(apiException));
            activity.finish();
        }
    }

    public static boolean checkConnection(Context context) {

        if (context != null) {
            try {
                boolean HaveConnectedWifi = false;
                boolean HaveConnectedMobile = false;
                ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if ( ni != null && ni.isConnected())
                {
                    if (ni.getType() == ConnectivityManager.TYPE_WIFI)
                        if (ni.isConnectedOrConnecting())
                            HaveConnectedWifi = true;
                    if (ni.getType() == ConnectivityManager.TYPE_MOBILE)
                        if (ni.isConnectedOrConnecting())
                            HaveConnectedMobile = true;
                }

                return HaveConnectedWifi || HaveConnectedMobile;
            }
            catch (Exception ex) {
                return false;
            }
        }
        else {
            return false;
        }
    }

    private static ApiException getApiException(Response<?> response) {

        Converter<ResponseBody, ApiException> converter =
                new Retrofit.Builder().build()
                        .responseBodyConverter(ApiException.class, new Annotation[0]);

        ApiException apiException = null;
        try {
            apiException = converter.convert(response.errorBody());

        } catch (Exception ex) {
            // do nothing
        }

        return apiException;
    }

    private static ApiException getApiException(Throwable throwable) {

        ApiException apiException = new ApiException();
        try {
            apiException.setMessage(throwable.getMessage());

        } catch (Exception ex) {
            // do nothing
        }

        return apiException;
    }
}
