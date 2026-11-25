package com.pucgo.edu.treinamais.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pucgo.edu.treinamais.databinding.ItemProfBinding;
import com.pucgo.edu.treinamais.network.dto.ProfessorResponseDto;

import java.util.ArrayList;
import java.util.List;

public class ProfAdapter extends RecyclerView.Adapter<ProfAdapter.ProfHolder>{
    private final List<ProfessorResponseDto> profs = new ArrayList<>();

    public interface OnProfClickListener {void onProfClick(ProfessorResponseDto prof);}
    private OnProfClickListener listener;
    public void setOnProfClickListener(OnProfClickListener listener){this.listener = listener;}

    public void setProfs(List<ProfessorResponseDto> newProfs){
        this.profs.clear();
        this.profs.addAll(newProfs);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProfHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProfBinding B = ItemProfBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ProfHolder(B);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfHolder holder, int position){
        ProfessorResponseDto prof = profs.get(position);

        holder.B.profNome.setText(prof.getNome());
        holder.B.profEmail.setText(prof.getEmail());
        holder.B.profCref.setText(prof.getCref());

        holder.B.getRoot().setOnClickListener(v -> {
            if(listener != null){
                listener.onProfClick(prof);
            }
        });
    }

    @Override
    public int getItemCount(){
        return profs.size();
    }

    static class ProfHolder extends RecyclerView.ViewHolder {
        final ItemProfBinding B;
        public ProfHolder(@NonNull ItemProfBinding B){
            super(B.getRoot());
            this.B = B;
        }

    }
}
