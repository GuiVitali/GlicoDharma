package com.example.glicodharma;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Medalhas extends Fragment {

    String _USERNAME;

    CardView medalha1medicao;

    LinearLayout semMedalhas;

    public Medalhas() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medalhas, container, false);

        medalha1medicao = view.findViewById(R.id.medalha1medicao);
        medalha1medicao.setVisibility(View.INVISIBLE);

        semMedalhas = view.findViewById(R.id.semMedalhas);
        semMedalhas.setVisibility(View.INVISIBLE);

        SharedPreferences recuperarUsername = requireActivity().getSharedPreferences("salvarUsername", Context.MODE_PRIVATE);
        _USERNAME = recuperarUsername.getString("username", "");

        DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference missoesMesRef = usuariosRef.child(_USERNAME).child("missoesMes");

        missoesMesRef.child("progresso").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int progresso = dataSnapshot.getValue(Integer.class);

                    if (progresso >= 100) {
                        semMedalhas.setVisibility(View.INVISIBLE);
                        exibirMedalha();
                    } else {
                        semMedalhas.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private void exibirMedalha() {
        medalha1medicao.setVisibility(View.VISIBLE);
    }
}