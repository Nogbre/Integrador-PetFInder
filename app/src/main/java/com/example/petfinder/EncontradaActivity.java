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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_encontrada);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerAdoptada);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EncontradaAdapter(publicaciones);
        recyclerView.setAdapter(adapter);

        // Botón para agregar una nueva mascota encontrada
        Button btnAgregar = findViewById(R.id.btnAgregarAdoptada);
        btnAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(this, CrearAdoptadaActivity.class);
            startActivity(intent);
        });

        // Cargar datos desde la API
        cargarMascotasEncontradas();
    }

    private void cargarMascotasEncontradas() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        String query = "{ publicaciones(tipo: \"Encontrada\") { id, raza, especie, foto, descripcion } }";
        GraphQLRequest request = new GraphQLRequest(query, null);

        apiService.executeGraphQL(request).enqueue(new Callback<GraphQLResponse>() {
            @Override
            public void onResponse(Call<GraphQLResponse> call, Response<GraphQLResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Publicacion> nuevasPublicaciones = parsearPublicaciones(response.body());
                    adapter.updateData(nuevasPublicaciones);
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

    private List<Publicacion> parsearPublicaciones(GraphQLResponse response) {
        List<Publicacion> publicaciones = new ArrayList<>();
        try {
            response.getData().getAsJsonArray("publicaciones").forEach(jsonElement -> {
                Publicacion publicacion = new Publicacion();
                publicacion.setId(jsonElement.getAsJsonObject().get("id").getAsString());
                publicacion.setRaza(jsonElement.getAsJsonObject().get("raza").getAsString());
                publicacion.setEspecie(jsonElement.getAsJsonObject().get("especie").getAsString());
                publicacion.setFoto(jsonElement.getAsJsonObject().get("foto").getAsString());
                publicacion.setDescripcion(jsonElement.getAsJsonObject().get("descripcion").getAsString());
                publicaciones.add(publicacion);
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error procesando datos", Toast.LENGTH_SHORT).show();
        }
        return publicaciones;
    }
}
