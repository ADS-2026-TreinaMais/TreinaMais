package com.pucgo.edu.treinamais.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pucgo.edu.treinamais.R;
import com.pucgo.edu.treinamais.model.Aluno;

import java.util.ArrayList;
import java.util.List;

public class AlunoAdapter extends RecyclerView.Adapter<AlunoAdapter.AlunoViewHolder> {

    private final List<Aluno> alunos = new ArrayList<>();
    private OnAlunoClickListener listener;

    public interface OnAlunoClickListener {
        void onAlunoClick(Aluno aluno);
    }

    public void setOnAlunoClickListener(OnAlunoClickListener listener) {
        this.listener = listener;
    }

    public void setAlunos(List<Aluno> novosAlunos) {
        this.alunos.clear();
        if (novosAlunos != null) {
            this.alunos.addAll(novosAlunos);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlunoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_aluno, parent, false);
        return new AlunoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlunoViewHolder holder, int position) {
        Aluno aluno = alunos.get(position);

        holder.tvNome.setText(aluno.getNome() != null ? aluno.getNome() : "Aluno");
        holder.tvEmail.setText(aluno.getEmail() != null ? aluno.getEmail() : "");

        if (aluno.getTelefone() != null && !aluno.getTelefone().trim().isEmpty()) {
            holder.tvTelefone.setText(aluno.getTelefone());
            holder.tvTelefone.setVisibility(View.VISIBLE);
        } else {
            holder.tvTelefone.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAlunoClick(aluno);
            }
        });
    }

    @Override
    public int getItemCount() {
        return alunos.size();
    }

    static class AlunoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNome;
        TextView tvEmail;
        TextView tvTelefone;

        public AlunoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvNomeAlunoItem);
            tvEmail = itemView.findViewById(R.id.tvEmailAlunoItem);
            tvTelefone = itemView.findViewById(R.id.tvTelefoneAlunoItem);
        }
    }
}
