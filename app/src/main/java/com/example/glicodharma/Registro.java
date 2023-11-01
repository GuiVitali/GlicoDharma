package com.example.glicodharma;

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
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registro extends AppCompatActivity {

    TextInputLayout regNome, regUsername, regEmail, regTelefone, regSenha;

    TextView logoText, sloganText;

    ImageView image;
    Button regBtn, regToLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registro);

        // Hooks para todos os elementos xml em activity_registro.xml
        image = findViewById(R.id.logo_img);
        logoText = findViewById(R.id.logo_nome);
        sloganText = findViewById(R.id.slogan_nome);
        regNome = findViewById(R.id.reg_nome);
        regUsername = findViewById(R.id.reg_username);
        regEmail = findViewById(R.id.reg_email);
        regTelefone = findViewById(R.id.reg_telefone);
        regSenha = findViewById(R.id.reg_senha);
        regBtn = findViewById(R.id.reg_btn);
        regToLoginBtn = findViewById(R.id.reg_login_btn);
    }

    private Boolean validarNome() {
        String val = regNome.getEditText().getText().toString();

        if(val.isEmpty()) {
            regNome.setError("O campo não pode ser vazio");
            return false;
        } else {
            regNome.setError(null);
            regNome.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validarUsername() {
        String val = regUsername.getEditText().getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";

        if(val.isEmpty()) {
            regUsername.setError("O campo não pode ser vazio");
            return false;
        } else if(val.length() >= 15) {
            regUsername.setError("Username grande demais");
            return false;
        } else if(!val.matches(noWhiteSpace)) {
            regUsername.setError("Espaços em branco não são permitidos");
            return false;
        } else {
            regUsername.setError(null);
            regUsername.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validarEmail() {
        String val = regEmail.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(val.isEmpty()) {
            regEmail.setError("O campo não pode ser vazio");
            return false;
        } else if(!val.matches(emailPattern)) {
            regEmail.setError("Endereço de email inválido");
            return false;
        } else {
            regEmail.setError(null);
            regEmail.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validarTelefone() {
        String val = regTelefone.getEditText().getText().toString();

        if(val.isEmpty()) {
            regTelefone.setError("O campo não pode ser vazio");
            return false;
        } else {
            regTelefone.setError(null);
            regTelefone.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validarSenha() {
        String val = regSenha.getEditText().getText().toString();
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
            regSenha.setError("O campo não pode ser vazio");
            return false;
        } else if(!val.matches(senhaVal)) {
            regSenha.setError("A Senha é muito fraca");
            return false;
        } else {
            regSenha.setError(null);
            regSenha.setErrorEnabled(false);
            return true;
        }
    }

    public void cadastrarUsuario(View view) {
        if(!validarNome() | !validarSenha() | !validarTelefone() | !validarEmail() | !validarUsername()) {
            return;
        }

        String telefone = regTelefone.getEditText().getText().toString();
        String nome = regNome.getEditText().getText().toString();
        String username = regUsername.getEditText().getText().toString();
        String email = regEmail.getEditText().getText().toString();
        String senha = regSenha.getEditText().getText().toString();

        if (telefone.startsWith("+55")) {
            telefone = telefone.substring(3); // Remove os primeiros 3 caracteres
        } else if (telefone.startsWith("55")) {
            telefone = telefone.substring(2); // Remove os primeiros 2 caracteres
        }

        Intent intent = new Intent(getApplicationContext(),VerificarTelefone.class);
        intent.putExtra("telefone",telefone);
        intent.putExtra("nome",nome);
        intent.putExtra("username",username);
        intent.putExtra("email",email);
        intent.putExtra("senha",senha);
        intent.putExtra("whatToDo", "criarNovosDados");
        startActivity(intent);

        //Salvar os dados em um SharedPreferences
        /*SharedPreferences sharedPreferences = getSharedPreferences("my_shared_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("nome", regNome.getEditText().getText().toString());
        editor.putString("username", regUsername.getEditText().getText().toString());
        editor.putString("email", regEmail.getEditText().getText().toString());
        editor.putString("telefone", regTelefone.getEditText().getText().toString());
        editor.putString("senha", regSenha.getEditText().getText().toString());
        editor.commit();*/

        Toast.makeText(this, "Your Account has been created sucessfully!", Toast.LENGTH_SHORT).show();

        /*Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();*/
    }

    public void voltar(View view) {
        Intent intent = new Intent(Registro.this, Login.class);

        Pair[] pairs = new Pair[7];

        pairs[0] = new Pair<View, String>(image,"logo_img");
        pairs[1] = new Pair<View, String>(logoText,"logo_text");
        pairs[2] = new Pair<View, String>(sloganText,"logo_desc");
        pairs[3] = new Pair<View, String>(regUsername, "username_tran");
        pairs[4] = new Pair<View, String>(regSenha, "senha_tran");
        pairs[5] = new Pair<View, String>(regBtn, "button_tran");
        pairs[6] = new Pair<View, String>(regToLoginBtn, "login_entrar_tran");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Registro.this,pairs);
        startActivity(intent, options.toBundle());
    }
}