package com.pucgo.edu.treinamais.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pucgo.edu.treinamais.R;
import com.pucgo.edu.treinamais.network.ApiClient;
import com.pucgo.edu.treinamais.network.dto.MessageResponseDto;
import com.pucgo.edu.treinamais.network.dto.RegisterRequestDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroActivity extends AppCompatActivity {

    private MaterialButton btnTipoAluno;
    private MaterialButton btnTipoProfessor;
    private TextInputEditText editNome;
    private TextInputEditText editEmail;
    private TextInputEditText editSenha;
    private TextInputEditText editCref;
    private TextInputEditText editCpf;
    private TextInputEditText editTelefone;
    private TextInputLayout crefInputLayout;
    private TextInputLayout cpfInputLayout;
    private ProgressBar progressBar;
    private Button btnCadastrar;
    private TextView tvVoltarLogin;

    private String tipoSelecionado = "ALUNO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro);

        inicializarViews();
        configurarSelecaoTipo();
        configurarListeners();
    }

    private void inicializarViews() {
        btnTipoAluno = findViewById(R.id.btnTipoAluno);
        btnTipoProfessor = findViewById(R.id.btnTipoProfessor);
        editNome = findViewById(R.id.editNome);
        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        editCref = findViewById(R.id.editCref);
        editCpf = findViewById(R.id.editCpf);
        editTelefone = findViewById(R.id.editTelefone);
        crefInputLayout = findViewById(R.id.crefInputLayout);
        cpfInputLayout = findViewById(R.id.cpfInputLayout);
        progressBar = findViewById(R.id.progressBarCadastro);
        btnCadastrar = findViewById(R.id.btnCadastrar);
        tvVoltarLogin = findViewById(R.id.tvVoltarLogin);
    }

    private void configurarSelecaoTipo() {
        atualizarBotoesTipo();

        btnTipoAluno.setOnClickListener(v -> {
            tipoSelecionado = "ALUNO";
            atualizarBotoesTipo();
        });

        btnTipoProfessor.setOnClickListener(v -> {
            tipoSelecionado = "PROFESSOR";
            atualizarBotoesTipo();
        });
    }

    private void atualizarBotoesTipo() {
        if ("PROFESSOR".equals(tipoSelecionado)) {
            crefInputLayout.setVisibility(View.VISIBLE);
            cpfInputLayout.setVisibility(View.GONE);
//            btnTipoProfessor.setAlpha(1.0f);
//            btnTipoAluno.setAlpha(0.5f);
            btnTipoAluno.setChecked(false);
            btnTipoProfessor.setChecked(true);
        } else {
            crefInputLayout.setVisibility(View.GONE);
            cpfInputLayout.setVisibility(View.VISIBLE);
//            btnTipoAluno.setAlpha(1.0f);
//            btnTipoProfessor.setAlpha(0.5f);
            btnTipoProfessor.setChecked(false);
            btnTipoAluno.setChecked(true);
        }
    }

    private void configurarListeners() {
        btnCadastrar.setOnClickListener(v -> realizarCadastro());
        tvVoltarLogin.setOnClickListener(v -> finish());
    }

    private void realizarCadastro() {
        String nome = editNome.getText() != null ? editNome.getText().toString().trim() : "";
        String email = editEmail.getText() != null ? editEmail.getText().toString().trim() : "";
        String senha = editSenha.getText() != null ? editSenha.getText().toString().trim() : "";
        String cref = editCref.getText() != null ? editCref.getText().toString().trim() : null;
        String cpf = editCpf.getText() != null ? editCpf.getText().toString().trim() : null;
        String telefone = editTelefone.getText() != null ? editTelefone.getText().toString().trim() : null;

        if (nome.isEmpty()) {
            editNome.setError("Informe seu nome completo");
            editNome.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editEmail.setError("Informe seu e-mail");
            editEmail.requestFocus();
            return;
        }

        if (senha.length() < 6) {
            editSenha.setError("A senha deve ter no mínimo 6 caracteres");
            editSenha.requestFocus();
            return;
        }

        if ("PROFESSOR".equals(tipoSelecionado) && (cref == null || cref.isEmpty())) {
            editCref.setError("O CREF é obrigatório para cadastro de professor");
            editCref.requestFocus();
            return;
        }

        setCarregando(true);

        RegisterRequestDto request = new RegisterRequestDto(
                nome,
                email,
                senha,
                tipoSelecionado,
                "PROFESSOR".equals(tipoSelecionado) ? cref : null,
                "ALUNO".equals(tipoSelecionado) ? cpf : null,
                telefone
        );

        ApiClient.getInstance(this).getAuthApiService().register(request).enqueue(new Callback<MessageResponseDto>() {
            @Override
            public void onResponse(Call<MessageResponseDto> call, Response<MessageResponseDto> response) {
                setCarregando(false);
                if (response.isSuccessful()) {
                    Toast.makeText(CadastroActivity.this, "Cadastro realizado com sucesso! Faça login.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    String erro = "Erro ao cadastrar usuário.";
                    try {
                        if (response.errorBody() != null) {
                            erro = response.errorBody().string();
                        }
                    } catch (Exception ignored) {
                    }
                    Toast.makeText(CadastroActivity.this, erro, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponseDto> call, Throwable t) {
                setCarregando(false);
                Toast.makeText(CadastroActivity.this, "Falha de rede: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setCarregando(boolean carregando) {
        if (progressBar != null) {
            progressBar.setVisibility(carregando ? View.VISIBLE : View.GONE);
        }
        if (btnCadastrar != null) {
            btnCadastrar.setEnabled(!carregando);
        }
    }
}
