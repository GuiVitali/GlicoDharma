package com.example.glicodharma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NovoRegistroGlicemicoResultado extends AppCompatActivity {

    TextView valorMedicao, unidadeMedicao, estadoMedicao, estadoNumeroMedicao;

    Button btnOk;

    String valorMedicaoString, unidadeMedicaoString, estadoMedicaoString, estadoNumeroMedicaoString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_registro_glicemico_resultado);

        valorMedicao = findViewById(R.id.valorMedicaoResultado);
        unidadeMedicao = findViewById(R.id.unidadeMedicaoResultado);
        estadoMedicao = findViewById(R.id.estadoTxtResultado);
        estadoNumeroMedicao = findViewById(R.id.estadoNumeroTxtResultado);
        btnOk = findViewById(R.id.btnOk);

        pegarDados();
        atualizarCard();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                intent.putExtra("whatToDo", "recuperarDados");
                startActivity(intent);
                finish();
            }
        });

    }

    private void pegarDados() {

        Intent intent2 = getIntent();
        valorMedicaoString = intent2.getStringExtra("valorMedicao");
        unidadeMedicaoString = intent2.getStringExtra("unidadeMedida");
        estadoMedicaoString = intent2.getStringExtra("estadoMedicao");
        estadoNumeroMedicaoString = intent2.getStringExtra("estadoMedicaoNumero");

    }

    private void atualizarCard() {

        valorMedicao.setText("" + valorMedicaoString);
        unidadeMedicao.setText("" + unidadeMedicaoString);
        estadoMedicao.setText("" + estadoMedicaoString);
        estadoNumeroMedicao.setText("" + estadoNumeroMedicaoString);

    }
}