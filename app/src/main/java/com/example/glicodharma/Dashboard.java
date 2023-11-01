package com.example.glicodharma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Dashboard extends AppCompatActivity {

    // Número da aba selecionada. Temos 4 abas, então o valor deve estar entre 1-4. O valor padrão é 1 (home)
    private int selectedTab = 1;

    public String _USERNAME, _NOME, _EMAIL, _TELEFONE, _SENHA; //Global Variables to hold user data inside this activity

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dashboard);

        reference = FirebaseDatabase.getInstance().getReference("users");
        pegarDados();

        // Hooks
        final LinearLayout homeLayout = findViewById(R.id.homeLayout);
        final LinearLayout graphicsLayout = findViewById(R.id.graphicsLayout);
        final LinearLayout notificacaoLayout = findViewById(R.id.notificationLayout);
        final LinearLayout perfilLayout = findViewById(R.id.perfilLayout);

        final ImageView homeImage = findViewById(R.id.homeImage);
        final ImageView graphicsImage = findViewById(R.id.graphicsImage);
        final ImageView notificacaoImage = findViewById(R.id.notificationImage);
        final ImageView perfilImage = findViewById(R.id.perfilImage);

        final TextView homeTxt = findViewById(R.id.homeTxt);
        final TextView graphicsTxt = findViewById(R.id.graphicsTxt);
        final TextView notificacaoTxt = findViewById(R.id.notificationTxt);
        final TextView perfilTxt = findViewById(R.id.perfilTxt);

        // Define o fragmento Home como padrão
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainer, HomeFragment.class, null)
                .commit();

        homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check if home tab is already selected or not
                if (selectedTab != 1) {

                    // set home fragment
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragmentContainer, HomeFragment.class, null)
                            .commit();

                    // unselect other tabs expect home tab
                    graphicsTxt.setVisibility(View.GONE);
                    notificacaoTxt.setVisibility(View.GONE);
                    perfilTxt.setVisibility(View.GONE);

                    graphicsImage.setImageResource(R.drawable.grafico);
                    notificacaoImage.setImageResource(R.drawable.notes);
                    perfilImage.setImageResource(R.drawable.perfil);

                    graphicsLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    notificacaoLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    perfilLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    // select home tab
                    homeTxt.setVisibility(View.VISIBLE);
                    homeImage.setImageResource(R.drawable.home_selected);
                    homeLayout.setBackgroundResource(R.drawable.round_back_home_100);

                    // create animation
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    homeLayout.startAnimation(scaleAnimation);

                    // set 1st tab as selected tab
                    selectedTab = 1;
                }
            }
        });

        graphicsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check if like tab is already selected or not
                if (selectedTab != 2) {

                    // set graphics fragment
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragmentContainer, GraphicsFragment.class, null)
                            .commit();

                    // unselect other tabs expect graphics tab
                    homeTxt.setVisibility(View.GONE);
                    notificacaoTxt.setVisibility(View.GONE);
                    perfilTxt.setVisibility(View.GONE);

                    homeImage.setImageResource(R.drawable.home);
                    notificacaoImage.setImageResource(R.drawable.notes);
                    perfilImage.setImageResource(R.drawable.perfil);

                    homeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    notificacaoLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    perfilLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    // select like tab
                    graphicsTxt.setVisibility(View.VISIBLE);
                    graphicsImage.setImageResource(R.drawable.grafico_select);
                    graphicsLayout.setBackgroundResource(R.drawable.round_back_graphics_100);

                    // create animation
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    graphicsLayout.startAnimation(scaleAnimation);

                    // set 2st tab as selected tab
                    selectedTab = 2;
                }
            }
        });

        notificacaoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check if notificação tab is already selected or not
                if (selectedTab != 3) {

                    // set notificações fragment
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragmentContainer, NotificacoesFragment.class, null)
                            .commit();

                    // unselect other tabs expect notificação tab
                    homeTxt.setVisibility(View.GONE);
                    graphicsTxt.setVisibility(View.GONE);
                    perfilTxt.setVisibility(View.GONE);

                    homeImage.setImageResource(R.drawable.home);
                    graphicsImage.setImageResource(R.drawable.grafico);
                    perfilImage.setImageResource(R.drawable.perfil);

                    homeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    graphicsLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    perfilLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    // select notificação tab
                    notificacaoTxt.setVisibility(View.VISIBLE);
                    notificacaoImage.setImageResource(R.drawable.notes_select);
                    notificacaoLayout.setBackgroundResource(R.drawable.round_back_notificacao_100);

                    // create animation
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    notificacaoLayout.startAnimation(scaleAnimation);

                    // set 3st tab as selected tab
                    selectedTab = 3;
                }
            }
        });

        perfilLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check if perfil tab is already selected or not
                if (selectedTab != 4) {

                    // set perfil fragment
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragmentContainer, PerfilFragment.class, null)
                            .commit();

                    // unselect other tabs expect perfil tab
                    homeTxt.setVisibility(View.GONE);
                    graphicsTxt.setVisibility(View.GONE);
                    notificacaoTxt.setVisibility(View.GONE);

                    homeImage.setImageResource(R.drawable.home);
                    graphicsImage.setImageResource(R.drawable.grafico);
                    notificacaoImage.setImageResource(R.drawable.notes);

                    homeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    graphicsLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    notificacaoLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    // select perfil tab
                    perfilTxt.setVisibility(View.VISIBLE);
                    perfilImage.setImageResource(R.drawable.perfil_selected);
                    perfilLayout.setBackgroundResource(R.drawable.round_back_perfil_100);

                    // create animation
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    perfilLayout.startAnimation(scaleAnimation);

                    // set 4st tab as selected tab
                    selectedTab = 4;
                }
            }
        });

    }

    private void pegarDados() {

        Intent intent = getIntent();
        String whatToDo = intent.getStringExtra("whatToDo");

        if (whatToDo.equals("Login")) {

            _USERNAME = intent.getStringExtra("username");
            _NOME = intent.getStringExtra("nome");
            _EMAIL = intent.getStringExtra("email");
            _TELEFONE = intent.getStringExtra("telefone");
            _SENHA = intent.getStringExtra("senha");

            gerarSharedPreferences();

        } else if (whatToDo.equals("recuperarDados")) {
            recuperarSharedPreferences();
        }

    }

    private void gerarSharedPreferences() {

        SharedPreferences sharedPreferences = getSharedPreferences("LoginDados", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("USERNAME", _USERNAME);
        editor.putString("NOME", _NOME);
        editor.putString("EMAIL", _EMAIL);
        editor.putString("TELEFONE", _TELEFONE);
        editor.putString("SENHA", _SENHA);
        editor.apply();

    }

    private void recuperarSharedPreferences() {

        SharedPreferences sharedPreferences = getSharedPreferences("LoginDados", MODE_PRIVATE);
        _USERNAME = sharedPreferences.getString("USERNAME", ""); // "" é o valor padrão caso não encontre a chave
        _NOME = sharedPreferences.getString("NOME", "");
        _EMAIL = sharedPreferences.getString("EMAIL", "");
        _TELEFONE = sharedPreferences.getString("TELEFONE", "");
        _SENHA = sharedPreferences.getString("SENHA", "");

    }

}