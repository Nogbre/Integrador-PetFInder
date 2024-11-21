package com.example.petfinder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnAdopcion, btnEncontrada, btnPerdida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnEncontrada = findViewById(R.id.btnEncontrada);
        btnEncontrada.setOnClickListener(v -> {
            Intent intent = new Intent(this, EncontradaActivity.class);
            startActivity(intent);
        });


    }


}
