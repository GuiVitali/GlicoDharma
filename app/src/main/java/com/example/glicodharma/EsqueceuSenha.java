package com.example.glicodharma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class EsqueceuSenha extends AppCompatActivity {

    TextInputLayout numero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueceu_senha);

        numero = findViewById(R.id.numero);
    }

    public void verificarNumero(View view) {
        if(!validarTelefone()) {
            return;
        }

        //Get dados
        String num = numero.getEditText().getText().toString().trim();

        //Check weather User exist or not in database
        Query checkUser = FirebaseDatabase.getInstance().getReference("users").orderByChild("telefone").equalTo(num);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //If número exists then call OTP to verify that is his/her phone number
                if (dataSnapshot.exists()) {
                    numero.setError(null);
                    numero.setErrorEnabled(false);

                    Intent intent = new Intent(getApplicationContext(), VerificarTelefone.class);
                    intent.putExtra("telefone", num);
                    intent.putExtra("whatToDo", "atualizarDados");
                    startActivity(intent);
                    finish();
                } else {
                    numero.setError("No such user exist!");
                    numero.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private Boolean validarTelefone() {
        String val = numero.getEditText().getText().toString();

        if(val.isEmpty()) {
            numero.setError("Field cannot be empty");
            return false;
        } else {
            numero.setError(null);
            numero.setErrorEnabled(false);
            return true;
        }
    }
}