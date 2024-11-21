package com.example.petfinder;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ApiClient {

    private static final String BASE_URL = "http://petfinder.somee.com/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);


            Interceptor headerInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request originalRequest = chain.request();
                    Request requestWithHeaders = originalRequest.newBuilder()
                            .header("Content-Type", "application/json")
                            .header("Accept", "application/json") //esto hace que se acepte el json
                            .build();
                    return chain.proceed(requestWithHeaders);
                }
            };


            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging) // log para la depuracion
                    .addInterceptor(headerInterceptor)

                    //aqui son los timeouts para la conexion, lectura y escritura
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            // aqui solo configuiramos el retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
