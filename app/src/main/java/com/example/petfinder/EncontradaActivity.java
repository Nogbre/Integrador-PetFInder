package com.example.petfinder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petfinder.Models.GraphQLRequest;
import com.example.petfinder.Models.GraphQLResponse;
import com.example.petfinder.Models.Publicacion;
import com.example.petfinder.Models.Ubicacion;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EncontradaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EncontradaAdapter adapter;
    private List<Publicacion> publicaciones = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encontrada);

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerAdoptada);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EncontradaAdapter(publicaciones);
        recyclerView.setAdapter(adapter);

        Button btnAgregar = findViewById(R.id.btnAgregarAdoptada);
        btnAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(EncontradaActivity.this, CrearAdoptadaActivity.class);
            startActivity(intent);
        });
        // Cargar datos desde la API
        cargarMascotasEncontradas();
    }

    private void cargarMascotasEncontradas() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        String query = "{ publicaciones { id, raza, especie, foto, descripcion } }"; // Ajustar la consulta según el esquema del servidor

        GraphQLRequest request = new GraphQLRequest(query, null);

        apiService.executeGraphQL(request).enqueue(new Callback<GraphQLResponse>() {
            @Override
            public void onResponse(Call<GraphQLResponse> call, Response<GraphQLResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Publicacion> todasLasPublicaciones = parsearPublicaciones(response.body());
                    List<Publicacion> publicacionesEncontradas = new ArrayList<>();
                    for (Publicacion publicacion : todasLasPublicaciones) {
                        if ("Encontrada".equals(publicacion.getTipo())) {
                            publicacionesEncontradas.add(publicacion);
                        }
                    }
                    adapter.updateData(publicacionesEncontradas);
                } else {
                    Toast.makeText(EncontradaActivity.this, "Error cargando datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GraphQLResponse> call, Throwable t) {
                Toast.makeText(EncontradaActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Aquí es donde debe ir el método parsearPublicaciones
    private List<Publicacion> parsearPublicaciones(GraphQLResponse response) {
        List<Publicacion> publicaciones = new ArrayList<>();
        try {
            response.getData().getAsJsonArray("publicaciones").forEach(jsonElement -> {
                JsonObject publicacionJson = jsonElement.getAsJsonObject();
                Publicacion publicacion = new Publicacion();

                publicacion.setId(publicacionJson.get("id").getAsString());
                publicacion.setRaza(publicacionJson.get("raza").getAsString());
                publicacion.setEspecie(publicacionJson.get("especie").getAsString());
                publicacion.setFoto(publicacionJson.get("foto").getAsString());
                publicacion.setDescripcion(publicacionJson.get("descripcion").getAsString());

                // Procesar la ubicación
                JsonObject ubicacionJson = publicacionJson.getAsJsonObject("ubicacion");
                if (ubicacionJson != null) {
                    Ubicacion ubicacion = new Ubicacion();
                    ubicacion.setLatitud(ubicacionJson.get("latitud").getAsDouble());
                    ubicacion.setLongitud(ubicacionJson.get("longitud").getAsDouble());
                    publicacion.setUbicacion(ubicacion);
                }

                publicaciones.add(publicacion);
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error procesando datos", Toast.LENGTH_SHORT).show();
        }
        return publicaciones;
    }
}

