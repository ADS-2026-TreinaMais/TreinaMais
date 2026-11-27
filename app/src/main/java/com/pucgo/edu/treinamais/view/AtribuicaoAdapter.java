
package com.pucgo.edu.treinamais.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pucgo.edu.treinamais.databinding.ItemAtribuicaoBinding;
import com.pucgo.edu.treinamais.network.dto.AlunoResponseDto;

import java.util.ArrayList;
import java.util.List;

public class AtribuicaoAdapter extends RecyclerView.Adapter<AtribuicaoAdapter.AtribuicaoViewHolder> {

    private final List<AlunoResponseDto> alunos = new ArrayList<>();
    private final List<Long> alunosAtribuidos = new ArrayList<>();
    private final OnAtribuicaoClickListener listener;

    public interface OnAtribuicaoClickListener {
        void onAtribuicaoClick(AlunoResponseDto aluno, boolean atribuido);
    }

    public AtribuicaoAdapter(OnAtribuicaoClickListener listener) {
        this.listener = listener;
    }

    public void setAlunos(List<AlunoResponseDto> listaAlunos, List<Long> idsAtribuidos) {
        alunos.clear();
        alunos.addAll(listaAlunos);

        alunosAtribuidos.clear();

        if (idsAtribuidos != null) {
            alunosAtribuidos.addAll(idsAtribuidos);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AtribuicaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAtribuicaoBinding binding = ItemAtribuicaoBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new AtribuicaoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AtribuicaoViewHolder holder, int position) {
        AlunoResponseDto aluno = alunos.get(position);

        boolean atribuido = alunosAtribuidos.contains(aluno.getId());

        holder.binding.tvNomeAlunoAtribuicao.setText(aluno.getNome());
        holder.binding.tvEmailAlunoAtribuicao.setText(aluno.getEmail());

        if (atribuido) {
            holder.binding.btnAtribuirTreino.setText("REMOVER TREINO");
        } else {
            holder.binding.btnAtribuirTreino.setText("ATRIBUIR TREINO");
        }

        holder.binding.btnAtribuirTreino.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAtribuicaoClick(aluno, atribuido);
            }
        });
    }

    @Override
    public int getItemCount() {
        return alunos.size();
    }

    static class AtribuicaoViewHolder extends RecyclerView.ViewHolder {

        private final ItemAtribuicaoBinding binding;

        public AtribuicaoViewHolder(@NonNull ItemAtribuicaoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
