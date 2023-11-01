package com.example.glicodharma;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class NovaSenha extends AppCompatActivity {

    TextInputLayout novaSenha, confirmarSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_senha);

        novaSenha = findViewById(R.id.senha_atualizar);
        confirmarSenha = findViewById(R.id.senha_atualizar_confirmar);
    }

    public void mudarSenha(View view) {

        if (!validarSenha() | !validarConfirmarSenha()) {
            return;
        }

        String _novaSenha = novaSenha.getEditText().getText().toString().trim();
        String _telefone = getIntent().getStringExtra("telefone");

        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        //eference.child(_telefone).child("senha").setValue(_novaSenha);
        ////////////////////////////////////////////////////////////////////////////////////////////

        //Atualizar dados no FireBase
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        Query checkUser = reference.orderByChild("telefone").equalTo(_telefone);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String usernameFromDb = childSnapshot.child("username").getValue(String.class);
                    //Log.i(TAG, "onDataChange: FUNCIONOU" + usernameFromDb);
                    String senhaCriptografada = criptografarSenha(_novaSenha);
                    reference.child(usernameFromDb).child("senha").setValue(_novaSenha);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////
       /* DataSnapshot snapshot = checkUser.get().getResult();
        if (snapshot.exists()) {
            // Get the user data
            String usernameFromDb = snapshot.getValue(String.class);
            reference.child(usernameFromDb).child("senha").setValue(_novaSenha);
        }*/

        /*DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query oneRef = rootRef.orderByChild("telefone").equalTo(_telefone);

        oneRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String senha = userSnapshot.child("senha").getValue(String.class);
                    System.out.println("A senha do usuário é: " + senha);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Erro ao ler dados do FireBase: " + databaseError.getCode());
            }
        });*/

        startActivity(new Intent(getApplicationContext(), EsqueceuSenhaMensagemSucesso.class));
        finish();
    }

    private String criptografarSenha(String novaSenha) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(novaSenha.getBytes());

            // Converter o array de bytes em uma representação hexadecimal
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    private boolean validarSenha() {
        String val = novaSenha.getEditText().getText().toString();
        String senhaVal = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z)" +          //at least 1 lower case letter
                //"?=.*[A-Z]" +           //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +     //any letter
                "(?=.*[@#$%^&+=])" +   //at least 1 special character
                "(?=\\S+$)" +          //no white spaces
                ".{4,}" +              //at least 4 characters
                "$";

        if(val.isEmpty()) {
            novaSenha.setError("Field cannot be empty");
            return false;
        } else if(!val.matches(senhaVal)) {
            novaSenha.setError("Password is too weak");
            return false;
        } else {
            novaSenha.setError(null);
            novaSenha.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validarConfirmarSenha() {
        String val = novaSenha.getEditText().getText().toString();

        if(val.isEmpty()) {
            confirmarSenha.setError("Field cannot be empty");
            return false;
        } else if(!val.equals(confirmarSenha.getEditText().getText().toString())) {
            confirmarSenha.setError("Password different");
            return false;
        } else {
            confirmarSenha.setError(null);
            confirmarSenha.setErrorEnabled(false);
            return true;
        }
    }
}