package com.example.glicodharma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class RegistrosGlicemicos extends AppCompatActivity {

    int baixo = 0, normal = 0, preDiabetes = 0, diabetes = 0;

    String _USERNAME, prefUnidade;

    TextView mediaTotalValor, mediaTotalUnidade, mediaAnualValor, mediaAnualUnidade, mediaMensalValor, mediaMensalUnidade, mediaSemanalValor, mediaSemanalUnidade, media3diasValor, media3diasUnidade, ultimaMedidaValor, ultimaMedidaUnidade;

    Button novoRegistro, btnHistorico;

    CardView card_alarme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registros_glicemicos);

        ImageView leftIcon = findViewById(R.id.left_icon);
        ImageView rightIcon = findViewById(R.id.right_icon);
        TextView title = findViewById(R.id.tollbar_title);

        title.setText("Registro Glicêmico");

        // Hooks Média
        mediaTotalValor = (findViewById(R.id.mediaTotalValor));
        mediaAnualValor = (findViewById(R.id.mediaAnualValor));
        mediaMensalValor = (findViewById(R.id.mediaMensalValor));
        mediaSemanalValor = (findViewById(R.id.mediaSemanalValor));
        media3diasValor = (findViewById(R.id.media3diasValor));
        ultimaMedidaValor = (findViewById(R.id.ultimaMedidaValor));

        mediaTotalUnidade = (findViewById(R.id.mediaTotalUnidade));
        mediaAnualUnidade = (findViewById(R.id.mediaAnualUnidade));
        mediaMensalUnidade = (findViewById(R.id.mediaMensalUnidade));
        mediaSemanalUnidade = (findViewById(R.id.mediaSemanalUnidade));
        media3diasUnidade = (findViewById(R.id.media3diasUnidade));
        ultimaMedidaUnidade = (findViewById(R.id.ultimaMedidaUnidade));

        card_alarme = findViewById(R.id.card_alarme);

        novoRegistro = findViewById(R.id.btnNovoRegistro);
        btnHistorico = findViewById(R.id.btnMostrarHistorico);

        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Voltar
            }
        });

        rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Definir Alarme
            }
        });

        Intent intent = getIntent();
        _USERNAME = intent.getStringExtra("username");

        inflarLayoutGrafico();
        obterEmostrarMedicoes();
        verificarMedicoes();

        btnHistorico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHistorico = new Intent(getApplicationContext(), HistoricoMedicoes.class);
                intentHistorico.putExtra("username",_USERNAME);
                startActivity(intentHistorico);
            }
        });

        card_alarme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Alarme.class));
            }
        });

        novoRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NovoRegistroGlicemico.class);
                intent.putExtra("username",_USERNAME);
                startActivity(intent);
                finish();
            }
        });

    }

    private void verificarMedicoes() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("registrosGlicemicos");
        Query query = reference.child(_USERNAME).child("medicoes");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean temMedicoes = dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0;

                if (temMedicoes) {
                    medicaoRecente();
                    obterPreferenciaUnidadeMedida();
                } else {
                    // Não há medições no banco
                    ultimaMedidaValor.setText("--/--");
                    media3diasValor.setText("--/--");
                    mediaSemanalValor.setText("--/--");
                    mediaMensalValor.setText("--/--");
                    mediaAnualValor.setText("--/--");
                    mediaTotalValor.setText("--/--");

                    ultimaMedidaUnidade.setText("mg/dL");
                    media3diasUnidade.setText("mg/dL");
                    mediaMensalUnidade.setText("mg/dL");
                    mediaSemanalUnidade.setText("mg/dL");
                    mediaAnualUnidade.setText("mg/dL");
                    mediaTotalUnidade.setText("mg/dL");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void obterPreferenciaUnidadeMedida() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("registrosGlicemicos");
        Query query = reference.child(_USERNAME).child("medicoes");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int mgdLCount = 0;
                int mmolLCount = 0;
                int somaMgdL = 0;
                int somaMmolL = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String unidadeMedida = snapshot.child("unidadeMedida").getValue(String.class);
                    int valorMedicao = Integer.parseInt(snapshot.child("valorMedicao").getValue(String.class));

                    if (unidadeMedida != null && unidadeMedida.equals("mg/dL")) {
                        mgdLCount++;
                        somaMgdL += valorMedicao;
                        somaMmolL += mgdLParaMmol(valorMedicao);
                    } else if (unidadeMedida != null && unidadeMedida.equals("mmol/L")) {
                        mmolLCount++;
                        somaMmolL += valorMedicao;
                        somaMgdL += mmolParaMgdL(valorMedicao);
                    }
                }

                if (mgdLCount > mmolLCount) {
                    int mediaEmMgdL = somaMgdL / (mgdLCount + mmolLCount);
                    metodoParaMgDL(mediaEmMgdL);
                } else {
                    int mediaEmMmolL = somaMmolL / (mmolLCount + mgdLCount);
                    metodoParaMmolL(mediaEmMmolL);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private int mmolParaMgdL(int mmolLValue) {
        return (int) (mmolLValue * 18.0182);
    }

    private int mgdLParaMmol(int mgdLValue) {
        return (int) (mgdLValue / 18.0182);
    }

    private void metodoParaMgDL(int media) {
        mediaTotalValor.setText("" + media + ",0");
        mediaTotalUnidade.setText("mg/dL");
        prefUnidade = "mg/dL";

        calcularMediaAnual(prefUnidade);
        calcularMediaMensal(prefUnidade);
        calcularMediaSemanal(prefUnidade);
        calcularMediaUltimos3dias(prefUnidade);
    }

    private void metodoParaMmolL(int media) {
        mediaTotalValor.setText("" + media + ",0");
        mediaTotalUnidade.setText("mmol/L");
        prefUnidade = "mmol/L";

        calcularMediaAnual(prefUnidade);
        calcularMediaMensal(prefUnidade);
        calcularMediaSemanal(prefUnidade);
        calcularMediaUltimos3dias(prefUnidade);
    }

    private void medicaoRecente() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("registrosGlicemicos");
        Query query = reference.child(_USERNAME).child("medicoes").orderByChild("data").limitToLast(1);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot ultimaMedicaoSnapshot = dataSnapshot.getChildren().iterator().next();
                    String valorMedicao = ultimaMedicaoSnapshot.child("valorMedicao").getValue(String.class);
                    String unidadeMedida = ultimaMedicaoSnapshot.child("unidadeMedida").getValue(String.class);

                    ultimaMedidaValor.setText(valorMedicao);
                    ultimaMedidaUnidade.setText(unidadeMedida);
                } else {
                    /////////////////////////////
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void calcularMediaAnual(String prefUnidade) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("registrosGlicemicos");
        Query query = reference.child(_USERNAME).child("medicoes");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Calendar calendar = Calendar.getInstance();
                int anoAtual = calendar.get(Calendar.YEAR) % 100; // %100 para retornar apenas o 23
                int mgdLCount = 0;
                int mmolLCount = 0;
                int somaMgdL = 0;
                int somaMmolL = 0;
                boolean temMedicoes = false;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String unidadeMedida = snapshot.child("unidadeMedida").getValue(String.class);
                    String dataMedicaoDataBase = snapshot.child("data").getValue(String.class);
                    int valorMedicao = Integer.parseInt(snapshot.child("valorMedicao").getValue(String.class));

                    // Verifica se a data está no ano atual
                    if (dataMedicaoDataBase != null && dataMedicaoDataBase.endsWith(String.valueOf(anoAtual))) {
                        temMedicoes = true;                              // |--> A string termina com os caracteres especificados?

                        if (unidadeMedida != null && unidadeMedida.equals("mg/dL")) {
                            mgdLCount++;
                            somaMgdL += valorMedicao;
                            somaMmolL += mgdLParaMmol(valorMedicao);
                        } else if (unidadeMedida != null && unidadeMedida.equals("mmol/L")) {
                            mmolLCount++;
                            somaMmolL += valorMedicao;
                            somaMgdL += mmolParaMgdL(valorMedicao);
                        }
                    }
                }

                if (temMedicoes) {
                    if (prefUnidade.equals("mg/dL")) {
                        int mediaEmMgdL = somaMgdL / (mgdLCount + mmolLCount);
                        mediaAnualValor.setText("" + mediaEmMgdL + ",0");
                        mediaAnualUnidade.setText("mg/dL");
                    } else if (prefUnidade.equals("mmol/L")) {
                        int mediaEmMmolL = somaMmolL / (mmolLCount + mgdLCount);
                        mediaAnualValor.setText("" + mediaEmMmolL + ",0");
                        mediaAnualUnidade.setText("mmol/L");
                    }
                } else {
                    // Não há medições no ano atual
                    mediaAnualValor.setText("--/--");
                    mediaAnualUnidade.setText(prefUnidade);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void calcularMediaMensal(String prefUnidade) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("registrosGlicemicos");
        Query query = reference.child(_USERNAME).child("medicoes");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Calendar calendar = Calendar.getInstance();
                int anoAtual = calendar.get(Calendar.YEAR) % 100;
                int mesAtual = calendar.get(Calendar.MONTH) + 1; // Mês atual --> começa de 0 (Janeiro) a 11 (Dezembro)
                int mgdLCount = 0;
                int mmolLCount = 0;
                int somaMgdL = 0;
                int somaMmolL = 0;
                boolean temMedicoes = false;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String unidadeMedida = snapshot.child("unidadeMedida").getValue(String.class);
                    String dataMedicaoDataBase = snapshot.child("data").getValue(String.class);
                    int valorMedicao = Integer.parseInt(snapshot.child("valorMedicao").getValue(String.class));

                    // Verifica se a data está no mês atual
                    String[] partesData = dataMedicaoDataBase.split("-");
                    int anoMedicao = Integer.parseInt(partesData[2]);
                    int mesMedicao = Integer.parseInt(partesData[1]);

                    if (anoMedicao == anoAtual && mesMedicao == mesAtual) {
                        temMedicoes = true;

                        if (unidadeMedida != null && unidadeMedida.equals("mg/dL")) {
                            mgdLCount++;
                            somaMgdL += valorMedicao;
                            somaMmolL += mgdLParaMmol(valorMedicao);
                        } else if (unidadeMedida != null && unidadeMedida.equals("mmol/L")) {
                            mmolLCount++;
                            somaMmolL += valorMedicao;
                            somaMgdL += mmolParaMgdL(valorMedicao);
                        }
                    }
                }

                if (temMedicoes) {
                    if (prefUnidade.equals("mg/dL")) {
                        int mediaEmMgdL = somaMgdL / (mgdLCount + mmolLCount);
                        mediaMensalValor.setText("" + mediaEmMgdL + ",0");
                        mediaMensalUnidade.setText("mg/dL");
                    } else if (prefUnidade.equals("mmol/L")) {
                        int mediaEmMmolL = somaMmolL / (mmolLCount + mgdLCount);
                        mediaMensalValor.setText("" + mediaEmMmolL + ",0");
                        mediaMensalUnidade.setText("mmol/L");
                    }
                } else {
                    mediaMensalValor.setText("--/--");
                    mediaSemanalUnidade.setText(prefUnidade);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void calcularMediaSemanal(String prefUnidade) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("registrosGlicemicos");
        Query query = reference.child(_USERNAME).child("medicoes");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Calendar calendar = Calendar.getInstance();
                int anoAtual = calendar.get(Calendar.YEAR) % 100;
                int semanaAtual = calendar.get(Calendar.WEEK_OF_YEAR); // retorna o número da semana do ano
                int mgdLCount = 0;
                int mmolLCount = 0;
                int somaMgdL = 0;
                int somaMmolL = 0;
                boolean temMedicoes = false;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String unidadeMedida = snapshot.child("unidadeMedida").getValue(String.class);
                    String dataMedicaoDataBase = snapshot.child("data").getValue(String.class);
                    int valorMedicao = Integer.parseInt(snapshot.child("valorMedicao").getValue(String.class));

                    // Verifica se a data está na semana atual
                    Calendar calMedicao = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
                    try {
                        calMedicao.setTime(sdf.parse(dataMedicaoDataBase));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    int anoMedicao = calMedicao.get(Calendar.YEAR) % 100;
                    int semanaMedicao = calMedicao.get(Calendar.WEEK_OF_YEAR);

                    if (anoMedicao == anoAtual && semanaMedicao == semanaAtual) {
                        temMedicoes = true;

                        if (unidadeMedida != null && unidadeMedida.equals("mg/dL")) {
                            mgdLCount++;
                            somaMgdL += valorMedicao;
                            somaMmolL += mgdLParaMmol(valorMedicao);
                        } else if (unidadeMedida != null && unidadeMedida.equals("mmol/L")) {
                            mmolLCount++;
                            somaMmolL += valorMedicao;
                            somaMgdL += mmolParaMgdL(valorMedicao);
                        }
                    }
                }

                if (temMedicoes) {
                    if (prefUnidade.equals("mg/dL")) {
                        int mediaEmMgdL = somaMgdL / (mgdLCount + mmolLCount);
                        mediaSemanalValor.setText("" + mediaEmMgdL + ",0");
                        mediaSemanalUnidade.setText("mg/dL");
                    } else if (prefUnidade.equals("mmol/L")) {
                        int mediaEmMmolL = somaMmolL / (mmolLCount + mgdLCount);
                        mediaSemanalValor.setText("" + mediaEmMmolL + ",0");
                        mediaSemanalUnidade.setText("mmol/L");
                    }
                } else {
                    mediaSemanalValor.setText("--/--");
                    mediaSemanalUnidade.setText(prefUnidade);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void calcularMediaUltimos3dias(String prefUnidade) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("registrosGlicemicos");
        Query query = reference.child(_USERNAME).child("medicoes");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Calendar calendar = Calendar.getInstance();
                int mgdLCount = 0;
                int mmolLCount = 0;
                int somaMgdL = 0;
                int somaMmolL = 0;

                // Obter a data atual
                Date dataAtual = calendar.getTime();

                // Subtrair 3 dias
                calendar.add(Calendar.DAY_OF_YEAR, -3);
                Date dataLimite = calendar.getTime();

                boolean temMedicoes = false;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String unidadeMedida = snapshot.child("unidadeMedida").getValue(String.class);
                    String dataMedicaoDataBase = snapshot.child("data").getValue(String.class);
                    int valorMedicao = Integer.parseInt(snapshot.child("valorMedicao").getValue(String.class));

                    // Converter a string de data para Date
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    Date dataMedicao = null;
                    try {
                        dataMedicao = sdf.parse(dataMedicaoDataBase);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    // Verificar se a data está nos últimos 3 dias
                    if (dataMedicao != null && dataMedicao.after(dataLimite) && dataMedicao.before(dataAtual)) {

                        if (unidadeMedida != null && unidadeMedida.equals("mg/dL")) {
                            mgdLCount++;
                            somaMgdL += valorMedicao;
                            somaMmolL += mgdLParaMmol(valorMedicao);
                        } else if (unidadeMedida != null && unidadeMedida.equals("mmol/L")) {
                            mmolLCount++;
                            somaMmolL += valorMedicao;
                            somaMgdL += mmolParaMgdL(valorMedicao);
                        }

                        temMedicoes = true;
                    }
                }

                if (temMedicoes) {
                    if (prefUnidade.equals("mg/dL")) {
                        int mediaEmMgdL = somaMgdL / (mgdLCount + mmolLCount);
                        media3diasValor.setText("" + mediaEmMgdL + ",0");
                        media3diasUnidade.setText("mg/dL");
                    } else if (prefUnidade.equals("mmol/L")) {
                        int mediaEmMmolL = somaMmolL / (mmolLCount + mgdLCount);
                        media3diasValor.setText("" + mediaEmMmolL + ",0");
                        media3diasUnidade.setText("mmol/L");
                    }
                } else {
                    // Não há medições nos últimos 3 dias
                    media3diasValor.setText("--/--");
                    media3diasUnidade.setText("" + prefUnidade);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Trate o erro aqui se necessário
            }
        });
    }

    private void inflarLayoutGrafico() {
        LinearLayout graficoContainer = findViewById(R.id.estatisticasContainer);
        View view = LayoutInflater.from(this).inflate(R.layout.grafico_estatisticas_inflar, graficoContainer, false);
        graficoContainer.addView(view);

        pegarDadosParaGrafico();
    }

    private void pegarDadosParaGrafico() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("registrosGlicemicos");
        Query query = reference.child(_USERNAME).child("medicoes");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String estadoMedicao = dataSnapshot.child("estadoMedicao").getValue(String.class);

                        switch (estadoMedicao) {
                            case "Baixa":
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

        PieChart pieChart = findViewById(R.id.pieChartInflar);

        ArrayList<PieEntry> estatisticas = new ArrayList<>();
        if (baixo > 0) estatisticas.add(new PieEntry(baixo, "Baixa"));
        if (normal > 0) estatisticas.add(new PieEntry(normal, "Normal"));
        if (preDiabetes > 0) estatisticas.add(new PieEntry(preDiabetes, "Pré-diabetes"));
        if (diabetes > 0) estatisticas.add(new PieEntry(diabetes, "Diabetes"));

        int[] colorArray = new int[] {Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED};

        PieDataSet pieDataSet = new PieDataSet(estatisticas, "Registros");
        pieDataSet.setColors(colorArray);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Registros");
        pieChart.invalidate();
        pieChart.animate();

    }

    private void obterEmostrarMedicoes() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("registrosGlicemicos");
        Query query = reference.child(_USERNAME).child("medicoes");

        query.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    LinearLayout cardContainer = findViewById(R.id.cardContainerTwo);

                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        // Há medições no banco de dados
                        if (dataSnapshot.getChildrenCount() == 1) {
                            // Apenas uma medição
                            DataSnapshot singleSnapshot = dataSnapshot.getChildren().iterator().next();
                            View cardView = criarCardView(singleSnapshot);
                            cardContainer.addView(cardView);
                        } else {
                            // Mais de uma medição
                            addCardsToContainer(dataSnapshot, cardContainer);
                        }
                    } else {
                        // Não há medições
                        addNoDataLayout(cardContainer);
                    }
                } else {
                    Exception exception = task.getException();
                }
            }
        });
    }

    private void addCardsToContainer(DataSnapshot dataSnapshot, LinearLayout cardContainer) {
        int counter = 0;

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            if (counter < 2) {
                // Chamar o método para configurar os elementos do card
                View cardView = criarCardView(snapshot);
                cardContainer.addView(cardView);

                counter++;
            } else {
                break;
            }
        }
    }

    private void addNoDataLayout(LinearLayout cardContainer) {
        View noDataView = LayoutInflater.from(this).inflate(R.layout.layout_no_data, cardContainer, false);
        cardContainer.addView(noDataView);
    }

    private View criarCardView(DataSnapshot snapshot) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.cards_historico_medicoes, null);

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

        return cardView;
    }

}