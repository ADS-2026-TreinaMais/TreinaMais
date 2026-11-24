
package com.pucgo.edu.treinamais.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pucgo.edu.treinamais.databinding.CadastroBinding;
import com.pucgo.edu.treinamais.network.ApiClient;
import com.pucgo.edu.treinamais.network.dto.MessageResponseDto;
import com.pucgo.edu.treinamais.network.dto.RegisterRequestDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroActivity extends AppCompatActivity {

    private CadastroBinding binding;

    private String tipoSelecionado = "ALUNO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = CadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configurarSelecaoTipo();
        configurarListeners();
    }

    private void configurarSelecaoTipo() {
        atualizarBotoesTipo();

        binding.btnTipoAluno.setOnClickListener(v -> {
            tipoSelecionado = "ALUNO";
            atualizarBotoesTipo();
        });

        binding.btnTipoProfessor.setOnClickListener(v -> {
            tipoSelecionado = "PROFESSOR";
            atualizarBotoesTipo();
        });
    }

    private void atualizarBotoesTipo() {
        if ("PROFESSOR".equals(tipoSelecionado)) {
            binding.crefInputLayout.setVisibility(View.VISIBLE);
            binding.cpfInputLayout.setVisibility(View.GONE);

            binding.btnTipoAluno.setChecked(false);
            binding.btnTipoProfessor.setChecked(true);
        } else {
            binding.crefInputLayout.setVisibility(View.GONE);
            binding.cpfInputLayout.setVisibility(View.VISIBLE);

            binding.btnTipoProfessor.setChecked(false);
            binding.btnTipoAluno.setChecked(true);
        }
    }

    private void configurarListeners() {
        binding.btnCadastrar.setOnClickListener(v -> realizarCadastro());
        binding.tvVoltarLogin.setOnClickListener(v -> finish());
    }

    private void realizarCadastro() {
        String nome = binding.editNome.getText() != null
                ? binding.editNome.getText().toString().trim() : "";

        String email = binding.editEmail.getText() != null
                ? binding.editEmail.getText().toString().trim() : "";

        String senha = binding.editSenha.getText() != null
                ? binding.editSenha.getText().toString().trim() : "";

        String cref = binding.editCref.getText() != null
                ? binding.editCref.getText().toString().trim() : null;

        String cpf = binding.editCpf.getText() != null
                ? binding.editCpf.getText().toString().trim() : null;

        String telefone = binding.editTelefone.getText() != null
                ? binding.editTelefone.getText().toString().trim() : null;

        if (nome.isEmpty()) {
            binding.editNome.setError("Informe seu nome completo");
            binding.editNome.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            binding.editEmail.setError("Informe seu e-mail");
            binding.editEmail.requestFocus();
            return;
        }

        if (senha.length() < 6) {
            binding.editSenha.setError("A senha deve ter no mínimo 6 caracteres");
            binding.editSenha.requestFocus();
            return;
        }

        if ("PROFESSOR".equals(tipoSelecionado) && (cref == null || cref.isEmpty())) {
            binding.editCref.setError("O CREF é obrigatório para cadastro de professor");
            binding.editCref.requestFocus();
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

        ApiClient.getInstance(this)
                .getAuthApiService()
                .register(request)
                .enqueue(new Callback<MessageResponseDto>() {

                    @Override
                    public void onResponse(Call<MessageResponseDto> call,
                                           Response<MessageResponseDto> response) {
                        setCarregando(false);

                        if (response.isSuccessful()) {
                            Toast.makeText(
                                    CadastroActivity.this,
                                    "Cadastro realizado com sucesso! Faça login.",
                                    Toast.LENGTH_LONG
                            ).show();

                            finish();
                        } else {
                            String erro = "Erro ao cadastrar usuário.";

                            try {
                                if (response.errorBody() != null) {
                                    erro = response.errorBody().string();
                                }
                            } catch (Exception ignored) {
                            }

                            Toast.makeText(
                                    CadastroActivity.this,
                                    erro,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageResponseDto> call, Throwable t) {
                        setCarregando(false);

                        Toast.makeText(
                                CadastroActivity.this,
                                "Falha de rede: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void setCarregando(boolean carregando) {
        binding.progressBarCadastro.setVisibility(
                carregando ? View.VISIBLE : View.GONE
        );

        binding.btnCadastrar.setEnabled(!carregando);
    }
}
