package com.example.petfinder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.petfinder.Models.GraphQLRequest;
import com.example.petfinder.Models.GraphQLResponse;

import java.util.HashMap;
import java.util.Map;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        EditText nameField = view.findViewById(R.id.name);
        EditText lastNameField = view.findViewById(R.id.last_name);
        EditText emailField = view.findViewById(R.id.email);
        EditText phoneField = view.findViewById(R.id.phone);
        EditText passwordField = view.findViewById(R.id.password);
        Button registerButton = view.findViewById(R.id.register_button);

        registerButton.setOnClickListener(v -> {
            String name = nameField.getText().toString().trim();
            String lastName = lastNameField.getText().toString().trim();
            String email = emailField.getText().toString().trim();
            String phone = phoneField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            // Validación de campos
            if (!isValidName(name)) {
                Toast.makeText(getContext(), "El nombre solo debe contener letras.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidName(lastName)) {
                Toast.makeText(getContext(), "El apellido solo debe contener letras.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidEmail(email)) {
                Toast.makeText(getContext(), "Por favor, introduce un correo electrónico válido.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidPhone(phone)) {
                Toast.makeText(getContext(), "El teléfono solo puede contener números y '+'.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, completa el campo de contraseña.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Define la mutación GraphQL para createUsuario
            String mutation = "mutation CreateUsuario($nombre: String!, $apellido: String!, $email: String!, $telefono: String!, $contrase_a: String!, $tipo: String!) { " +
                    "  createUsuario(input: {nombre: $nombre, apellido: $apellido, email: $email, telefono: $telefono, contrase_a: $contrase_a, tipo: $tipo}) { " +
                    "    id " +
                    "    nombre " +
                    "    apellido " +
                    "    email " +
                    "    telefono " +
                    "  } " +
                    "}";

            // Crear las variables
            Map<String, Object> variables = new HashMap<>();
            variables.put("nombre", name);
            variables.put("apellido", lastName);
            variables.put("email", email);
            variables.put("telefono", phone);
            variables.put("contrase_a", password);
            variables.put("tipo", "User"); // Tipo predeterminado

            // Crear la solicitud GraphQL
            GraphQLRequest request = new GraphQLRequest(mutation, variables);

            // Llama al endpoint de GraphQL
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            apiService.executeGraphQL(request).enqueue(new Callback<GraphQLResponse>() {
                @Override
                public void onResponse(Call<GraphQLResponse> call, Response<GraphQLResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        GraphQLResponse graphQLResponse = response.body();
                        if (graphQLResponse.getErrors() == null) {
                            Toast.makeText(getContext(), "Registro exitoso.", Toast.LENGTH_SHORT).show();
                            getParentFragmentManager().popBackStack(); // Regresa al fragmento anterior
                        } else {
                            Toast.makeText(getContext(), "Error en el registro: " + graphQLResponse.getErrors().toString(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error en la respuesta del servidor.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<GraphQLResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }

    // Validación para nombres y apellidos (solo letras)
    private boolean isValidName(String name) {
        return Pattern.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", name);
    }

    // Validación de correo electrónico
    private boolean isValidEmail(String email) {
        return Pattern.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$", email);
    }

    // Validación de teléfono (solo números y '+')
    private boolean isValidPhone(String phone) {
        return Pattern.matches("^\\+?[0-9]+$", phone);
    }
}
