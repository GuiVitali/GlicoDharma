package com.example.glicodharma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class HistoricoMedicoes extends AppCompatActivity {

    String _USERNAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_medicoes);

        // Obtendo o username da Intent
        Intent intentHistorico = getIntent();
        _USERNAME = intentHistorico.getStringExtra("username");

        obterEmostrarMedicoes();
    }

    private void obterEmostrarMedicoes() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("registrosGlicemicos");
        Query query = reference.child(_USERNAME).child("medicoes");

        query.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    mostrarCards(task.getResult());
                } else {
                    Exception exception = task.getException();
                }
            }
        });
    }

    private void mostrarCards(DataSnapshot dataSnapshot) {
        LinearLayout cardContainer = findViewById(R.id.cardContainer);

        if (dataSnapshot.getChildrenCount() == 0) {
            // Não há medições, inflar o novo layout
            View noDataView = LayoutInflater.from(this).inflate(R.layout.layout_no_data, cardContainer, false);
            cardContainer.addView(noDataView);
        } else {
            // Existem medições, criar os cards
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                // Inflar o layout do card
                View cardView = LayoutInflater.from(HistoricoMedicoes.this).inflate(R.layout.cards_historico_medicoes, cardContainer, false);

                // Configurar os elementos dentro do card com os dados do banco
                TextView valorMedicao = cardView.findViewById(R.id.cardTxtValorMedicao);
                TextView unidadeMedida = cardView.findViewById(R.id.cardTxtUnidadeMedida);
                TextView resultadoMedicao = cardView.findViewById(R.id.cardTxtResultadoMedicao);
                TextView tipoMedicao = cardView.findViewById(R.id.cardTextTipoMedicao);
                TextView dataMedicao = cardView.findViewById(R.id.cardTxtDataMedicao);
                TextView horaMedicao = cardView.findViewById(R.id.cardTxtHoraMedicao);

                String valorMedicaoDataBase = snapshot.child("valorMedicao").getValue(String.class);
                String unidadeMedidaDataBase = snapshot.child("unidadeMedida").getValue(String.class);
                String resultadoMedicaoDataBase = snapshot.child("estadoMedicao").getValue(String.class);
                String tipoMedicaoDataBase = snapshot.child("tipo").getValue(String.class);
                String dataMedicaoDataBase = snapshot.child("data").getValue(String.class);
                String horaMedicaoDataBase = snapshot.child("hora").getValue(String.class);

                valorMedicao.setText(valorMedicaoDataBase);
                unidadeMedida.setText(unidadeMedidaDataBase);
                resultadoMedicao.setText(resultadoMedicaoDataBase);
                tipoMedicao.setText(tipoMedicaoDataBase);
                dataMedicao.setText(dataMedicaoDataBase);
                horaMedicao.setText(horaMedicaoDataBase);

                // Adicionar o card ao contêiner
                cardContainer.addView(cardView);
            }
        }
    }

}