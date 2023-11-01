package com.example.glicodharma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kevalpatel2106.rulerpicker.RulerValuePicker;
import com.kevalpatel2106.rulerpicker.RulerValuePickerListener;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NovoRegistroGlicemico extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    RulerValuePicker rulerValuePicker;

    TextView valorMedicao, tipoTxt, estadoTxt, estadoNumeroTxt, dataTxt, horaTxt;

    String _USERNAME, valorFinalMedicao;

    Button salvarRegistro;

    int mgOummol = 1;

    FirebaseDatabase rootNode;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_registro);

        // Hooks
        ImageView leftIcon = findViewById(R.id.left_icon);
        ImageView rightIcon = findViewById(R.id.right_icon);
        TextView title = findViewById(R.id.tollbar_title);
        CardView tipoMedicao = findViewById(R.id.tipoMedicao);
        CardView data = findViewById(R.id.data);
        CardView hora = findViewById(R.id.hora);
        MaterialButtonToggleGroup toggleGroup = findViewById(R.id.toggleButton);
        MaterialButton button1 = findViewById(R.id.button1);
        MaterialButton button2 = findViewById(R.id.button2);

        rulerValuePicker = (RulerValuePicker) findViewById(R.id.ruler_picker);
        rulerValuePicker.selectValue(100);

        valorMedicao = (TextView) findViewById(R.id.valorMedicao);
        tipoTxt = findViewById(R.id.tipoTxt);
        estadoTxt = findViewById(R.id.estadoTxt);
        estadoNumeroTxt = findViewById(R.id.estadoNumeroTxt);
        dataTxt = findViewById(R.id.dataTxt);
        horaTxt = findViewById(R.id.horaTxt);

        salvarRegistro = findViewById(R.id.bottomButton);

        // Definir o botão 1 como selecionado por padrão
        button1.setChecked(true);
        button1.setBackgroundColor(0xFF4CAF50);
        button1.setTextColor(0xFFFFFFFF);

        // Definir os elementos da tollbar
        title.setText("Novo Registro");
        rightIcon.setImageDrawable(getResources().getDrawable(R.drawable.baseline_question_mark_24));

        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Voltar
            }
        });

        rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Definir Alarme
            }
        });

        tipoMedicao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarMenu(v);
            }
        });

        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Selecione a Data")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        String data = new SimpleDateFormat("dd-MM-yyy", Locale.getDefault()).format(new Date(selection));
                        dataTxt.setText("" + data);
                    }
                });
                materialDatePicker.show(getSupportFragmentManager(), "tag");
            }
        });

        hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setHour(12)
                        .setMinute(0)
                        .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                        .setTitleText("Selecione a Hora")
                        .build();
                timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        horaTxt.setText(MessageFormat.format("{0}:{1}", String.format(Locale.getDefault(), "%02d", timePicker.getHour()), String.format(Locale.getDefault(), "%02d", timePicker.getMinute())));
                    }
                });
                timePicker.show(getSupportFragmentManager(), "tag");
            }
        });

        toggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    // Define a cor do botão selecionado
                    MaterialButton checkedButton = findViewById(checkedId);
                    checkedButton.setBackgroundColor(0xFF4CAF50);
                    checkedButton.setTextColor(0xFFFFFFFF);

                    // Verifica qual botão foi selecionado
                    if (checkedId == R.id.button1) {
                        // Botão (mg/dL) foi selecionado
                        rulerValuePicker.setMinMaxValue(18, 630);
                        rulerValuePicker.selectValue(100);
                        mgOummol = 1;
                    } else if (checkedId == R.id.button2) {
                        // Botão (mmol/L) foi selecionado
                        rulerValuePicker.setMinMaxValue(1, 35);
                        rulerValuePicker.selectValue(5);
                        valorMedicao.setText("5");
                        mgOummol = 2;
                    }
                } else {
                    // Define a cor do botão não selecionado
                    MaterialButton uncheckedButton = findViewById(checkedId);
                    uncheckedButton.setBackgroundColor(0xFFFFFFFF);
                    uncheckedButton.setTextColor(0xFF000000);
                }
            }
        });

        rulerValuePicker.setValuePickerListener(new RulerValuePickerListener() {
            @Override
            public void onValueChange(int selectedValue) {
                valorMedicao.setText("" + selectedValue);
                valorFinalMedicao = "" + selectedValue;
            }

            @Override
            public void onIntermediateValueChange(int selectedValue) {
                if (mgOummol == 1) {
                    reguaMgdl(selectedValue);
                } else if (mgOummol == 2) {
                    reguaMmol(selectedValue);
                }
            }
        });

        salvarRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarRegistro();
            }
        });
    }

    private void mostrarMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.tipo_medicao);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.item_1) {
            tipoTxt.setText("Antes Café");
            return true;
        } else if (itemId == R.id.item_2) {
            tipoTxt.setText("Depois Café");
            return true;
        } else if (itemId == R.id.item_3) {
            tipoTxt.setText("Antes Almoço");
            return true;
        } else if (itemId == R.id.item_4) {
            tipoTxt.setText("Depois Almoço");
            return true;
        } else if (itemId == R.id.item_5) {
            tipoTxt.setText("Antes Jantar");
            return true;
        } else if (itemId == R.id.item_6) {
            tipoTxt.setText("Depois Jantar");
            return true;
        } else if (itemId == R.id.item_7) {
            tipoTxt.setText("Extra");
            return true;
        } else if (itemId == R.id.item_8) {
            tipoTxt.setText("Antes dormir");
            return true;
        } else {
            return false;
        }
    }

    private void reguaMgdl(int selectedValue) {
        valorMedicao.setText("" + selectedValue);

        if (selectedValue < 72) {
            estadoTxt.setText("Baixa");
            estadoNumeroTxt.setText("<72");
        } else if (selectedValue >= 73 && selectedValue < 99) {
            estadoTxt.setText("Normal");
            estadoNumeroTxt.setText("72~99");
        } else if (selectedValue >= 99 && selectedValue < 126) {
            estadoTxt.setText("Pré-diabetes");
            estadoNumeroTxt.setText("99~126");
        } else if (selectedValue >= 126) {
            estadoTxt.setText("Diabetes");
            estadoNumeroTxt.setText("≥126");
        }
    }

    private void reguaMmol(int selectedValue) {
        valorMedicao.setText("" + selectedValue);

        if (selectedValue < 4) {
            estadoTxt.setText("Baixa");
            estadoNumeroTxt.setText("<4");
        } else if (selectedValue >= 4 && selectedValue < 5.5) {
            estadoTxt.setText("Normal");
            estadoNumeroTxt.setText("4~5,5");
        } else if (selectedValue >= 5.5 && selectedValue < 7) {
            estadoTxt.setText("Pré-diabetes");
            estadoNumeroTxt.setText("5,5~7");
        } else if (selectedValue >= 7) {
            estadoTxt.setText("Diabetes");
            estadoNumeroTxt.setText("≥7");
        }
    }

    private void salvarRegistro() {

        Intent intent = getIntent();
        _USERNAME = intent.getStringExtra("username");
        //valorMedicao.setText("" + _USERNAME);

        //Salvar os dados no FireBase
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("registrosGlicemicos");

        // Pegar todos os dados
        String data = dataTxt.getText().toString();
        String hora = horaTxt.getText().toString();
        String tipo = tipoTxt.getText().toString();
        String unidadeMedida = (mgOummol == 1) ? "mg/dL" : "mmol/L"; // Determina a unidade de medida
        String valorMedicao = valorFinalMedicao;
        String estadoMedicao = estadoTxt.getText().toString();

        // Gerar um ID único para cada medição
        String medicaoId = reference.child(_USERNAME).child("medicoes").push().getKey();

        MedicaoHelperClass medicaoHelperClass = new MedicaoHelperClass(_USERNAME, data, hora, tipo, unidadeMedida, valorMedicao, estadoMedicao);
        reference.child(_USERNAME).child("medicoes").child(medicaoId).setValue(medicaoHelperClass);

        // Atualizar progresso nas missões
        atualizarMissoesDia("Faça 4 registros", 25);
        atualizarMissaoMes("Faça o seu 1° registro", 100);

        if (tipo.equals("Antes Café")) {
            atualizarMissoesDia("Faça um registro antes do café", 100);
        } else if (tipo.equals("Antes dormir")) {
            atualizarMissoesDia("Faça um registro antes de dormir", 100);
        }

        Intent intent2 = new Intent(getApplicationContext(),NovoRegistroGlicemicoResultado.class);
        intent2.putExtra("unidadeMedida", unidadeMedida);
        intent2.putExtra("valorMedicao", valorMedicao);
        intent2.putExtra("estadoMedicao", estadoMedicao);
        intent2.putExtra("estadoMedicaoNumero", estadoNumeroTxt.getText().toString());

        startActivity(intent2);

    }

    private void atualizarMissoesDia(String descricaoMissao, int novoProgresso) {
        DatabaseReference usersRef = rootNode.getReference("users");
        DatabaseReference missoesDia = usersRef.child(_USERNAME).child("missoesDia");

        // Encontrar a missão pela sua descrição
        Query query = missoesDia.orderByChild("descricao").equalTo(descricaoMissao);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String missaoKey = dataSnapshot.getKey();

                    // Recuperar o progresso atual
                    int progressoAtual = dataSnapshot.child("progresso").getValue(Integer.class);

                    int novoProgressoTotal = progressoAtual + novoProgresso;

                    // Atualizar o progresso da missão
                    missoesDia.child(missaoKey).child("progresso").setValue(novoProgressoTotal);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void atualizarMissaoMes(String descricaoMissao, int novoProgresso) {
        DatabaseReference usersRef = rootNode.getReference("users");
        DatabaseReference missaoMes = usersRef.child(_USERNAME).child("missoesMes");


        int novoProgressoTotal = novoProgresso;

        missaoMes.child("progresso").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int progresso = dataSnapshot.getValue(Integer.class);

                    if (progresso < 100) {
                        missaoMes.child("progresso").setValue(novoProgressoTotal);
                        Toast.makeText(getApplicationContext(), "Você recebeu uma Nova Medalha EXCLUSIVA! Obrigado por apoiar o projeto\uD83D\uDE18", Toast.LENGTH_LONG).show();
                    } else {
                        missaoMes.child("progresso").setValue(novoProgressoTotal);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Encontrar a missão pela sua descrição
       /* Query query = missaoMes.orderByChild("descricao").equalTo(descricaoMissao);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String missaoKey = dataSnapshot.getKey();

                    // Recuperar o progresso atual
                    int progressoAtual = dataSnapshot.child("progresso").getValue(Integer.class);

                    int novoProgressoTotal = progressoAtual + novoProgresso;

                    // Atualizar o progresso da missão
                    missaoMes.child(missaoKey).child("progresso").setValue(novoProgressoTotal);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }); */
    }

}