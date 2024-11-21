package com.example.petfinder;

import android.content.Intent;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CrearAdoptadaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_IMAGE_PICK = 1;

    private Spinner especieSpinner, razaSpinner, sexoSpinner;
    private ImageView imagenPreview;
    private EditText colorField, tamanhoField, descripcionField;
    private Uri imagenUri;
    private GoogleMap googleMap;
    private LatLng selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_adoptada);

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

        // Configurar el Spinner de especie
        List<String> especies = Arrays.asList("Perro", "Gato");
        ArrayAdapter<String> especieAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, especies);
        especieAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        especieSpinner.setAdapter(especieAdapter);

        // Configurar el Spinner de sexo
        List<String> sexos = Arrays.asList("Macho", "Hembra");
        ArrayAdapter<String> sexoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sexos);
        sexoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexoSpinner.setAdapter(sexoAdapter);

        // Configurar el Spinner de raza según la especie seleccionada
        especieSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String especieSeleccionada = (String) parent.getItemAtPosition(position);
                actualizarRazas(especieSeleccionada);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Acción para subir foto
        subirFotoButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_IMAGE_PICK);
        });

        // Acción para enviar formulario
        enviarButton.setOnClickListener(v -> {
            String color = colorField.getText().toString().trim();
            String tamanho = tamanhoField.getText().toString().trim();
            String descripcion = descripcionField.getText().toString().trim();
            String especie = (String) especieSpinner.getSelectedItem();
            String raza = (String) razaSpinner.getSelectedItem();
            String sexo = (String) sexoSpinner.getSelectedItem();

            if (color.isEmpty() || tamanho.isEmpty() || descripcion.isEmpty() || imagenUri == null || selectedLocation == null) {
                Toast.makeText(this, "Por favor, completa todos los campos, selecciona una foto y elige una ubicación.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Aquí envías los datos al backend o los procesas
            double latitud = selectedLocation.latitude;
            double longitud = selectedLocation.longitude;

            Toast.makeText(this, "Formulario enviado con éxito\nLatitud: " + latitud + "\nLongitud: " + longitud, Toast.LENGTH_SHORT).show();
        });
    }

    private void actualizarRazas(String especie) {
        List<String> razas;
        if (especie.equals("Perro")) {
            razas = Arrays.asList(
                    "Labrador", "Golden Retriever", "Bulldog", "Beagle", "Poodle", "Pastor Alemán",
                    "Husky", "Chihuahua", "Boxer", "Pomeranian"
            );
        } else {
            razas = Arrays.asList(
                    "Siameses", "Persa", "Maine Coon", "Bengala", "Ragdoll", "Sphynx", "Azul Ruso", "Exótico de Pelo Corto"
            );
        }
        ArrayAdapter<String> razaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, razas);
        razaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        razaSpinner.setAdapter(razaAdapter);
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

        // Configurar el mapa inicial
        LatLng posicionInicial = new LatLng(-34.6037, -58.3816); // Cambiar por la ubicación deseada
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicionInicial, 15));

        // Listener para obtener la ubicación seleccionada
        googleMap.setOnMapClickListener(latLng -> {
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Ubicación seleccionada"));
            selectedLocation = latLng;
            Toast.makeText(this, "Ubicación seleccionada: " + latLng.latitude + ", " + latLng.longitude, Toast.LENGTH_SHORT).show();
        });
    }
}
