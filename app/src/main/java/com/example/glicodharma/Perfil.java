package com.example.glicodharma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Perfil extends AppCompatActivity {

    TextInputLayout nomeCompleto, email, telefone, senha;
    TextView nomeCompletoLabel, usernameLabel;
    String _USERNAME, _NOME, _EMAIL, _TELEFONE, _SENHA; //Global Variables to hold user data inside this activity

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        reference = FirebaseDatabase.getInstance().getReference("users");

        //Hooks
        nomeCompleto = findViewById(R.id.nome_completo_perfil);
        email = findViewById(R.id.email_perfil);
        telefone = findViewById(R.id.telefone_perfil);
        senha = findViewById(R.id.senha_perfil);
        nomeCompletoLabel = findViewById(R.id.nome_completo_label);
        usernameLabel = findViewById(R.id.username_label);

        //Mostrar Dados
        mostrarDados();
    }

    private void mostrarDados() {

        Intent intent = getIntent();
        _USERNAME = intent.getStringExtra("username");
        _NOME = intent.getStringExtra("nome");
        _EMAIL = intent.getStringExtra("email");
        _TELEFONE = intent.getStringExtra("telefone");
        _SENHA = intent.getStringExtra("senha");

        nomeCompletoLabel.setText(_NOME);
        usernameLabel.setText(_USERNAME);
        nomeCompleto.getEditText().setText(_NOME);
        email.getEditText().setText(_EMAIL);
        telefone.getEditText().setText(_TELEFONE);
        senha.getEditText().setText(_SENHA);
    }

    public void update(View view) {

        Map<String, Object> dados = new HashMap<>();

        dados.put("nome", nomeCompleto.getEditText().getText().toString());
        dados.put("username", usernameLabel.getText().toString());
        dados.put("email", email.getEditText().getText().toString());
        dados.put("telefone", telefone.getEditText().getText().toString());
        dados.put("senha", senha.getEditText().getText().toString());

        //Verifica se o nome ou a senha do usuário foi alterado
        boolean nomeMudou = seNomeMudou();
        boolean senhaMudou = seSenhaMudou();

        //Atualiza os dados no Firebase
        if (nomeMudou || senhaMudou) {
            reference.child(_USERNAME).setValue(dados);
            _NOME = nomeCompleto.getEditText().getText().toString();
            _SENHA = senha.getEditText().getText().toString();
        }

        Toast.makeText(this, "Data has been updated", Toast.LENGTH_LONG).show();

    }

    private boolean seNomeMudou() {

        return !_NOME.equals(nomeCompleto.getEditText().getText().toString());

    }

    private boolean seSenhaMudou() {

        return !_SENHA.equals(senha.getEditText().getText().toString());

    }

}