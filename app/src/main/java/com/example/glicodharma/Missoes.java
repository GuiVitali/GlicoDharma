package com.example.glicodharma;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Missoes extends Fragment {

    TextView tvMissaoMes, tvMissaoDia1, tvMissaoDia2, tvMissaoDia3;

    ProgressBar progressBarMes, progressBarMissao1, progressBarMissao2, progressBarMissao3;

    String _USERNAME;

    public Missoes() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_missoes, container, false);

        tvMissaoMes = view.findViewById(R.id.tvMissaoMes);
        tvMissaoDia1 = view.findViewById(R.id.tvMissaoDia1);
        tvMissaoDia2 = view.findViewById(R.id.tvMissaoDia2);
        tvMissaoDia3 = view.findViewById(R.id.tvMissaoDia3);

        progressBarMes = view.findViewById(R.id.progressBarMes);
        progressBarMissao1 = view.findViewById(R.id.progressBarMissao1);
        progressBarMissao2 = view.findViewById(R.id.progressBarMissao2);
        progressBarMissao3 = view.findViewById(R.id.progressBarMissao3);

        // Recupera o Username
        SharedPreferences recuperarUsername = requireActivity().getSharedPreferences("salvarUsername", Context.MODE_PRIVATE);
        _USERNAME = recuperarUsername.getString("username", "");

        // Verifica se o usuário atual tem missões do dia no banco
        verificarMissoesBanco();

        verificarMudancaMes();
        
        // Verificar se é um novo dia
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("minhas_missoes", Context.MODE_PRIVATE);
        String dataUltimaAtualizacao = sharedPreferences.getString("data_ultima_atualizacao", "");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        String dataAtual = dateFormat.format(calendar.getTime());

        if (!dataUltimaAtualizacao.equals(dataAtual)) {
            // É um novo dia, sortear novas missões e atualizar o SharedPref
            sortearNovasMissoes();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("data_ultima_atualizacao", dataAtual);
            editor.apply();
        } else {
            // As missões de hoje já foram definidas!!
            tvMissaoDia1.setText(sharedPreferences.getString("missao1", ""));
            tvMissaoDia2.setText(sharedPreferences.getString("missao2", ""));
            tvMissaoDia3.setText(sharedPreferences.getString("missao3", ""));
        }

        return view;
    }

    private void verificarMudancaMes() {
        Calendar calendar = Calendar.getInstance();
        int mesAtual = calendar.get(Calendar.MONTH);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("minhas_missoes", Context.MODE_PRIVATE);
        int ultimoMes = sharedPreferences.getInt("ultimo_mes", -1);

        if (mesAtual != ultimoMes) {
            // O mês mudou, sorteiar uma nova missão do mês
            sortearNovasMissaoMes();

            // Atualizar o último mês no SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("ultimo_mes", mesAtual);
            editor.apply();
        } else {
            tvMissaoMes.setText(sharedPreferences.getString("missaoMes", ""));
        }
    }

    private void sortearNovasMissaoMes() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference missoesMes = database.getReference("missoesMes");
        DatabaseReference usuariosRef = database.getReference("users");

        missoesMes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<DataSnapshot> missaoMesSnapshots = new ArrayList<>();

                for (DataSnapshot missaoSnapshot : dataSnapshot.getChildren()) {
                    missaoMesSnapshots.add(missaoSnapshot);
                }

                // Seleciona uma missão aleatória
                Collections.shuffle(missaoMesSnapshots);
                DataSnapshot missaoMes = missaoMesSnapshots.get(0);

                missaoMes.getRef().child("progresso").setValue(0);

                // Atualiza a missão do mês
                tvMissaoMes.setText(missaoMes.child("descricao").getValue(String.class));

                // Armazena a nova missão no SharedPreferences
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("minhas_missoes", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("missaoMes", missaoMes.child("descricao").getValue(String.class));
                editor.apply();

                // Atualiza a missão no Firebase para o usuário atual
                String usuarioAtual = _USERNAME;

                DatabaseReference usuarioMissaoMesRef = usuariosRef.child(usuarioAtual).child("missoesMes");
                String descricao = missaoMes.child("descricao").getValue(String.class);
                int progresso = 0;

                usuarioMissaoMesRef.child("descricao").setValue(descricao);
                usuarioMissaoMesRef.child("progresso").setValue(progresso);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void verificarMissoesBanco() {
        DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference missoesDiaRef = usuariosRef.child(_USERNAME).child("missoesDia");

        missoesDiaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    atualizarProgressoMissoes();
                    atualizarProgressoMissaMes();
                } else {
                    sortearNovasMissoes();
                    sortearNovasMissaoMes();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

    private void atualizarProgressoMissaMes() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference missaoMesRef = usersRef.child(_USERNAME).child("missoesMes");

        missaoMesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String descricaoMissao = dataSnapshot.child("descricao").getValue(String.class);
                int progresso = dataSnapshot.child("progresso").getValue(Integer.class);

                progressBarMes.setProgress(progresso);
                // Atualiza a barra de progresso da missão do mês
                /*if (descricaoMissao.equals(tvMissaoMes.getText().toString())) {
                    progressBarMes.setProgress(progresso);
                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

    private void atualizarProgressoMissoes() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference missoesDia = usersRef.child(_USERNAME).child("missoesDia");

        missoesDia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot missaoSnapshot : dataSnapshot.getChildren()) {
                    String descricaoMissao = missaoSnapshot.child("descricao").getValue(String.class);
                    int progresso = missaoSnapshot.child("progresso").getValue(Integer.class);

                    // Atualiza as barras de progresso
                    if (descricaoMissao.equals(tvMissaoDia1.getText().toString())) {
                        progressBarMissao1.setProgress(progresso);
                    } else if (descricaoMissao.equals(tvMissaoDia2.getText().toString())) {
                        progressBarMissao2.setProgress(progresso);
                    } else if (descricaoMissao.equals(tvMissaoDia3.getText().toString())) {
                        progressBarMissao3.setProgress(progresso);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sortearNovasMissoes() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference missoesDia = database.getReference("missoesDia");

        DatabaseReference usuariosRef = database.getReference("users");

        missoesDia.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<DataSnapshot> missaoSnapshots = new ArrayList<>();

                for (DataSnapshot missaoSnapshot : dataSnapshot.getChildren()) {
                    missaoSnapshots.add(missaoSnapshot);
                }

                // Seleciona três missões aleatórias
                Collections.shuffle(missaoSnapshots);
                List<DataSnapshot> missoesDiarias = missaoSnapshots.subList(0, 3);

                // Define o progresso para 0
                for (DataSnapshot missaoSnapshot : missoesDiarias) {
                    missoesDia.child(missaoSnapshot.getKey()).child("progresso").setValue(0);
                }

                // Atualiza as missões diárias
                tvMissaoDia1.setText(missoesDiarias.get(0).child("descricao").getValue(String.class));
                tvMissaoDia2.setText(missoesDiarias.get(1).child("descricao").getValue(String.class));
                tvMissaoDia3.setText(missoesDiarias.get(2).child("descricao").getValue(String.class));

                // Armazena as novas missões no SharedPreferences
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("minhas_missoes", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("missao1", missoesDiarias.get(0).child("descricao").getValue(String.class));
                editor.putString("missao2", missoesDiarias.get(1).child("descricao").getValue(String.class));
                editor.putString("missao3", missoesDiarias.get(2).child("descricao").getValue(String.class));
                editor.apply();

                // Atualiza as missões no Firebase para o usuário atual
                String usuarioAtual = _USERNAME;
                //tvMissaoMes.setText(usuarioAtual);

                // Verifica se a seção "missoesDia" existe para o usuário atual
                usuariosRef.child(usuarioAtual).child("missoesDia").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            // Se a seção não existir, crie-a
                            usuariosRef.child(usuarioAtual).child("missoesDia").setValue(new HashMap<>());
                        }

                        // Atualize as missões para o usuário atual
                        DatabaseReference usuarioMissoesRef = usuariosRef.child(usuarioAtual).child("missoesDia");
                        for (DataSnapshot missaoSnapshot : missoesDiarias) {
                            String descricao = missaoSnapshot.child("descricao").getValue(String.class);
                            int progresso = 0;
                            usuarioMissoesRef.child(descricao).child("descricao").setValue(descricao);
                            usuarioMissoesRef.child(descricao).child("progresso").setValue(progresso);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors here
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors here
            }
        });
    }
}