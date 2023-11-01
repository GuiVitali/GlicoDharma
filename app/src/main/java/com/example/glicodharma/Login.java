package com.example.glicodharma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Login extends AppCompatActivity {

    Button callRegistro, login_btn;
    ImageView image;
    TextView logoText, sloganText;
    TextInputLayout username, senha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        //Hooks
        callRegistro = findViewById(R.id.registro_tela);
        image = findViewById(R.id.logo_img);
        logoText = findViewById(R.id.logo_nome);
        sloganText = findViewById(R.id.slogan_nome);
        username = findViewById(R.id.username);
        senha = findViewById(R.id.senha);
        login_btn = findViewById(R.id.login_btn);
    }

    private Boolean validarUsername() {
        String val = username.getEditText().getText().toString();

        if(val.isEmpty()) {
            username.setError("O campo não pode ser vazio");
            return false;
        } else {
            username.setError(null);
            username.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validarSenha() {
        String val = senha.getEditText().getText().toString();

        if(val.isEmpty()) {
            senha.setError("O campo não pode ser vazio");
            return false;
        } else {
            senha.setError(null);
            senha.setErrorEnabled(false);
            return true;
        }
    }

    public void loginUser(View view) {
        //Validar Login
        if (!validarUsername() | !validarSenha()) {
            return;
        } else {
            isUser();
        }
    }

    private void isUser() {
        final String userEnteredUsername = username.getEditText().getText().toString().trim();
        final String userEnteredSenha = senha.getEditText().getText().toString().trim();
        final String senhaCriptografada = criptografarSenha(userEnteredSenha);


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        Query checkUser = reference.orderByChild("username").equalTo(userEnteredUsername);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    username.setError(null);
                    username.setErrorEnabled(false);

                    String senhaFromDB = dataSnapshot.child(userEnteredUsername).child("senha").getValue(String.class);

                    if(senhaFromDB.equals(senhaCriptografada)){

                        username.setError(null);
                        username.setErrorEnabled(false);

                        String nomeFromDB = dataSnapshot.child(userEnteredUsername).child("nome").getValue(String.class);
                        String usernameFromDB = dataSnapshot.child(userEnteredUsername).child("username").getValue(String.class);
                        String telefoneFromDB = dataSnapshot.child(userEnteredUsername).child("telefone").getValue(String.class);
                        String emailFromDB = dataSnapshot.child(userEnteredUsername).child("email").getValue(String.class);

                        SharedPreferences toLogado = getSharedPreferences("toLogado", MODE_PRIVATE);
                        SharedPreferences.Editor editor = toLogado.edit();
                        editor.putBoolean("logado", true);
                        editor.apply();

                        Intent intent = new Intent(getApplicationContext(),Dashboard.class);

                        intent.putExtra("nome", nomeFromDB);
                        intent.putExtra("username", usernameFromDB);
                        intent.putExtra("email", emailFromDB);
                        intent.putExtra("telefone", telefoneFromDB);
                        intent.putExtra("senha", senhaFromDB);
                        intent.putExtra("whatToDo", "Login");

                        startActivity(intent);

                    } else {
                        senha.setError("Senha Incorreta");
                        senha.requestFocus();
                    }
                } else {
                    username.setError("Ops! Parece que esse usuário não existe...");
                    username.requestFocus();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String criptografarSenha(String userEnteredSenha) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(userEnteredSenha.getBytes());

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

    //Call Tela de Registro
    public void callRegistro(View view) {
        Intent intent = new Intent(Login.this, Registro.class);

        Pair[] pairs = new Pair[7];

        pairs[0] = new Pair<View, String>(image,"logo_img");
        pairs[1] = new Pair<View, String>(logoText,"logo_text");
        pairs[2] = new Pair<View, String>(sloganText,"logo_desc");
        pairs[3] = new Pair<View, String>(username,"username_tran");
        pairs[4] = new Pair<View, String>(senha,"senha_tran");
        pairs[5] = new Pair<View, String>(login_btn,"button_tran");
        pairs[6] = new Pair<View, String>(callRegistro,"login_entrar_tran");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this,pairs);
        startActivity(intent, options.toBundle());
    }

    public void callEsqueceuSenha(View view) {
        startActivity(new Intent(getApplicationContext(), EsqueceuSenha.class));
    }

}