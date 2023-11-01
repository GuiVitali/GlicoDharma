package com.example.glicodharma;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextInputLayout nomeCompleto, email, telefone, senha;

    TextView nomeCompletoLabel, usernameLabel;

    String nomeG, usernameG, emailG, telefoneG, senhaG;

    Button btnAtualizar;

    DatabaseReference reference;

    public PerfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        reference = FirebaseDatabase.getInstance().getReference("users");

        //Hooks
        nomeCompleto = view.findViewById(R.id.nome_completo_perfil);
        email = view.findViewById(R.id.email_perfil);
        telefone = view.findViewById(R.id.telefone_perfil);
        senha = view.findViewById(R.id.senha_perfil);
        nomeCompletoLabel = view.findViewById(R.id.nome_completo_label);
        usernameLabel = view.findViewById(R.id.username_label);
        btnAtualizar = view.findViewById(R.id.btn_atualizar);

        nomeG = ((Dashboard) requireActivity())._NOME;
        usernameG = ((Dashboard) requireActivity())._USERNAME;
        emailG = ((Dashboard) requireActivity())._EMAIL;
        telefoneG = ((Dashboard) requireActivity())._TELEFONE;
        senhaG = ((Dashboard) requireActivity())._SENHA;

        CardView cardMetas = view.findViewById(R.id.cardMetas);

        //Mostrar Dados
        mostrarDados();

        btnAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizarDados();
            }
        });

        cardMetas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TabsMetas.class);

                intent.putExtra("username", usernameG);

                startActivity(intent);
            }
        });

        return view;
    }

    private void mostrarDados() {

        nomeCompletoLabel.setText(nomeG);
        usernameLabel.setText(usernameG);
        nomeCompleto.getEditText().setText(nomeG);
        email.getEditText().setText(emailG);
        telefone.getEditText().setText(telefoneG);
        senha.getEditText().setText(senhaG);

    }

    public void atualizarDados() {

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
            reference.child(usernameG).setValue(dados);
            nomeG = nomeCompleto.getEditText().getText().toString();
            senhaG = senha.getEditText().getText().toString();
        }

        Toast.makeText(requireActivity(), "Os dados foram atualizados", Toast.LENGTH_LONG).show();

    }

    private boolean seNomeMudou() {

        return !nomeG.equals(nomeCompleto.getEditText().getText().toString());

    }

    private boolean seSenhaMudou() {

        return !senhaG.equals(senha.getEditText().getText().toString());

    }
}