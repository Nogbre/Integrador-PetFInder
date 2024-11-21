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
    // URL base de tu backend en Somee
    private static final String BASE_URL = "http://petfinder.somee.com/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Configuración del interceptor de logs
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Interceptor para agregar cabeceras comunes
            Interceptor headerInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request originalRequest = chain.request();
                    Request requestWithHeaders = originalRequest.newBuilder()
                            .header("Content-Type", "application/json") // Cabecera de tipo de contenido
                            .header("Accept", "application/json") // Aceptar JSON
                            .build();
                    return chain.proceed(requestWithHeaders);
                }
            };

            // Cliente HTTP con tiempo de espera y los interceptores
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging) // Logs para depuración
                    .addInterceptor(headerInterceptor) // Cabeceras
                    .connectTimeout(30, TimeUnit.SECONDS) // Timeout para conexión
                    .readTimeout(30, TimeUnit.SECONDS) // Timeout para lectura
                    .writeTimeout(30, TimeUnit.SECONDS) // Timeout para escritura
                    .build();

            // Configuración de Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
