package com.example.petfinder;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.petfinder.Models.GraphQLRequest;
import com.example.petfinder.Models.GraphQLResponse;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        EditText emailField = view.findViewById(R.id.email);
        EditText passwordField = view.findViewById(R.id.password);
        Button loginButton = view.findViewById(R.id.login_button);
        TextView signupLink = view.findViewById(R.id.no_account);

        // Acción para el botón de inicio de sesión
        loginButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Define la mutación GraphQL para el login
            String mutation = "mutation Login($email: String!, $contrase_a: String!) { " +
                    "  login(email: $email, contrase_a: $contrase_a) { " +
                    "    token " +
                    "    usuario { " +
                    "      _id " +
                    "      nombre " +
                    "      email " +
                    "    } " +
                    "  } " +
                    "}";

            // Crea las variables para la mutación
            Map<String, Object> variables = new HashMap<>();
            variables.put("email", email);
            variables.put("contrase_a", password);

            // Crea la solicitud GraphQL
            GraphQLRequest request = new GraphQLRequest(mutation, variables);

            // Llama al endpoint de GraphQL
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            apiService.executeGraphQL(request).enqueue(new Callback<GraphQLResponse>() {
                @Override
                public void onResponse(Call<GraphQLResponse> call, Response<GraphQLResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        GraphQLResponse graphQLResponse = response.body();
                        if (graphQLResponse.getErrors() == null) {
                            String token = graphQLResponse.getData()
                                    .getAsJsonObject("login")
                                    .get("token")
                                    .getAsString();

                            // Guarda el token en SharedPreferences
                            SharedPreferences sharedPreferences = getContext().getSharedPreferences("AppPrefs", getContext().MODE_PRIVATE);
                            sharedPreferences.edit().putString("auth_token", token).apply();

                            Toast.makeText(getContext(), "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Error en el inicio de sesión: " + graphQLResponse.getErrors().toString(), Toast.LENGTH_SHORT).show();
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

        // Acción para el enlace de registro
        signupLink.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RegisterFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
