package com.example.petfinder;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.petfinder.Models.GraphQLRequest;
import com.example.petfinder.Models.GraphQLResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearAdoptadaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private Spinner especieSpinner, razaSpinner, sexoSpinner;
    private ImageView imagenPreview;
    private EditText colorField, tamanhoField, descripcionField;
    private Uri imagenUri;
    private GoogleMap googleMap;
    private LatLng selectedLocation;

    private String usuarioId; // ID del usuario autenticado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_adoptada);

        // Obtener usuarioId desde SharedPreferences
        usuarioId = obtenerUsuarioId();
        if (usuarioId == null || usuarioId.isEmpty()) {
            Toast.makeText(this, "No se encontró el usuario autenticado. Por favor, inicia sesión.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }



        // Referencias a los elementos del formulario
        colorField = findViewById(R.id.color);
        tamanhoField = findViewById(R.id.tamanho);
        descripcionField = findViewById(R.id.descripcion);
        especieSpinner = findViewById(R.id.especie);
        razaSpinner = findViewById(R.id.raza);
        sexoSpinner = findViewById(R.id.sexo);
        imagenPreview = findViewById(R.id.imagen_preview);
        Button subirFotoButton = findViewById(R.id.btn_subir_foto);
        Button enviarButton = findViewById(R.id.btn_enviar);

        // Configuración del mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        configurarSpinners();

        // Acción para subir foto
        subirFotoButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else {
                abrirGaleria();
            }
        });

        // Acción para enviar formulario
        enviarButton.setOnClickListener(v -> {
            if (validarCampos()) {
                crearUbicacion();
            }
        });
    }



    private String obtenerUsuarioId() {
        SharedPreferences sharedPreferences = getSharedPreferences("UsuarioAuth", MODE_PRIVATE);
        return sharedPreferences.getString("usuarioId", null);
    }

    private void configurarSpinners() {
        List<String> especies = Arrays.asList("Perro", "Gato");
        ArrayAdapter<String> especieAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, especies);
        especieAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        especieSpinner.setAdapter(especieAdapter);

        List<String> sexos = Arrays.asList("Macho", "Hembra");
        ArrayAdapter<String> sexoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sexos);
        sexoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexoSpinner.setAdapter(sexoAdapter);

        especieSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Aquí obtienes el ítem seleccionado y actualizas las razas
                String especieSeleccionada = parent.getItemAtPosition(position).toString();
                actualizarRazas(especieSeleccionada);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Este método es obligatorio implementarlo, aunque puede estar vacío
            }
        });
    }

    private boolean validarCampos() {
        if (colorField.getText().toString().trim().isEmpty() ||
                tamanhoField.getText().toString().trim().isEmpty() ||
                descripcionField.getText().toString().trim().isEmpty() ||
                imagenUri == null ||
                selectedLocation == null) {
            Toast.makeText(this, "Por favor, completa todos los campos, selecciona una foto y elige una ubicación.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void actualizarRazas(String especie) {
        List<String> razas = especie.equals("Perro")
                ? Arrays.asList("Labrador", "Golden Retriever", "Bulldog", "Beagle", "Poodle", "Pastor Alemán", "Husky", "Chihuahua", "Boxer", "Pomeranian")
                : Arrays.asList("Siameses", "Persa", "Maine Coon", "Bengala", "Ragdoll", "Sphynx", "Azul Ruso", "Exótico de Pelo Corto");

        ArrayAdapter<String> razaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, razas);
        razaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        razaSpinner.setAdapter(razaAdapter);
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            imagenUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imagenUri);
                imagenPreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        LatLng posicionInicial = new LatLng(-17.783488, -63.182031);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicionInicial, 15));

        googleMap.setOnMapClickListener(latLng -> {
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Ubicación seleccionada"));
            selectedLocation = latLng;
            Toast.makeText(this, "Ubicación seleccionada: " + latLng.latitude + ", " + latLng.longitude, Toast.LENGTH_SHORT).show();
        });
    }

    private void crearUbicacion() {
        String mutation = "mutation CreateUbicacion($latitud: String!, $longitud: String!) { " +
                "  createUbicacion(input: {latitud: $latitud, longitud: $longitud}) { " +
                "    id " +
                "  } " +
                "}";

        // Convierte las coordenadas a String
        Map<String, Object> variables = new HashMap<>();
        variables.put("latitud", String.valueOf(selectedLocation.latitude));
        variables.put("longitud", String.valueOf(selectedLocation.longitude));

        GraphQLRequest request = new GraphQLRequest(mutation, variables);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.executeGraphQL(request).enqueue(new Callback<GraphQLResponse>() {
            @Override
            public void onResponse(Call<GraphQLResponse> call, Response<GraphQLResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String ubicacionId = response.body().getData()
                            .getAsJsonObject("createUbicacion")
                            .get("id").getAsString();
                    crearPublicacion(ubicacionId);
                } else {
                    Toast.makeText(CrearAdoptadaActivity.this, "Error al crear la ubicación", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GraphQLResponse> call, Throwable t) {
                Toast.makeText(CrearAdoptadaActivity.this, "Error de conexión al crear ubicación", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void crearPublicacion(String ubicacionId) {
        String mutation = "mutation CreatePublicacion($raza: String!, $especie: String!, $foto: String!, $descripcion: String!, $usuarioId:  String!, $ubicacionId:  String!) { " +
                "  createPublicacion(input: {raza: $raza, especie: $especie, foto: $foto, descripcion: $descripcion, usuarioId: $usuarioId, ubicacionId: $ubicacionId}) { " +
                "    id " +
                "    raza " +
                "    especie " +
                "    foto " +
                "    descripcion " +
                "  } " +
                "}";

        Map<String, Object> variables = new HashMap<>();
        variables.put("raza", razaSpinner.getSelectedItem().toString());
        variables.put("especie", especieSpinner.getSelectedItem().toString());
        variables.put("foto", imagenUri.toString());
        variables.put("descripcion", descripcionField.getText().toString().trim());
        variables.put("usuarioId", usuarioId); // Obtenido automáticamente al inicio de sesión
        variables.put("ubicacionId", ubicacionId);

        GraphQLRequest request = new GraphQLRequest(mutation, variables);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.executeGraphQL(request).enqueue(new Callback<GraphQLResponse>() {
            @Override
            public void onResponse(Call<GraphQLResponse> call, Response<GraphQLResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CrearAdoptadaActivity.this, "Publicación creada con éxito", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CrearAdoptadaActivity.this, "Error al crear publicación", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GraphQLResponse> call, Throwable t) {
                Toast.makeText(CrearAdoptadaActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
