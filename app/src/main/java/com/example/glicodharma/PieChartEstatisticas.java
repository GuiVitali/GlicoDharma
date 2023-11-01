package com.example.glicodharma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PieChartEstatisticas extends AppCompatActivity {

    int baixo = 0, normal = 0, preDiabetes = 0, diabetes = 0;

    String _USERNAME = "qwww";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart_estatisticas);

        pegarDadosdoBanco();
    }

    private void pegarDadosdoBanco() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("registrosGlicemicos");
        Query query = reference.child(_USERNAME).child("medicoes");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String estadoMedicao = dataSnapshot.child("estadoMedicao").getValue(String.class);

                        switch (estadoMedicao) {
                            case "Baixo":
                                baixo++;
                                break;
                            case "Normal":
                                normal++;
                                break;
                            case "Pré-diabetes":
                                preDiabetes++;
                                break;
                            case "Diabetes":
                                diabetes++;
                                break;
                        }
                    }
                }

                criarGrafico();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void criarGrafico() {

        PieChart pieChart = findViewById(R.id.pieChart);

        ArrayList<PieEntry> estatisticas = new ArrayList<>();
        if (baixo > 0) estatisticas.add(new PieEntry(baixo, "Baixo"));
        if (normal > 0) estatisticas.add(new PieEntry(normal, "Normal"));
        if (preDiabetes > 0) estatisticas.add(new PieEntry(preDiabetes, "Pré-diabetes"));
        if (diabetes > 0) estatisticas.add(new PieEntry(diabetes, "Diabetes"));

        PieDataSet pieDataSet = new PieDataSet(estatisticas, "Estatísticas");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Registros");
        pieChart.animate();

    }

}