package com.pucgo.edu.treinamais.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pucgo.edu.treinamais.R;
import com.pucgo.edu.treinamais.network.dto.TreinoResponseDto;

import java.util.ArrayList;
import java.util.List;

public class TreinoAdapter extends RecyclerView.Adapter<TreinoAdapter.TreinoViewHolder> {

    private final List<TreinoResponseDto> treinos = new ArrayList<>();

    public void setTreinos(List<TreinoResponseDto> novosTreinos) {
        this.treinos.clear();
        if (novosTreinos != null) {
            this.treinos.addAll(novosTreinos);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TreinoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_treino, parent, false);
        return new TreinoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TreinoViewHolder holder, int position) {
        TreinoResponseDto treino = treinos.get(position);

        holder.tvNome.setText(treino.getNome() != null ? treino.getNome() : "Treino");

        StringBuilder info = new StringBuilder();
        if (treino.getObjetivo() != null && !treino.getObjetivo().isBlank()) {
            info.append(treino.getObjetivo());
        }
        int qtdExercicios = treino.getExercicios() != null ? treino.getExercicios().size() : 0;
        if (qtdExercicios > 0) {
            if (info.length() > 0) {
                info.append(" • ");
            }
            info.append(qtdExercicios).append(qtdExercicios == 1 ? " exercício" : " exercícios");
        }
        holder.tvObjetivo.setText(info.toString());

        if (treino.getProfessorNome() != null && !treino.getProfessorNome().isBlank()) {
            holder.tvProfessor.setText("Prof: " + treino.getProfessorNome());
            holder.tvProfessor.setVisibility(View.VISIBLE);
        } else {
            holder.tvProfessor.setVisibility(View.GONE);
        }

        String status = treino.getStatus() != null ? treino.getStatus() : "ATIVO";
        holder.tvStatus.setText(status);
    }

    @Override
    public int getItemCount() {
        return treinos.size();
    }

    static class TreinoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNome;
        TextView tvObjetivo;
        TextView tvProfessor;
        TextView tvStatus;

        public TreinoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvNomeTreinoItem);
            tvObjetivo = itemView.findViewById(R.id.tvObjetivoTreinoItem);
            tvProfessor = itemView.findViewById(R.id.tvProfessorTreinoItem);
            tvStatus = itemView.findViewById(R.id.tvStatusTreinoItem);
        }
    }
}
