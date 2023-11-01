package com.example.glicodharma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class VerificarTelefone extends AppCompatActivity {

    String verificarCodeBySystem, whatToDo, telefone, nome, username, email, senha;
    FirebaseAuth mAuth;
    Button verificar_btn;
    PinView telefoneEnteredByTheUser;
    ProgressBar progressBar;
    FirebaseDatabase rootNode;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificar_telefone);

        //Hooks
        mAuth = FirebaseAuth.getInstance();
        verificar_btn = findViewById(R.id.verificar_btn);
        telefoneEnteredByTheUser = findViewById(R.id.pin_view);
        progressBar = findViewById(R.id.progress_bar);

        progressBar.setVisibility(View.GONE);

        telefone = getIntent().getStringExtra("telefone");
        nome = getIntent().getStringExtra("nome");
        username = getIntent().getStringExtra("username");
        email = getIntent().getStringExtra("email");
        senha = getIntent().getStringExtra("senha");
        whatToDo = getIntent().getStringExtra("whatToDo");

        sendVerificationCodeToUser();
    }

    private void sendVerificationCodeToUser() {
        // [START start_phone_auth]
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+55" + telefone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        // [END start_phone_auth]
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            //Get the code in global variable
            verificarCodeBySystem = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();
            if(code!=null){
                progressBar.setVisibility(View.VISIBLE);
                verificarCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(VerificarTelefone.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verificarCode(String codeByUser) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificarCodeBySystem, codeByUser);
        signInTheUserByCredentials(credential);
    }

    private void signInTheUserByCredentials(PhoneAuthCredential credential) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerificarTelefone.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            if (whatToDo.equals("atualizarDados")) {
                                atualizarDados();
                            } else if (whatToDo.equals("criarNovosDados")) {
                                salvarDadosNovoUser();
                            }


                        } else {
                            Toast.makeText(VerificarTelefone.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void atualizarDados() {

        Intent intent = new Intent(getApplicationContext(), NovaSenha.class);
        intent.putExtra("telefone",telefone);
        startActivity(intent);
        finish();

    }

    private void salvarDadosNovoUser() {

        //Recuperar os dados do SharedPreferences
        /*SharedPreferences sharedPreferences = getSharedPreferences("my_shared_preferences", MODE_PRIVATE);
        String nome = sharedPreferences.getString("nome", "");
        String username = sharedPreferences.getString("username", "");
        String email = sharedPreferences.getString("email", "");
        String telefone = sharedPreferences.getString("telefone", "");
        String senha = sharedPreferences.getString("senha", "");*/
        SharedPreferences toLogado = getSharedPreferences("toLogado", MODE_PRIVATE);
        SharedPreferences.Editor editor = toLogado.edit();
        editor.putBoolean("logado", true);
        editor.apply();

        //Salvar os dados no FireBase
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");

        UserHelperClass helperClass = new UserHelperClass(nome, username, email, telefone, senha);
        reference.child(username).setValue(helperClass);

        Toast.makeText(VerificarTelefone.this, "Sua Conta foi criada com sucesso!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getApplicationContext(), TelaInicialInfo.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("nome", nome);
        intent.putExtra("username", username);
        intent.putExtra("email", email);
        intent.putExtra("telefone", telefone);
        intent.putExtra("senha", senha);
        intent.putExtra("whatToDo", "Login");
        startActivity(intent);

    }

    public void verificar(View view) {

        String code = telefoneEnteredByTheUser.getText().toString();

        if (code.isEmpty() || code.length() < 6) {
            telefoneEnteredByTheUser.setError("Código Incorreto...");
            telefoneEnteredByTheUser.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        verificarCode(code);
    }
}